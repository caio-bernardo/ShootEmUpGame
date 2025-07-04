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


public class GameLevel {
    private long startTimeMillis = 0;
    private boolean hasStarted = false;
    private PriorityQueue<Map.Entry<Long, Enemy>> enemiesQueue;
    private PriorityQueue<Map.Entry<Long, Powerup>> powerUps;

    public GameLevel() {
        enemiesQueue = new PriorityQueue<>(
            (self, other) -> (int)(self.getKey() - other.getKey())
        );
    }

    public void addEnemyFromRawData(String enemy_type, long spawnTime, int life, int x, int y) {
        switch (enemy_type) {
            case "Common":
                addEnemy(new Enemy.Common(new Vector2D(x, y)), spawnTime);
                break;
            case "Flyer":
                addEnemy(new Enemy.Flyer(new Vector2D(x, y), x > GameLib.WIDTH /2 ), spawnTime);
                for (int i = 1; i < 10; i++) {
                    addEnemy(new Enemy.Flyer(new Vector2D(x, y), x > GameLib.WIDTH / 2), spawnTime + (i * 120));
                }
                break;
            case "FirstBoss":
                addEnemy(new Enemy.FirstBoss(new Vector2D(x, y), life), spawnTime);
                break;
            default:
                throw new IllegalStateException("Inimigo de Tipo não suportado: " + enemy_type);
        }
    }

    public void addEnemy(Enemy enemy, long spawnTime) {
        enemiesQueue.add(new AbstractMap.SimpleEntry<>(spawnTime, enemy) );
    }

    public List<Enemy> takeEnemiesLessThan(long timeMilis) {
        List<Enemy> enemies = new ArrayList<>();
        if (!hasStarted) {return enemies; }

        while (enemiesQueue.peek() != null && enemiesQueue.peek().getKey() <= timeMilis - startTimeMillis) {
            var enemy = enemiesQueue.poll();
            enemies.add(enemy.getValue());
        }
        return enemies;
    }

    /// Inicia a fase com o tempo atual
    public void start(long currentTime) {
        hasStarted = true;
        startTimeMillis = currentTime;
    }

    public boolean getHasStarted() {
        return hasStarted;
    }

    public boolean isEmpty() {
        return enemiesQueue.isEmpty();
    }
}
