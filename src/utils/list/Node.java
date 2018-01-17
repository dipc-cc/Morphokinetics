package utils.list;

/**
 * @author antonio081014
 * @param <T>
 * @time Jul 5, 2013, 9:31:32 PM
 */
public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

  private T data;
  private Node<T> left;
  private Node<T> right;
  private Node<T> next;
  private int level;
  private int depth;

  public Node(T data) {
    this(data, null, null);
  }

  public Node(T data, Node<T> left, Node<T> right) {
    super();
    this.data = data;
    this.left = left;
    this.right = right;
    if (left == null && right == null) {
      setDepth(1);
    } else if (left == null) {
      setDepth(right.getDepth() + 1);
    } else if (right == null) {
      setDepth(left.getDepth() + 1);
    } else {
      setDepth(Math.max(left.getDepth(), right.getDepth()) + 1);
    }
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Node<T> getLeft() {
    return left;
  }

  public void setLeft(Node<T> left) {
    this.left = left;
  }

  public Node<T> getRight() {
    return right;
  }

  public void setRight(Node<T> right) {
    this.right = right;
  }
  
  public void setNext(Node<T> next) {
    this.next = next;
  }
  
  public Node<T> next() {
    return next;
  }

  /**
   * @return the depth
   */
  public int getDepth() {
    return depth;
  }

  /**
   * @param depth the depth to set
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }

  @Override
  public int compareTo(Node<T> o) {
    return this.data.compareTo(o.data);
  }

  public boolean isLeaf() {
    return left == null && right == null;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  /*@Override
	public String toString() {
		return "Level " + level + ": " + data;
	}*/
  @Override
  public String toString() {
    if (this.getLeft() != null && this.getRight() != null) {
      return "Level " + level + ": " + data + " || left " + this.getLeft().data + " | right " + this.getRight().data;
    } else if (this.getLeft() != null && this.getRight() == null) {
      return "Level " + level + ": " + data + " || left " + this.getLeft().data;
    } else if (this.getLeft() == null && this.getRight() != null) {
      return "Level " + level + ": " + data + " || right " + this.getRight().data;
    }

    return "Level " + level + ": " + data;
  }
}
