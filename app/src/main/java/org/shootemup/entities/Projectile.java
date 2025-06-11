package org.shootemup.entities;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Renderable;

public abstract class Projectile implements Renderable {
    private Vector2D position;
    private Vector2D velocity;
    private Color color;

    protected Projectile(Vector2D position, Vector2D velocity, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
    }

    public abstract void move(long dt);

    public Vector2D getPosition() {
        return position;
    }

    public static class Bullet extends Projectile {

        public Bullet(Vector2D position) {
            super(position, new Vector2D(0.0, -1.0), Color.GREEN);
        }

        @Override
        public void move(long dt) {
            super.position = super.position.addVector(super.velocity.multiplyScalar(dt));
        }


  		@Override
  		public void render() {
            GameLib.setColor(super.color);
            GameLib.drawLine(super.position.getX(), super.position.getY() - 5, super.position.getX(), super.position.getY() + 5);
            GameLib.drawLine(super.position.getX() - 1, super.position.getY() - 3, super.position.getX() - 1, super.position.getY() + 3);
            GameLib.drawLine(super.position.getX() + 1, super.position.getY() - 3, super.position.getX() + 1, super.position.getY() + 3);
  		}

    }

    public static class Ball extends Projectile {
        private double radius = 1.0;

        public Ball(Vector2D position) {
            super(position, new Vector2D(0.0, 1.0), Color.ORANGE);
        }

		@Override
		public void render() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'render'");
		}

		@Override
		public void move(long dt) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'move'");
		}

    }

}
