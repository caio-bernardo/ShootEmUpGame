package org.shootemup.components;

import java.awt.Color;

import org.shootemup.GameLib;
import org.shootemup.utils.Renderable;

public class LifeBar implements Renderable{

    private double downLine, upLine, leftLine, rightLine;

    private int initialLife, finalLife;
    private Color color;
    private boolean canPrint = true;

    public LifeBar(Color color, int life, double downLine, double upLine){
        this.color = color;
        this.initialLife = life;
        this.downLine = downLine;
        this.upLine = upLine;
    }

    public void setFinalLife(int newLife){this.finalLife = newLife;}
    public int getFinalLife(){return finalLife;}
    public int getInitialLife(){return initialLife;}
    public Color getColor(){return this.color;}
    public boolean getCanPrint(){return this.canPrint;}
    public void setCanPrint(boolean canPrint){this.canPrint = canPrint;}

    @Override
    public void render(){
        double lostLife = (this.getInitialLife() - this.getFinalLife()) * (385.0 / this.getInitialLife());
        if(lostLife < 400){
            /*Preenchimento da Vida */
            GameLib.setColor(this.getColor());
            for (int i = 0; i < 4; i++) {
                    GameLib.drawLine(50.0, upLine + i, 430 - lostLife, upLine + i);//desenhe uma linha horizontal da esquerda para a direita.
            }
        } else {
            if(this.getCanPrint()) this.setCanPrint(false);
            else this.setCanPrint(false);
        }
        if(this.getCanPrint()){
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(50.0, upLine, GameLib.WIDTH - 50.0, upLine); // 1. Linha de cima (esquerda para direita)
            GameLib.drawLine(GameLib.WIDTH - 50.0, upLine, GameLib.WIDTH - 50.0, downLine);// 2. Linha da direita (de cima para baixo)
            GameLib.drawLine(GameLib.WIDTH - 50.0, downLine, 50.0, downLine);// 3. Linha de baixo (direita para esquerda)
            GameLib.drawLine(50.0, downLine, 50.0, upLine); // 4. Linha da esquerda (de baixo para cima)
        }
    }

}
