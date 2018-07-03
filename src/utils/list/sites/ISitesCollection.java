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

import java.util.Iterator;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public interface ISitesCollection<T extends Comparable<T>> {

  public void insert(T site);

  public void addRate(T site);
  
  public void remove(T site);

  /**
   * Removes site's rate, with its old desorption rate and sets to zero.
   * 
   * @param site
   */
  public void removeAtomRate(T site);

  public void updateRate(T site, double diff);

  public void populate();

  public double getTotalRate(byte process);
  
  public void recomputeTotalRate(byte process);
  
  /**
   * Used to recompute the collection.
   */
  public void clear();
  
  /**
   * Used to empty the collection.
   */
  public void reset();
  
  /**
   * Chooses a random site or island.
   * @return site/island.
   */
  public T randomElement();
  
  public Iterator<T> iterator();
  
  public int size();
  
  public boolean isEmpty();
  
  public T search(T site);
}
