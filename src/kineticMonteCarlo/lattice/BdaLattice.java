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
import static kineticMonteCarlo.process.BdaProcess.ROTATION;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaLattice extends AbstractGrowthLattice {
  /**
   * Unit cell array, where all the surface Ag atoms are located.
   */
  private final BdaSurfaceUc[][] agUcArray;
  private final BdaLatticeHelper lh; 
  

  public BdaLattice(int hexaSizeI, int hexaSizeJ) {
    super(hexaSizeI, hexaSizeJ, null);
    agUcArray = new BdaSurfaceUc[hexaSizeI][hexaSizeJ];
    createAgUcSurface();
    lh = new BdaLatticeHelper();
  }

  @Override
  public void deposit(AbstractSurfaceSite agSite, boolean rotated) {
    BdaAgSurfaceSite a = (BdaAgSurfaceSite) agSite;
    BdaSurfaceUc agUc = getAgUc(a);
    //if (!agUc.isAvailable(ADSORPTION))
    //  return; // should not happen. It happens in a diffusion. So, it must be allowed with rotations
    BdaMoleculeUc bdaUc = new BdaMoleculeUc();
    bdaUc.setRotated(rotated);
    agUc.getSite(0).setOccupied(true);
    ((BdaAgSurfaceSite) agUc.getSite(0)).setBdaUc(bdaUc);
    lh.changeAvailability(agUc, false);
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
    lh.changeAvailability(agUc, true);
    subtractOccupied();
    return 0;
  }

  @Override
  public float getCoverage() {
    return 10 * (float) getOccupied() / (float) (getHexaSizeI() * getHexaSizeJ());
  }  
  
  /**
   * 
   * @param origin must be occupied.
   * @param direction 0-4
   * @return 
   */
  public boolean canDiffuse(BdaAgSurfaceSite origin, int direction) {
    boolean canDiffuse = true;
    boolean rotated = origin.getBdaUc().isRotated();
    AbstractGrowthSite startingSite = lh.getStartingNeighbour(origin, direction, rotated);
    Set<AbstractGrowthSite> modifiedSites = lh.getNeighbourSites(startingSite, direction, rotated);
    Iterator iter = modifiedSites.iterator();
    int i = 0;
    while (iter.hasNext()) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) iter.next();
      if (neighbour.isOccupied()) {
        canDiffuse = false;
        if (rotated == neighbour.getBdaUc().isRotated()) {// set the neighbourhood
          int neighbourCode = getNeighbourCode(i, direction, rotated);
          origin.getBdaUc().setNeighbour(neighbour.getBdaUc(), neighbourCode);
          neighbour.getBdaUc().setNeighbour(origin.getBdaUc(), (neighbourCode + 6) % 12);
        }//*/
      }
      i++;
    }
    startingSite = lh.getStartingAvailable(origin, direction, rotated);
    modifiedSites = lh.getAvailableSites(startingSite, direction, rotated);
    iter = modifiedSites.iterator();
     while (iter.hasNext()) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) iter.next();
      if (!getAgUc(neighbour).isAvailable(DIFFUSION)){
        canDiffuse = false;
      }
    }
    return canDiffuse;
  }
  
  /**
   * 
   * @param origin must be occupied.
   * @return 
   */
  /*public boolean canRotate(BdaAgSurfaceSite origin) {
    boolean canRotate = true;
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    for (int i = 0; i < bdaUc.getNumberOfNeighbours(); i++) {
      BdaMoleculeUc neighbour = bdaUc.getNeighbour(i);
      if (neighbour != null && bdaUc.getNeighbour(i).getSite(0).isOccupied())
        canRotate = false;
    }
    if (canRotate) { // check upper 2nd neighbours
      origin = (BdaAgSurfaceSite) origin.getNeighbour(0).getNeighbour(0).getNeighbour(0);
      canRotate = !origin.isOccupied();
      AbstractGrowthSite neighbour = origin;
      for (int i = 0; i < 3; i++) {
        neighbour = neighbour.getNeighbour(3);
        if (neighbour.isOccupied()) {
          canRotate = false;
        }
      }
      neighbour = origin;
      for (int i = 0; i < 3; i++) {
        neighbour = neighbour.getNeighbour(1);
        if (neighbour.isOccupied()) {
          canRotate = false;
        }
      }
    }
    return canRotate;
  }//*/
  
  /**
   * 
   * @param origin must be occupied.
   * @return 
   */
  public boolean canRotate(BdaAgSurfaceSite origin) {
    boolean canRotate = true;
    Set<AbstractGrowthSite> modifiedSites = getModifiedSitesRotation(null, origin);
    BdaMoleculeUc refBdaUc = origin.getBdaUc();
    
    Iterator i = modifiedSites.iterator();
    BdaAgSurfaceSite site;
    while (i.hasNext()) {
      site = (BdaAgSurfaceSite) i.next();
      BdaSurfaceUc agUc = getAgUc(site);
      site.getBdaUc();
      if (!agUc.isAvailable(ROTATION) && !site.hasTheSameBdaUc(refBdaUc)){
        canRotate = false;
        agUc.isAvailable(ROTATION);
        site.hasTheSameBdaUc(refBdaUc);
        break;
      }
    }
    return canRotate;
  }

  private int getNeighbourCode(int pos, int direction, boolean rotated) {
    int code = 3 * direction;
    if (rotated) { // to be checked
      direction = (direction + 1) % 4;
    }
    if (direction % 2 == 0) {
      if (pos == 4) {
        code += 1;
      }
      if (pos > 4) {
        code += 2;
      }
    } else {
      if (pos == 1) {
        code += 1;
      }
      if (pos > 1) {
        code += 2;
      }

    }
    return code;
  }

  /**
   * Adds all the neighbour positions of the rotated and not rotated central
   * position. It iterates in a spiral, and 27 positions are computed in vain.
   *
   * @param site central Ag site.
   * @return all positions to be checked.
   */
  private Set<AbstractGrowthSite> getRotationSites(AbstractGrowthSite site) {
    Set<AbstractGrowthSite> modifiedSites = new HashSet<>();
    modifiedSites.add(site);
    int possibleDistance = 0;
    int thresholdDistance = 5; 
    int quantity;
    while (true) {
      site = site.getNeighbour(2).getNeighbour(3); // get the first neighbour
      quantity = (possibleDistance * 2 + 2);
      for (int direction = 0; direction < 4; direction++) {
        for (int j = 0; j < quantity; j++) {
          site = site.getNeighbour(direction);
          //getAgUc((BdaAgSurfaceSite) site).setAvailable(DIFFUSION, true);
          //getAgUc((BdaAgSurfaceSite) site).setAvailable(DIFFUSION, false);
          modifiedSites.add(site);
        }
      }
      possibleDistance++;
      if (possibleDistance >= thresholdDistance) {
        break;
      }
    }
    return modifiedSites;
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
    return lh.getModifiedSites(modifiedSites, site);
  }
  
  /**
   * Includes all -4, +4 in main molecule axis and -1,+1 in the other axis of the current site in a
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
    
    boolean rotated = ((BdaAgSurfaceSite) site).getBdaUc().isRotated();
    for (int i = 0; i < 4; i++) {
      AbstractGrowthSite startingSite = lh.getStartingNeighbour(site, i, rotated);
      modifiedSites.addAll(lh.getNeighbourSites(startingSite, i, rotated));
    }
    return modifiedSites;
  }  
  
  /**
   * Includes all previous and after rotation positions.
   *
   * @param modifiedSites previously added sites, can be null.
   * @param site current central site.
   * @return A list with of sites that should be recomputed their rate.
   */
  public Set<AbstractGrowthSite> getModifiedSitesRotation(Set<AbstractGrowthSite> modifiedSites, AbstractGrowthSite site) {
    if (modifiedSites == null) {
      modifiedSites = new HashSet<>();
    }
    //AbstractGrowthSite startingSite = getStartingSite(site, i);
    modifiedSites.addAll(getRotationSites(site));

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
