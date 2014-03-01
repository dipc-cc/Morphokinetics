/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphic_interfaces.Difussion2D_Growth.AgAg_growth;



import Graphic_interfaces.Difussion2D_Growth.AbstractKMC_canvas;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import java.awt.*;



public class AgAgKMC_canvas extends AbstractKMC_canvas {

    public static float constant_Y = (float)Math.sqrt(3)/2.0f;

    public AgAgKMC_canvas(Abstract_2D_diffusion_lattice lattice) {
        super(lattice);
    }

    
    @Override
    public void paint(Graphics g) { //real drawing method

        super.paint(g);
  
        g.setColor(Color.black);
        g.fillRect(baseX, baseY, (int) (lattice.getSizeX() * escalado), (int) (lattice.getSizeY()  * escalado * constant_Y));

        for (int j = 0; j < lattice.getSizeY(); j++) {          //Y
            int i = 0;
            int cont = 0;
            int Y = Math.round((lattice.getSizeY() - 1 - j) * escalado * constant_Y) + baseY;
            while (true) {

                int X = (int)((cont * escalado) +(j)/2.0f * escalado);
                X-=lattice.getSizeY() /4.0f * escalado;
                
                if (X>=lattice.getSizeX() * escalado) X-=lattice.getSizeX() * escalado;
                if (X<0) X+=lattice.getSizeX() * escalado;
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

                if (escalado < 3) {
                    if ( lattice.getAtom(i, j).isOccupied()) {
                        g.fillRect(X, Y, escalado, escalado);
                    } else if (!lattice.getAtom(i, j).is_outside()) {
                      g.drawRect(X, Y, escalado, escalado);
                    }

                } else {

                    if ( lattice.getAtom(i, j).isOccupied()) {
                        g.fillOval(X, Y, escalado, escalado);
                    } else if (!lattice.getAtom(i, j).is_outside()) {
                       g.drawOval(X, Y, escalado, escalado);
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