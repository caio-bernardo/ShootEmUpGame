package org.shootemup.entities;

import java.awt.Color;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.components.Weapon;
import org.shootemup.utils.Collidable;
import org.shootemup.utils.Direction;
import org.shootemup.utils.Shooter;
import org.shootemup.entities.Powerup;


public class Player extends Entity implements Shooter {
    private boolean isAlive = true;
    private long reviveAt = 0;
    private Weapon<? extends Projectile> gun;
    private Weapon<? extends Projectile> laserGun;
    private long zaWarudoTimer = 0;
    private long laserModeTimer = 0;
    private int zaWarudoRadius = 0;

    public Player(Vector2D pos, double radius, Vector2D velocity) {
        super(pos, velocity, radius, Color.BLUE);
        gun = Weapon.Pistol();
        laserGun = Weapon.LaserPistol();
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

    public void revive(long now) {
        if (now > reviveAt) {
            isAlive = true;
        }
    }

    public void pickPowerUp(Powerup powerup) {
        if (powerup instanceof Powerup.ZaWarudo) {
            zaWarudoTimer = Powerup.ZaWarudo.duration;
        } else if (powerup instanceof Powerup.LaserMode) {
            laserModeTimer = Powerup.LaserMode.duration;
        }
    }

    public boolean isZaWarudoActive()
    {
        if(zaWarudoTimer > 0){
            if(zaWarudoTimer % 2 == 1){
                GameLib.setColor(Color.WHITE);
            } else {
                GameLib.setColor(Color.YELLOW);
            }
            zaWarudoRadius += 3;
            GameLib.drawCircle(position.x, position.y, zaWarudoRadius);
        } else {
            zaWarudoRadius = 0;
        }
        return zaWarudoTimer > 0;
    }

    public boolean isLaserModeActive() {
        boolean result = laserModeTimer > 0;
        if(result) Powerup.LaserMode.renderEffect(position, laserModeTimer);
        return result;
    }

    public void updatePowerUpTimers(long dt) {
        zaWarudoTimer = zaWarudoTimer - dt < 0 ? 0 : zaWarudoTimer - dt;
        laserModeTimer = laserModeTimer - dt < 0 ? 0 : laserModeTimer - dt;
    }

    @Override
    public Optional<Projectile> shot(long currentTime) {
        if (!isAlive) return Optional.empty();
        return gun.fire(
            currentTime,
            new Vector2D(position.x, position.y - 2 * radius),
            new Vector2D(0.0, -1.0)
        ).map(b -> (Projectile)b);
    }

    public Optional<Projectile> laserShot(long currentTime) {
        if (!isAlive) return Optional.empty();
        // angulando os tiros com o passar do tempo
        //double vx = ((Math.cos(currentTime / 25) * Math.PI / 10) - Math.PI / 20);
        return laserGun.fire(
            currentTime,
            new Vector2D(position.x, position.y - 2 * radius),
            new Vector2D(0.0, -2.0)
        ).map(b -> (Projectile)b);        
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

}
