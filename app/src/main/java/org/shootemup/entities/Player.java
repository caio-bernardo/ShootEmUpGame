package org.shootemup.entities;

import java.awt.Color;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Collidable;
import org.shootemup.utils.Direction;


public class Player extends Entity {
    private boolean isAlive = true;
    private long nextShot = 0;
    private long reckoil = 100;
    private long reviveAt = 0;

    public Player(Vector2D pos, double radius, Vector2D velocity) {
        position = pos;
        this.radius = radius;
        this.velocity = velocity;
        color = Color.BLUE;
    }



    public void move(long dt, Direction dir) {
        if (!isAlive) return;
        switch (dir) {
            case NORTH: // cima
                position.y = Math.max(position.getY() - dt * velocity.getY(), 25.0);
                break;
            case SOUTH: // baixo
                position.y = Math.min(position.getY() + dt * velocity.getY(), GameLib.HEIGHT);
                break;
            case EAST: // direita
                position.x = Math.min(position.getX() + dt * velocity.getY(), GameLib.WIDTH);
                break;
            case WEST: // esquerda
                position.x = Math.max(position.getX() - dt * velocity.getX(), 0.0);
                break;
            default:
                // No movement
                break;
        }
    }

    public void dieForDuration(long now, long durationMilis) {
        isAlive = false;
        reviveAt = now + durationMilis;
    }

    public void die() {
        isAlive = false;
    }

    public void revive(long now) {
        if (now > reviveAt) {
            isAlive = true;
        }
    }


    public Optional<Projectile.Bullet> shot(long currentTime) {
        if (!isAlive) return Optional.empty();
        if (currentTime > nextShot) {
            // dispara um novo tiro a partir da ponta do nave na direção Norte
            var bullet = new Projectile.Bullet(
                new Vector2D(position.getX(), position.getY() - 2 * radius),
                new Vector2D(0.0, -1.0)
            );
            nextShot = currentTime + reckoil;
            return Optional.of(bullet);
        }
        return Optional.empty();
    }

	@Override
	public void render() {
	    if (!isAlive) return;
    	GameLib.setColor(color);
		GameLib.drawPlayer(position.getX(), position.getY(), radius);
	}


	@Override
	public boolean intersects(Collidable other) {
	    if (!isAlive) return false;
		double dist = position.distance(other.getPosition());
		return dist < (radius + other.getRadius()) * 0.8;
	}


	@Override
	public void solve() {
	    // TODO: find if collision is against enemy/projectile/powerup
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'solve'");
	}
}
