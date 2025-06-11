package org.shootemup.components;

import java.awt.geom.Point2D;

// Representa um vetor em um espaço vetorial R^2
public class Vector2D extends Point2D.Double {

    public Vector2D(double x, double y) {
        super(x, y);
    }

    /**
    * Cria um vetor (t, t)
    * @param t Valor escalar
    * @return instancia do vetor
    */
    public static Vector2D ofScalar(double t) {
        return new Vector2D(t, t);
    }

    /**
     * Adiciona um valor escalar aos componentes do vetor
     * @param s Valor escalar
     * @return Vetor resultante da expressão
     */
    public Vector2D addScalar(double s) {
        return new Vector2D(x + s, y + s);
    }

    public Vector2D addVector(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D multiplyScalar(double s) {
        return new Vector2D(x * s, y * s);
    }
}
