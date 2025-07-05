package org.shootemup.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.shootemup.GameLib;
import org.shootemup.components.Background;
import org.shootemup.components.Explosion;
import org.shootemup.components.Vector2D;
import org.shootemup.config.Config;
import org.shootemup.entities.Enemy;
import org.shootemup.entities.Player;
import org.shootemup.entities.Projectile;
import org.shootemup.entities.Powerup;
import org.shootemup.utils.Direction;

/// Classe que representa o sistema de jogo
public class Game {
    private long currentTime = System.currentTimeMillis();
    private long delta;

    private boolean isRunning = false;

    private Background farStarBackground;
    private Background nearStarBackground;

    private Player player;
    private List<Projectile> projectiles;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Powerup> powerups;

    private Queue<GameLevel> levels; // Fila de fases

    public Game() {
        // Inicializa o jogo com os arquivos de configuração
        var config = new Config();
        // Carrega as fases
        levels = config.loadPhases();

        // Cria o fundo de estrelas
        nearStarBackground = Background.forStars(Color.GRAY, 20, 3, 0.070);
        farStarBackground = Background.forStars(Color.DARK_GRAY, 20, 2, 0.045);

        // Cria o player
        player = new Player(
            config.playerLife,
            new Vector2D(GameLib.WIDTH / 2, GameLib.HEIGHT / 2),
            12.0,
            Vector2D.ofScalar(0.25)
        );

        // Cria os arrays de entidades do jogo
        projectiles = new ArrayList<>(200);
        enemies = new ArrayList<>(20);
        explosions = new ArrayList<>(15);
        powerups = new ArrayList<>(5);
    }

    /// Lê a entrada do usuário e reage
    private void read_input() {
        if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.move(delta, Direction.NORTH);
        if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.move(delta, Direction.SOUTH);
        if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.move(delta, Direction.WEST);
        if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.move(delta, Direction.EAST);

        if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
            // Tenta atirar se for um sucesso adiciona projétil a lista de projeteis
            player.shot(currentTime).ifPresent((bullet) -> projectiles.add(bullet));
        }

        if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) isRunning = false;
    }

    /// Função para atualizar movimentos, animações, checar colisões e spawnar inimigos
    private void update() {
        /* Lógica das fases */
        // Se não tem mais fases nem inimigos o jogador venceu
        if (levels.peek() == null) {
            if (enemies.isEmpty()) {
                System.out.println("Game Over!!! Você venceu o jogo!! Meus parabéns!");
                isRunning = false;
            }
        } else {
            // Inicia a fase atual se ela nao comecou ainda
            if (!levels.peek().getHasStarted()) {
                levels.peek().start(currentTime);
            }

            // Spawna entidades com spawn < que o tempo atual
            enemies.addAll(levels.peek().takeEnemiesLessThan(currentTime));
            powerups.addAll(levels.peek().takePowerUpsLessThan(currentTime));

            // Se nao tem mais entidades e a fase atual nao tem mais quem adicionar -> puxa a proxima fase
            if (enemies.isEmpty() && levels.peek().isComplete()) {
                levels.poll();
            }
        }

        // Se o jogador perder todos os pontos de vida encerra o jogo
        if (player.getHP() == 0) {
            System.out.println("Game Over!!! Você morreu! Seja melhor na próxima vez, jogue de novo!");
            isRunning = false;
            return;
        }

        // Se o powerup de pausa de tempo esta desativado mexa animações e inimigos
        if(!player.isZaWarudoActive()) {

            // Movimenta os planos de fundo
            nearStarBackground.animate(delta);
            farStarBackground.animate(delta);

            // Atualiza a posicao dos projéteis inimigos
            // e remove se fora da tela
            projectiles.removeIf(proj -> {
                if(proj instanceof Projectile.Ball) {
                    proj.move(delta);
                    Vector2D pos = proj.getPosition();
                    return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
                }
                return false;
            });

            // Movimenta e remove inimigos fora da tela
            // (com um pequeno offset para que o jogo rode mais alguns frames antes do ultimo inimigo sair da tela)
            enemies.removeIf(enemy -> {
                enemy.move(delta);
                Vector2D pos = enemy.getPosition();
                return pos.getY() > GameLib.HEIGHT + 10 || pos.getX() < -10 || pos.getX() > GameLib.WIDTH + 10;
            });

            // Inimigos fazem uma tentativa de tiro
            enemies.forEach(e -> {
                // Se inimigo for do tipo flyer atira com multiShot
                if (e instanceof Enemy.Flyer) {
                    Enemy.Flyer advancedEnemy = (Enemy.Flyer) e;
                    List<Projectile> multiShot = advancedEnemy.shotMultiple(currentTime);
                    projectiles.addAll(multiShot);
                } else {
                    e.shot(currentTime).ifPresent(bullet -> projectiles.add(bullet));
                }
            });

            // Movimenta e remove power ups fora da tela
            powerups.removeIf(pow -> {
                pow.move(delta);
                Vector2D pos = pow.getPosition();
                return pos.getY() > GameLib.HEIGHT + 10;
            });
        }

        // checa se power ups acabaram
        player.updatePowerUpTimers(delta);

        /* Colisões */

        // colisoes entre player e projeteis do tipo Ball
        projectiles.stream()
            .filter(proj -> proj instanceof Projectile.Ball)
            .forEach(proj -> {
                if (player.intersects(proj)) {
                    player.damage(currentTime);
                    explosions.add(new Explosion(player.getPosition(), currentTime, 2000));
                }
            });

        // colisão entre player e enemy
        enemies.forEach(enemy -> {
           if (player.intersects(enemy)) {
               player.damage(currentTime);
               explosions.add(new Explosion(player.getPosition(), currentTime, 2000));
           }
        });

        // checa se cada inimigo colidiu com um projetil do player
        projectiles.stream()
            .filter(proj -> proj instanceof Projectile.Bullet || proj instanceof Projectile.Laser)
            .forEach(bullet -> {
                enemies.removeIf(enemy -> {
                    if (enemy.intersects(bullet)) {
                        /*Precisamos, agora, fazer uma alteração na vida dos inimigos. Isso
                        é mais relevante quando estamos falando de algum boss. Primeiro, subtraímos
                        e depois vemos se a vida do inimigo zerou*/
                        int momentLife = enemy.getLife();
                        enemy.setLife(--momentLife);
                        explosions.add(new Explosion(enemy.getPosition(), currentTime, 500));
                        if(enemy.getLife() == 0){
                            return true;
                        }
                    }
                    return false;
                });
            });

        // Checa se o jogador pegou um power up
        powerups.removeIf(pow -> {
            if(player.intersects(pow)) {
                player.pickPowerUp(pow);
                return true;
            }
            return false;
        });

        /* Movimenta/ Atualiza entidades */

        // Atualiza o estado das explosões e remove as que completaram
        explosions.removeIf(expl -> {expl.update(currentTime); return expl.isFinished();});

        // Atualiza a posicao das balas do jogador
        // e remove se fora da tela
        projectiles.removeIf(proj -> {
            if(proj instanceof Projectile.Bullet || proj instanceof Projectile.Laser) {
                proj.move(delta);
                Vector2D pos = proj.getPosition();
                return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
            }
            return false;
        });
    }

    private void render() {

        // Renderiza cada background
        farStarBackground.render();
        nearStarBackground.render();

        // Renderiza os projéteis
        projectiles.forEach((b)-> b.render());
        // Renderiza inimigos
        enemies.forEach((e)-> e.render());
        // Renderiza power ups
        powerups.forEach((p)-> p.render());
        // Renderiza explosoes
        explosions.forEach(expl -> expl.render());

        // Renderiza o player
        player.render();

        GameLib.display();
    }

    // Executa o jogo
	public void run() {
   	    // inicializa a biblioteca gráfica
       	GameLib.initGraphics();
        //GameLib.initGraphics_SAFE_MODE();  // chame esta versão do método caso nada seja desenhado na janela do jogo.

        isRunning = true;

        while (isRunning) {
            delta = System.currentTimeMillis() - currentTime;
            currentTime = System.currentTimeMillis();

            update();
            read_input();
            render();

            // Deixa a thread em _idle_ para normalizar o frame rate
            busyWait(currentTime + 3);
        }
   	    System.exit(0);
	}

	/// Mantem a thread em estado de espera
	private static void busyWait(long time){
		while(System.currentTimeMillis() < time) Thread.yield();
	}
}
