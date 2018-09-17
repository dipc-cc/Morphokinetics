/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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

import kineticMonteCarlo.site.BasicSite;
import kineticMonteCarlo.unitCell.IUc;
import kineticMonteCarlo.unitCell.Simple3dUc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BasicLattice extends AbstractLattice {

  private final BasicSite[] sites;
  private final Simple3dUc[] ucList;
  
  public BasicLattice(int hexaSizeI, int hexaSizeJ) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(1);
    
    sites = new BasicSite[hexaSizeI * hexaSizeJ];
    ucList = new Simple3dUc[hexaSizeI * hexaSizeJ];
    generateSites(hexaSizeI, hexaSizeJ);
    interconnectSites();
  }

  @Override
  public BasicSite getSite(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    return sites[((jHexa) * getHexaSizeI() + iHexa) * getUnitCellSize() + unitCellPos];
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < sites.length; i++) {
      sites[i].setProbabilities(probabilities);
    }
  }

  @Override
  public IUc getUc(int pos) {
    return ucList[pos];
  }
  
  @Override
  public void reset() {
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        sites[i * getHexaSizeI() + j].setList(null);
        sites[i * getHexaSizeI() + j].unRemove();
      }
    }

    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        sites[i * getHexaSizeI() + j].updateN1FromScratch();
      }
    }

    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        if (i < 4) {
          sites[i * getHexaSizeI() + j].remove();
        }
      }
    }
  }

  private void generateSites(int hexaSizeI, int hexaSizeJ) {
    for (short i = 0; i < hexaSizeJ; i++) {
      for (short j = 0; j < hexaSizeI; j++) {
        sites[i * hexaSizeI + j] = new BasicSite(j, i);
        ucList[i * hexaSizeI + j] = new Simple3dUc(j, i, sites[i * hexaSizeI + j]);
      }
    }
  }

  private void interconnectSites() {
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        if (i - 1 >= 0) {
          sites[i * getHexaSizeI() + j].setNeighbour(sites[(i - 1) * getHexaSizeI() + j], 0);                      //up        
        }
        sites[i * getHexaSizeI() + j].setNeighbour(sites[Math.min(i + 1, getHexaSizeJ() - 1) * getHexaSizeI() + j], 1); //down        
        int izq = j - 1;
        if (izq < 0) {
          izq = getHexaSizeI() - 1;
        }
        sites[i * getHexaSizeI() + j].setNeighbour(sites[i * getHexaSizeI() + izq], 2);                   //left        
        sites[i * getHexaSizeI() + j].setNeighbour(sites[i * getHexaSizeI() + ((j + 1) % getHexaSizeI())], 3); //right       
      }
    }
  }

}
