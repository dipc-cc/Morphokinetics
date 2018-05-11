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
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;
import kineticMonteCarlo.site.BdaMoleculeSite;
import java.util.List;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import static java.lang.Math.floorDiv;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaLattice extends AbstractGrowthLattice {
  /**
   * Unit cell array, where all the surface Ag atoms are located.
   */
  private final BdaSurfaceUc[][] agUcArray;
  private final List<BdaSurfaceUc> moleculeList;
  
  private final int[] alphaTravelling = {3,0,3,2,3,0,3,2,2,1,1,1,1,1,1,1,1,0,0,3,2,3,0,3,2,3,0};
    

  public BdaLattice(int hexaSizeI, int hexaSizeJ) {
    super(hexaSizeI, hexaSizeJ, null);
    agUcArray = new BdaSurfaceUc[hexaSizeI][hexaSizeJ];
    createAgUcSurface();
    moleculeList = new ArrayList<>();
  }
  
  public int getMoleculeUcSize() {
    return moleculeList.size();
  }

  @Override
  public void deposit(AbstractSurfaceSite agSite, boolean forceNucleation) {
    BdaAgSurfaceSite a = (BdaAgSurfaceSite) agSite;
    deposit(getAgUc(a));
  }
  
  public BdaSurfaceUc getAgUc(BdaAgSurfaceSite agSite) {
    return agUcArray[agSite.getiHexa()][agSite.getjHexa()];
  }
  
  public void deposit(BdaSurfaceUc agUc) {
    deposit(agUc, null);
  }
  
  public void deposit(BdaSurfaceUc agUc, BdaMoleculeUc bdaUc) {
    if (!agUc.isAvailable())
      return;
    if (bdaUc == null)
      bdaUc = new BdaMoleculeUc();
    agUc.setBdaUc(bdaUc);
    changeAvailability(agUc, false);
    
    moleculeList.add(agUc);
    
  }

  public double extract(int index) {
    BdaSurfaceUc currentAgUc = moleculeList.get(index);
    changeAvailability(currentAgUc, true);
    currentAgUc.setOccupied(false);
    moleculeList.remove(index);
    return 0;
  }
  
  public double extract(BdaSurfaceUc agUc) {
    changeAvailability(agUc, true);
    agUc.setOccupied(false);
    moleculeList.remove(agUc);
    return 0;
  }
  
  public BdaSurfaceUc getRandomOccupiedUc() {
    int random = StaticRandom.rawInteger(getMoleculeUcSize());
    return moleculeList.get(random);
  }

  /**
   * Reserve or release the space for current BDA molecule.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  private void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaSurfaceUc neighbourAgUc = origin.getNeighbour(alphaTravelling[0]);
    neighbourAgUc.setAvailable(makeAvailable);
    for (int i = 1; i < alphaTravelling.length; i++) {
      neighbourAgUc = neighbourAgUc.getNeighbour(getNeighbourIndex(origin, i));
      neighbourAgUc.setAvailable(makeAvailable);
    }
  }

  private int getNeighbourIndex(BdaSurfaceUc origin, int i) {
    if (origin.getBdaUc().isRotated()) {
      return (alphaTravelling[i] + 3) % 4;
    } else {
      return alphaTravelling[i];
    }
  }
  
  private int getNeighbourIndex(BdaAgSurfaceSite agSite, int i) {
    return getNeighbourIndex(getAgUc(agSite), i);
  }

  private void changeNeighbourhood(BdaMoleculeUc mUc) {
    // check east/west
    /*BdaSurfaceUc agUc = mUc.getAgUc();
    int i = agUc.getPosI();
    int iEast = i + 5;
    if (iEast >= getHexaSizeI())
      iEast = iEast % getHexaSizeI();
    int j = agUc.getPosJ();
    if (agUcArray[i][j].isOccupied()) {
      mUc.setNeighbour(agUc, j);
    }*/
  }
  
  /**
   * Includes all -2, +2 in main molecule axis and -1,+1 in the other axis of the current site in a
   * list without repeated elements.
   *
   * @param modifiedSites previously added sites, can be null.
   * @param site current central site.
   * @return A list with of sites that should be recomputed their rate.
   */
  @Override
  public Set<AbstractGrowthSite> getModifiedSites(Set<AbstractGrowthSite> modifiedSites, AbstractGrowthSite site) {
    if (modifiedSites == null) {
      modifiedSites = new HashSet<>();
    }
    BdaAgSurfaceSite origin = (BdaAgSurfaceSite) site;
    site = origin.getNeighbour(alphaTravelling[0]);
    modifiedSites.add(site);
    for (int i = 1; i < alphaTravelling.length; i++) {
      site = site.getNeighbour(getNeighbourIndex(origin, i));
      modifiedSites.add(site);
    }
    return modifiedSites;
  }
  
  @Override
  public AbstractGrowthSite getNeighbour(int iHexa, int jHexa, int neighbour) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getiHexa(double xCart, double yCart) {
    return (int) xCart;
  }

  @Override
  public int getjHexa(double yCart) {
    return (int) yCart;
  }

  @Override
  public AbstractGrowthSite getCentralAtom() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2);
    return (BdaAgSurfaceSite) getSite(iCentre, jCentre, 0);
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI() / 2, getHexaSizeJ() / 2);
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
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ();
  }

  @Override
  public void changeOccupationByHand(double xMouse, double yMouse, int scale) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double extract(AbstractSurfaceSite m) {
    BdaMoleculeSite molecule = (BdaMoleculeSite) m;
    moleculeList.remove(molecule);
    return 0;
  }

  @Override
  public int getAvailableDistance(AbstractGrowthSite atom, int thresholdDistance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthSite getFarSite(AbstractGrowthSite atom, int distance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  private BdaAgSurfaceSite[][] createAgUcSurface() {
    BdaAgSurfaceSite[][] sites;
    sites = new BdaAgSurfaceSite[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        sites[i][j] = new BdaAgSurfaceSite(createId(i, j), (short) i, (short) j);
        agUcArray[i][j] = new BdaSurfaceUc(i, j, sites[i][j]);
        agUcArray[i][j].setPosX(getCartX(i, j));
        agUcArray[i][j].setPosY(getCartY(j));
        //sites[i][j].setCartesianPosition(agUcArray[i][j].getPos());
        sites[i][j].setCartesianPosition(new Point3D(getCartX(i, j),getCartY(j),0 ));
       }
    }//Interconect sites
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current site
        BdaAgSurfaceSite site = (BdaAgSurfaceSite) sites[iHexa][jHexa];
        BdaSurfaceUc uc = agUcArray[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 0);
        uc.setNeighbour(agUcArray[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 1);
        uc.setNeighbour(agUcArray[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 2);
        uc.setNeighbour(agUcArray[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 3);
        uc.setNeighbour(agUcArray[i][j], 3);
      }
    }
    return sites;
  }  
  
  /*private void setAgSurface(BdaAgSurfaceSite[][] sites) {
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        AbstractGrowthSite site = sites[i][j];
        agUcArray[i][j] = new BdaSurfaceUc(i, j, site);

        agUcArray[i][j].setPosX(getCartX(i, j));
        agUcArray[i][j].setPosY(getCartY(j));
        sites[i][j].setCartesianPosition(agUcArray[i][j].getPos());
      }
    }
  }//*/
  
  @Override
  public BdaSurfaceUc getUc(int pos) {
    int j = floorDiv(pos, getHexaSizeI());
    int i = pos - (j * getHexaSizeI());

    return agUcArray[i][j];
  }
}
