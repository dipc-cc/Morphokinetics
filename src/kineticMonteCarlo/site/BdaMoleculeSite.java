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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.List;
import javafx.geometry.Point3D;
import kineticMonteCarlo.process.BdaProcess;
import static utils.MathUtils.rotateAngle;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaMoleculeSite extends AbstractGrowthSite {
  
  public static final int ALPHA = 1; // 2⁰
  public static final int BETA = 2; // 2¹
  public static final int GAMMA = 4; // 2²
  public static final int DELTA = 8; // 2³
  
  public static final int BETA_2 = 2;
  
  private final BdaAtomSite[] atoms;
  private final BdaProcess[] processes;
  /** Alpha, Beta (1 or 2), Gamma or Delta. */
  private byte type;
  private boolean rotated;
  private final BdaMoleculeSite[] neighbours;
  
  private final double[][] xyz = {{3.7500, 0.0000},
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

  public BdaMoleculeSite(int id, boolean rotated, Byte type) { // int type (alpha, beta...)
    super(id, (short) -1, (short) -1, 0, -1);
    atoms = new BdaAtomSite[19];
    rotateAtoms();
    this.rotated = rotated;
    neighbours = new BdaMoleculeSite[4];
    processes = new BdaProcess[6];
    for (int i = 0; i < processes.length; i++) {
      processes[i]= new BdaProcess();
    }
    setProcceses(processes);
    setNumberOfNeighbours(4);
    if (type == null)
      this.type = ALPHA;
    else 
      this.type = type;
  }
  
  public boolean isRotated() {
    return rotated;
  }

  public void setRotated(boolean rotated) {
    this.rotated = rotated;
    rotateAtoms();
  }

  private void rotateAtoms() {
    for (int pos = 0; pos < atoms.length; pos++) {
      Point3D cartPos = null;
      switch (type) {
        case ALPHA:
          if (rotated) {
            cartPos = new Point3D(xyz[pos][1], xyz[pos][0], 0);
          } else {
            cartPos = new Point3D(xyz[pos][0], xyz[pos][1], 0);
          }
          break;
        case BETA:
          if (rotated) {
            double[] xy = rotateAngle(xyz[pos][0], xyz[pos][1], 22.5);
            cartPos = new Point3D(xy[0], xy[1], 0);
          } else {
            cartPos = new Point3D(xyz[pos][0], xyz[pos][1], 0);
          }
          break;

      }

      atoms[pos] = new BdaAtomSite(pos, pos, cartPos);
      atoms[pos].setOccupied(true);
    }
  }
  
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  //@Override
  public int size() {
    return atoms.length;
  }
  
  /**
   * Choose the corresponding atom of the molecule.
   *
   * @param pos position in the array of atoms.
   * @return an atom.
   */
  public AbstractGrowthSite getSite(int pos) {
    return atoms[pos];
  }

  @Override
  public void setType(byte type) {
    this.type = type;
  }
  
  @Override
  public byte getType() {
    return (byte) type;
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
  public AbstractGrowthSite getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite m, int pos) {
    neighbours[pos]= (BdaMoleculeSite) m;
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
