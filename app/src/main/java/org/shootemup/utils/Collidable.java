package org.shootemup.utils;

import org.shootemup.components.Vector2D;

/// Interface para objetos com uma hitbox
public interface Collidable {
    public boolean intersects(Collidable other);
    public Vector2D getPosition();
    public double getRadius();
}
