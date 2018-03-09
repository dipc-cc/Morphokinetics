/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import basic.io.Restart;
import java.awt.geom.Point2D;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import kineticMonteCarlo.unitCell.AbstractSurfaceUc;
import utils.MathUtils;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractSurfaceKmc extends AbstractKmc {

  private AbstractSurfaceLattice lattice;

  /**
   * Attribute to control the output of data every 1% and nucleation.
   */
  private final boolean extraOutput;
  /**
   * Attribute to control the output of extra data of delta time between two attachments and between
   * an atom is deposited and attached to an island.
   */
  private final boolean extraOutput2;
  private Restart restart;

  public AbstractSurfaceKmc(Parser parser) {
    super(parser);
    getList().autoCleanup(true);
    
    extraOutput2 = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA2);
    if (extraOutput2) {
      extraOutput = extraOutput2;
    } else {
      extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    }
    restart = new Restart(extraOutput, extraOutput2);
  }
  
  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    Point2D corner1 = lattice.getCartesianLocation(0, 0);
    double scaleX = binX / lattice.getCartSizeX();
    double scaleY = binY / lattice.getCartSizeY();

    if (scaleX > 1.01 || scaleY > 1.02) {
      System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
      System.err.println("The size of the surface should be " + binX + " and it is " + lattice.getCartSizeX() + "/" + scaleX+" (hexagonal size is "+lattice.getHexaSizeI()+")");
      System.err.println("The size of the surface should be " + binY + " and it is " + lattice.getCartSizeY() + "/" + scaleY+" (hexagonal size is "+lattice.getHexaSizeJ()+")");
      System.err.println("X scale is " + scaleX + " Y scale is " + scaleY);
      return null;
    }

    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        surface[i][j] = -1;
      }
    }
    int x;
    int y;
    for (int i = 0; i < lattice.size(); i++) {
      AbstractSurfaceUc uc = lattice.getUc(i);
      double posUcX = uc.getPos().getX();
      double posUcY = uc.getPos().getY();
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getSite(j).isOccupied()) {
          double posAtomX = uc.getSite(j).getPos().getX();
          double posAtomY = uc.getSite(j).getPos().getY();
          x = (int) ((posUcX + posAtomX - corner1.getX()) * scaleX);
          y = (int) ((posUcY + posAtomY - corner1.getY()) * scaleY);

          surface[x][y] = 0;
        }
      }
    }
    MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
    MathUtils.normalise(surface);
    return surface;
  }
  
  /**
   * Returns the coverage of the simulation. Thus, the number of occupied locations divided by the
   * total number of locations.
   *
   * @return A value between 0 and 1.
   */
  public float getCoverage() {
    return lattice.getCoverage();
  }

  /**
   * @param lattice the lattice to set
   */
  public final void setLattice(AbstractSurfaceLattice lattice) {
    this.lattice = lattice;
  }

  @Override
  public void reset() {
    lattice.reset();
    getList().reset();

    restart.reset();
  }
  
  @Override
  public AbstractSurfaceLattice getLattice() {
    return lattice;
  }
  
  @Override
  public void initialiseRates(double[] rates) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
