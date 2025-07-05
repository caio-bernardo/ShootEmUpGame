package org.shootemup.components;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.utils.Renderable;

public class LifeBar implements Renderable{

    private int initialLife;
    private int finalLife;
    private Color color;
    private boolean canPrint = true;

    public LifeBar(Color color, int life){
        initialLife = life;
        this.color = color;
    }

    public void setFinalLife(int newLife){
        finalLife = newLife;
    }

    @Override
    public void render(){

        double lostLife = (initialLife - finalLife) * (385.0 / initialLife);

        if(lostLife < 400){
            /*Preenchimento da Vida */
            GameLib.setColor(this.color);
            for (int i = 0; i < 4; i++) {
                    // ...desenhe uma linha horizontal da esquerda para a direita.
                    GameLib.drawLine(50.0, 100.0 + i, 430 - lostLife, 100.0 + i);
            }
        } else {
            if(canPrint){
                canPrint = false;
            } else {
                canPrint = true;
            }
        }

        if(canPrint){
            GameLib.setColor(Color.BLUE);
            // 1. Linha de cima (esquerda para direita)
            GameLib.drawLine(50.0, 100.0, GameLib.WIDTH - 50.0, 100.0);

            // 2. Linha da direita (de cima para baixo)
            GameLib.drawLine(GameLib.WIDTH - 50.0, 100.0, GameLib.WIDTH - 50.0, 105);

            // 3. Linha de baixo (direita para esquerda)
            GameLib.drawLine(GameLib.WIDTH - 50.0, 105, 50.0, 105);

            // 4. Linha da esquerda (de baixo para cima)
            GameLib.drawLine(50.0, 105, 50.0, 100.0);
        }
    }
}
