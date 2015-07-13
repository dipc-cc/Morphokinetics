/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.diffusion2DGrowth.agAgGrowth;



import graphicInterfaces.diffusion2DGrowth.AbstractKmcCanvas;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import java.awt.*;



public class AgAgKmcCanvas extends AbstractKmcCanvas {

    public static float constant_Y = (float)Math.sqrt(3)/2.0f;

    public AgAgKmcCanvas(Abstract2DDiffusionLattice lattice) {
        super(lattice);
    }

    
    @Override
    public void paint(Graphics g) { //real drawing method

        super.paint(g);
  
        g.setColor(Color.black);
        g.fillRect(baseX, baseY, (int) (lattice.getSizeX() * scale), (int) (lattice.getSizeY()  * scale * constant_Y));

        for (int j = 0; j < lattice.getSizeY(); j++) {          //Y
            int i = 0;
            int cont = 0;
            int Y = Math.round((lattice.getSizeY() - 1 - j) * scale * constant_Y) + baseY;
            while (true) {

                int X = (int)((cont * scale) +(j)/2.0f * scale);
                
                if (X>=lattice.getSizeX() * scale) X-=lattice.getSizeX() * scale;
                if (X<0) X+=lattice.getSizeX() * scale;
                X+=baseX;   
                
                if (X < 0 || X > 1024 || Y < 0 || Y > 1024) {
                    i++;
                    cont++;
                    if (i == lattice.getSizeX()) {
                        break;
                    }
                    continue;
                }

                byte type = lattice.getAtom(i, j).getType();
                switch (type) {
                    case 0:
                        g.setColor(Color.RED);
                        break;
                    case 1:
                        g.setColor(Color.MAGENTA);
                        break;
                    case 2:
                        g.setColor(Color.ORANGE);
                        break;
                    case 3:
                        g.setColor(Color.YELLOW);
                        break;
                    case 4:
                        g.setColor(Color.GREEN);
                        break;
                    case 5:
                        g.setColor(Color.WHITE);
                        break;
                    case 6:
                        g.setColor(Color.CYAN);
                        break;
                    case 7:
                        g.setColor(Color.BLUE);
                        break;
                }

                if (scale < 3) {
                    if ( lattice.getAtom(i, j).isOccupied()) {
                        g.fillRect(X, Y, scale, scale);
                    } else if (!lattice.getAtom(i, j).is_outside() && type>0) {
                      g.drawRect(X, Y, scale, scale);
                    }

                } else {

                    if ( lattice.getAtom(i, j).isOccupied()) {
                        g.fillOval(X, Y, scale, scale);
                    } else if (!lattice.getAtom(i, j).is_outside() && type>0) {
                       g.drawOval(X, Y, scale, scale);
                    }
                }

                i++;
                cont++;
                if (i == lattice.getSizeX()) {
                    break;
                }
            }
        } 
        g.dispose();
    }
}