package org.shootemup.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.entities.Background;
import org.shootemup.entities.Enemy;
import org.shootemup.entities.Player;
import org.shootemup.entities.Projectile;
import org.shootemup.utils.Direction;

/// Classe que representa o sistema de jogo
// Runnable indica que a classe é executavel e nunca retorna
public class Game implements Runnable {
    private long currentTime = System.currentTimeMillis();
    private long delta;

    private boolean isRunning = false;

    private Background farStarBackground;
    private Background nearStarBackground;

    private List<Projectile> projectiles;
    private List<Enemy> enemies;
    private long nextCommonSpawn = currentTime + 2000;

    private Player player;


    public Game() {

        nearStarBackground = Background.forStars(Color.GRAY, 20, 3, 0.070);
        farStarBackground = Background.forStars(Color.DARK_GRAY, 20, 2, 0.045);

        player = new Player(
            new Vector2D(GameLib.WIDTH / 2, GameLib.HEIGHT / 2),
            12.0,
            Vector2D.ofScalar(0.25)
        );

        projectiles = new ArrayList<>(200);
        enemies = new ArrayList<>(10);
    }

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

    private void update() {

        /* Colisões */
        // TODO: checar colisoes
        // TODO: resolver colisoes
        // Se colisao, deleta colidido e coloca uma explosão no lugar da entity
        // Array de explosoes
        // Explosao: position e startTime


        /* Movimenta entidades */

        // Movimenta os planos de fundo
        nearStarBackground.animate(delta);
        farStarBackground.animate(delta);

        // Atualiza a posicao dos projeteis
        projectiles.forEach(proj -> proj.move(delta));
        // Remove projéteis fora da tela
        projectiles.removeIf(proj -> {
            Vector2D pos = proj.getPosition();
            return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
        });

        // Movimenta e remove inimigos fora da tela
        enemies.forEach(e -> e.move(delta));
        enemies.removeIf(enemy -> {
            Vector2D pos = enemy.getPosition();
            return pos.getY() > GameLib.HEIGHT + 10;
        });

        // Inimigos fazem uma tentativa de tiro
        enemies.forEach(e -> e.shot(currentTime)
            .ifPresent(bullet -> projectiles.add(bullet))
        );

        /* Spawnar inimigos */
        if (currentTime > nextCommonSpawn) {
            enemies.add(Enemy.forCommon(
                new Vector2D(Math.random() * (GameLib.WIDTH - 20) + 10, -10.0)
            ));
            nextCommonSpawn = currentTime + 500;
        }
    }

    private void render() {

        // Renderiza cada background
        farStarBackground.render();
        nearStarBackground.render();

        // Renderiza os projéteis
        projectiles.forEach((b)->b.render());
        // Renderiza inimigos
        enemies.forEach((e)->e.render());

        // Renderiza o player
        player.render();

        GameLib.display();
    }

    @Override
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
