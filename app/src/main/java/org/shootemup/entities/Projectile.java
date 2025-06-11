package org.shootemup.entities;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Renderable;

public abstract class Projectile implements Renderable {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Color color;

    protected Projectile(Vector2D position, Vector2D velocity, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
    }

    public abstract void move(long dt);

    public Vector2D getPosition() {
        return position;
    }

    /// Representa um projétil em formato de bala
    public static class Bullet extends Projectile {

        public Bullet(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.GREEN);
        }

        @Override
        public void move(long dt) {
            position = position.addVector(velocity.multiplyScalar(dt));
        }


  		@Override
  		public void render() {
            GameLib.setColor(color);
            GameLib.drawLine(position.getX(), position.getY() - 5, position.getX(), position.getY() + 5);
            GameLib.drawLine(position.getX() - 1, position.getY() - 3, position.getX() - 1, position.getY() + 3);
            GameLib.drawLine(position.getX() + 1, position.getY() - 3, position.getX() + 1, position.getY() + 3);
  		}

    }

    /// Representa um projétil em formato esférico
    public static class Ball extends Projectile {
        private double radius = 2.0;

        public Ball(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.RED);
        }

		@Override
		public void render() {
			GameLib.setColor(color);
			GameLib.drawCircle(position.getX(), position.getY(), radius);
		}

		@Override
		public void move(long dt) {
		    position = position.addVector(velocity.multiplyScalar(dt));
		}

    }

}
