package org.shootemup.entities;

import org.shootemup.components.Vector2D;

/// Menor unidade básica interavel no jogo
public abstract class Entity {
    protected Vector2D position;
    protected Vector2D velocity;

    public abstract void update();
    public abstract void move(long dt);
    public abstract void render();
}
