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
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import static java.lang.Math.floorDiv;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaLattice extends AbstractGrowthLattice {
  /**
   * Unit cell array, where all the surface Ag atoms are located.
   */
  private final BdaSurfaceUc[][] agUcArray;
  private final int[] alphaTravelling = {3,3,2,1,1,0,1,2,1,0,1,2,1,0,0,3,3,3,3,3,3,3,2,2,3,0,0};
  

  public BdaLattice(int hexaSizeI, int hexaSizeJ) {
    super(hexaSizeI, hexaSizeJ, null);
    agUcArray = new BdaSurfaceUc[hexaSizeI][hexaSizeJ];
    createAgUcSurface();
  }

  @Override
  public void deposit(AbstractSurfaceSite agSite, boolean forceNucleation) {
    BdaAgSurfaceSite a = (BdaAgSurfaceSite) agSite;
    BdaSurfaceUc agUc = getAgUc(a);
    if (!agUc.isAvailable(ADSORPTION))
      return; // should not happen
    BdaMoleculeUc bdaUc = new BdaMoleculeUc();
    agUc.getSite(0).setOccupied(true);
    ((BdaAgSurfaceSite) agUc.getSite(0)).setBdaUc(bdaUc);
    changeAvailability(agUc, false);
    addOccupied();
  }
  
  public BdaSurfaceUc getAgUc(BdaAgSurfaceSite agSite) {
    return agUcArray[agSite.getiHexa()][agSite.getjHexa()];
  }
  
  @Override
  public double extract(AbstractSurfaceSite m) {
    BdaAgSurfaceSite agSite = (BdaAgSurfaceSite) m;
    BdaSurfaceUc agUc = getAgUc(agSite);
    agUc.getSite(0).setOccupied(false);
    changeAvailability(agUc, true);
    subtractOccupied();
    return 0;
  }

  @Override
  public float getCoverage() {
    return 10 * (float) getOccupied() / (float) (getHexaSizeI() * getHexaSizeJ());
  }
  
  /**
   * Reserve or release the space for current BDA molecule.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  private void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaMoleculeUc bdaUc = ((BdaAgSurfaceSite) origin.getSite(0)).getBdaUc();
    BdaSurfaceUc neighbourAgUc = origin;
    for (int i = 0; i < alphaTravelling.length; i++) {
      neighbourAgUc = neighbourAgUc.getNeighbour(getNeighbourIndex(i));
      if (makeAvailable) {
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).setBdaUc(bdaUc);
      }
      neighbourAgUc.setAvailable(ADSORPTION, makeAvailable);
      if (i < 10) {
        neighbourAgUc.setAvailable(DIFFUSION, makeAvailable);
      }
    }
  }
  
  private AbstractGrowthSite getStartingSite(AbstractGrowthSite site, int direction) {
    AbstractGrowthSite startingSite;
    switch (direction) {
      case 0:
        startingSite = site.getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(0).getNeighbour(0);
        break;
      case 1:
        startingSite = site.getNeighbour(1).getNeighbour(1).getNeighbour(1).getNeighbour(1).getNeighbour(1).getNeighbour(0);
        break;
      case 2:
        startingSite = site.getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(2).getNeighbour(2);
        break;
      case 3:
        startingSite = site.getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(3).getNeighbour(0);
        break;
      default:
        throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
    }
    return startingSite;
  }
  
  /**
   * 
   * @param origin must be occupied.
   * @param direction 0-4
   * @return 
   */
  public boolean canDiffuse(BdaAgSurfaceSite origin, int direction) {
    boolean canDiffuse = true;
    AbstractGrowthSite startingSite = getStartingSite(origin, direction);
    Set<AbstractGrowthSite> modifiedSites = getDiffusionSites(startingSite, direction);
    Iterator iter = modifiedSites.iterator();
    while (iter.hasNext()) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) iter.next();
      if (neighbour.isOccupied()) {
        canDiffuse = false;
        // set the neighbourhood
        int neighbourCode = getNeighbourCode(origin, neighbour, direction);
        origin.getBdaUc().setNeighbour(neighbour.getBdaUc(), neighbourCode);
        neighbour.getBdaUc().setNeighbour(origin.getBdaUc(), (neighbourCode + 6) % 12);
      }
    }
    return canDiffuse;
  }
  
  /**
   * 
   * @param origin must be occupied.
   * @return 
   */
  public boolean canRotate(BdaAgSurfaceSite origin) {
    boolean canRotate = true;
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    for (int i = 0; i < bdaUc.getNumberOfNeighbours(); i++) {
      BdaMoleculeUc neighbour = bdaUc.getNeighbour(i);
      if (neighbour != null && bdaUc.getNeighbour(i).getSite(0).isOccupied())
        canRotate = false;
    }
    return canRotate;
  }
 
  // it doesn't consider the periodicity!!!
  private int getNeighbourCode(BdaAgSurfaceSite origin, BdaAgSurfaceSite neighbour, int direction) {
    int x1 = getAgUc(origin).getPosI();
    int y1 = getAgUc(origin).getPosJ();
    int x2 = getAgUc(neighbour).getPosI();
    int y2 = getAgUc(neighbour).getPosJ();
    switch (direction){
      case 0:
        if (x1 >  x2) return 0;
        if (x1 == x2) return 1;
        if (x1 <  x2) return 2;
      case 1:
        if (y1 >  y2) return 3;
        if (y1 == y2) return 4;
        if (y1 <  y2) return 5;
      case 2:
        if (x1 <  x2) return 6;
        if (x1 == x2) return 7;
        if (x1 >  x2) return 8;
      case 3:
        if (y1 <  y2) return 9;
        if (y1 == y2) return 10;
        if (y1 >  y2) return 11;
    }    
    return -1;
  }
  
  private Set<AbstractGrowthSite> getDiffusionSites(AbstractGrowthSite site, int direction) {
    Set<AbstractGrowthSite> modifiedSites = new HashSet<>();
    switch (direction) {
      case 0:
      case 2:
        for (int i = 0; i < 9; i++) {
          modifiedSites.add(site);
          site = site.getNeighbour(1);
        }
        break;
      case 1:
      case 3:
        for (int i = 0; i < 3; i++) {
          modifiedSites.add(site);
          site = site.getNeighbour(2);
        }
        break;
      default:
        throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
    }
    
    return modifiedSites;
  }
  

  private int getNeighbourIndex(int i) {
    /*BdaMoleculeSite mSite = (BdaMoleculeSite) origin.getSite(0);
    if (mSite.isRotated()) {
      return (alphaTravelling[i] + 3) % 4;
    } else {
      return alphaTravelling[i];
    }//*/
    return alphaTravelling[i];
  }
  
  private int getNeighbourIndex(BdaAgSurfaceSite agSite, int i) {
    return getNeighbourIndex(i);
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
  
  /**
   * Includes all -5, +5 in main molecule axis and -2,+2 in the other axis of the current site in a
   * list without repeated elements.
   *
   * @param modifiedSites previously added sites, can be null.
   * @param site current central site.
   * @return A list with of sites that should be recomputed their rate.
   */
  public Set<AbstractGrowthSite> getModifiedSitesDiffusion(Set<AbstractGrowthSite> modifiedSites, AbstractGrowthSite site) {
    if (modifiedSites == null) {
      modifiedSites = new HashSet<>();
    }
    for (int i = 0; i < 4; i++) {
      AbstractGrowthSite startingSite = getStartingSite(site, i);
      modifiedSites.addAll(getDiffusionSites(startingSite, i));
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
  
  @Override
  public BdaSurfaceUc getUc(int pos) {
    int j = floorDiv(pos, getHexaSizeI());
    int i = pos - (j * getHexaSizeI());

    return agUcArray[i][j];
  }
}
