package org.shootemup.entities;

import java.awt.Color;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.components.Weapon;
import org.shootemup.components.LifeBar;
import org.shootemup.utils.Collidable;
import org.shootemup.utils.Direction;
import org.shootemup.utils.Shooter;


public class Player extends Entity implements Shooter {
    private int hp; // Pontos de vida
    private LifeBar playerLife;
    private long damageCoolDown = 0;
    private boolean isAlive = true;
    private Weapon<? extends Projectile> pistolGun;
    private Weapon<? extends Projectile> laserGun;
    private long zaWarudoTimer = 0;
    private long laserModeTimer = 0;

    public Player(int hp, Vector2D pos, double radius, Vector2D velocity) {
        super(pos, velocity, radius, Color.BLUE);
        this.hp = hp;
        playerLife = new LifeBar(this.color, hp, 655.0, 650.0);
        pistolGun = Weapon.Pistol();
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
                // Sem movimentos
                break;
        }
    }


    /// Função que atualiza a vida do player se ele tiver recebido dano
    public void damage(long now) {
        if (now < damageCoolDown) return;
        if (hp > 0) {
            hp--;
        }
        damageCoolDown = now + 100;
    }

    public int getHP() {
        return hp;
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
        boolean result = zaWarudoTimer > 0;
        if(result) Powerup.ZaWarudo.renderEffect(position, zaWarudoTimer, Color.WHITE);
        return result;
    }

    public boolean isLaserModeActive() {
        boolean result = laserModeTimer > 0;
        if(result) Powerup.LaserMode.renderEffect(position, laserModeTimer, true);
        return result;
    }

    public void updatePowerUpTimers(long dt) {
        zaWarudoTimer = zaWarudoTimer - dt < 0 ? 0 : zaWarudoTimer - dt;
        laserModeTimer = laserModeTimer - dt < 0 ? 0 : laserModeTimer - dt;
    }

    @Override
    public Optional<Projectile> shot(long currentTime) {
        if (isLaserModeActive()) {
            return laserShot(currentTime);
        } else {
            return pistolShot(currentTime);
        }
    }

    /// Tiro com pistola
    public Optional<Projectile> pistolShot(long currentTime) {
            if (!isAlive) return Optional.empty();
            return pistolGun.fire(
                currentTime,
                new Vector2D(position.x, position.y - 2 * radius),
                new Vector2D(0.0, -1.0)
            ).map(b -> (Projectile)b);
        }

    /// Tiro com laser
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
		playerLife.setFinalLife(this.getHP());
        playerLife.render();
	}


	@Override
	public boolean intersects(Collidable other) {
	    if (!isAlive) return false;
		double dist = position.distance(other.getPosition());
		return dist < (radius + other.getRadius()) * 0.8;
	}

}
