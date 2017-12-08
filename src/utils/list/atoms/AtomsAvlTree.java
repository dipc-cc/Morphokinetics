/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package utils.list.atoms;
import java.util.Iterator;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import utils.StaticRandom;
import utils.list.Node;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public class AtomsAvlTree<T extends Comparable<T>> implements IAtomsCollection<T> {

  /**
   * Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;
  /** How much is the occupancy of the tree. */
  private int occupied;
  /** Actual AvlTree. */
  private final AvlTree tree;

  public AtomsAvlTree(byte process, AvlTree tree) {
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
    return ((AbstractGrowthAtom) tree.getRoot().getData()).getSumRate(process);
  }
  
  /**
   * Do nothing
   * @param data 
   */
  @Override
  public void insert(T data) {
  }
    
  /**
   * Method to update rates from root to an atom.
   * 
   * @param data atom that has a delta in rate.
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
    ((AbstractGrowthAtom) n.getData()).addToSumRate(process, diff);
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
   * Removes atom's rate from the tree, with its old rate and sets to zero.
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
    ((AbstractGrowthAtom) n.getData()).addToSumRate(process, -((AbstractGrowthAtom) data).getRate(process));
    if (n.getData().compareTo(data) == 0) {
      ((AbstractGrowthAtom) data).setRate(process, 0.0);
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
   * Rate of current atom is added in the tree.
   * 
   * @param data current atom.
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
    ((AbstractGrowthAtom) n.getData()).addToSumRate(process, ((AbstractGrowthAtom) data).getRate(process));
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

  @Override
  public void clear() {
    occupied = 0;
    clear(tree.getRoot());
  }
  
  private void clear(Node n) {
    if (n == null)
      return;
    ((AbstractGrowthAtom) n.getData()).setSumRate(process, 0.0);
    clear(n.getLeft());
    clear(n.getRight());
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
    populateAtom(tree.getRoot());
  }
  
  /**
   * Populates tree with the sum of child rates to current node.
   * 
   * @param n
   * @return 
   */
  private double populateAtom(Node n) {
    if (n == null) {
      return 0;
    }

    if (((AbstractGrowthAtom) n.getData()).getRate(process) > 0) {
      occupied++;
    }
    if (n.isLeaf()) {
      ((AbstractGrowthAtom) n.getData()).equalRate(process);
      return ((AbstractGrowthAtom) n.getData()).getRate(process);
    }
    // add current rate to the sum
    ((AbstractGrowthAtom) n.getData()).setSumRate(process, ((AbstractGrowthAtom) n.getData()).getRate(process));
    if (n.getLeft() != null) {
      // add left childen rate sum 
      ((AbstractGrowthAtom) n.getData()).addToSumRate(process, populateAtom(n.getLeft()));
    }
    if (n.getRight() != null) {
      ((AbstractGrowthAtom) n.getData()).addToSumRate(process, populateAtom(n.getRight()));
    }
    return ((AbstractGrowthAtom) n.getData()).getSumRate(process);
  }
  
  /**
   * Chooses a random atom from current tree.
   *
   * @return an atom.
   */
  @Override
  public T randomAtom() {
    double randomNumber = StaticRandom.raw() * getTotalRate(process);
    AbstractGrowthAtom atom = (AbstractGrowthAtom) randomAtom(tree.getRoot(), randomNumber).getData();
    while (atom.getRate(process) == 0) {
      //System.out.println("Something is not going perfectly "+counter+++" "+localCounter++);
      randomNumber = StaticRandom.raw() * getTotalRate(process);
      atom = (AbstractGrowthAtom) randomAtom(tree.getRoot(), randomNumber).getData();
    }
    return (T) atom;
  }
  
  private Node randomAtom(Node n, double r) {
    if (n.isLeaf()) {
      return n;
    }
    double leftRate = 0.0;
    if (n.getLeft() != null) 
      leftRate = ((AbstractGrowthAtom) n.getLeft().getData()).getSumRate(process);
    double rightRate = 0.0;
    if (n.getRight() != null) 
      rightRate = ((AbstractGrowthAtom) n.getRight().getData()).getSumRate(process);

    if (r < leftRate) {
      return randomAtom(n.getLeft(), r);
    } else if (r < leftRate + rightRate) {
      return randomAtom(n.getRight(), r - leftRate);
    } else {
      return n;
    }
  }

  @Override
  public void remove(T atom) {
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
   * @param atom
   * @return 
   */
  @Override
  public T search(T atom) {
    return null;
  }
}