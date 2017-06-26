/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package utils.list.atoms;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import kineticMonteCarlo.atom.CatalysisAtom;
import utils.StaticRandom;
import utils.list.Node;

/**
 *
 * @author antonio081014 original code
 * @author J. Alberdi-Rodriguez morphokinetics modification
 * @param <T>
 */
public class AtomsAvlTree<T extends Comparable<T>> implements IAtomsCollection<T> {

  private Node<T> root;
  /**
   * Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;

  public AtomsAvlTree(byte process) {
    root = null;
    this.process = process;
  }
  
  Node<T> getRoot() {
    return root;
  }
  
  @Override
  public double getTotalRate(byte process) {
    if (root == null) {
      return 0;
    }
    return ((CatalysisAtom) root.getData()).getSumRate(process);
  }
  
  public T getMaximum() {
    Node<T> local = root;
    if (local == null)
      return null;
    while (local.getRight() != null)
      local = local.getRight();
    return local.getData();
  }
  
  public T getMinimum() {
    Node<T> local = root;
    if (local == null)
      return null;
    while (local.getLeft() != null) {
      local = local.getLeft();
    }
    return local.getData();
  }
  
  public Node<T> getMinimumNode() {
    Node<T> local = root;
    if (local == null)
      return null;
    while (local.getLeft() != null) {
      local = local.getLeft();
    }
    return local;
  }  
  
  private int depth(Node<T> node) {
    if (node == null)
      return 0;
    return node.getDepth();
    // 1 + Math.max(depth(node.getLeft()), depth(node.getRight()));
  }
  
  @Override
  public void insert(T data) {
    root = insert(root, data);
    switch (balanceNumber(root)) {
      case 1:
        root = rotateLeft(root);
        break;
      case -1:
        root = rotateRight(root);
        break;
      default:
        break;
    }
  }
  
  private Node<T> insert(Node<T> node, T data) {
    if (node == null)
      return new Node<>(data);
    if (node.getData().compareTo(data) > 0) {
      node = new Node<>(node.getData(), insert(node.getLeft(), data),
              node.getRight());
      // node.setLeft(insert(node.getLeft(), data));
    } else if (node.getData().compareTo(data) < 0) {
      // node.setRight(insert(node.getRight(), data));
      node = new Node<>(node.getData(), node.getLeft(), insert(
              node.getRight(), data));
    }
    // After insert the new node, check and rebalance the current node if
    // necessary.
    switch (balanceNumber(node)) {
      case 1:
        node = rotateLeft(node);
        break;
      case -1:
        node = rotateRight(node);
        break;
      default:
        return node;
    }
    return node;
  }
  
  private int balanceNumber(Node<T> node) {
    int L = depth(node.getLeft());
    int R = depth(node.getRight());
    if (L - R >= 2)
      return -1;
    else if (L - R <= -2)
      return 1;
    return 0;
  }
  
  private Node<T> rotateLeft(Node<T> node) {
    Node<T> q = node;
    Node<T> p = q.getRight();
    Node<T> c = q.getLeft();
    Node<T> a = p.getLeft();
    Node<T> b = p.getRight();
    q = new Node<>(q.getData(), c, a);
    p = new Node<>(p.getData(), q, b);
    return p;
  }
  
  private Node<T> rotateRight(Node<T> node) {
    Node<T> q = node;
    Node<T> p = q.getLeft();
    Node<T> c = q.getRight();
    Node<T> a = p.getLeft();
    Node<T> b = p.getRight();
    q = new Node<>(q.getData(), b, c);
    p = new Node<>(p.getData(), a, q);
    return p;
  }
  
  public boolean search(T data) {
    Node<T> local = root;
    while (local != null) {
      if (local.getData().compareTo(data) == 0)
        return true;
      else if (local.getData().compareTo(data) > 0)
        local = local.getLeft();
      else
        local = local.getRight();
    }
    return false;
  }
  
  /**
   * Finds the element itself.
   * @param data
   * @return element if found, null otherwise.
   */
  public T find(T data) {
    Node<T> local = root;
    while (local != null) {
      if (local.getData().compareTo(data) == 0)
        return local.getData();
      else if (local.getData().compareTo(data) > 0)
        local = local.getLeft();
      else
        local = local.getRight();
    }
    return null;
  }
  
  /**
   * Method to update rates from root to an atom.
   * 
   * @param data atom that has a delta in rate.
   * @param diff rate to be added.
   */
  @Override
  public void removeRate(T data, double diff){
    removeRate(root, data, diff);
  }
  
  private void removeRate(Node<T> n, T data, double diff) {
    if (n == null) {
      return;
    }
    ((CatalysisAtom) n.getData()).addToSumRate(process, -diff);
    if (n.getData().compareTo(data) == 0) {
      return;
    }
    if (n.getData().compareTo(data) > 0) {
      removeRate(n.getLeft(), data, diff);
    }
    if (n.getData().compareTo(data) < 0) {
      removeRate(n.getRight(), data, diff);
    }
  }    
  
  /**
   * Removes atom's rate from the tree, with its old desorption rate and sets to zero.
   * 
   * @param data
   */
  @Override
  public void removeAtomRate(T data) {
    removeAtomRate(root, data);
  }
  
  private void removeAtomRate(Node<T> n, T data) {
    if (n == null) {
      return;
    }
    ((CatalysisAtom) n.getData()).addToSumRate(process, -((CatalysisAtom) data).getRate(process));
    if (n.getData().compareTo(data) == 0) {
      ((CatalysisAtom) data).setRate(process, 0.0);
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
    return root.toString();
  }
  
  /**
   * Rate of current atom is added in the tree.
   * 
   * @param data current atom.
   */
  @Override
  public void addRate(T data) {
    addRate(root, data);
  }
  
  private void addRate(Node<T> n, T data) {
    if (n == null) {
      return;
    }
    ((CatalysisAtom) n.getData()).addToSumRate(process, ((CatalysisAtom) data).getRate(process));
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
  
  public void printTree() {
    root.setLevel(0);
    Queue<Node<T>> queue = new LinkedList<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node<T> node = queue.poll();
      System.out.println(node);
      int level = node.getLevel();
      Node<T> left = node.getLeft();
      Node<T> right = node.getRight();
      if (left != null) {
        left.setLevel(level + 1);
        queue.add(left);
      }
      if (right != null) {
        right.setLevel(level + 1);
        queue.add(right);
      }
    }
  }

  @Override
  public void clear() {
    clear(root);
  }
  
  private void clear(Node n) {
    if (n == null)
      return;
    ((CatalysisAtom) n.getData()).setSumRate(process, 0.0);
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
    populateCatalysisAtom(root);
  }
  
  /**
   * Populates tree with the sum of child rates to current node.
   * 
   * @param n
   * @return 
   */
  private double populateCatalysisAtom(Node n) {
    if (n == null) {
      return 0;
    }

    if (n.isLeaf()) {
      ((CatalysisAtom) n.getData()).equalRate(process);
      return ((CatalysisAtom) n.getData()).getRate(process);
    }
    // add current rate to the sum
    ((CatalysisAtom) n.getData()).setSumRate(process, ((CatalysisAtom) n.getData()).getRate(process));
    if (n.getLeft() != null) {
      // add left childen rate sum 
      ((CatalysisAtom) n.getData()).addToSumRate(process, populateCatalysisAtom(n.getLeft()));
    }
    if (n.getRight() != null) {
      ((CatalysisAtom) n.getData()).addToSumRate(process, populateCatalysisAtom(n.getRight()));
    }
    return ((CatalysisAtom) n.getData()).getSumRate(process);
  }

  /**
   * Chooses a random atom from current tree.
   *
   * @return an atom.
   */
  @Override
  public T randomAtom() {
    double randomNumber = StaticRandom.raw() * getTotalRate(process);
    CatalysisAtom atom = (CatalysisAtom) randomAtom(root, randomNumber).getData();
    while (atom.getRate(process) == 0) {
      //System.out.println("Something is not going perfectly "+counter+++" "+localCounter++);
      randomNumber = StaticRandom.raw() * getTotalRate(process);
      atom = (CatalysisAtom) randomAtom(root, randomNumber).getData();
    }
    return (T) atom;
  }
  
  private Node randomAtom(Node n, double r) {
    if (n.isLeaf()) {
      return n;
    }
    double leftRate = 0.0;
    if (n.getLeft() != null) 
      leftRate = ((CatalysisAtom) n.getLeft().getData()).getSumRate(process);
    double rightRate = 0.0;
    if (n.getRight() != null) 
      rightRate = ((CatalysisAtom) n.getRight().getData()).getSumRate(process);

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
    return depth(root);
  }
  
  @Override
  public boolean isEmpty() {
    return root == null;
  }
  
  @Override
  public Iterator<T> iterator() {
    Itr itr = new Itr();
    return itr;
  }
  
  private class Itr implements Iterator<T> {

    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such
    int max; // last element

    private Itr() {
      cursor = -1;
      max = getMaximum().hashCode();
    }

    /**
     * Left -> current -> right.
     * @return 
     */
    @Override
    public boolean hasNext() {
      return cursor < max;
    }

    /**
     * Very inefficient way to iterate over all elements.
     * 
     * @return next element.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T next() {
      // go to next element
      cursor++;
      T a  = (T) new CatalysisAtom(cursor, (short)-1, (short)-1);
      return find(a);
    }      

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super T> consumer) {
    }

    final void checkForComodification() {
    }
  }
}