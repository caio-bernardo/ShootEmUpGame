package org.shootemup.engine;

import org.shootemup.components.Background;
import org.shootemup.components.Explosion;
import org.shootemup.entities.Enemy;
import org.shootemup.entities.Player;
import org.shootemup.entities.Powerup;
import org.shootemup.entities.Projectile;

import java.util.List;
import java.util.Queue;

public class GameData {
    boolean isRunning = false;
    GameState state = GameState.MENU;

    Background farStarBackground;
    Background nearStarBackground;

    Player player;
    boolean secondBossZaWarudo = false;
    List<Projectile> projectiles;
    List<Enemy> enemies;
    List<Explosion> explosions;
    List<Powerup> powerups;

    Queue<GameLevel> levels; // Fila de fases

    public GameData(List<Enemy> enemies, List<Explosion> explosions, Background farStarBackground, boolean isRunning, Queue<GameLevel> levels, Background nearStarBackground, Player player, List<Powerup> powerups, List<Projectile> projectiles, boolean secondBossZaWarudo) {
        this.enemies = enemies;
        this.explosions = explosions;
        this.farStarBackground = farStarBackground;
        this.isRunning = isRunning;
        this.levels = levels;
        this.nearStarBackground = nearStarBackground;
        this.player = player;
        this.powerups = powerups;
        this.projectiles = projectiles;
        this.secondBossZaWarudo = secondBossZaWarudo;
    }
}
