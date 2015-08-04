/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.diffusion2DGrowth.agAgGrowth;

import graphicInterfaces.diffusion2DGrowth.AbstractKmcCanvas;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import java.awt.Color;
import java.awt.Graphics;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.lattice.AgAgLattice;

public class AgAgKmcCanvas extends AbstractKmcCanvas {

  public AgAgKmcCanvas(Abstract2DDiffusionLattice lattice) {
    super(lattice);
  }

  @Override
  public void paint(Graphics g) { //real drawing method

    super.paint(g);

    g.setColor(Color.black);
    g.fillRect(baseX, baseY, (int) (lattice.getSizeX() * scale), (int) (lattice.getSizeY() * scale * AgAgLattice.YRatio));

    for (int j = 0; j < lattice.getSizeY(); j++) {          //Y
      int i = 0;
      int cont = 0;
      int Y = Math.round((lattice.getSizeY() - 1 - j) * scale * AgAgLattice.YRatio) + baseY;
      while (true) {

        int X = (int) ((cont * scale) + (j) / 2.0f * scale);

        if (X >= lattice.getSizeX() * scale) {
          X -= lattice.getSizeX() * scale;
        }
        if (X < 0) {
          X += lattice.getSizeX() * scale;
        }
        X += baseX;

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
          case AbstractAtom.TERRACE:
            g.setColor(Color.RED);
            break;
          case AbstractAtom.CORNER:
            g.setColor(Color.MAGENTA);
            break;
          case AbstractAtom.EDGE:
            g.setColor(Color.ORANGE);
            break;
          case AbstractAtom.KINK:
            g.setColor(Color.YELLOW);
            break;
          case AbstractAtom.BULK:
            g.setColor(Color.GREEN);
            break;
          case 5: // imposible
            g.setColor(Color.WHITE);
            break;
          case 6: // imposible
            g.setColor(Color.CYAN);
            break;
          case 7: // imposible
            g.setColor(Color.BLUE);
            break;
        }

        if (scale < 3) {
          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillRect(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside() && type > 0) {
            g.drawRect(X, Y, scale, scale);
          }

        } else {

          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillOval(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside() && type > 0) {
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
  
  @Override
  public int getSizeY() {
    return (int) (this.lattice.getSizeY() * scale * AgAgLattice.YRatio);
  }
}
