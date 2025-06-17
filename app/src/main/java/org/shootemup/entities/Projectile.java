package org.shootemup.entities;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;

public abstract class Projectile extends Entity {

    protected Projectile(Vector2D position, Vector2D velocity, Color color) {
        super(position, velocity, 1.0, color);
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

    }

    /// Representa um projétil em formato esférico
    public static class Ball extends Projectile {

        public Ball(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.RED);
            radius = 2.0;
        }

		@Override
		public void render() {
			GameLib.setColor(color);
			GameLib.drawCircle(position.getX(), position.getY(), radius);
		}
    }

}
