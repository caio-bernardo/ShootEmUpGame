package org.shootemup.components;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.utils.Renderable;



public class Particle2D implements Renderable {
    private Vector2D position;

	private Color color;
    private Vector2D size;

    public Particle2D(Vector2D position, Vector2D size, Color color) {
        this.position = position;
        this.color = color;
        this.size = size;
    }

	@Override
	public void render() {
        GameLib.setColor(color);
        GameLib.fillRect(position.getX(), position.getY(), size.getX(), size.getY());
	}

    public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}
}
