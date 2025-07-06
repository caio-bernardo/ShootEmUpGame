package org.shootemup.engine;

import java.util.PriorityQueue;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.IllegalStateException;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.entities.Enemy;
import org.shootemup.entities.Powerup;


/// Classe GameLevel, representa cada nível do jogo, marca o tempo de inicio da fase e spawna inimigos e powerups com base no tempo de spawn
public class GameLevel {
    private long startTimeMillis = 0;
    private boolean hasStarted = false;
    // Lista de Prioridade com base no tempo de spawn das entidades
    private PriorityQueue<Map.Entry<Long, Enemy>> enemiesQueue;
    private PriorityQueue<Map.Entry<Long, Powerup>> powerUpsQueue;

    public GameLevel() {
        enemiesQueue = new PriorityQueue<>(
            (self, other) -> (int)(self.getKey() - other.getKey())
        );
        powerUpsQueue = new PriorityQueue<>(
            (self, other) -> (int)(self.getKey() - other.getKey())
        );
    }

    public void addEnemyFromRawData(String enemyType, long spawnTime, int life, int x, int y) throws IllegalStateException {
        switch (enemyType) {
            case "Common":
                addEnemy(new Enemy.Common(new Vector2D(x, y)), spawnTime);
                break;
            case "Flyer":
                addEnemy(new Enemy.Flyer(new Vector2D(x, y), x > GameLib.WIDTH /2 ), spawnTime);
                for (int i = 1; i < 10; i++) {
                    addEnemy(new Enemy.Flyer(new Vector2D(x, y), x > GameLib.WIDTH / 2), spawnTime + (i * 120));
                }
                break;
            case "ShadowPlayer":
                addEnemy(new Enemy.ShadowPlayer(new Vector2D(x, y), life, spawnTime + 5000), spawnTime);
                break;
            case "ZaWarudo":
                addEnemy(new Enemy.ZaWarudo(new Vector2D(x, y), life, spawnTime + 5000), spawnTime);
                break;
            default:
                throw new IllegalStateException("Inimigo de Tipo não suportado: " + enemyType);
        }
    }

    /// Adiciona inimigos com um tempo de spawn relativo ao inicio da fase: 2000 -> inimigo spawna 2 segundos após o inicio da fase
    public void addEnemy(Enemy enemy, long spawnTime) {
        enemiesQueue.add(new AbstractMap.SimpleEntry<>(spawnTime, enemy) );
    }

    /// Adiciona powerups com um tempo de spawn relativo ao inicio da fase: 2000 -> powerup spawna 2 segundos após o inicio da fase
    public void addPowerUp(Powerup powerup, long spawnTime) {
        powerUpsQueue.add(new AbstractMap.SimpleEntry<>(spawnTime, powerup));
    }

    public void addPowerUpFromRawParts(String powerupType, long spawnTime, int x, int y) throws IllegalStateException {
        switch (powerupType) {
            case "LaserMode":
                addPowerUp(new Powerup.LaserMode(new Vector2D(x, y)), spawnTime);
                break;
            case "ZaWarudo":
                addPowerUp(new Powerup.ZaWarudo(new Vector2D(x, y)), spawnTime);
                break;
            default:
                throw new IllegalStateException("Tipo de powerup não suportado: " + powerupType);
        }
    }

    /// Retorna uma lista de inimigos com spawntime menor que o tempo atual
    public List<Enemy> takeEnemiesLessThan(long timeMilis) {
        List<Enemy> enemies = new ArrayList<>();
        if (!getHasStarted()) {return enemies; }

        while (enemiesQueue.peek() != null && enemiesQueue.peek().getKey() <= timeMilis - startTimeMillis) {
            var enemy = enemiesQueue.poll();
            enemies.add(enemy.getValue());
        }
        return enemies;
    }

    public List<Powerup> takePowerUpsLessThan(long timeMillis) {
        List<Powerup> powerups = new ArrayList<>();
        if (!getHasStarted()) {return powerups;}
        while (powerUpsQueue.peek() != null && powerUpsQueue.peek().getKey() <= timeMillis - startTimeMillis) {
            var powerup = powerUpsQueue.poll();
            powerups.add(powerup.getValue());
        }
        return powerups;
    }

    /// Inicia a fase com o tempo atual
    public void start(long currentTime) {
        hasStarted = true;
        startTimeMillis = currentTime;
    }

    // Getter para saber se a fase começou ou nao
    public boolean getHasStarted() {
        return hasStarted;
    }

    public boolean isComplete() {
        return enemiesQueue.isEmpty() && powerUpsQueue.isEmpty();
    }

}
