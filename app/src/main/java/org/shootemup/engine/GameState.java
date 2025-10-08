package org.shootemup.engine;

import org.shootemup.components.Background;
import org.shootemup.components.Explosion;
import org.shootemup.components.Vector2D;
import org.shootemup.config.Config;
import org.shootemup.entities.Enemy;
import org.shootemup.entities.Player;
import org.shootemup.entities.Powerup;
import org.shootemup.entities.Projectile;
import org.shootemup.utils.Direction;
import org.shootemup.utils.Renderable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public enum GameState implements GameEval {
    MENU {
        @Override
        public void update(GameData data, long delta, long currentTime) {
            // Movimenta os planos de fundo
            data.nearStarBackground.animate(delta);
            data.farStarBackground.animate(delta);
        }

        @Override
        public void read_input(GameData data, long delta, long currentTime) {
            if (GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) data.isRunning = false;
            if (GameLib.iskeyPressed(GameLib.KEY_SPACE)) data.state = GameState.PLAYING;
        }

        @Override
        public void render(GameData data, long delta, long currentTime) {
            data.farStarBackground.render();
            data.nearStarBackground.render();
            var fontSize = 40;
            var fontWidth = fontSize * 0.6;

            GameLib.setColor(Color.PINK);
            GameLib.drawText("Shoot'Em Up!", GameLib.WIDTH / 2.0 - (fontWidth * 6.0), GameLib.HEIGHT / 2.0 - 130.0, fontSize);
            GameLib.drawText("Press Space to Start", GameLib.WIDTH / 2.0 - (30 * 0.6 * 8.5), GameLib.HEIGHT / 2.0 + 50, 30);
            GameLib.drawText("←↓↑→ to move", GameLib.WIDTH / 2.0 - (30 * 0.6 * 6.0), GameLib.HEIGHT / 2.0 + 140, 30);
            GameLib.drawText("<Ctrl> to shoot", GameLib.WIDTH / 2.0 - (30 * 0.6 * 6.0), GameLib.HEIGHT / 2.0 + 170, 30);
            GameLib.drawText("<Esc> to pause", GameLib.WIDTH / 2.0 - (30 * 0.6 * 6.0), GameLib.HEIGHT / 2.0 + 200, 30);

            GameLib.display();
        }
    },
    PAUSED {
        @Override
        public void update(GameData data, long delta, long currentTime) {
            // Movimenta os planos de fundo
            data.nearStarBackground.animate(delta);
            data.farStarBackground.animate(delta);
        }

        @Override
        public void read_input(GameData data, long delta, long currentTime) {
            if (GameLib.iskeyPressed(GameLib.KEY_SPACE)) data.state = GameState.PLAYING;
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) data.isRunning = false;
        }

        @Override
        public void render(GameData data, long delta, long currentTime) {
            data.farStarBackground.render();
            data.nearStarBackground.render();
            var fontSize = 40;
            var fontWidth = fontSize * 0.6;

            GameLib.setColor(Color.PINK);
            GameLib.drawText("Game Paused", GameLib.WIDTH / 2.0 - (fontWidth * 6.0), GameLib.HEIGHT / 2.0 - 130.0, fontSize);

            GameLib.drawText("Press Space to Resume", GameLib.WIDTH / 2.0 - (30 * 0.6 * 10.0), GameLib.HEIGHT / 2.0 + 50, 30);
            GameLib.drawText("Press <Ctrl> to Exit", GameLib.WIDTH / 2.0 - (30 * 0.6 * 8.5), GameLib.HEIGHT / 2.0 + 150, 30);

            GameLib.display();
        }
    },
    PLAYING {
        @Override
        public void update(GameData data, long delta, long currentTime) {
            /* Lógica das fases */
            // Se não tem mais fases nem inimigos o jogador venceu
            if (data.levels.peek() == null) {
                if (data.enemies.isEmpty()) {
                    data.state = GameState.FINISHED;
                }
            } else {
                // Inicia a fase atual se ela nao começou ainda
                if (!data.levels.peek().getHasStarted()) {
                    data.levels.peek().start(currentTime);
                }

                // Spawn entidades com spawn < que o tempo atual
                data.enemies.addAll(data.levels.peek().takeEnemiesLessThan(currentTime));
                data.powerups.addAll(data.levels.peek().takePowerUpsLessThan(currentTime));

                // Se nao tem mais entidades e a fase atual nao tem mais quem adicionar -> puxa a proxima fase
                if (data.enemies.isEmpty() && data.levels.peek().isComplete()) {
                    data.levels.poll();
                }
            }

            // Se o jogador perder todos os pontos de vida encerra o jogo
            if (data.player.getHP() == 0) {
                data.state = GameState.FINISHED;
                return;
            }

            // Se o powerup de pausa de tempo esta desativado mexa animações e inimigos
            if(!data.player.isZaWarudoActive() && !data.secondBossZaWarudo) {

                // Movimenta os planos de fundo
                data.nearStarBackground.animate(delta);
                data.farStarBackground.animate(delta);

                // Atualiza a posição dos projéteis inimigos
                // e remove se fora da tela
                data.projectiles.removeIf(proj -> {
                    if(proj instanceof Projectile.Ball || proj instanceof Projectile.timeBall) {
                        proj.move(delta);
                        Vector2D pos = proj.getPosition();
                        return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
                    }
                    return false;
                });

                // Movimenta e remove inimigos fora da tela
                // (com um pequeno offset para que o jogo rode mais alguns frames antes do ultimo inimigo sair da tela)
                data.enemies.removeIf(enemy -> {
                    enemy.move(delta);
                    Vector2D pos = enemy.getPosition();
                    return pos.getY() > GameLib.HEIGHT + 10 || pos.getX() < -10 || pos.getX() > GameLib.WIDTH + 10;
                });

                // Inimigos fazem uma tentativa de tiro
                data.enemies.forEach(e -> {
                    // Se inimigo for do tipo flyer atira com multiShot
                    if (e instanceof Enemy.Flyer advancedEnemy) {
                        List<Projectile> multiShot = advancedEnemy.shotMultiple(currentTime);
                        data.projectiles.addAll(multiShot);
                    } else {
                        e.shot(currentTime).ifPresent(bullet -> data.projectiles.add(bullet));
                    }
                });

                // Movimenta e remove power ups fora da tela
                data.powerups.removeIf(pow -> {
                    pow.move(delta);
                    Vector2D pos = pow.getPosition();
                    return pos.getY() > GameLib.HEIGHT + 10;
                });
            }

            // checa se power ups acabaram
            data.player.updatePowerUpTimers(delta);

            /* Colisões */

            // colisões entre player e projeteis do tipo Ball
            data.projectiles.stream()
                    .filter(proj -> proj instanceof Projectile.Ball || proj instanceof Projectile.timeBall || proj instanceof Projectile.Laser)
                    .forEach(proj -> {
                        if (data.player.intersects(proj)) {
                            data.player.damage(currentTime);
                            data.explosions.add(new Explosion(data.player.getPosition(), currentTime, 2000));
                        }
                    });

            // colisão entre player e enemy
            data.enemies.forEach(enemy -> {
                if (data.player.intersects(enemy)) {
                    data.player.damage(currentTime);
                    data.explosions.add(new Explosion(data.player.getPosition(), currentTime, 2000));
                }
            });

            // checa se cada inimigo colidiu com um projetil do player
            data.projectiles.stream()
                    .filter(proj -> proj instanceof Projectile.Bullet || proj instanceof Projectile.Laser)
                    .forEach(bullet -> {
                        data.enemies.removeIf(enemy -> {
                            if (enemy.intersects(bullet)) {
                        /*Precisamos, agora, fazer uma alteração na vida dos inimigos. Isso
                        é mais relevante quando estamos falando de algum boss. Primeiro, subtraímos
                        e depois vemos se a vida do inimigo zerou*/
                                int momentLife = enemy.getLife();
                                enemy.setLife(--momentLife);
                                data.explosions.add(new Explosion(enemy.getPosition(), currentTime, 500));
                                return enemy.getLife() == 0;
                            }
                            return false;
                        });
                    });

            // Checa se o jogador pegou um power up
            data.powerups.removeIf(pow -> {
                if(data.player.intersects(pow)) {
                    data.player.pickPowerUp(pow);
                    return true;
                }
                return false;
            });

            /* Movimenta/ Atualiza entidades */
            // Atualiza o estado das explosões e remove quais completaram
            data.explosions.removeIf(expl -> {expl.update(currentTime); return expl.isFinished();});

            // Atualiza a posição das balas do jogador
            // e remove se fora da tela
            data.projectiles.removeIf(proj -> {
                if(proj instanceof Projectile.Bullet || proj instanceof Projectile.Laser) {
                    proj.move(delta);
                    Vector2D pos = proj.getPosition();
                    return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
                }
                return false;
            });

            data.enemies.forEach(e -> {
                if (e instanceof Enemy.ZaWarudo advancedEnemy) {
                    if(!data.secondBossZaWarudo) advancedEnemy.activateBossZaWarudo(currentTime);
                    data.secondBossZaWarudo = advancedEnemy.isZaWarudoActive(currentTime);
                    advancedEnemy.updateZaWarudoTimer(delta);

                    if(data.secondBossZaWarudo){
                        advancedEnemy.move(delta);
                        advancedEnemy.shot(currentTime).ifPresent(bullet -> data.projectiles.add(bullet));
                    }
                } else if(e instanceof Enemy.ShadowPlayer advancedEnemy){
                    advancedEnemy.activateBossLaser(currentTime);
                    advancedEnemy.updateLaserTimer(delta);
                }
            });
        }
        @Override
        public void read_input(GameData data, long delta, long currentTime) {
            if(!data.secondBossZaWarudo){
                if(GameLib.iskeyPressed(GameLib.KEY_UP)) data.player.move(delta, Direction.NORTH);
                if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) data.player.move(delta, Direction.SOUTH);
                if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) data.player.move(delta, Direction.WEST);
                if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) data.player.move(delta, Direction.EAST);

                if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
                    // Tenta atirar se for um sucesso adiciona projétil a lista de projeteis
                    if(data.player.isLaserModeActive()){
                        data.player.laserShot(currentTime).ifPresent((laser)-> data.projectiles.add(laser));
                    } else {
                        data.player.shot(currentTime).ifPresent((bullet) -> data.projectiles.add(bullet));
                    }
                }
            }
            if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) data.state = GameState.PAUSED;
        }
        @Override
        public void render(GameData data, long delta, long currentTime) {
            // Renderiza cada background
            data.farStarBackground.render();
            data.nearStarBackground.render();

            // Renderiza os projéteis
            data.projectiles.forEach(Renderable::render);
            // Renderiza inimigos
            data.enemies.forEach(Enemy::render);
            // Renderiza power ups
            data.powerups.forEach(Powerup::render);
            // Renderiza explosões
            data.explosions.forEach(Explosion::render);

            // Renderiza o player
            data.player.render();

            GameLib.display();
        }
    },
    FINISHED {
        @Override
        public void update(GameData data, long delta, long currentTime) {
            // Movimenta os planos de fundo
            data.nearStarBackground.animate(delta);
            data.farStarBackground.animate(delta);
        }
        @Override
        public void read_input(GameData data, long delta, long currentTime) {
            // TODO: reset GameData to start again
            if (GameLib.iskeyPressed(GameLib.KEY_SPACE)) {
                var config = new Config();
                // Carrega as fases
                var levels = config.loadPhases();

                // Cria o fundo de estrelas
                var nearStarBackground = Background.forStars(Color.GRAY, 20, 3, 0.070);
                var farStarBackground = Background.forStars(Color.DARK_GRAY, 20, 2, 0.045);

                // Cria o player
                var player = new Player(
                        config.playerLife,
                        new Vector2D(GameLib.WIDTH / 2.0, GameLib.HEIGHT / 2.0),
                        12.0,
                        Vector2D.ofScalar(0.25)
                );

                data = new GameData(
                        new ArrayList<>(20),
                        new ArrayList<>(15),
                        farStarBackground,
                        false,
                        levels,
                        nearStarBackground,
                        player,
                        new ArrayList<>(5),
                        new ArrayList<>(200),
                        false
                );
                data.state = GameState.MENU;
            };
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) data.isRunning = false;
        }
        @Override
        public void render(GameData data, long delta, long currentTime) {
            data.farStarBackground.render();
            data.nearStarBackground.render();
            var fontSize = 40;
            var fontWidth = fontSize * 0.6;

            GameLib.setColor(Color.PINK);
            GameLib.drawText("Game Over!!!", GameLib.WIDTH / 2.0 - (fontWidth * 6.0), GameLib.HEIGHT / 2.0 - 130.0, fontSize);

            GameLib.drawText("Press Space to Play Again", GameLib.WIDTH / 2.0 - (30 * 0.6 * 11.0), GameLib.HEIGHT / 2.0 + 50, 30);
            GameLib.drawText("Press <Ctrl> to Exit", GameLib.WIDTH / 2.0 - (30 * 0.6 * 8.5), GameLib.HEIGHT / 2.0 + 150, 30);

            GameLib.display();
        }
    }
}