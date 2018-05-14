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

import basic.io.BdaRestart;
import java.awt.Color;
import static java.awt.Color.BLACK;
import java.awt.Graphics;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAtomSite;
import kineticMonteCarlo.site.BdaMoleculeSite;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class KmcCanvasBda extends KmcCanvas {
  
  /** Distance between centres of Ag molecules (from ASE) in Angstrom. */
  double distanceAg;
  BdaRestart restart;
  
  public KmcCanvasBda(AbstractSurfaceLattice lattice) {
    super(lattice);
    distanceAg = 2.892;
    restart = new BdaRestart("results/");
  }
  
  boolean painted = false;
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    for (int i = 0; i < getLattice().size(); i++) {
      BdaSurfaceUc uc = (BdaSurfaceUc) getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractSurfaceSite site = uc.getSite(j);
        int Y = (int) Math.round(((site.getPos().getY() + uc.getPos().getY()) * getScale() * distanceAg) - (distanceAg * getScale() / 2.0)) + getBaseY();
        int X = (int) Math.round(((site.getPos().getX() + uc.getPos().getX()) * getScale() * distanceAg) - (distanceAg * getScale() / 2.0)) + getBaseX();

        g.setColor(colours[2]);
        int ball = (int) Math.round(getScale() * distanceAg);
        g.drawOval(X, Y, ball, ball);
        if (!uc.isAvailable()) {
          g.setColor(RED);
          g.drawOval(X, Y, ball, ball);
        }
        if (getScale() >= 8 && true) {
          g.setColor(Color.BLACK);
          g.drawString(Integer.toString(site.getId()), X + getScale(), Y + 2*getScale());
        }
      }
      if (uc.isOccupied()) {
        paintBdaMolecule(g, uc, uc.getBdaUc());
      }
    }
  }
  
  private void paintBdaMolecule(Graphics g, BdaSurfaceUc sUc, BdaMoleculeUc muc) {
    double sizeBall = 2.0;
    BdaMoleculeSite bdaMolecule = (BdaMoleculeSite) muc.getSite(-1);
    for (int i = 0; i < bdaMolecule.size() - 1; i++) {
      BdaAtomSite atom = (BdaAtomSite) bdaMolecule.getSite(i);

      int Y = (int) Math.round(((atom.getPos().getY() + (sUc.getPos().getY() + muc.getPos().getY()) * distanceAg) * getScale()) - sizeBall * getScale() / 2.0) + getBaseY();
      int X = (int) Math.round(((atom.getPos().getX() + (sUc.getPos().getX() + muc.getPos().getX()) * distanceAg) * getScale()) - sizeBall * getScale() / 2.0) + getBaseX();

      g.setColor(BLACK);
      if (i > 13) { // Oxygen, instead of Carbon
        g.setColor(RED);
      }
      int width = (int) Math.round(getScale() * sizeBall);
      int height = (int) Math.round(getScale() * sizeBall);
      g.fillOval(X, Y, width, height);
    }
  }

  @Override
  public int getSizeX() {
    return (int) (super.getSizeX() * distanceAg);
  }

  @Override
  public int getSizeY() {
    return (int) (super.getSizeY() * distanceAg);
  }

  @Override
  void changeBlackAndWhite() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void changePrintPerimeter() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void setPrintId(boolean selected) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void setPrintIslandNumber(boolean selected) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void setPrintMultiAtom(boolean selected) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  void writeSvg() {
    restart.writeSvg(1, getLattice());
  }
}
