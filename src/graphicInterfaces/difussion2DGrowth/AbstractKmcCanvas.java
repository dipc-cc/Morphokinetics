/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.difussion2DGrowth;

import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

/**
 *
 * @author Nestor
 */
public abstract class AbstractKmcCanvas extends Canvas {

    protected int baseX = 0;
    protected int baseY = 0;
    protected BufferStrategy strategy;  //BufferStratrgy
    protected boolean initialized = false;
    protected Abstract2DDiffusionLattice lattice;
    public int escalado = 2;

    public AbstractKmcCanvas(Abstract2DDiffusionLattice lattice) {
        this.lattice = lattice;
    }

    public void setBaseLocation(int baseX, int baseY) {
        this.baseX += baseX;
        this.baseY += baseY;
    }

    public int getScale() {
        return escalado;
    }

    public void setScale(int escalado) {
        this.escalado = escalado;
    }

    public AbstractKmcCanvas() {   //constructor
        this.setIgnoreRepaint(true); //we repaint manually
        this.setFocusable(false);
    }

    public void dispose() {
        strategy.dispose();
    }

    public void initialize() { //call this before starting game loop, it initializes the bufferStrategy
        createBufferStrategy(2);  //double buffering
        strategy = getBufferStrategy();
    }

    public void performDraw() {  //public drawing method, call this from your game loop for update image

        Graphics g;
        try {
            g = strategy.getDrawGraphics();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.paint((Graphics2D) g);
        g.dispose();

        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }
}
