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
package utils.list.sites;

import java.util.ArrayList;
import java.util.Iterator;
import kineticMonteCarlo.process.IElement;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public class SitesArrayList<T extends Comparable<T>> implements ISitesCollection<T> {

  private final ArrayList<T> sitesArray;
  private double totalRate;
  /**
   * The "movements" that site can do. For example: Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;
  
  /**
   * Array List implementation for site collection storing.
   * 
   * @param process The "movements" that site can do. For example: Adsorption, desorption, reaction or diffusion.
   */
  public SitesArrayList(byte process) {
    sitesArray = new ArrayList();
    totalRate = 0.0;
    this.process = process;
  }
  
  /**
   * Current site is added. This should only be called in the initialisation. Add sites only if they
   * have a rate.
   *
   * @param site
   */
  @Override
  public void insert(T site) {
    IElement a = (IElement) site;
    a.setOnList(process, a.getRate(process) > 0);
    if (a.getRate(process) > 0) {
      add(site);
    }
  }

  /**
   * Current site is added.
   * 
   * @param site 
   */
  @Override
  public void addRate(T site) {
    add(site);
  }
  
  private void add(T site) {
    IElement a = (IElement) site;
    totalRate += a.getRate(process);
    sitesArray.add(site);
  }

  @Override
  public void remove(T site) {
    sitesArray.remove(site);
  }

  @Override
  public void removeAtomRate(T site) {
    IElement a = (IElement) site;
    totalRate -= a.getRate(process);
    a.setRate(process, 0.0);
    sitesArray.remove(site);
  }

  /**
   * Updates total rate.
   * @param site Ignored.
   * @param diff Value to be added.
   */
  @Override
  public void updateRate(T site, double diff) {
    totalRate += diff;
  }

  /**
   * Does nothing.
   */
  @Override
  public void populate() {
  }
  
  @Override
  public void recomputeTotalRate(byte process) {
    double sum = 0.0;
    for (int i = 0; i < sitesArray.size(); i++) {
      sum += ((IElement) sitesArray.get(i)).getRate(process);
    }
    totalRate = sum;
  }

  @Override
  public double getTotalRate(byte process) {
    return totalRate;
  }
  
  @Override
  public void clear() {
    sitesArray.clear();
    totalRate = 0.0;
  }

  @Override
  public void reset() {
    clear();
  }
  
  @Override
  public T randomElement() {
    T a = null;
    double randomNumber = StaticRandom.raw() * totalRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < sitesArray.size(); i++) {
      sum += ((IElement) sitesArray.get(i)).getRate(process);
      if (sum > randomNumber) {
        a = sitesArray.get(i);
        break;
      }
    }
    
    if (a == null) { // Search has failed
      // Recompute total rate and try again
      recomputeTotalRate(process);
      a = randomElement();
    }
    
    return a;
  }
  
  @Override
  public Iterator<T> iterator() {
    return sitesArray.iterator();
  }
  
  @Override
  public int size() {
    return sitesArray.size();
  }
  
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }
  
  /**
   * Return an site with the same site Id that was given from the list.
   * 
   * @param site fake site with correct Id to search for.
   * @return found site, null otherwise.
   */
  @Override
  public T search(T site) {
    Iterator<T> iter = sitesArray.iterator();
    while (iter.hasNext()) {
      T current = iter.next();
      if (current.compareTo(site) == 0) {
        return current;
      }
    }
    return null;
  }
}
