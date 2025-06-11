package org.shootemup.components;

import java.awt.geom.Point2D;

// Representa um vetor em um espaço vetorial R^2
public class Vector2D extends Point2D.Double {

    public Vector2D(double x, double y) {
        super(x, y);
    }

    public static Vector2D ofScalar(double t) {
        return new Vector2D(t, t);
    }

    public Vector2D addScalar(double s) {
        return new Vector2D(x + s, y + s);
    }
}
