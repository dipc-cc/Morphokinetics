/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package utils.list;
import java.util.LinkedList;
import java.util.Queue;
import kineticMonteCarlo.atom.CatalysisAtom;
import utils.StaticRandom;
import utils.list.atoms.IAtomsCollection;

/**
 *
 * @author antonio081014 original code
 * @author J. Alberdi-Rodriguez morphokinetics modification
 * @param <T>
 */
public class AvlTree<T extends Comparable<T>> implements IAtomsCollection<T> {

  private Node<T> root;

  public AvlTree() {
    root = null;
  }
  
  Node<T> getRoot() {
    return root;
  }
  
  public double getDesorptionRate() {
    return ((CatalysisAtom) root.getData()).getSumDesorptionRate();
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
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(-diff);
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
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(-((CatalysisAtom) data).getDesorptionProbability());
    if (n.getData().compareTo(data) == 0) {
      ((CatalysisAtom) data).setDesorptionProbability(0.0);
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
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(((CatalysisAtom) data).getDesorptionProbability());
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
  
  public void PrintTree() {
    root.level = 0;
    Queue<Node<T>> queue = new LinkedList<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node<T> node = queue.poll();
      System.out.println(node);
      int level = node.level;
      Node<T> left = node.getLeft();
      Node<T> right = node.getRight();
      if (left != null) {
        left.level = level + 1;
        queue.add(left);
      }
      if (right != null) {
        right.level = level + 1;
        queue.add(right);
      }
    }
  }
  
  public void setParents() {
    setParents(root, null);
  }
  
  private void setParents(Node n, Node parent) {
    if (n == null) 
      return;
    n.parent = parent;
    setParents(n.getLeft(), n);
    setParents(n.getRight(), n);
  }

  @Override
  public void clear() {
    clear(root);
  }
  
  private void clear(Node n) {
    if (n == null)
      return;
    ((CatalysisAtom) n.getData()).setSumDesorptionRate(0.0);
    clear(n.getLeft());
    clear(n.getRight());
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
      ((CatalysisAtom) n.getData()).equalRate();
      return ((CatalysisAtom) n.getData()).getDesorptionProbability();
    }
    // add current rate to the sum
    ((CatalysisAtom) n.getData()).setSumDesorptionRate(((CatalysisAtom) n.getData()).getDesorptionProbability());
    if (n.getLeft() != null) {
      // add left childen rate sum 
      ((CatalysisAtom) n.getData()).addToSumDesorptionRate(populateCatalysisAtom(n.getLeft()));
    }
    if (n.getRight() != null) {
      ((CatalysisAtom) n.getData()).addToSumDesorptionRate(populateCatalysisAtom(n.getRight()));
    }
    return ((CatalysisAtom) n.getData()).getSumDesorptionRate();
  }

  /**
   * Chooses a random atom from current tree.
   *
   * @return an atom.
   */
  @Override
  public T randomAtom() {
    double randomNumber = StaticRandom.raw() * getDesorptionRate();
    CatalysisAtom atom = (CatalysisAtom) randomAtom(root, randomNumber).getData();
    while (atom.getDesorptionProbability() == 0) {
      //System.out.println("Something is not going perfectly "+counter+++" "+localCounter++);
      randomNumber = StaticRandom.raw() * getDesorptionRate();
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
      leftRate = ((CatalysisAtom) n.getLeft().getData()).getSumDesorptionRate();
    double rightRate = 0.0;
    if (n.getRight() != null) 
      rightRate = ((CatalysisAtom) n.getRight().getData()).getSumDesorptionRate();

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
}