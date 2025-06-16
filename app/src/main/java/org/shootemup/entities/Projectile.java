package org.shootemup.entities;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Collidable;

public abstract class Projectile extends Entity {

    protected Projectile(Vector2D position, Vector2D velocity, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
    }

    public Vector2D getPosition() {
        return position;
    }

    /// Representa um projétil em formato de bala
    public static class Bullet extends Projectile {

        public Bullet(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.GREEN);
        }

  		@Override
  		public void render() {
            GameLib.setColor(color);
            GameLib.drawLine(position.getX(), position.getY() - 5, position.getX(), position.getY() + 5);
            GameLib.drawLine(position.getX() - 1, position.getY() - 3, position.getX() - 1, position.getY() + 3);
            GameLib.drawLine(position.getX() + 1, position.getY() - 3, position.getX() + 1, position.getY() + 3);
  		}

		@Override
		public boolean intersects(Collidable other) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'intersects'");
		}

		@Override
		public void solve() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'solve'");
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
		public boolean intersects(Collidable other) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'intersects'");
		}

		@Override
		public void solve() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'solve'");
		}

    }

}
