package org.shootemup.entities;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;

/// Classe abstrata que representa um projétil
public abstract class Projectile extends Entity {

    protected Projectile(Vector2D position, Vector2D velocity, Color color) {
        // Todos os projéteis tem raio 1.0
        super(position, velocity, 1.0, color);
    }

    /// Representa um projétil em formato de bala (um conjunto de 3 linhas)
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

    /// Representa um projétil em formato de laser
    public static class Laser extends Projectile {

        public Laser(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.RED);
        }

        @Override
        public void render() {
            GameLib.setColor(color);
            GameLib.drawLine(position.getX(), position.getY() - 20, position.getX() - 1, position.getY() + 20);
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

    public static class timeBall extends Projectile {

        public timeBall(Vector2D position, Vector2D velocity) {
            super(position, velocity, Color.LIGHT_GRAY);
            radius = 2.0;
        }

		@Override
		public void render() {
			GameLib.setColor(color);
			GameLib.drawCircle(position.getX(), position.getY(), radius);
		}
    }

}
