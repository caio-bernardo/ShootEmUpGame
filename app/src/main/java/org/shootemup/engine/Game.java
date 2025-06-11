package org.shootemup.engine;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.entities.Background;
import org.shootemup.entities.Player;
import org.shootemup.utils.Direction;

/// Classe que representa o sistema de jogo
// Runnable indica que a classe é executavel e nunca retorna
public class Game implements Runnable {
    private long currentTime = System.currentTimeMillis();
    private long delta;

    private boolean isRunning = false;

    private Background farStarBackground;
    private Background nearStarBackground;


    private Player player;

    public Game() {

        nearStarBackground = Background.forStars(Color.GRAY, 20, 3, 0.070);
        farStarBackground = Background.forStars(Color.DARK_GRAY, 20, 2, 0.045);

        player = new Player(
            new Vector2D(GameLib.WIDTH / 2, GameLib.HEIGHT / 2),
            12.0,
            Vector2D.ofScalar(0.25)
        );
    }

    protected void read_input() {
        if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.move(delta, Direction.NORTH);
        if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.move(delta, Direction.SOUTH);
        if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.move(delta, Direction.WEST);
        if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.move(delta, Direction.EAST);

        if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) isRunning = false;
    }

    protected void update() {

        nearStarBackground.animate(delta);
        farStarBackground.animate(delta);
    }

    protected void render() {

        // Renderiza cada background
        farStarBackground.render();
        nearStarBackground.render();

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
	protected static void busyWait(long time){
		while(System.currentTimeMillis() < time) Thread.yield();
	}
}
