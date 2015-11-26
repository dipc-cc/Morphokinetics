package utils.akting;

import java.util.Comparator;

public class RichArrayComparator implements Comparator<Integer> {

  private final RichArray richArray;

  public RichArrayComparator(RichArray richArray) {
    this.richArray = richArray;
  }

  public Integer[] createIndexArray() {
    int size = richArray.size();
    Integer[] indexes = new Integer[size];

    for (int i = 0; i < size; i++) {
      indexes[i] = i;
    }

    return indexes;
  }

  public int compare(Integer o1, Integer o2) {
    return richArray.get(o1).compareTo(richArray.get(o2));
  }

}
