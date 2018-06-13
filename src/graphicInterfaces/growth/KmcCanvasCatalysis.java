/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package graphicInterfaces.growth;

import java.awt.Color;
import static java.awt.Color.BLACK;
import java.awt.Graphics;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.CatalysisAmmoniaSite;
import kineticMonteCarlo.unitCell.CatalysisUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class KmcCanvasCatalysis extends KmcCanvas {
  
  private final Color[] colours = {BLUE, INDIANRED, BLUEVIOLET, GRAY, CORNFLOWERBLUE, DARKBLUE, GOLD, GREEN};
  public KmcCanvasCatalysis(AbstractSurfaceLattice lattice) {
    super(lattice);
  }
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    for (int i = 0; i < getLattice().size(); i++) {
      if (i % 2 == 0) {
        g.setColor(GRAY);
      } else {
        g.setColor(WHITE);
      }

      CatalysisUc uc = (CatalysisUc) getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractSurfaceSite atom = uc.getSite(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * getScale()) + getBaseY();
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * getScale()) + getBaseX();

        g.fillRect(X, Y, getScale(), getScale());
        g.setColor(colours[atom.getType()]);

        if (getScale() < 10) {
          if (atom.isOccupied()) {
            g.fillRect(X, Y, getScale(), getScale());
          }

        } else if (atom.isOccupied()) {
          g.fillOval(X, Y, getScale(), getScale());
          /*if (scale > 8) {
            g.setColor(getContrastColor(g.getColor()));
            if (printId) {
              g.drawString(Integer.toString(atom.getId()), X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
            if (printIslandNumber) {
              String text = Integer.toString(atom.getIslandNumber());
              g.drawString(text, X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
          }//*/
        }
      }
    }
  }

  @Override
  void changeBlackAndWhite() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void changePrintPerimeter() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
