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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class KmcCanvasGrowth extends KmcCanvas {

  private RoundPerimeter perimeter;
  private boolean printIslandCentres;
  private boolean blackAndWhite; 
  private boolean printPerimeter; 
  
  public KmcCanvasGrowth(AbstractSurfaceLattice lattice, RoundPerimeter perimeter) {
    this(lattice);
    this.perimeter = perimeter;
  }
  
  public KmcCanvasGrowth(AbstractSurfaceLattice lattice) {
    super(lattice);
    printIslandCentres = false;
    blackAndWhite = false;
    printPerimeter = false;
  }
  
  @Override
  public void changeBlackAndWhite() {
    blackAndWhite = !blackAndWhite;
  }
  
  @Override
  public void changePrintPerimeter() {
    printPerimeter = !printPerimeter;
  }
  
  @Override
  public void paint(Graphics g) { //real drawing method
    super.paint(g);
    AbstractGrowthLattice l = (AbstractGrowthLattice) super.getLattice();
    for (int i = 0; i < l.size(); i++) {
      AbstractGrowthUc uc = (AbstractGrowthUc) l.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthSite atom = (AbstractGrowthSite) uc.getSite(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * getScale()) + getBaseY();
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * getScale()) + getBaseX();

        if (blackAndWhite) {
          if (atom.isOccupied()) {
            g.setColor(BLUE);
            if (atom.isInnerPerimeter() && printPerimeter) {
              g.setColor(RED);
            }
          } else {
            g.setColor(WHITE_GRAY);
            if (atom.isOuterPerimeter() && printPerimeter) {
              g.setColor(BLACK);
            }
          }
        } else {
          g.setColor(colours[atom.getType()]);
          if (printPerimeter && perimeter != null) {
            if (perimeter.contains(atom)) {
              g.setColor(ORANGE);
            }
          }
        }
        
        if (getScale() < 3) {
          if (atom.isOccupied()) {
            g.fillRect(X, Y, getScale(), getScale());
          } else if (!atom.isOutside()) {
            g.drawRect(X, Y, getScale(), getScale());
          }
          
        } else if (atom.isOccupied()) {
          g.fillOval(X, Y, getScale(), getScale());
          if (getScale() > 8) {
            g.setColor(getContrastColor(g.getColor()));
            if (printId()) {
              g.drawString(Integer.toString(atom.getId()), X + (getScale() / 2) - (getScale() / 4), Y + (getScale() / 2) + (getScale() / 4));
            }
            if (printIslandNumber()) {
              String text = Integer.toString(atom.getIslandNumber());
              g.drawString(text, X + (getScale() / 2) - (getScale() / 4), Y + (getScale() / 2) + (getScale() / 4));
            }
            if (printMultiAtom()) {
              g.drawString(atom.getAttributes().getMultiAtomNumber().toString(), X + (getScale() / 2) - (getScale() / 4), Y + (getScale() / 2) + (getScale() / 4));
            }
          }
        } else if (!atom.isOutside()) {
          g.drawOval(X, Y, getScale(), getScale());
        }
      }
    }
    if (printIslandCentres) {
      try {
        for (int i = 0; i < l.getIslandCount(); i++) {
          Island island = l.getIsland(i);
          Point2D point = island.getCentreOfMass();
          int Y = (int) Math.round((point.getY()) * getScale()) + getBaseY();
          int X = (int) Math.round((point.getX()) * getScale()) + getBaseX();
          g.setColor(BLACK);
          g.drawLine(X - 5, Y - 5, X + 5, Y + 5);
          g.drawLine(X - 5, Y + 5, X + 5, Y - 5);
          g.drawOval(X - 5, Y - 5, 10, 10);
          g.setColor(RED);
          int diameter = (int) Math.round(2.0 * getScale() * island.getMaxDistance());
          int radius = (int) Math.round(getScale() * island.getMaxDistance());
          g.drawOval(X - radius, Y - radius, diameter, diameter);
          g.drawString(new DecimalFormat("##.##").format(island.getMaxDistance()), X, Y + 40);
          g.setColor(GREEN);
          diameter = (int) Math.round(2.0 * getScale() * island.getAvgDistance());
          radius = (int) Math.round(getScale() * island.getAvgDistance());
          g.drawOval(X - radius, Y - radius, diameter, diameter);
          g.setColor(BLACK);
          g.drawString(new DecimalFormat("##.##").format(island.getAvgDistance()), X, Y + 10);
        }
      } catch (NullPointerException e) {
        System.err.println("Some island centre or gyradius can not be printed. Ignoring and continuing... ");
      }
    }
  }
  
  /**
   * Method taken from http://stackoverflow.com/questions/4672271/reverse-opposing-colors
   *
   * @param colour base colour.
   * @return black or white; the one with highest contrast.
   */
  private Color getContrastColor(Color colour) {
    double y = (299 * colour.getRed() + 587 * colour.getGreen() + 114 * colour.getBlue()) / 1000;
    return y >= 128 ? Color.black : Color.white;
  }
  
}
