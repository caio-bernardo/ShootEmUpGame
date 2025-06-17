package org.shootemup.entities;

import java.awt.Color;
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
}
