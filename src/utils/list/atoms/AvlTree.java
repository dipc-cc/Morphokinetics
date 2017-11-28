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
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.atom.ConcertedAtom;
import utils.list.Node;

/**
 *
 * @author antonio081014 original code
 * @author J. Alberdi-Rodriguez morphokinetics modification
 * @param <T>
 */
public class AvlTree<T extends Comparable<T>> {

  private Node<T> root;
  private Node<T> current;
  private Node<T> next;

  public AvlTree() {
    root = null;
  }
  
  Node<T> getRoot() {
    return root;
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
   * 
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
  
  public Node<T> findNode(T data) {
    Node<T> local = root;
    while (local != null) {
      if (local.getData().compareTo(data) == 0)
        return local;
      else if (local.getData().compareTo(data) > 0)
        local = local.getLeft();
      else
        local = local.getRight();
    }
    return null;
  }
  
  @Override
  public String toString() {
    return root.toString();
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

  /**
   * Creates a list inside the tree. It is aimed to iterate over the tree
   * faster.
   *
   * @param type it is used just to decide the actual class to be used
   */
  public void createList(String type) {
    current = getMinimumNode();
    int i = 0;
    while (current != null) {
      i++;
      T a;
      switch (type) {
        case "catalysis":
          a = (T) new CatalysisAtom(i, (short)-1, (short)-1);
          break;
        case "concerted":
         a = (T) new ConcertedAtom(i, -1);
         break;
        default:
          a = null;
      }
      next = findNode(a);
      current.setNext(next);
      current = next;
    }
  }
  
  public boolean isEmpty() {
    return root == null;
  }
  
  public Iterator<T> iterator(byte process) {
    Itr itr = new Itr(process);
    return itr;
  }
  
  private class Itr implements Iterator<T> {

    private final byte process;
    
    private Itr(byte process) {
      current = next = getMinimumNode();
      this.process = process;
    }

    /**
     * Skip non-occupied atoms.
     * 
     * @return 
     */
    @Override
    public boolean hasNext() {
      if (next == null) { // We have reached the end of the list
        return false;
      } else {
        T atom = next.getData();
        while (!((AbstractGrowthAtom) atom).isOnList(process)) {
          next = next.next();
          if (next == null) { // We have reached the end of the list
            return false;
          } else {
            atom = next.getData();
          }
        }
        current = next;
        next = current.next();
        return true;
      }
    }

    /**
     * Return current atom.
     * 
     * @return next element.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T next() {
      return current.getData();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super T> consumer) {
    }

    final void checkForComodification() {
    }
  }
}