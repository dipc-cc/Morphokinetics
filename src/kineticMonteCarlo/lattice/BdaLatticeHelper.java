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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.site.BdaMoleculeSite;
import static kineticMonteCarlo.site.BdaMoleculeSite.ALPHA;
import static kineticMonteCarlo.site.BdaMoleculeSite.BETA;
import kineticMonteCarlo.site.ISite;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;

/**
 *  
 * @author J. Alberdi-Rodriguez
 */
class BdaLatticeHelper<T> {

  private final int[] alphaTravelling = {3, 3, 2, 1, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 2, 2, 3, 0, 0};
  private final int[] beta2Travelling = {3, 3, 2, 1, 1, 0, 0, 1, 1, 2, 3};

  /**
   * It only changes the availability of the required positions: upper/lower
   * bound for 0/2 directions and left/right for 1/3 directions.
   *
   * @param origin
   * @param bdaUc
   * @param direction 
   */
  List changeAvailability(BdaSurfaceUc origin, BdaMoleculeUc bdaUc, int direction) {
    BdaAgSurfaceSite originAgSite = (BdaAgSurfaceSite) origin.getSite(0);
    List<ISite> modifiedSites = new ArrayList<>();
    modifiedSites.add(origin.getSite(0)); // add origin
    modifiedSites.add(origin.getSite(0).getNeighbour(direction)); // just in case, add destination too
    List<ISite> allNeighbour = originAgSite.getSpiralSites(5);
    int[] affectedSitesOccAdd = null;
    int[] affectedSitesOccRem = null;
    int[] affectedSitesSurAdd = new int[9];
    int[] affectedSitesSurRem = new int[9];
    switch (direction) {
      case 0:
        affectedSitesOccAdd = new int[]{11,2,3,4,17};
        affectedSitesOccRem = new int[]{9,8,7,6,19};
        for (int i = 0; i < affectedSitesSurAdd.length; i++) {
          affectedSitesSurAdd[i] = i + 91;
          affectedSitesSurRem[i] = i + 72;
        }
        break;
      case 1:
        affectedSitesOccAdd = new int[]{39,40};
        affectedSitesOccRem = new int[]{9,10};
        for (int i = 0; i < affectedSitesSurAdd.length; i++) {
          affectedSitesSurAdd[i] = i + 101;
          affectedSitesSurRem[i] = i + 49;
        }
        affectedSitesSurRem[8] = 80;
        break;
      case 2:
        affectedSitesOccAdd = new int[]{24,23,22,21,20};
        affectedSitesOccRem = new int[]{10,1,0,5,18};
        for (int i = 0; i < affectedSitesSurAdd.length; i++) {
          affectedSitesSurAdd[i] = i + 111;
          affectedSitesSurRem[i] = i + 56;
        }
        break;
      case 3:
        affectedSitesOccAdd = new int[]{26,27};
        affectedSitesOccRem = new int[]{18,19};
        for (int i = 0; i < affectedSitesSurAdd.length; i++) {
          affectedSitesSurAdd[i] = i + 81;
          affectedSitesSurRem[i] = i + 64;
        }
        break;
      default:
        System.out.println("Error!!");
    }
    for (int i = 0; i < affectedSitesOccAdd.length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSitesOccAdd[i]);
      modifiedSites.add(neighbour);
      neighbour.setAvailable(DIFFUSION, false);
    }
    for (int i = 0; i < affectedSitesOccRem.length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSitesOccRem[i]);
      modifiedSites.add(neighbour);
      neighbour.setAvailable(DIFFUSION, true);
    }
    for (int i = 0; i < affectedSitesSurAdd.length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSitesSurAdd[i]);
      modifiedSites.add(neighbour);
      neighbour.setAvailable(ADSORPTION, false);
      neighbour.setBelongingBdaUc(bdaUc);
    }
    for (int i = 0; i < affectedSitesSurRem.length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSitesSurRem[i]);
      modifiedSites.add(neighbour);
      neighbour.removeBdaUc(bdaUc);
      neighbour.setAvailable(ADSORPTION, true);
    }
    return modifiedSites;
  }
  
  /**
   * Reserve or release the space for current BDA molecule.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaAgSurfaceSite agSite = ((BdaAgSurfaceSite) origin.getSite(0));
    BdaMoleculeUc bdaUc = agSite.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);
    BdaAgSurfaceSite neighbourSite = (BdaAgSurfaceSite) origin.getSite(0);
    for (int i = 0; i < 11;
     //       alphaTravelling.length;
    i++) {
      neighbourSite = (BdaAgSurfaceSite) neighbourSite.getNeighbour(getNeighbourIndex(bdaSite, i));
      if (makeAvailable) {
        neighbourSite.removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        neighbourSite.setBelongingBdaUc(bdaUc);
      }
      neighbourSite.setAvailable(ADSORPTION, makeAvailable);
      if (i < 10) { // Beta 11
        neighbourSite.setAvailable(DIFFUSION, makeAvailable);
        neighbourSite.setAvailable(ROTATION, makeAvailable);
      }
    }
    List<BdaAgSurfaceSite> modifiedSites = (List<BdaAgSurfaceSite>) getSpiralSites((T) agSite, 4);
    Iterator iter = modifiedSites.iterator();
    while (iter.hasNext()) { // increasing the radius that forbids to adsorb
      neighbourSite = (BdaAgSurfaceSite) iter.next();
      if (makeAvailable) {
        neighbourSite.removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        neighbourSite.setBelongingBdaUc(bdaUc);
      }
      neighbourSite.setAvailable(ADSORPTION, makeAvailable);
    }
  }
  
  /**
   * Includes all -2, +2 in main molecule axis and -1,+1 in the other axis of
   * the current site in a list without repeated elements.
   *
   * @param modifiedSites previously added sites, can be null.
   * @param site current central site.
   * @return A list with of sites that should be recomputed their rate.
   */
  public Set<AbstractGrowthSite> getModifiedSites(Set<AbstractGrowthSite> modifiedSites, AbstractGrowthSite site) {
    if (modifiedSites == null) {
      modifiedSites = new HashSet<>();
    }
    BdaAgSurfaceSite origin = (BdaAgSurfaceSite) site;
    site = origin.getNeighbour(alphaTravelling[0]);
    BdaMoleculeUc bdaUc = origin.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);

    modifiedSites.add(site);
    for (int i = 1; i < alphaTravelling.length; i++) {
      site = site.getNeighbour(getNeighbourIndex(bdaSite, i));
      modifiedSites.add(site);
    }
    return modifiedSites;
  }
  
  private int getNeighbourIndex(BdaMoleculeSite bdaSite, int i) {
    switch (bdaSite.getType()) {
      case ALPHA:
        if (bdaSite.isRotated()) {
          return (alphaTravelling[i] + 3) % 4;
        } else {
          return alphaTravelling[i];
        }
      case BETA:
        if (bdaSite.isRotated()) {
          return beta2Travelling[i];
        } else {
          return alphaTravelling[i];
        }
    }

    return -1; // should not come to here
  }
  
  AbstractGrowthSite getStartingAvailable(AbstractGrowthSite site, int direction, boolean rotated){
    BdaMoleculeUc mUc = ((BdaAgSurfaceSite) site).getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) mUc.getSite(0);
    switch (bdaSite.getType()) {
      case ALPHA:
        return getStartingAvailableAlpha(site, direction, rotated);
      case BETA:
        return getStartingAvailableBeta(site, direction, rotated);
    }
    return null;
  }
  
  /**
   * Check surrounding sites to check if they're empty. Could happen that two
   * molecules are rotated (which are not neighbours), but touch it other.
   *
   * @param site
   * @param direction
   * @param rotated
   * @return 
   */
  private AbstractGrowthSite getStartingAvailableAlpha(AbstractGrowthSite site, int direction, boolean rotated){
    int[] travel = new int[4];
    if (rotated) {
      direction = (direction + 1) % 4;
    }
    switch (direction) {
      case 0:
        travel = new int[]{3, 3, 3, 0};
        break;
      case 1:
        travel = new int[]{1, 1, 1};
        break;
      case 2:
        travel = new int[]{1, 1, 2, 2};
        break;
      case 3:
        travel = new int[]{3, 3, 3, 2};
        break;
      default:
        throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
    }
    return getStartingSite(site, travel, rotated);
  }
  
  private AbstractGrowthSite getStartingAvailableBeta(AbstractGrowthSite site, int direction, boolean rotated){
    if (!rotated) {
      return getStartingAvailableAlpha(site, direction, rotated);
    } else {
      int[] travel = new int[4];
      rotated = false;
      switch (direction) {
        case 0:
          travel = new int[]{3, 3, 0};
          break;
        case 1:
          travel = new int[]{1, 1, 1, 0};
          break;
        case 2:
          travel = new int[]{1, 1, 2};
          break;
        case 3:
          travel = new int[]{3, 3, 3, 2};
          break;
        default:
          throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
      }
      return getStartingSite(site, travel, rotated);
    }
  }
  
  /**
   * Check the possible neighbour in a certain direction
   * 
   * @param site
   * @param direction
   * @param rotated
   * @return 
   */
  AbstractGrowthSite getStartingNeighbour(AbstractGrowthSite site, int direction, boolean rotated) {
    BdaMoleculeUc mUc = ((BdaAgSurfaceSite) site).getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) mUc.getSite(0);
    switch (bdaSite.getType()) {
      case ALPHA:
        return getStartingNeighbourAlpha(site, direction, rotated);
      case BETA:
        return getStartingNeighbourBeta(site, direction, rotated);
    }
    
    return null;
  }
  
  private AbstractGrowthSite getStartingNeighbourAlpha(AbstractGrowthSite site, int direction, boolean rotated) {
    int[] travel = new int[6];
    if (rotated) {
      direction = (direction + 1) % 4;
    }
    switch (direction) {
      case 0:
        travel = new int[]{3, 3, 3, 3, 0, 0};
        break;
      case 1:
        travel = new int[]{1, 1, 1, 1, 1, 0};
        break;
      case 2:
        travel = new int[]{1, 1, 1, 1, 2, 2};
        break;
      case 3:
        travel = new int[]{3, 3, 3, 3, 3, 2};
        break;
      default:
        throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
    }
    return getStartingSite(site, travel, rotated);
  }
  
  private AbstractGrowthSite getStartingNeighbourBeta(AbstractGrowthSite site, int direction, boolean rotated) {
    int[] travel = new int[6];
    if (rotated) {
      //direction = (direction + 1) % 4;
      rotated = false;
      switch (direction) {
        case 0:
          travel = new int[]{3, 3, 3, 3, 0, 0, 0};
          //travel = new int[]{3, 3, 3, 3, 0, 0};
          break;
        case 1:
          //travel = new int[]{1, 1, 1, 1, 1, 0, 0};
          travel = new int[]{1, 1, 1, 1, 1, 0};
          break;
        case 2:
          travel = new int[]{1, 1, 1, 1, 2, 2};
          break;
        case 3:
          //travel = new int[]{3, 3, 3, 3, 3, 2, 2};
          travel = new int[]{3, 3, 3, 3, 3, 2};
          break;
        default:
          throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
      }
    } else {
      switch (direction) {
        case 0:
          travel = new int[]{3, 3, 3, 3, 0, 0};
          break;
        case 1:
          travel = new int[]{1, 1, 1, 1, 1, 0};
          break;
        case 2:
          travel = new int[]{1, 1, 1, 1, 2, 2, 2};
          break;
        case 3:
          travel = new int[]{3, 3, 3, 3, 3, 2};
          break;
        default:
          throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
      }
    }
    return getStartingSite(site, travel, rotated);
  }
  
  
  private AbstractGrowthSite getStartingSite(AbstractGrowthSite site, int[] travel, boolean rotated) {
    if (rotated) {
      for (int i = 0; i < travel.length; i++) {
        travel[i] = (travel[i] + 3) % 4;
      }
    }
    for (int i = 0; i < travel.length; i++) {
      site = site.getNeighbour(travel[i]);
    }
    return site;
  }
  
  Set<AbstractGrowthSite> getAvailableSites(AbstractGrowthSite site, int direction, boolean rotated, byte type) {
    switch (type) {
      case ALPHA:
        return getAvailableSitesAlpha(site, direction, rotated);
      case BETA:
        return getAvailableSitesBeta(site, direction, rotated);
    }
    return null;
  }
  
  
  private Set<AbstractGrowthSite> getAvailableSitesAlpha(AbstractGrowthSite site, int direction, boolean rotated) {
    int neighbourDirection = (direction + 1) % 4;
    int numberOfSites;
    if (rotated) {
      if (direction % 2 == 0) {
        numberOfSites = 2;
      } else {
        numberOfSites = 5;
      }
    } else {
      if (direction % 2 == 0) {
        numberOfSites = 5;
      } else {
        numberOfSites = 2;
      }
    }
    return getSites(site, numberOfSites, neighbourDirection);    
  }

  private Set<AbstractGrowthSite> getAvailableSitesBeta(AbstractGrowthSite site, int direction, boolean rotated) {
    int neighbourDirection = (direction + 1) % 4;
    if (rotated) {
      Set<AbstractGrowthSite> modifiedSites = new LinkedHashSet<>();
      modifiedSites.add(site);
      switch (direction) {
        case 0:
        case 2:
          for (int i = 0; i < 4; i++) {
            site = site.getNeighbour(neighbourDirection);
            modifiedSites.add(site);
            if (i == 0) { // jump the kink
              site = site.getNeighbour(direction);
            }
          }
          break;
        case 1:
        case 3:
          site = site.getNeighbour(neighbourDirection);
          modifiedSites.add(site);
          break;
        default:
          throw new IllegalArgumentException("BDA molecule can only diffuse in one of the 4 directions");
      }
      return modifiedSites;
    } else {
      return getAvailableSitesAlpha(site, direction, rotated);
    }
  }

  
  Set<AbstractGrowthSite> getNeighbourSites(AbstractGrowthSite site, int direction, boolean rotated) {
    int neighbourDirection = (direction + 1) % 4;
    int numberOfSites;
    if (rotated) {
      if (direction % 2 == 0) {
        numberOfSites = 3;
      } else {
        numberOfSites = 9;
      }
    } else {
      if (direction % 2 == 0) {
        numberOfSites = 9;
      } else {
        numberOfSites = 3;
      }
    }
    return getSites(site, numberOfSites, neighbourDirection);
  }
  
  private Set<AbstractGrowthSite> getSites(AbstractGrowthSite site, int numberOfSites, int direction) {
    Set<AbstractGrowthSite> modifiedSites = new LinkedHashSet<>();
    for (int i = 0; i < numberOfSites; i++) {
      modifiedSites.add(site);
      site = site.getNeighbour(direction);
    }
    
    return modifiedSites;
  }
  
  /**
   * It iterates in a spiral from given central site. 
   *
   * @param site central Ag site.
   * @param thresholdDistance how big the square radius have to be. The side of
   * the square is the double of this number + 1.
   * @return all positions to be checked.
   */
  private List<T> getSpiralSites(T site, int thresholdDistance) {
    ISite originSite = (ISite) site;
    return (List<T>) originSite.getSpiralSites(thresholdDistance);
  }

  int getNeighbourCode(int pos, int direction, boolean rotated, int type) {
    int upDown = -1;
    switch (type) {
      case ALPHA:
        upDown = 4;
        break;
      case BETA:
        upDown = 6;
        break;
    }
    int code = 3 * direction;
    if (rotated) { // to be checked
      direction = (direction + 1) % 4;
    }
    if (direction % 2 == 0) {
      if (pos == upDown) {
        code += 1;
      }
      if (pos > upDown) {
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
}