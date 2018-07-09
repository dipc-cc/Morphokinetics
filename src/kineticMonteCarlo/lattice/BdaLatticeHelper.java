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

import static java.lang.Math.log;
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
  /** [type][rotated][direction][edge][atom]. */
  private final int[][][][][] affectedSites;

  private final int hexaSizeI;
  private final int hexaSizeJ;
  /**
   * Unit cell array, where all the surface Ag atoms are located.
   */
  private final BdaAgSurfaceSite[][] agArray;

  public BdaLatticeHelper(int sizeI, int sizeJ, BdaSurfaceUc[][] agUcArray) {
    hexaSizeI = sizeI;
    hexaSizeJ = sizeJ;
    agArray = new BdaAgSurfaceSite[sizeI][sizeJ];
    for (int i = 0; i < agUcArray.length; i++) {
      for (int j = 0; j < agUcArray[0].length; j++) {
        agArray[i][j] = (BdaAgSurfaceSite) agUcArray[i][j].getSite(0);
      }
    }
    int[][][] affectedFar = new int[][][] {
      { // direction 0
        {91, 92, 93, 94, 95, 96, 97, 98, 99}, {72, 73, 74, 75, 76, 77, 78, 79, 80}},
      {// direction 1
        {101, 102, 103, 104, 105, 106, 107, 108, 109}, {49, 50, 51, 52, 53, 54, 55, 56, 80}},
      {// direction 2
        {111, 112, 113, 114, 115, 116, 117, 118, 119}, {56, 57, 58, 59, 60, 61, 62, 63, 64}},
      {// direction 3
      {81, 82, 83, 84, 85, 86, 87, 88, 89}, {64, 65, 66, 67, 68, 69, 70, 71, 72}}
    };
    
    int[][][] affectedSitesNotRotated = new int[][][]{
      // direction 0
      {{11, 2, 3, 4, 17}, {9, 8, 7, 6, 19}},
      // direction 1
      {{39, 40}, {9, 10}},
      // direction 2
      {{24, 23, 22, 21, 20}, {10, 1, 0, 5, 18}},
      // direction 3
      {{26, 27}, {18, 19}}
    };
    int[][][] affectedSitesYesRotated = new int[][][]{
      // direction 0
      {{33, 34}, {22, 21}},
      // direction 1
      {{16, 17, 18, 19, 20}, {14, 3, 0, 7, 22}},
      // direction 2
      {{44, 45}, {14, 15}},
      // direction 3
      {{13, 2, 1, 8, 23}, {15, 4, 5, 6, 21}}
    };
    
    int[][][] affectedSitesYesBetaRot = new int[][][]{
      // direction 0
      {{14, 15, 16, 11, 2}, {5, 18, 7, 8, 9}},
      // direction 1
      {{38, 39, 6}, {9, 10, 3}},
      // direction 2
      {{22, 23, 24, 6, 19}, {3, 4, 17, 10, 1}},
      // direction 3
      {{26, 27, 2}, {17, 18, 7}}};

    //int[][][] pos2 = new int[][][]{affectedSites[0],affectedSites[1],affectedSites[2],affectedSites[3]};
    affectedSites = new int[][][][][]{ //Alpha
      {// not rotated
        {// direction 0
          {affectedSitesNotRotated[0][0], affectedSitesNotRotated[0][1], affectedFar[0][0], affectedFar[0][1]},
          // direction 1
          {affectedSitesNotRotated[1][0], affectedSitesNotRotated[1][1], affectedFar[1][0], affectedFar[1][1]},
          // direction 2
          {affectedSitesNotRotated[2][0], affectedSitesNotRotated[2][1], affectedFar[2][0], affectedFar[2][1]},
          // direction 3
          {affectedSitesNotRotated[3][0], affectedSitesNotRotated[3][1], affectedFar[3][0], affectedFar[3][1]}
        }, {// direction 0
          {affectedSitesYesRotated[0][0], affectedSitesYesRotated[0][1], affectedFar[0][0], affectedFar[0][1]},
          // direction 1
          {affectedSitesYesRotated[1][0], affectedSitesYesRotated[1][1], affectedFar[1][0], affectedFar[1][1]},
          // direction 2
          {affectedSitesYesRotated[2][0], affectedSitesYesRotated[2][1], affectedFar[2][0], affectedFar[2][1]},
          // direction 3
          {affectedSitesYesRotated[3][0], affectedSitesYesRotated[3][1], affectedFar[3][0], affectedFar[3][1]}
        }
      }, { // Beta
        {// direction 0
          {affectedSitesNotRotated[0][0], affectedSitesNotRotated[0][1], affectedFar[0][0], affectedFar[0][1]},
          // direction 1
          {affectedSitesNotRotated[1][0], affectedSitesNotRotated[1][1], affectedFar[1][0], affectedFar[1][1]},
          // direction 2
          {affectedSitesNotRotated[2][0], affectedSitesNotRotated[2][1], affectedFar[2][0], affectedFar[2][1]},
          // direction 3
          {affectedSitesNotRotated[3][0], affectedSitesNotRotated[3][1], affectedFar[3][0], affectedFar[3][1]}
        }, {// direction 0
          {affectedSitesYesBetaRot[0][0], affectedSitesYesBetaRot[0][1], affectedFar[0][0], affectedFar[0][1]},
          // direction 1
          {affectedSitesYesBetaRot[1][0], affectedSitesYesBetaRot[1][1], affectedFar[1][0], affectedFar[1][1]},
          // direction 2
          {affectedSitesYesBetaRot[2][0], affectedSitesYesBetaRot[2][1], affectedFar[2][0], affectedFar[2][1]},
          // direction 3
          {affectedSitesYesBetaRot[3][0], affectedSitesYesBetaRot[3][1], affectedFar[3][0], affectedFar[3][1]}
        }
      }
    };
  }

  void changeAvailability(boolean rotated, BdaSurfaceUc origin) {
    int[][] rmvSites = {{1,1},{2,1}};//{6, 19};
    int[][] addSites = {{0,-1},{1,-1},{2,-1}};//{3,4,17};
    if (!rotated) {
      int[][] tmpSites = rmvSites;
      rmvSites = addSites;
      addSites = tmpSites;
    }
    int index0;
    int index1;
    int x = origin.getPosI();
    int y = origin.getPosJ();
    for (int[] rmvSite : rmvSites) {
      index0 = getXIndex(x + rmvSite[0]);
      index1 = getYIndex(y + rmvSite[1]);
      BdaAgSurfaceSite neighbour = agArray[index0][index1];
      neighbour.setAvailable(DIFFUSION, true);
    }
    for (int[] addSite : addSites) {
      index0 = getXIndex(x + addSite[0]);
      index1 = getYIndex(y + addSite[1]);
      BdaAgSurfaceSite neighbour = agArray[index0][index1];
      neighbour.setAvailable(DIFFUSION, false);
    }
  }
  
  /**
   * It only changes the availability of the required positions: upper/lower
   * bound for 0/2 directions and left/right for 1/3 directions. For diffusion.
   *
   * @param origin
   * @param bdaUc
   * @param direction 
   */
  void changeAvailability(BdaSurfaceUc origin, BdaMoleculeUc bdaUc, int direction) {
    BdaAgSurfaceSite originAgSite = (BdaAgSurfaceSite) origin.getSite(0);
    List<ISite> allNeighbour = originAgSite.getSpiralSites(5);
    int rotated = bdaUc.isRotated() ? 1 : 0;
    int type = (int) (log(bdaUc.getSite(0).getType()) / log(2));
    
    for (int i = 0; i < affectedSites[type][rotated][direction][0].length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSites[type][rotated][direction][0][i]);
      neighbour.setAvailable(DIFFUSION, false);
    }
    for (int i = 0; i < affectedSites[type][rotated][direction][1].length; i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) allNeighbour.get(affectedSites[type][rotated][direction][1][i]);
      neighbour.setAvailable(DIFFUSION, true);
    }
    
    // far sites
    int x = origin.getPosI();
    int y = origin.getPosJ();
    
    int sign = -1;
    int index1 = 0; // the value should not be used
    int index2 = 0; // the value should not be used
    
    if (direction % 3 == 0) {
      sign = 1;
  }
  
    // sets fixed index
    if (direction % 2 == 0) { // x travelling
      index2 = getYIndex(y + sign * 4); // y fixed
    } else { // y travelling
      index1 = getXIndex(x + sign * 4); // x fixed
    }

    for (int i = -4; i <= 4; i++) {
      if (direction % 2 == 0) {
        index1 = getXIndex(x + i);
      } else {
        index2 = getYIndex(y + i);
      }
      BdaAgSurfaceSite neighbour = agArray[index1][index2];
      neighbour.removeBdaUc(bdaUc);
      neighbour.setAvailable(ADSORPTION, true);
    }
    
    sign *= -1;
    // sets fixed index
    if (direction % 2 == 0) { // x travelling
      index2 = getYIndex(y + sign * 5);
    } else { // y travelling
      index1 = getXIndex(x + sign * 5);
    }
    for (int i = -4; i <= 4; i++) {
      if (direction % 2 == 0) {
        index1 = getXIndex(x + i);
      } else {
        index2 = getYIndex(y + i);
      }
      BdaAgSurfaceSite neighbour = agArray[index1][index2];
      neighbour.removeBdaUc(bdaUc);
      neighbour.setAvailable(ADSORPTION, false);
      neighbour.setBelongingBdaUc(bdaUc);
    }
  }
  
  private int getXIndex(int x) {
    if (x < 0) {
      return hexaSizeI + x;
    }
    if (x >= hexaSizeI) {
      return x % hexaSizeI;
    }
    return x;
  }
  
  private int getYIndex(int y) {
    if (y < 0) {
      return hexaSizeJ + y;
    }
    if (y >= hexaSizeJ) {
      return y % hexaSizeJ;
    }
    return y;
  }

  /**
   * Reserve or release the space for current BDA molecule, completely.
   * 
   * @param origin central atom (position) of the BDA molecule.
   * @param makeAvailable change availability.
   */
  void changeAvailability(BdaSurfaceUc origin, boolean makeAvailable){
    BdaAgSurfaceSite agSite = ((BdaAgSurfaceSite) origin.getSite(0));
    BdaMoleculeUc bdaUc = agSite.getBdaUc();
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) bdaUc.getSite(0);
    BdaAgSurfaceSite neighbourSite = (BdaAgSurfaceSite) origin.getSite(0);
    int diffusionAvailableLimit = bdaSite.getType() == BETA && bdaSite.isRotated() ? 11 : 10;
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
      if (i < diffusionAvailableLimit) { // Beta 11
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
        travel = new int[]{3, 3, 0};
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
          site = site.getNeighbour(neighbourDirection);
          modifiedSites.add(site);
          site = site.getNeighbour(neighbourDirection).getNeighbour(3).getNeighbour(3);
          modifiedSites.add(site);
          break;
        case 3:
          site = site.getNeighbour(neighbourDirection);
          modifiedSites.add(site);
          site = site.getNeighbour(neighbourDirection).getNeighbour(1).getNeighbour(1);
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