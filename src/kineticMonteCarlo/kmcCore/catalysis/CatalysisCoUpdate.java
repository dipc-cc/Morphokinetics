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
package kineticMonteCarlo.kmcCore.catalysis;

import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisSite.O;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoUpdate {
  
  /**
   * Checks a concrete process if can be done.
   * @param process
   * @param site
   * @return -1, 0 or +1 (delete, maintain or add the process).
   */
  public int check(int process, CatalysisSite site) {
    switch (process) {
      case 0:
        return check0(site);
      case 1:
        return check1(site);
      case 2:
        return check2(site);
      case 3:
        return check3(site);
      case 4:
        return check4(site);
      case 5:
        return check5(site);
      case 6:
        return check6(site);
      case 7:
        return check7(site);
      case 8:
        return check8(site);
      case 9:
        return check9(site);
      case 10:
        return check10(site);
      case 11:
        return check11(site);
      case 12:
        return check12(site);
      case 13:
        return check13(site);
      case 14:
        return check14(site);
      case 15:
        return check15(site);
      case 16:
        return check16(site);
      case 17:
        return check17(site);
      case 18:
        return check18(site);
      case 19:
        return check19(site);
    }
    return 0;
  }

  /**
   * CO adsorption.
   * 
   * @param site
   * @return 
   */
  private int check0(CatalysisSite site) {
    byte process = 0;
    int returnSize = 0;

    if (site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (!site.isOnList(process)) {
        returnSize = +1;
        site.setOnList(process, true);
      }
    }
    return returnSize;
  }
  
  /**
   * O adsorption
   * @param site
   * @return 
   */
  private int check1(CatalysisSite site) {
    byte process = 1;
    int returnSize = 0;
    
    if (site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (!site.isOnList(process)) {
        if (!site.isIsolated()) {
          returnSize = +1;
          site.setOnList(process, true);
        } 
      } else { // it is on list
        if (site.isIsolated()) { // no neighbour to deposit with
          returnSize = -1;
          site.setOnList(process, false);
        }
      }
    }
    return returnSize;
  }
  
  /**
   * CO^B desorption.
   * @param site
   * @return 
   */
  public int check2(CatalysisSite site) {
    byte process = 2;
    byte type = BR;
    return checkCoDesorbtion(site, process, type);
  }
  
  /**
   * CO^C desorption.
   * @param site
   * @return 
   */
  public int check3(CatalysisSite site) {
    byte process = 3;
    byte type = CUS;
    return checkCoDesorbtion(site, process, type);
  }
  
  private int checkCoDesorbtion(CatalysisSite site, byte process, byte type) {
    int returnSize = 0;
    if (!site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (site.getType() == CO && site.getLatticeSite() == type) {
        if (!site.isOnList(process)) {
          returnSize = +1;
          site.setOnList(process, true);
        }
      }
    }
    return returnSize;
  }
  
  /**
   * O^B + O^B desorption
   * @param site
   * @return 
   */
  private int check4(CatalysisSite site) {
    byte process = 4;
    byte type = BR;
    byte otherSite = BR;
    return checkO2Desorption(site, process, type, otherSite);
  }
  
  /**
   * O^B + O^C desorption
   * @param site
   * @return 
   */
  private int check5(CatalysisSite site) {
    byte process = 5;
    byte type = BR;
    byte otherSite = CUS;
    return checkO2Desorption(site, process, type, otherSite);
  }
  
  /**
   * O^C + O^B desorption
   * @param site
   * @return 
   */
  private int check6(CatalysisSite site) {
    byte process = 6;
    byte type = CUS;
    byte otherSite = BR;
    return checkO2Desorption(site, process, type, otherSite);
  }
  
  /**
   * O^C + O^C desorption
   * @param site
   * @return 
   */
  private int check7(CatalysisSite site) {
    byte process = 7;
    byte type = CUS;
    byte otherSite = CUS;
    return checkO2Desorption(site, process, type, otherSite);
  }
  
  private int checkO2Desorption(CatalysisSite site, byte process, byte type, byte otherSite) {
    int returnSize = 0;
    if (!site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (site.getType() == O && site.getLatticeSite() == type) {
        int counter = 0;
        for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
          CatalysisSite neighbour = site.getNeighbour(i);
          if (neighbour.isOccupied()
                  && neighbour.getType() == O
                  && neighbour.getLatticeSite() == otherSite) {
            counter++;
            if (!site.isOnList(process)) {
              returnSize = +1;
              site.setOnList(process, true);
            }
          }
        }
        if (counter == 0 && site.isOnList(process)) {
          returnSize = -1;
          site.setOnList(process, false);
        }
      }
    }
    return returnSize;
  }
  
  /**
   * CO^B + O^B reaction
   * @param site
   * @return 
   */
  private int check8(CatalysisSite site) {
    byte process = 8;
    byte type = BR;
    byte otherSite = BR;
    return checkReaction(site, process, type, otherSite);
  }
  
  /**
   * CO^B + O^C reaction
   * @param site
   * @return 
   */
  private int check9(CatalysisSite site) {
    byte process = 9;
    byte type = BR;
    byte otherSite = CUS;
    return checkReaction(site, process, type, otherSite);
  }
  
  /**
   * CO^C + O^B reaction
   * @param site
   * @return 
   */
  private int check10(CatalysisSite site) {
    byte process = 10;
    byte type = CUS;
    byte otherSite = BR;
    return checkReaction(site, process, type, otherSite);
  }
  
  /**
   * CO^C + O^C reaction
   * @param site
   * @return 
   */
  private int check11(CatalysisSite site) {
    byte process = 11;
    byte type = CUS;
    byte otherSite = CUS;
    return checkReaction(site, process, type, otherSite);
  }
  
  private int checkReaction(CatalysisSite site, byte process, byte type, byte otherSite) {      
    int returnSize = 0;
    if (!site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (site.getType() == CO && site.getLatticeSite() == type) {
        int counter = 0;
        for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
          CatalysisSite neighbour = site.getNeighbour(i);
          if (neighbour.isOccupied()
                  && neighbour.getType() == O
                  && neighbour.getLatticeSite() == otherSite) {
            counter++;
            if (!site.isOnList(process)) {
              returnSize = +1;
              site.setOnList(process, true);
            }
          }
        }
        if (counter == 0 && site.isOnList(process)) {
          returnSize = -1;
          site.setOnList(process, false);
        }
      }
    }
    return returnSize;
  }
  
  /**
   * CO^B -> CO^B
   * @param site
   * @return 
   */
  private int check12(CatalysisSite site) {
    byte process = 12;
    byte type = CO;
    byte siteType = BR;
    byte otherSite = BR;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * CO^B -> CO^C
   * @param site
   * @return 
   */
  private int check13(CatalysisSite site) {
    byte process = 13;
    byte type = CO;
    byte siteType = BR;
    byte otherSite = CUS;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * CO^C -> CO^B
   * @param site
   * @return 
   */
  private int check14(CatalysisSite site) {
    byte process = 14;
    byte type = CO;
    byte siteType = CUS;
    byte otherSite = BR;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * CO^C -> CO^C
   * @param site
   * @return 
   */
  private int check15(CatalysisSite site) {
    byte process = 15;
    byte type = CO;
    byte siteType = CUS;
    byte otherSite = CUS;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * O^B -> O^B
   * @param site
   * @return 
   */
  private int check16(CatalysisSite site) {
    byte process = 16;
    byte type = O;
    byte siteType = BR;
    byte otherSite = BR;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * O^B -> O^C
   * @param site
   * @return 
   */
  private int check17(CatalysisSite site) {
    byte process = 17;
    byte type = O;
    byte siteType = BR;
    byte otherSite = CUS;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * O^C -> O^B
   * @param site
   * @return 
   */
  private int check18(CatalysisSite site) {
    byte process = 18;
    byte type = O;
    byte siteType = CUS;
    byte otherSite = BR;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
  
  /**
   * O^C -> O^C
   * @param site
   * @return 
   */
  private int check19(CatalysisSite site) {
    byte process = 19;
    byte type = O;
    byte siteType = CUS;
    byte otherSite = CUS;
    return checkDiffusion(site, process, type, siteType, otherSite);
  }
   
  private int checkDiffusion(CatalysisSite site, byte process, byte type, byte siteType, byte otherSite) {      
   int returnSize = 0;
    if (!site.isOccupied()) {
      if (site.isOnList(process)) {
        returnSize = -1;
        site.setOnList(process, false);
      }
    } else {
      if (site.getType() == type && site.getLatticeSite() == siteType) {
        int counter = 0;
        for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
          CatalysisSite neighbour = site.getNeighbour(i);
          if (!neighbour.isOccupied()
                  && neighbour.getLatticeSite() == otherSite) {
            counter++;
            if (!site.isOnList(process)) {
              returnSize = +1;
              site.setOnList(process, true);
            }
          }
        }
        if (counter == 0 && site.isOnList(process)) {
          returnSize = -1;
          site.setOnList(process, false);
        }
      }
    }
    return returnSize;
  }
}
