/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package utils.list;
import java.util.LinkedList;
import java.util.Queue;
import kineticMonteCarlo.atom.CatalysisAtom;

/**
 *
 * @author antonio081014 original code
 * @author J. Alberdi-Rodriguez morphokinetics modification
 * @param <T>
 */
public class AvlTree<T extends Comparable<T>> {
  Node<T> root;
  
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
  
  public Node<T> insert(T data) {
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
    return root;
  }
  
  public Node<T> insert(Node<T> node, T data) {
    if (node == null)
      return new Node<T>(data);
    if (node.getData().compareTo(data) > 0) {
      node = new Node<T>(node.getData(), insert(node.getLeft(), data),
              node.getRight());
      // node.setLeft(insert(node.getLeft(), data));
    } else if (node.getData().compareTo(data) < 0) {
      // node.setRight(insert(node.getRight(), data));
      node = new Node<T>(node.getData(), node.getLeft(), insert(
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
    q = new Node<T>(q.getData(), c, a);
    p = new Node<T>(p.getData(), q, b);
    return p;
  }
  
  private Node<T> rotateRight(Node<T> node) {
    Node<T> q = node;
    Node<T> p = q.getLeft();
    Node<T> c = q.getRight();
    Node<T> a = p.getLeft();
    Node<T> b = p.getRight();
    q = new Node<T>(q.getData(), b, c);
    p = new Node<T>(p.getData(), a, q);
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
   * Method to update rates from root to atom.
   * 
   * @param data atom that has a delta in rate.
   * @param diff rate to be added.
   * @return 
   */
  public boolean removeRate(T data, double diff){
    return removeRate(root, data, diff);
  }
  
  private boolean removeRate(Node<T> n, T data, double diff) {
    if (n == null) {
      return false;
    }
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(-diff);
    if (n.getData().compareTo(data) == 0) {
      return true;
    }
    if (n.getData().compareTo(data) > 0) {
      return removeRate(n.getLeft(), data, diff);
    }
    if (n.getData().compareTo(data) < 0) {
      return removeRate(n.getRight(), data, diff);
    }
    return false;
  }
      
  
  /**
   * Removes atom's rate, with its old desorption rate and sets to zero.
   * 
   * @param data
   * @return 
   */
  public boolean removeAtomRate(T data) {
    return removeAtomRate(root, data);
  }
  
  private boolean removeAtomRate(Node<T> n, T data) {
    if (n == null) {
      return false;
    }
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(-((CatalysisAtom) data).getDesorptionProbability());
    if (n.getData().compareTo(data) == 0) {
      ((CatalysisAtom) data).setDesorptionProbability(0.0);
      return true;
    }
    if (n.getData().compareTo(data) > 0) {
      return removeAtomRate(n.getLeft(), data);
    }
    if (n.getData().compareTo(data) < 0) {
      return removeAtomRate(n.getRight(), data);
    }
    return false;
  }
  
  @Override
  public String toString() {
    return root.toString();
  }
  
  public boolean addRate(T data) {
    return addRate(root, data);
  }
  
  private boolean addRate(Node<T> n, T data) {
    if (n == null) 
      return false;
    ((CatalysisAtom) n.getData()).addToSumDesorptionRate(((CatalysisAtom) data).getDesorptionProbability());
    if (n.getData().compareTo(data) == 0){
      return true;}
    if (n.getData().compareTo(data) > 0)
        return addRate(n.getLeft(), data);
    if (n.getData().compareTo(data) < 0)
      return addRate(n.getRight(), data);
    return false;
  }
  
  public void PrintTree() {
    root.level = 0;
    Queue<Node<T>> queue = new LinkedList<Node<T>>();
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
  
  public CatalysisAtom randomAtom(double randomNumber) {
    return (CatalysisAtom) randomAtom(root, randomNumber).getData();
  }
  
  private Node randomAtom(Node n, double r) {
    CatalysisAtom a = (CatalysisAtom) n.getData();
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
      return randomAtom(n.getRight(), r-leftRate);
    } else {
      return n;
    }
  }
}