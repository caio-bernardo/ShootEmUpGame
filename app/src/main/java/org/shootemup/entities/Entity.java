package org.shootemup.entities;

import org.shootemup.components.Vector2D;
import org.shootemup.utils.Renderable;

/// Menor unidade básica interavel no jogo
public abstract class Entity implements Renderable {
    protected Vector2D position;
    protected Vector2D velocity;

    public abstract void move(long dt);
}
