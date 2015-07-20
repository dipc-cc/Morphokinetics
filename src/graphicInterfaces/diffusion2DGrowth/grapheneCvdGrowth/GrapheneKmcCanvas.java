/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.diffusion2DGrowth.grapheneCvdGrowth;

import graphicInterfaces.diffusion2DGrowth.AbstractKmcCanvas;
import java.awt.Color;
import java.awt.Graphics;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;


public class GrapheneKmcCanvas extends AbstractKmcCanvas {

  public static float constant_Y = (float) Math.sqrt(3) / 2.0f;

  public GrapheneKmcCanvas(Abstract2DDiffusionLattice lattice) {
    super(lattice);
  }

  @Override
  public void paint(Graphics g) { //real drawing method

    super.paint(g);

    g.setColor(Color.black);
    g.fillRect(baseX, baseY, (int) (lattice.getSizeX() * scale * 1.5f), (int) (lattice.getSizeY() * scale * constant_Y));

    for (int j = 0; j < lattice.getSizeY(); j++) {          //Y
      int i = 0;
      int cont = 0;
      int Y = Math.round((lattice.getSizeY() - 1 - j) * scale * constant_Y) + baseY;
      while (true) {

        if ((j & 1) == 0) {
          if ((cont % 3) == 2) {
            cont++;
            continue;
          }
        } else {
          if ((cont % 3) == 1) {
            cont++;
            continue;
          }
        }

        int X = (cont * scale) + baseX;
        if ((j & 1) == 0) {
          X += 0.5f * scale;
        }

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
          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillRect(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside()) {
            g.drawRect(X, Y, scale, scale);
          }

        } else {

          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillOval(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside()) {
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
