package org.shootemup.components;

import org.shootemup.GameLib;

public class Explosion {
    private Vector2D position;
    private long startTime;
    private long explosionDelta = 0;
    private long duration;

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
