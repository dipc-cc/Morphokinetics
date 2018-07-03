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
import kineticMonteCarlo.process.IElement;
import utils.StaticRandom;
import utils.list.Node;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public class SitesAvlTree<T extends Comparable<T>> implements ISitesCollection<T> {

  /**
   * Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;
  /** How much is the occupancy of the tree. */
  private int occupied;
  /** Actual AvlTree. */
  private final AvlTree tree;

  public SitesAvlTree(byte process, AvlTree tree) {
    super();
    occupied = 0;
    this.process = process;
    this.tree = tree;
  }

  @Override
  public double getTotalRate(byte process) {
    if (tree.getRoot() == null) {
      return 0;
    }
    return ((IElement) tree.getRoot().getData()).getSumRate(process);
  }
  
  /**
   * Do nothing
   * @param data 
   */
  @Override
  public void insert(T data) {
  }
    
  /**
   * Method to update rates from root to an site.
   * 
   * @param data site that has a delta in rate.
   * @param diff rate to be added.
   */
  @Override
  public void updateRate(T data, double diff){
    updateRate(tree.getRoot(), data, diff);
  }
  
  private void updateRate(Node<T> n, T data, double diff) {
    if (n == null) {
      return;
    }
    ((IElement) n.getData()).addToSumRate(process, diff);
    if (n.getData().compareTo(data) == 0) {
      return;
    }
    if (n.getData().compareTo(data) > 0) {
      updateRate(n.getLeft(), data, diff);
    }
    if (n.getData().compareTo(data) < 0) {
      updateRate(n.getRight(), data, diff);
    }
  }    
  
  /**
   * Removes site's rate from the tree, with its old rate and sets to zero.
   * 
   * @param data
   */
  @Override
  public void removeAtomRate(T data) {
    occupied--;
    removeAtomRate(tree.getRoot(), data);
  }
  
  private void removeAtomRate(Node<T> n, T data) {
    if (n == null) {
      return;
    }
    ((IElement) n.getData()).addToSumRate(process, -((IElement) data).getRate(process));
    if (n.getData().compareTo(data) == 0) {
      ((IElement) data).setRate(process, 0.0);
      return;
    }
    if (n.getData().compareTo(data) > 0) {
      removeAtomRate(n.getLeft(), data);
    }
    if (n.getData().compareTo(data) < 0) {
      removeAtomRate(n.getRight(), data);
    }
  }
  
  @Override
  public String toString() {
    return tree.toString();
  }
  
  /**
   * Rate of current site is added in the tree.
   * 
   * @param data current site.
   */
  @Override
  public void addRate(T data) {
    occupied++;
    addRate(tree.getRoot(), data);
  }
  
  private void addRate(Node<T> n, T data) {
    if (n == null) {
      return;
    }
    ((IElement) n.getData()).addToSumRate(process, ((IElement) data).getRate(process));
    if (n.getData().compareTo(data) == 0) {
      return;
    }
    if (n.getData().compareTo(data) > 0) {
      addRate(n.getLeft(), data);
    }
    if (n.getData().compareTo(data) < 0) {
      addRate(n.getRight(), data);
    }
  }

  /**
   * Used to recompute the tree/total rate.
   */
  @Override
  public void clear() {
    occupied = 0;
    clear(tree.getRoot());
  }
  
  private void clear(Node n) {
    if (n == null)
      return;
    ((IElement) n.getData()).setSumRate(process, 0.0);
    clear(n.getLeft());
    clear(n.getRight());
  }
  
  /**
   * Empties current tree.
   */
  @Override
  public void reset() {
    occupied = 0;
    reset(tree.getRoot());
  }
  
  private void reset(Node n) {
    if (n == null)
      return;
    ((IElement) n.getData()).clear();
    reset(n.getLeft());
    reset(n.getRight());
  }
  @Override
  public void recomputeTotalRate(byte process) {
    clear();
    populate();
  }
  
  /**
   * It goes through all nodes and recomputes sum of the rates.
   */
  @Override
  public void populate() {
    populateSite(tree.getRoot());
  }
  
  /**
   * Populates tree with the sum of child rates to current node.
   * 
   * @param n
   * @return 
   */
  private double populateSite(Node n) {
    if (n == null) {
      return 0;
    }

    if (((IElement) n.getData()).getRate(process) > 0) {
      occupied++;
    }
    if (n.isLeaf()) {
      ((IElement) n.getData()).equalRate(process);
      return ((IElement) n.getData()).getRate(process);
    }
    // add current rate to the sum
    ((IElement) n.getData()).setSumRate(process, ((IElement) n.getData()).getRate(process));
    if (n.getLeft() != null) {
      // add left childen rate sum 
      ((IElement) n.getData()).addToSumRate(process, populateSite(n.getLeft()));
    }
    if (n.getRight() != null) {
      ((IElement) n.getData()).addToSumRate(process, populateSite(n.getRight()));
    }
    return ((IElement) n.getData()).getSumRate(process);
  }
  
  /**
   * Chooses a random site from current tree.
   *
   * @return an site.
   */
  @Override
  public T randomElement() {
    double randomNumber = StaticRandom.raw() * getTotalRate(process);
    IElement site = (IElement) randomAtom(tree.getRoot(), randomNumber).getData();
    while (site.getRate(process) == 0) {
      //System.out.println("Something is not going perfectly "+counter+++" "+localCounter++);
      randomNumber = StaticRandom.raw() * getTotalRate(process);
      site = (IElement) randomAtom(tree.getRoot(), randomNumber).getData();
    }
    return (T) site;
  }
  
  private Node randomAtom(Node n, double r) {
    if (n.isLeaf()) {
      return n;
    }
    double leftRate = 0.0;
    if (n.getLeft() != null) 
      leftRate = ((IElement) n.getLeft().getData()).getSumRate(process);
    double rightRate = 0.0;
    if (n.getRight() != null) 
      rightRate = ((IElement) n.getRight().getData()).getSumRate(process);

    if (r < leftRate) {
      return randomAtom(n.getLeft(), r);
    } else if (r < leftRate + rightRate) {
      return randomAtom(n.getRight(), r - leftRate);
    } else {
      return n;
    }
  }

  @Override
  public void remove(T site) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int size() {
    return occupied;
  }
  
  @Override
  public boolean isEmpty() {
    return tree.isEmpty();
  }
  
  @Override
  public Iterator<T> iterator() {
    return tree.iterator(process);
  }
  
  /**
   * Not yet implemented.
   * 
   * @param site
   * @return 
   */
  @Override
  public T search(T site) {
    return null;
  }
}