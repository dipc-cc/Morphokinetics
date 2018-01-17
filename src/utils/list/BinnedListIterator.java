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
package utils.list;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BinnedListIterator implements ListIterator {

  private List<ListIterator> iterators;
  private int currentIterator;

  public BinnedListIterator(BinnedList list) {
    iterators = new ArrayList();
    addBinsToList(list);
    currentIterator = 0;
  }

  private void addBinsToList(BinnedList list) {

    for (int i = 0; i < list.getBins().length; i++) {
      if (list.getLevel() > 0) {
        addBinsToList((BinnedList) list.getBins()[i]);
      } else {
        iterators.add((list.getBins()[i]).getIterator());
      }
    }
  }

  @Override
  public boolean hasNext() {

    for (int i = currentIterator; i < iterators.size(); i++) {
      if (iterators.get(i).hasNext()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object next() {

    while (currentIterator < iterators.size() && !iterators.get(currentIterator).hasNext()) {
      currentIterator++;
    }

    if (currentIterator == iterators.size()) {
      return null;
    } else {
      return iterators.get(currentIterator).next();
    }
  }

  @Override
  public boolean hasPrevious() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object previous() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int nextIndex() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int previousIndex() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void set(Object e) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void add(Object e) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
