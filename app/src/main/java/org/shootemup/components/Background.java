package org.shootemup.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.shootemup.GameLib;
import org.shootemup.utils.Renderable;

/**
 * Representa o fundo do jogo, composto por partículas.
 * Implementa a interface Renderable para ser renderizado na tela.
 */
public class Background implements Renderable {
    List<Particle2D> particles;
    private double speed;

    private Background() {}

    /**
     * Cria um fundo de estrelas
     * @param color Cor das estrelas
     * @param qnt Quantidade de estrelas
     * @param size Tamanho das estrelas
     * @param speed Velocidade de movimento das estrelas
     * @return Uma instância de Background
     */
    public static Background forStars(Color color, int qnt, int size, double speed) {
        var bg = new Background();
        bg.particles = new ArrayList<>(qnt);
        for (int i = 0; i < qnt; i++) {
            var pos = new Vector2D(Math.random() * GameLib.WIDTH, Math.random() * GameLib.HEIGHT);
            bg.particles.add(new Particle2D(pos, new Vector2D(size, size), color));
        }
        bg.speed = speed;

        return bg;
    }

    /* Anima o fundo, atualizando a posição das partículas em função de dt
    * @param dt Diferença de tempo entre um frame e o outro
    */
    public void animate(long dt) {
        for (Particle2D p: particles) {
            Vector2D newpos = p.getPosition();
            newpos.y = (newpos.y + speed * dt) % GameLib.HEIGHT;
            p.setPosition(newpos);
        }
    }

    @Override
    public void render() {
        particles.stream().forEach((p)-> p.render());
    }
}
