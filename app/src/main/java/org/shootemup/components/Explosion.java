package org.shootemup.components;

import org.shootemup.engine.GameLib;

public class Explosion {
    private final Vector2D position;
    private final long startTime;
    private long explosionDelta = 0;
    private final long duration;

    public Explosion(Vector2D position, long starTime, long durationMilis){
        this.position = position;
        this.startTime = starTime;
        this.duration = durationMilis;
    }

    public void update(long currentTime) {
        explosionDelta = currentTime - startTime;
    }

    public boolean isFinished() {
        return explosionDelta > duration;
    }

	public void render() {
	    double alpha = (double)explosionDelta / (double) duration;
		GameLib.drawExplosion(position.getX(), position.getY(), alpha);
	}
}
