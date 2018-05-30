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
import java.util.Iterator;
import java.util.LinkedHashSet;
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
   * Reserve or release the space for current BDA molecule.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaAgSurfaceSite agSite = ((BdaAgSurfaceSite) origin.getSite(0));
    BdaMoleculeUc bdaUc = agSite.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);
    BdaSurfaceUc neighbourAgUc = origin;
    for (int i = 0; i < 11;
     //       alphaTravelling.length;
    i++) {
      neighbourAgUc = neighbourAgUc.getNeighbour(getNeighbourIndex(bdaSite, i));
      if (makeAvailable) {
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).setBelongingBdaUc(bdaUc);
      }
      neighbourAgUc.setAvailable(ADSORPTION, makeAvailable);
      if (i < 10) { // Beta 11
        neighbourAgUc.setAvailable(DIFFUSION, makeAvailable);
        neighbourAgUc.setAvailable(ROTATION, makeAvailable);
      }
    }
    Set<BdaSurfaceUc> modifiedSites = (Set<BdaSurfaceUc>) getSpiralSites((T) origin, 4);
    Iterator iter = modifiedSites.iterator();
    while (iter.hasNext()) { // increasing the radious that forbids to adsorb
      neighbourAgUc = (BdaSurfaceUc) iter.next();
      if (makeAvailable) {
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).removeBdaUc(bdaUc);
      }
      if (!makeAvailable) { // can not adsorb at this position.
        ((BdaAgSurfaceSite) neighbourAgUc.getSite(0)).setBelongingBdaUc(bdaUc);
      }
      neighbourAgUc.setAvailable(ADSORPTION, makeAvailable);
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
   * Adds all the neighbour positions of the rotated and not rotated central
   * position. It iterates in a spiral -5+5 positions. Thus, 11x11 positions
   * (121) around the current molecule must be free to be able to rotate.
   *
   * @param site central Ag Unit cell site.
   * @return all positions to be checked.
   */
  Set<AbstractGrowthSite> getRotationSites(AbstractGrowthSite site) {
    return (Set<AbstractGrowthSite>) getSpiralSites((T) site, 5);
  }
  
  /**
   * It iterates in a spiral from given central site. 
   *
   * @param site central Ag site.
   * @param thresholdDistance how big the square radius have to be. The side of
   * the square is the double of this number + 1.
   * @return all positions to be checked.
   */
  private Set<T> getSpiralSites(T site, int thresholdDistance) {
    Set<T> modifiedSites = new HashSet<>();
    ISite s = (ISite) site;
    modifiedSites.add(site);
    int possibleDistance = 0;
    int quantity;
    while (true) {
      s = s.getNeighbour(2).getNeighbour(3); // get the first neighbour
      quantity = (possibleDistance * 2 + 2);
      for (int direction = 0; direction < 4; direction++) {
        for (int j = 0; j < quantity; j++) {
          s = s.getNeighbour(direction);
          //getAgUc((BdaAgSurfaceSite) site).setAvailable(DIFFUSION, true);
          //getAgUc((BdaAgSurfaceSite) site).setAvailable(DIFFUSION, false);
          modifiedSites.add((T) s);
        }
      }
      possibleDistance++;
      if (possibleDistance >= thresholdDistance) {
        break;
      }
    }
    return modifiedSites;
  }

  int getNeighbourCode(int pos, int direction, boolean rotated, int type) {
    int upDown = -1;
    switch (type) {
      case ALPHA:
        upDown = 4;
      case BETA:
        upDown = 6;
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