/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import static java.lang.Math.floorDiv;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import kineticMonteCarlo.site.AbstractSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.AbstractCatalysisSite;
import kineticMonteCarlo.unitCell.CatalysisUc;
import utils.LinearRegression;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
abstract public class AbstractCatalysisLattice extends AbstractSurfaceLattice {

  final List<float[]> last1000events;
  final List<Double> last1000eventsTime;
  ArrayList<LinearRegression> regressions;
  /**
   * Unit cell array, where all the atoms are located.
   */
  private final CatalysisUc[][] ucArray;

  public AbstractCatalysisLattice(int hexaSizeI, int hexaSizeJ) {
    super(hexaSizeI, hexaSizeJ);
    last1000events = new LinkedList<>();
    last1000eventsTime = new LinkedList<>();
    ucArray = new CatalysisUc[hexaSizeI][hexaSizeJ];
  }
  
  @Override
  public CatalysisUc getUc(int pos) {
    int j = floorDiv(pos, getHexaSizeI());
    int i = pos - (j * getHexaSizeI());

    return ucArray[i][j];
  }
  
  public CatalysisUc getUc(int iLattice, int jLattice) {
    return ucArray[iLattice][jLattice];
  }
  
  public final void setSites(AbstractSurfaceSite[][] sites) {
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        AbstractSurfaceSite site = sites[i][j];
        ucArray[i][j] = new CatalysisUc(i, j, (AbstractCatalysisSite) site);

        ucArray[i][j].setPosX(getCartX(i, j));
        ucArray[i][j].setPosY(getCartY(j));
      }
    }
  }  
  
  /**
   * Identifies stationary situation, when sufficient number of previous steps are saved and their
   * R² is lower than 0.1 for all the species.
   *
   * @param time
   * @return
   */
  abstract public boolean isStationary(double time);

  public double[] getR2() {
    float[] covTmp = getCoverages();
    double[] r2 = new double[covTmp.length];
    for (int i = 0; i < 4; i++) {
      r2[i] = regressions.get(i).R2();
    }
    return r2;
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ();
  }

  @Override
  public double getCartX(int iHexa, int jHexa) {
    return iHexa;
  }

  @Override
  public double getCartY(int jHexa) {
    return jHexa;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    return new Point2D.Double(iHexa, jHexa);
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI() / 2, getHexaSizeJ() / 2);
  }
  
  abstract public float getCoverage(byte type);
  
  /**
   * Computes a partial coverage for CO and O in BR and CUS sites.
   * 
   * @return coverage CO^BR, CO^CUS, O^BR, CO^CUS
   */
  abstract public float[] getCoverages();
  
  abstract void setCoverage(int type, int site, int change);
  
  abstract double getCoverage(int type, int site);
  
  public void init() {
    setSites(generateSites());
  }    

  @Override
  public void deposit(AbstractSurfaceSite a, boolean forceNucleation) {
    AbstractCatalysisSite site = (AbstractCatalysisSite) a;
    site.setOccupied(true);

    updateCoCus(site);
    for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
      AbstractCatalysisSite neighbour = site.getNeighbour(i);
      site.getNeighbour(i).addOccupiedNeighbour(1);
      updateCoCus(neighbour);
    }
    addOccupied();
    setCoverage(a.getType(), site.getLatticeSite(), 1);
  }
  
  @Override
  public double extract(AbstractSurfaceSite a) {
    AbstractCatalysisSite site = (AbstractCatalysisSite) a;
    site.setOccupied(false);
    site.cleanCoCusNeighbours();
    for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
      AbstractCatalysisSite neighbour = site.getNeighbour(i);
      site.getNeighbour(i).addOccupiedNeighbour(-1);
      updateCoCus(neighbour);
    }
    subtractOccupied();
    setCoverage(a.getType(), site.getLatticeSite(), -1);
    return 0;
  }
  
  @Override
  public void reset() {
    last1000events.clear();
    last1000eventsTime.clear();
    super.reset();
  }
  
  public float getGapCoverage() {
    return (float) (1 - getCoverage());
  }
  
  /**
   * Changes the occupation of the clicked atom from unoccupied to occupied, or vice versa. It is
   * experimental and only works with AgUc simulation mode. If fails, the execution continues
   * normally.
   *
   * @param xMouse absolute X location of the pressed point
   * @param yMouse absolute Y location of the pressed point
   * @param scale zoom level
   */
  @Override
  public void changeOccupationByHand(double xMouse, double yMouse, int scale) {
    int iLattice;
    int jLattice;
    // scale the position with respect to the current scale.
    double xCanvas = xMouse / scale;
    double yCanvas = yMouse / scale;
    // choose the correct lattice
    iLattice = (int) Math.floor(xCanvas);
    jLattice = (int) Math.floor(yCanvas);
    double j = yCanvas;
    int pos = 0;

    // for debugging
    System.out.println("scale " + scale + " " + (jLattice - j));
    System.out.println("x y " + xMouse + " " + yMouse + " | " + xCanvas + " " + yCanvas + " | " + iLattice + " " + jLattice + " | ");
    AbstractSurfaceSite atom = getUc(iLattice, jLattice).getSite(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
  
  int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  AbstractCatalysisSite[][] instantiateAtoms() {
    return new AbstractCatalysisSite[getHexaSizeI()][getHexaSizeJ()];
  }
  
  abstract AbstractCatalysisSite newAtom(int i, int j);
  
  private AbstractCatalysisSite[][] generateSites() {
    //Instantiate atoms
    AbstractCatalysisSite[][] atoms = instantiateAtoms();
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = newAtom(i, j);
      }
    }
    
    //Interconnect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current atom
        AbstractCatalysisSite atom = atoms[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        atom.setNeighbour(atoms[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        atom.setNeighbour(atoms[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        atom.setNeighbour(atoms[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour(atoms[i][j], 3);
      }
    }
    return atoms;
  }

  /**
   * Check whether two CO^CUS atoms are together. Only for Farkas
   * 
   * @param atom
   */
  abstract void updateCoCus(AbstractCatalysisSite atom);

  @Override
  public AbstractSite getSite(int i, int j, int k, int unitCellPos) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
