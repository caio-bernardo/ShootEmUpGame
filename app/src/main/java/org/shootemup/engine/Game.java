package org.shootemup.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.entities.Background;
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

    private Player player;

    public Game() {

        nearStarBackground = Background.forStars(Color.GRAY, 20, 3, 0.070);
        farStarBackground = Background.forStars(Color.DARK_GRAY, 20, 2, 0.045);

        player = new Player(
            new Vector2D(GameLib.WIDTH / 2, GameLib.HEIGHT / 2),
            12.0,
            Vector2D.ofScalar(0.25)
        );

        projectiles = new ArrayList<>(10);
    }

    protected void read_input() {
        if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.move(delta, Direction.NORTH);
        if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.move(delta, Direction.SOUTH);
        if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.move(delta, Direction.WEST);
        if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.move(delta, Direction.EAST);

        if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
            Optional<Projectile.Bullet> bullet = player.shot(currentTime);
            if (bullet.isPresent()) {
                projectiles.add(bullet.get());
            }
        }

        if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) isRunning = false;
    }

    protected void update() {

        // Anima os planos de fundo
        nearStarBackground.animate(delta);
        farStarBackground.animate(delta);

        // Atualiza a posicao dos projeteis
        projectiles.forEach(proj -> proj.move(delta));
        // Remove projéteis fora da tela
        projectiles.removeIf(proj -> {
            Vector2D pos = proj.getPosition();
            return pos.getX() < 0 || pos.getX() > GameLib.WIDTH || pos.getY() < 0 || pos.getY() > GameLib.HEIGHT;
        });
    }

    protected void render() {

        // Renderiza cada background
        farStarBackground.render();
        nearStarBackground.render();

        player.render();
        projectiles.forEach((b)->b.render());

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
	protected static void busyWait(long time){
		while(System.currentTimeMillis() < time) Thread.yield();
	}
}
