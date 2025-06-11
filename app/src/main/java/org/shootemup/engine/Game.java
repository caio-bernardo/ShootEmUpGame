package org.shootemup.engine;

import org.shootemup.GameLib;

/// Classe que representa o sistema de jogo
// Runnable indica que a classe é executavel e nunca retorna
public class Game implements Runnable {

    private long currentTime = 0;
    private long delta;

    private boolean isRunning = false;

    public Game() {

    }

    protected void read_input() {

    }

    protected void update() {

    }

    protected void render() {


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
