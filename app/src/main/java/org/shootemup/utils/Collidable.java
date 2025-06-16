package org.shootemup.utils;

import org.shootemup.components.Vector2D;

public interface Collidable {
    public boolean intersects(Collidable other);
    public void solve();
    public Vector2D getPosition();
    public double getRadius();
}
