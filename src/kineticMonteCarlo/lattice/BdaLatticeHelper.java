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

import java.util.HashSet;
import java.util.Set;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
class BdaLatticeHelper {

  private final int[] alphaTravelling = {3, 3, 2, 1, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 2, 2, 3, 0, 0};

  /**
   * Reserve or release the space for current BDA molecule.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaMoleculeUc bdaUc = ((BdaAgSurfaceSite) origin.getSite(0)).getBdaUc();
    BdaSurfaceUc neighbourAgUc = origin;
    for (int i = 0; i < alphaTravelling.length; i++) {
      neighbourAgUc = neighbourAgUc.getNeighbour(getNeighbourIndex(i, bdaUc.isRotated()));
      if (makeAvailable) {
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).setBelongingBdaUc(bdaUc);
      }
      neighbourAgUc.setAvailable(ADSORPTION, makeAvailable);
      if (i < 10) {
        neighbourAgUc.setAvailable(DIFFUSION, makeAvailable);
        neighbourAgUc.setAvailable(ROTATION, makeAvailable);
      }
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
    modifiedSites.add(site);
    for (int i = 1; i < alphaTravelling.length; i++) {
      site = site.getNeighbour(getNeighbourIndex(origin, i));
      modifiedSites.add(site);
    }
    return modifiedSites;
  }
  
  private int getNeighbourIndex(BdaAgSurfaceSite agSite, int i) {
    return getNeighbourIndex(i, false);
  }//*/
    
  private int getNeighbourIndex(int i, boolean rotated) {
    if (rotated) {
      return (alphaTravelling[i] + 3) % 4;
    } else {
      return alphaTravelling[i];
    }
  }
  
  /**
   * Check surrounding sites to check if they're empty. Could happen that two
   * molecules are rotated (which are not neighbour), but touch it other.
   *
   * @param site
   * @param direction
   * @param rotated
   * @return 
   */
  AbstractGrowthSite getStartingAvailable(AbstractGrowthSite site, int direction, boolean rotated){
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
  
  /**
   * Check the possible neighbour in a certain direction
   * 
   * @param site
   * @param direction
   * @param rotated
   * @return 
   */
  AbstractGrowthSite getStartingNeighbour(AbstractGrowthSite site, int direction, boolean rotated) {
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
  
  Set<AbstractGrowthSite> getAvailableSites(AbstractGrowthSite site, int direction, boolean rotated) {
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
    Set<AbstractGrowthSite> modifiedSites = new HashSet<>();
    for (int i = 0; i < numberOfSites; i++) {
      modifiedSites.add(site);
      site = site.getNeighbour(direction);
    }
    
    return modifiedSites;
    
  }
}
