package org.shootemup.engine;

import java.awt.Color;
import java.util.ArrayList;

import org.shootemup.components.Background;
import org.shootemup.components.Vector2D;
import org.shootemup.config.Config;
import org.shootemup.entities.Player;

/// Classe que representa o sistema de jogo
public class Game {
    private GameData data;

    public Game() {
        // Inicializa o jogo com os arquivos de configuração
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
    }

    // Executa o jogo
	public void run() {
   	    // inicializa a biblioteca gráfica
       	GameLib.initGraphics("Shoot'em Up!");
        //GameLib.initGraphics_SAFE_MODE("Shoot'em Up!);  // chame esta versão do método caso nada seja desenhado na janela do jogo.

        long delta;
        long currentTime = System.currentTimeMillis();;
        data.isRunning = true;

        while (data.isRunning) {
            delta = System.currentTimeMillis() - currentTime;
            currentTime = System.currentTimeMillis();

            data.state.update(data, delta, currentTime);
            data.state.read_input(data, delta, currentTime);
            data.state.render(data, delta, currentTime);

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
