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
import static java.awt.Color.YELLOW;
import static java.awt.Color.white;
import java.awt.Graphics;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.site.BdaAtomSite;
import kineticMonteCarlo.site.BdaMoleculeSite;
import static kineticMonteCarlo.site.BdaMoleculeSite.ALPHA;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class KmcCanvasBda extends KmcCanvas {
  
  /** Distance between centres of Ag molecules (from ASE) in Angstrom. */
  private final double distanceAg;
  private final BdaRestart restart;
  private boolean detailed;
  
  public KmcCanvasBda(AbstractSurfaceLattice lattice) {
    super(lattice);
    distanceAg = 2.892;
    restart = new BdaRestart("results/");
    detailed = false;
  }
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    for (int i = 0; i < getLattice().size(); i++) {
      BdaSurfaceUc agUc = (BdaSurfaceUc) getLattice().getUc(i);
      for (int j = 0; j < agUc.size(); j++) {
        AbstractSurfaceSite agSite = agUc.getSite(j);
        int Y = (int) Math.round(((agSite.getPos().getY() + agUc.getPos().getY()) * getScale() * distanceAg) - (distanceAg * getScale() / 2.0)) + getBaseY();
        int X = (int) Math.round(((agSite.getPos().getX() + agUc.getPos().getX()) * getScale() * distanceAg) - (distanceAg * getScale() / 2.0)) + getBaseX();

        g.setColor(colours[2]);
        int ball = (int) Math.round(getScale() * distanceAg);
        if (detailed) {
          if (!((BdaAgSurfaceSite)agSite).isAvailable(ADSORPTION)) {
            g.setColor(white);
          }
          if (!((BdaAgSurfaceSite)agSite).isAvailable(DIFFUSION)) {
            g.setColor(BLUE);
            g.fillOval(X, Y, ball, ball);
          } else {
            g.drawOval(X, Y, ball, ball);
          }
          if (getScale() >= 8) {
            g.setColor(Color.BLACK);
            if (printId()) {
              g.drawString(Integer.toString(agSite.getId()), X + getScale(), Y + 2 * getScale());
            }
            if (printIslandNumber()) {
              String text = Integer.toString(((BdaAgSurfaceSite) agSite).getBdaSize());
              g.drawString(text, X + getScale(), Y + 2 * getScale());
            }
            if (printMultiAtom()) {
              g.drawString(Integer.toString(agUc.getPosI()) + "," + Integer.toString(agUc.getPosJ()),
                      X + getScale(), Y + 2 * getScale());
            }
          }
        }
        if (agSite.isOccupied()) {
          paintBdaMolecule(g, agUc);
        }
        if (detailed && agSite.getRate(ROTATION) > 0) {
          g.setColor(GREEN);
          g.fillOval(X, Y, ball, ball);   
        }
      }
    }
  }
  
  private void paintBdaMolecule(Graphics g, BdaSurfaceUc sUc) {
    double sizeBall = 2.0;
    BdaMoleculeUc muc = ((BdaAgSurfaceSite) sUc.getSite(0)).getBdaUc();
    if (muc == null) {
      System.out.println("Warning: a molecule could not be printed");
      return;
    }
    BdaMoleculeSite bdaMolecule = (BdaMoleculeSite) muc.getSite(-1);
    for (int i = 0; i < bdaMolecule.size(); i++) {
      BdaAtomSite atom = (BdaAtomSite) bdaMolecule.getSite(i);
      int Y = (int) Math.round(((atom.getPos().getY() + (sUc.getPos().getY() + muc.getPos().getY()) * distanceAg) * getScale()) - sizeBall * getScale() / 2.0) + getBaseY();
      int X = (int) Math.round(((atom.getPos().getX() + (sUc.getPos().getX() + muc.getPos().getX()) * distanceAg) * getScale()) - sizeBall * getScale() / 2.0) + getBaseX();

      if (i < bdaMolecule.size() - 1) {
        if (bdaMolecule.getType() == ALPHA) {
          g.setColor(BLACK);
        } else {
          g.setColor(DARKBLUE);
        }
        if (i > 13) { // Oxygen, instead of Carbon
          g.setColor(RED);
        }
        int width = (int) Math.round(getScale() * sizeBall);
        int height = (int) Math.round(getScale() * sizeBall);
        g.fillOval(X, Y, width, height);
      } else if (printMultiAtom()) {
        g.setColor(YELLOW);
        String text = Integer.toString(bdaMolecule.getId());
        g.drawString(text, X + getScale(), Y + 2 * getScale());
      }
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
    detailed = !detailed;
  }

  @Override
  void changePrintPerimeter() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  void writeSvg() {
    restart.writeSvg(1, getLattice());
  }
}
