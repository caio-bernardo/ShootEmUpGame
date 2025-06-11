package org.shootemup.entities;

import java.awt.Color;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Direction;
import org.shootemup.utils.Renderable;

public class Player implements Renderable {
    private Vector2D position;
    private double radius;
    private Vector2D velocity;
    private Color color = Color.BLUE;
    private long nextShot = 0;

    private long reckoil = 100;

    private double explosionStart = 0.0;
    private double explosionEnd = 0.0;

    public Player(Vector2D pos, double radius, Vector2D velocity) {
        position = pos;
        this.radius = radius;
        this.velocity = velocity;
    }


    public void move(long dt, Direction dir) {
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

    public Optional<Projectile.Bullet> shot(long currentTime) {
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


    public void update() {
        // Collision check and solver
    }

	@Override
	public void render() {
    	GameLib.setColor(color);
		GameLib.drawPlayer(position.getX(), position.getY(), radius);
	}


}
