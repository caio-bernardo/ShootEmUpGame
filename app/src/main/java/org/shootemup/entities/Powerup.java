package org.shootemup.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.shootemup.GameLib;
import org.shootemup.components.Vector2D;
import org.shootemup.components.Weapon;
import org.shootemup.utils.Shooter;


public abstract class Powerup extends Entity {
    protected Powerup(Color color, Vector2D pos, double radius, Vector2D velocity) {
           super(pos, velocity, radius, color);
    }

    public static class ZaWarudo extends Powerup {
        public final static long duration = 4000;

        public ZaWarudo(Vector2D pos) {
           super(Color.YELLOW, pos, 8.0, new Vector2D(0.0, 0.08 + Math.random() * 0.07));
        }
    }

    public static class LaserMode extends Powerup {
        public final static long duration = 8000;

        public LaserMode(Vector2D pos) {
            super(Color.ORANGE, pos, 8.0, new Vector2D(0.0, 0.08 + Math.random() * 0.07));
        }
    }

    @Override
    public void render() {
        GameLib.setColor(color);
        GameLib.drawDiamond(position.getX(), position.getY(), radius);
    }
}
