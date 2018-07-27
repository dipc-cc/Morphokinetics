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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import kineticMonteCarlo.site.BdaMoleculeSite;
import static kineticMonteCarlo.site.BdaMoleculeSite.ALPHA;
import static kineticMonteCarlo.site.BdaMoleculeSite.BETA;
import kineticMonteCarlo.site.ISite;
import static utils.MathUtils.rotateAngle;

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
  private int depositions;
  Stencil stencil;  

  public BdaLattice(int hexaSizeI, int hexaSizeJ) {
    super(hexaSizeI, hexaSizeJ, null);
    agUcArray = new BdaSurfaceUc[hexaSizeI][hexaSizeJ];
    createAgUcSurface();
    lh = new BdaLatticeHelper(hexaSizeI, hexaSizeJ, agUcArray);
    depositions = 0;
    stencil = new Stencil(hexaSizeI, hexaSizeJ);
  }
  
  public BdaSurfaceUc getAgUc(BdaAgSurfaceSite agSite) {
    return agUcArray[agSite.getiHexa()][agSite.getjHexa()];
  }
  
  @Override
  public float getCoverage() {
    return 10 * (float) getOccupied() / (float) (getHexaSizeI() * getHexaSizeJ());
  }

  @Override
  public void deposit(AbstractSurfaceSite agSite, boolean rotated) {
    deposit(agSite, rotated, null);
  }
  
  public void deposit(AbstractSurfaceSite agSite, boolean rotated, Byte type) {
    BdaAgSurfaceSite a = (BdaAgSurfaceSite) agSite;
    BdaSurfaceUc agUc = getAgUc(a);
    //if (!agUc.isAvailable(ADSORPTION))
    //  return; // should not happen. It happens in a diffusion. So, it must be allowed with rotations
    BdaMoleculeUc bdaUc = new BdaMoleculeUc(depositions, type);
    bdaUc.setRotated(rotated);
    a.setOccupied(true);
    a.setBdaUc(bdaUc);
    lh.changeAvailability(agUc, false);
    addOccupied();
    depositions++;
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
  
  public void diffuse(BdaAgSurfaceSite agSiteOrigin, BdaAgSurfaceSite agSiteDestination, int direction) {
    BdaMoleculeUc bdaUc = agSiteOrigin.getBdaUc();
    agSiteDestination.setOccupied(true);
    agSiteDestination.setBdaUc(bdaUc);
    agSiteOrigin.setOccupied(false);
    
    BdaSurfaceUc agUc = getAgUc(agSiteOrigin);
    lh.changeAvailability(agUc, bdaUc, direction);
  }
  
  public void rotate(BdaAgSurfaceSite agSiteOrigin) {
    BdaMoleculeUc bdaUc = agSiteOrigin.getBdaUc();
    boolean rotated = !agSiteOrigin.getBdaUc().isRotated();
    bdaUc.setRotated(rotated);
    
    BdaSurfaceUc agUc = getAgUc(agSiteOrigin);
    changeAvailabilityRotation(bdaUc.isShifted(), rotated, agUc);
  }
  
  /**
   * Beta molecules have to position for one rotation, shifted by 22º.
   * 
   * @param agSiteOrigin 
   */
  public void shift(BdaAgSurfaceSite agSiteOrigin) {
    BdaMoleculeUc bdaUc = agSiteOrigin.getBdaUc();
    boolean shifted = !agSiteOrigin.getBdaUc().isShifted();
    bdaUc.setShifted(shifted);
    
    BdaSurfaceUc agUc = getAgUc(agSiteOrigin);
    lh.changeAvailability(shifted, bdaUc.isRotated(), agUc);
  }
  
  private void changeAvailabilityRotation(boolean shifted, boolean rotated, BdaSurfaceUc origin) {
    int[][] rmvCoords = {{-2, 1}, {-2, 0}, {-1, 0}, {-1, 1}, {2, 0}, {2, 1}};
    int[][] addCoords = {{0, -2}, {1, -2}, {0, -1}, {1, -1}, {0, 2}, {1, 2}};
    if (shifted) {
      rmvCoords = new int[][]{{-2, 0}, {-2, 1}, {-1, 1}, {1, -1}, {2, -1}, {2, 0}};
      addCoords = new int[][]{{-1, -1}, {-1, -2}, {0, -2}, {1, 1}, {1, 2}, {0, 2}};
    }
    if (!rotated) {
      int[][] tmpCoords = rmvCoords;
      rmvCoords = addCoords;
      addCoords = tmpCoords;
    }
    int x = origin.getPosI();
    int y = origin.getPosJ();
    for (int i = 0; i < 6; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) getSite(
              stencil.getXIndex(x + rmvCoords[i][0]), stencil.getYIndex(y + rmvCoords[i][1]), 0);
      neighbour.setAvailable(DIFFUSION, true);
    }
    for (int i = 0; i < 6; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) getSite(
              stencil.getXIndex(x + addCoords[i][0]), stencil.getYIndex(y + addCoords[i][1]), 0);
      neighbour.setAvailable(DIFFUSION, false);
    }
  }
  
  /**
   * Checks certain positions and if the conditions are fulfilled neighbours are set.
   * 
   * @param origin 
   */
  public void setNeighbours(BdaAgSurfaceSite origin) {
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);
    switch (bdaSite.getType()) {
      case ALPHA:
        break; // does nothing
      case BETA:
        int[][] possibleNeighbours = {{-3,-1},{2,-2},{3,2},{-2,3},{5,-1},{-5,1}};
        if (bdaSite.isShifted())
          possibleNeighbours = new int[][]{{-3,-2},{2,-3},{3,1},{-2,2},{5,-1},{-5,1}};
        setNeighboursBeta(origin, bdaSite, possibleNeighbours);
    }
  }
  
  private void setNeighboursBeta(BdaAgSurfaceSite origin, BdaMoleculeSite bdaSite, int[][] pos) {
    boolean rotated = bdaSite.isRotated();
    boolean shifted = bdaSite.isShifted();
    int x = (int) origin.getCartesianPosition().getX();
    int y = (int) origin.getCartesianPosition().getY();
    for (int i = 0; i < pos.length; i++) {
      if (rotated) {
        pos[i] = rotateAngle(pos[i][0], pos[i][1], 90);
      }
      BdaAgSurfaceSite neighbour = lh.agArray(x + pos[i][0], y + pos[i][1]);
      if (neighbour.isOccupied()) {
        BdaMoleculeSite neighbourSite = (BdaMoleculeSite) neighbour.getBdaUc().getSite(0);
        if (neighbourSite.isRotated() != rotated) {
          continue; // only the same rotation molecules can be neighbours
        }
        if (neighbourSite.isShifted() != shifted) { // set neighbour
          origin.getBdaUc().setNeighbour(neighbour.getBdaUc(), 1);
          neighbour.getBdaUc().setNeighbour(origin.getBdaUc(), 1);
        }
      }
      if (i == 3) {
        shifted = !shifted; // the last two neighbour should have the same shift than the origin
      }
    }
  }

  /**
   * 
   * @param origin must be occupied.
   * @param direction 0-4
   * @return 
   */
  public boolean canDiffuse(BdaAgSurfaceSite origin, int direction) {
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);
    switch (bdaSite.getType()) {
      case ALPHA:
        return canDiffuseAlpha(origin, direction);
      case BETA:
        return canDiffuseBeta(origin, direction);
    }
    return false;
  }
  
  private boolean canDiffuseAlpha(BdaAgSurfaceSite origin, int direction) {
    boolean canDiffuse = true;
    boolean rotated = origin.getBdaUc().isRotated();
    AbstractGrowthSite startingSite = lh.getStartingNeighbour(origin, direction, rotated);
    Set<AbstractGrowthSite> modifiedSites = lh.getNeighbourSites(startingSite, direction, rotated);
    Iterator iter = modifiedSites.iterator();
    int i = 0;
    // Sets the neighbours
    while (iter.hasNext()) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) iter.next();
      if (neighbour.isOccupied()) {
        canDiffuse = false;
        if (rotated == neighbour.getBdaUc().isRotated()) {// set the neighbourhood
          int neighbourCode = lh.getNeighbourCode(i, direction, rotated, ALPHA);
          origin.getBdaUc().setNeighbour(neighbour.getBdaUc(), neighbourCode);
          neighbour.getBdaUc().setNeighbour(origin.getBdaUc(), (neighbourCode + 6) % 12);
        }
      }
      i++;
    }
    // Checks diffusion
    BdaMoleculeUc mUc = origin.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) mUc.getSite(0);
    byte type = bdaSite.getType();
    startingSite = lh.getStartingAvailable(origin, direction, rotated);
    modifiedSites = lh.getAvailableSites(startingSite, direction, rotated, type);
    iter = modifiedSites.iterator();
     while (iter.hasNext()) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) iter.next();
      if (!neighbour.isAvailable(DIFFUSION)){
        canDiffuse = false;
      }
    }
    return canDiffuse;
  }
  
  private boolean canDiffuseBeta(BdaAgSurfaceSite origin, int direction) {
    boolean canDiffuse = true;
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    boolean rotated = bdaUc.isRotated();
    int shifted = bdaUc.isShifted() ? 1 : 0;

    // Checks diffusion
    int[] index;
    int x = (int) origin.getCartesianPosition().getX();
    int y = (int) origin.getCartesianPosition().getY();
    stencil.init(x, y, direction, rotated, shifted);
    while (stencil.hasNext()) {
      index = stencil.getNextIndexClose();
      BdaAgSurfaceSite neighbour = lh.agArray(index[0], index[1]);
      if (!neighbour.isAvailable(DIFFUSION)) {
        canDiffuse = false;
        break;
      }
    }
    
    return canDiffuse;
  }
  
  /**
   * 
   * @param origin must be occupied.
   * @return 
   */
  public boolean canShift(BdaAgSurfaceSite origin) {
    if (origin.getBdaUc().getSite(0).getType() == ALPHA) // only beta can shift.
      return false;
    boolean canShift = true;

    int[][] addSites = {{0, -1}, {1, -1}, {2, -1}};
    int[][] rmvSites = {{1, 1}, {2, 1}};
    int[][] sites = addSites;
    if (origin.getBdaUc().isShifted()) {
      sites = rmvSites;
    }
    int x = (int) origin.getCartesianPosition().getX();
    int y = (int) origin.getCartesianPosition().getY();
    for (int i = 0; i < sites.length; i++) {
      int[] site = sites[i];
      if (origin.getBdaUc().isRotated()) {
        site = rotateAngle(site[0], site[1], 90); 
      }
      BdaAgSurfaceSite neighbour = lh.agArray(x + site[0], y + site[1]);
      if (!neighbour.isAvailable(DIFFUSION)) {
        canShift = false;
        break;
      }
    }
    return canShift;
  }
  
  public boolean canRotate(BdaAgSurfaceSite origin) {
    boolean canRotate = true;
    // General case
    List<ISite> modifiedSites = getModifiedSitesRotation(origin);
    Iterator i = modifiedSites.iterator();
    BdaAgSurfaceSite site;
    while (i.hasNext()) {
      site = (BdaAgSurfaceSite) i.next();
      if (site.getBdaSize() > 1) {
        canRotate = false;
        break;
      }
    }
    return canRotate;
  }
  
  /**
   * A BDA molecule can transform from alpha to beta if is not bulk.
   * 
   * @param origin must be occupied.
   * @return 
   */
  public boolean canTransform(BdaAgSurfaceSite origin) {
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    return bdaUc.getOccupiedNeighbours() == 0;
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
  public List<ISite> getModifiedSitesDiffusion(List<ISite> modifiedSites, AbstractGrowthSite site) {
    if (modifiedSites == null) {
      modifiedSites = new ArrayList<>();
    }
    
    boolean rotated = ((BdaAgSurfaceSite) site).getBdaUc().isRotated();
    for (int i = 0; i < 4; i++) {
      AbstractGrowthSite startingSite = lh.getStartingNeighbour(site, i, rotated);
      modifiedSites.addAll(lh.getNeighbourSites(startingSite, i, rotated));
    }
    return modifiedSites;
  }  
  
  /**
   * Adds all the neighbour positions of the rotated and not rotated central
   * position. It iterates in a spiral -5+5 positions. Thus, 11x11 positions
   * (121) around the current molecule must be free to be able to rotate.
   *
   * @param site current central site. Central Ag Unit cell site.
   * @return A list with of sites that should be recomputed their rate.
   */
  public List<ISite> getModifiedSitesRotation(AbstractGrowthSite site) {
    return site.getSpiralSites(5);
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
        sites[i][j].setCartesianPosition(new Point3D(getCartX(i, j),getCartY(j),0 ));
       }
    }//Interconect sites
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current site
        BdaAgSurfaceSite site = (BdaAgSurfaceSite) sites[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        site.setNeighbour((BdaAgSurfaceSite) sites[i][j], 3);
      }
    }
    
    //Create large neighbourhood sites
    int thresholdDistance = 5;
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current site
        BdaAgSurfaceSite originSite = (BdaAgSurfaceSite) sites[iHexa][jHexa];
        List<ISite> modifiedSites = new ArrayList<>(121);
        AbstractSurfaceSite s = originSite;
        modifiedSites.add(s);
        int possibleDistance = 0;
        int quantity;
        while (true) {
          s = s.getNeighbour(2).getNeighbour(3); // get the first neighbour
          quantity = (possibleDistance * 2 + 2);
          for (int direction = 0; direction < 4; direction++) {
            for (int j = 0; j < quantity; j++) {
              s = s.getNeighbour(direction);
              modifiedSites.add(s);
            }
          }
          possibleDistance++;
          if (possibleDistance >= thresholdDistance) {
            break;
          }
        }
        originSite.setSpiralSites(modifiedSites, thresholdDistance);
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
  
  @Override
  public void reset() {
    for (int i = 0; i < size(); i++) {
      BdaSurfaceUc agUc = getUc(i);
      for (int j = 0; j < agUc.size(); j++) {
        AbstractSurfaceSite agSite = agUc.getSite(j);
        agSite.clear();
      }
    }
    depositions = 0;
  }
}
