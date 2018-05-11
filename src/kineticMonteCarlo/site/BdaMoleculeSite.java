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
package kineticMonteCarlo.site;

import java.util.List;
import javafx.geometry.Point3D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaMoleculeSite extends AbstractGrowthSite {
  /**
   * Position within unit cell.
   */
  private final int pos;
  /** Alpha, Beta (1 or 2), Gamma or Delta. */
  private byte type;
  private boolean rotated;

  private BdaMoleculeSite[] neighbours;
  private final double[][] alphaXyz = {{3.7500, 0.0000},
  {3.0000, 1.2990},
  {1.5000, 1.2991},
  {0.7500, 0.0000},
  {1.5000, -1.2991},
  {3.0000, -1.2990},
  {-3.7500, 0.0000},
  {-3.0000, -1.2990},
  {-1.5000, -1.2991},
  {-0.7500, 0.0000},
  {-1.5000, 1.2990},
  {-3.0000, 1.2990},
  {-5.2500, 0.0000},
  {5.2500, 0.0000},
  {-6.0000, -1.2991},
  {-6.0000, 1.2991},
  {6.0000, 1.2991},
  {6.0000, -1.2991},
  {0.0000, 0.0000}};
  
  private final double[][] beta1Xyz = alphaXyz;
  private final double[][] beta2Xyz = {{3.665, 0.7937},
  {2.657, 1.9045},
  {1.191, 1.5871},
  {0.733, 0.1587},
  {1.741, -0.9522},
  {3.2069, -0.6346},
  {-3.6651, -0.7938},
  {-2.6571, -1.9046},
  {-1.1911, -1.5872},
  {-0.733, -0.1588},
  {-1.741, 0.952},
  {-3.207, 0.6345},
  {-5.1311, -1.1113},
  {5.131, 1.1112},
  {-5.5891, -2.5397},
  {-6.1391, -0.0004},
  {5.589, 2.5396},
  {6.139, 0.0003},
  {0, 0}};


  public BdaMoleculeSite(int id,  int pos) {
    super(id, (short)-1, (short)-1, 0, -1);
    this.pos = pos;
    neighbours = new BdaMoleculeSite[4];
    rotated = false;
  }
  
  /**
   * Returns the position within the unit cell.
   *
   * @return coordinates in unit cell
   */
  @Override
  public Point3D getPos() {
    if (rotated)
      return new Point3D(alphaXyz[pos][1], alphaXyz[pos][0], 0);
    else
      return new Point3D(alphaXyz[pos][0], alphaXyz[pos][1], 0);
  }

  @Override
  public byte getTypeWithoutNeighbour(int posNeighbour) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean areTwoTerracesTogether() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthSite chooseRandomHop() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getOrientation() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void obtainRateFromNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double probJumpToNeighbour(int originType, int position) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List getAllNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double updateOneBound(int bond) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isPartOfImmobilSubstrate() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public BdaMoleculeSite getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite m, int pos) {
    neighbours[pos] = (BdaMoleculeSite) m;
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public boolean isRotated() {
    return rotated;
  }

  public void setRotated(boolean rotated) {
    this.rotated = rotated;
  }
}
