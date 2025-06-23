package org.shootemup.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.components.Weapon;
import org.shootemup.utils.Shooter;


public abstract class Enemy extends Entity implements Shooter {
    protected double rotationSpeed;
    protected Weapon<? extends Projectile> gun;

    protected Enemy(Color color, Vector2D pos, double radius, Vector2D velocity, double rotationSpeed) {
           super(pos, velocity, radius, color);
           this.rotationSpeed = rotationSpeed;
    }

    @Override
	public void render() {
            GameLib.setColor(color);
            GameLib.drawCircle(position.getX(), position.getY(), radius);
	}

    public static class Common extends Enemy {

        public Common(Vector2D pos) {
           super(Color.CYAN, pos, 9.0, new Vector2D(0.0, 0.20 + Math.random() * 0.15), 0.0);
           gun = Weapon.Cannon();
        }

		@Override
		public Optional<Projectile> shot(long currentTime) {
		    var bullet = gun.fire(currentTime, position, new Vector2D(0.0, 0.45));
			if (bullet.isPresent()) {
			    gun.setNextShot((long)(currentTime + 200 + Math.random() * gun.getReckoilMilis()));
			}
            return bullet.map(b-> (Projectile)b);
		}
    }

    /// Inimigo Flyers (voadores), andam em formação e giram
    public static class Flyer extends Enemy {
        private double angle;
        private boolean canShoot = false;
        private long nextShootTime = 0;

        public Flyer(Vector2D pos, boolean spawnOnRight) {
            super(Color.MAGENTA, pos, 12.0, new Vector2D(0.42, 0.42), 0.0);
            this.angle = (3 * Math.PI) / 2;
            this.rotationSpeed = 0.0;
            gun = Weapon.TripleCannon();
        }

        @Override
        public void render() {
            GameLib.setColor(color);
            GameLib.drawDiamond(position.getX(), position.getY(), radius);
        }

        @Override
        public void move(long dt) {
            double prevY = position.getY();

            position.x += velocity.getX() * Math.cos(angle) * dt;
            position.y -= velocity.getY() * Math.sin(angle) * dt;

            angle += rotationSpeed * dt;

            double threshold = GameLib.HEIGHT * 0.30;

            if (prevY < threshold && position.getY() >= threshold) {
                if (position.getX() < GameLib.WIDTH / 2) {
                    rotationSpeed = 0.003;
                } else {
                    rotationSpeed = -0.003;
                }
            }

            if (rotationSpeed > 0 && Math.abs(angle - 3 * Math.PI) < 0.05) {
                rotationSpeed = 0.0;
                angle = 3 * Math.PI;
                canShoot = true;
            } else if (rotationSpeed < 0 && Math.abs(angle) < 0.05) {
                rotationSpeed = 0.0;
                angle = 0.0;
                canShoot = true;
            }
        }

        @Override
        public Optional<Projectile> shot(long currentTime) {
            return Optional.empty();
        }

        // Dispara multiplos tiros
        public List<Projectile> shotMultiple(long currentTime) {
            List<Projectile> projectiles = new ArrayList<>();

            if (!canShoot || currentTime < nextShootTime) {
                return projectiles;
            }

            canShoot = false;
            nextShootTime = currentTime + 1000;

            double[] angles = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };

            for (double shotAngle : angles) {
                double finalAngle = shotAngle + Math.random() * Math.PI/6 - Math.PI/12;
                double vx = Math.cos(finalAngle) * 0.30;
                double vy = Math.sin(finalAngle) * 0.30;

                Vector2D velocity = new Vector2D(vx, vy);

                Projectile ball = new Projectile.Ball(position, velocity);
                projectiles.add(ball);
            }

            return projectiles;
        }
    }
}
