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
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.ConcertedSite;
import kineticMonteCarlo.site.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.site.AbstractSurfaceSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Concerted6LatticeSimple extends AgUcLatticeSimple {
  
  public Concerted6LatticeSimple(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep, 2);
  }
  
  @Override 
  public void deposit(AbstractSurfaceSite a, boolean forceNucleation) {
    AbstractGrowthSite atom = (AbstractGrowthSite) a;
    atom.setOccupied(true);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      addNeighbour(atom.getNeighbour(i));
    }
    addOccupied();
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractSurfaceSite a) {
    AbstractGrowthSite atom = (AbstractGrowthSite) a;
    atom.setOccupied(false);
    double probabilityChange = atom.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      removeNeighbour(atom.getNeighbour(i));
    }

    atom.resetProbability();
    atom.setList(false);
    subtractOccupied();
    return probabilityChange;
  }
  
    
  /**
   * Includes all the first and second neighbourhood of the current site in a
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
    modifiedSites.add(site);
    // collect first and second neighbour sites
    int possibleDistance = 0;
    int thresholdDistance = 2;
    while (true) {
      site = site.getNeighbour(4); // get the first neighbour
      for (int direction = 0; direction < 6; direction++) {
        for (int j = 0; j <= possibleDistance; j++) {
          modifiedSites.add(site);
          site = site.getNeighbour(direction);
        }
      }
      possibleDistance++;
      if (possibleDistance == thresholdDistance) {
        break;
      }
    }
    return modifiedSites;
  }
  
  /**
   * Éste lo ejecutan los primeros vecinos
   *
   * @param neighbourAtom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void addNeighbour(AbstractSurfaceSite neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(1);
    byte newType = (byte) (neighbourAtom.getType() + 1);
    if (newType > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >6, which is in practice impossible");
    }
    ((ConcertedSite) neighbourAtom).addNMobile(1);

    // Always changes the type of neighbour
    neighbourAtom.setType(newType);
  }
  
  /**
   * Computes the removal of one mobile atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void removeNeighbour(AbstractSurfaceSite neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(-1);
    byte newType = (byte) (neighbourAtom.getType() - 1);
    if (newType < 0) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is <0, which is in practice impossible " + neighbourAtom + neighbourAtom.getType());
    }
    ((ConcertedSite) neighbourAtom).addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    neighbourAtom.setType(newType);
  }
}
