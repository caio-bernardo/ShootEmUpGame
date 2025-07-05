package org.shootemup.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.components.Weapon;
import org.shootemup.components.LifeBar;
import org.shootemup.utils.Shooter;

// Classe Abstrata que engloba os diferentes inimigos do jogo
public abstract class Enemy extends Entity implements Shooter {
    protected double rotationSpeed;
    protected Weapon<? extends Projectile> gun;
    protected int life;

    protected Enemy(Color color, Vector2D pos, double radius, Vector2D velocity, double rotationSpeed, int life) {
           super(pos, velocity, radius, color);
           this.rotationSpeed = rotationSpeed;
           this.life = life;
    }

    public void setLife(int newLife){
        this.life = newLife;
    }

    public int getLife(){
        return this.life;
    }

    @Override
	public void render() {
            GameLib.setColor(color);
            GameLib.drawCircle(position.getX(), position.getY(), radius);
	}

	// Inimigo tipo comum: se movimenta para frente e atira
    public static class Common extends Enemy {

        public Common(Vector2D pos) {
           super(Color.CYAN, pos, 9.0, new Vector2D(0.0, 0.20 + Math.random() * 0.15), 0.0, 1);
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

    /// Inimigo tipo Flyers (voadores), andam em formação, giram e disparam multiplos tiros
    public static class Flyer extends Enemy {
        private double angle;
        private boolean canShoot = false;
        private long nextShootTime = 0;

        public Flyer(Vector2D pos, boolean spawnOnRight) {
            super(Color.MAGENTA, pos, 12.0, new Vector2D(0.42, 0.42), 1.0, 1);
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

    /// Inimigo ShadowPlayer: versão maior e maligna do player, se movimenta em circulos
    public static class ShadowPlayer extends Enemy{

        private double angle;
        protected LifeBar bossLife;


        public ShadowPlayer(Vector2D pos, int life) {
           super(Color.RED, pos, 30.0, new Vector2D(0.05, 0.05), 1.0, life);
           gun = Weapon.Cannon();
           this.angle = (3 * Math.PI) / 2;
           this.rotationSpeed = 0.0;
           bossLife = new LifeBar(this.color, life);
        }


        @Override
		public Optional<Projectile> shot(long currentTime) {
		    var bullet = gun.fire(currentTime, position, new Vector2D(0.0, 0.45));
			if (bullet.isPresent()) {
			    gun.setNextShot((long)(currentTime + 15 + Math.random() * gun.getReckoilMilis()));
			}
                  return bullet.map(b-> (Projectile)b);
		}

        @Override
        public void move(long dt) {
            double prevY = position.getY();

            position.x += 7 * (velocity.getX() * Math.cos(angle) * dt);
            position.y -= 5 * (velocity.getY() * Math.sin(angle) * dt);

            angle += rotationSpeed * dt;

            double threshold = GameLib.HEIGHT * 0.30;

            if(prevY < threshold && position.getY() >= threshold){

                if(position.getX() < GameLib.WIDTH / 2){
                    rotationSpeed = 0.004;
                }
                if(position.getX() >= GameLib.WIDTH / 2){
                    rotationSpeed = -0.004;
                }
            }
        }

		@Override
        public void render() {
            GameLib.setColor(color);
            GameLib.drawLine(position.getX() - radius, position.getY() - radius, position.getX(), position.getY() + radius);
            GameLib.drawLine(position.getX() + radius, position.getY() - radius, position.getX(), position.getY() + radius);
            GameLib.drawLine(position.getX() - radius, position.getY() - radius, position.getX(), position.getY() - radius * 0.5);
            GameLib.drawLine(position.getX() + radius, position.getY() - radius, position.getX(), position.getY() - radius * 0.5);
            bossLife.setFinalLife(this.getLife());
            bossLife.render();
        }
    }

    public static class ZaWarudo extends Enemy{

        private LifeBar bossLife;
        private boolean shift = true;

        private long zaWarudoTimer = 0;
        private boolean canZawarudo = true;
        private long nextBossZawarudo;

        double thresholdUpY = GameLib.HEIGHT * 0.20;
        double thresholdDownY = GameLib.HEIGHT * 0.85;
        double thresholdRightX = GameLib.WIDTH * 0.85;
        double thresholdLeftX = GameLib.WIDTH * 0.15;

        public ZaWarudo(Vector2D pos, int life, long nextBossZawarudo) {
           super(Color.YELLOW, pos, 30.0, new Vector2D(0.05, 0.05), 1.0, life);
           gun = Weapon.zapCannon();
           bossLife = new LifeBar(this.color, life);
           this.nextBossZawarudo = nextBossZawarudo;
        }

        @Override
        public Optional<Projectile> shot(long currentTime) {
            double senseOfShot = position.getY() == thresholdDownY ? -1.0 : 1.0;
            return gun.fire(
                currentTime,
                new Vector2D(position.x, position.y),
                new Vector2D(0.0, senseOfShot)
            ).map(b -> (Projectile)b);
        }

        @Override
        public void move(long dt) {
            if(position.getY() < thresholdUpY || position.getX() == thresholdRightX && position.getY() < thresholdDownY){
                position.y += 1;
            }
            if(position.getY() == thresholdUpY && position.getX() < thresholdRightX){
                position.x += 1;
            }
            if(position.getY() == thresholdDownY && position.getX() > thresholdLeftX){
                position.x -= 1;
            }
            if(position.getX() == thresholdLeftX){
                position.y -= 1;
            }
        }

        public void activateBossZaWarudo(long currentTime){
            if(currentTime > nextBossZawarudo && canZawarudo){
                zaWarudoTimer = Powerup.ZaWarudo.duration;
                canZawarudo = false;
            }
        }

        public boolean isZaWarudoActive(long currentTime){
            if(zaWarudoTimer > 0){
                Powerup.ZaWarudo.renderEffect(position, zaWarudoTimer, Color.YELLOW);
            } else {
                if(!canZawarudo){
                    nextBossZawarudo = currentTime + 10000;
                    canZawarudo = true;
                }
            }
            return zaWarudoTimer > 0;
        }

        public void updateZaWarudoTimer(long dt) {
            zaWarudoTimer = zaWarudoTimer - dt < 0 ? 0 : zaWarudoTimer - dt;
        }

		@Override
        public void render() {
            GameLib.setColor(color);
            if(shift){
                GameLib.drawDiamond(position.getX(), position.getY(), 30);
                shift = false;
            } else{
                GameLib.drawCircle(position.getX(), position.getY(), 30);
                shift = true;
            }
            bossLife.setFinalLife(this.getLife());
            bossLife.render();
        }
    }
}
