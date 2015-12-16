/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Nestor
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
