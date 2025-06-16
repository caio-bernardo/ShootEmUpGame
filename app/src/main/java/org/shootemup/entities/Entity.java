package org.shootemup.entities;

import java.awt.Color;
import org.shootemup.components.Vector2D;
import org.shootemup.utils.Collidable;
import org.shootemup.utils.Movable;
import org.shootemup.utils.Renderable;

/// Menor unidade básica interagivel no jogo
public abstract class Entity implements Renderable, Movable, Collidable {
    protected Vector2D position;
    protected Vector2D velocity;
    protected double radius;
    protected Color color;

    @Override
    public void move(long dt) {
        position = position.addVector(velocity.multiplyScalar(dt));
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Color getColor() {
        return color;
    }
}
