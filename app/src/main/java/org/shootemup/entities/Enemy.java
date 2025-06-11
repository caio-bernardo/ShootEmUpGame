package org.shootemup.entities;

import java.awt.Color;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Renderable;

public class Enemy implements Renderable {
    private Vector2D position;
    private Vector2D velocity;
    private double radius;
    private Color color;
    private double rotationSpeed;
    private long nextShot;
    private long reckoil = 500;

    public Enemy(Color color, Vector2D pos, double radius, Vector2D velocity, double rotationSpeed) {
        position = pos;
        this.radius = radius;
        this.velocity = velocity;
        this.rotationSpeed = rotationSpeed;
        this.color = color;
    }

    /// Factory para um inimigo de tipo comum
    public static Enemy forCommon(Vector2D pos) {
        return new Enemy(Color.CYAN, pos, 9.0, new Vector2D(0.0, 0.20 + Math.random() * 0.15), 0.0);
    }

    // public static Enemy forFlyer(Vector2D pos) {
    //     // return new Enemy(pos, 12.0, new Vector2D(0.0, 0.15));
    // }

    public Vector2D getPosition() {
        return position;
    }

    public void move(long dt) {
        position = position.addVector(velocity.multiplyScalar(dt));
    }

    public Optional<Projectile.Ball> shot(long currentTime) {
        if (currentTime > nextShot) {
            // dispara um novo tiro a partir da ponta do nave na direção Norte
            var bullet = new Projectile.Ball(
                new Vector2D(position.getX(), position.getY()),
                new Vector2D(0.0, 0.45)
            );
            nextShot = (long)(currentTime + 200 + Math.random() * reckoil);
            return Optional.of(bullet);
        }
        return Optional.empty();
    }


	@Override
	public void render() {
    	GameLib.setColor(color);
        GameLib.drawCircle(position.getX(), position.getY(), radius);
	}

}
