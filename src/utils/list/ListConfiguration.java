/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list;

/**
 *
 * @author Nestor
 */
public class ListConfiguration {

  public static final int LINEAR_LIST = 0;
  public static final int BINNED_LIST = 1;

  private int binLevels;
  private int binsPerLevel;
  private int listType;

  public ListConfiguration setExtraLevels(int binLevels) {

    this.binLevels = binLevels;
    return this;
  }

  public ListConfiguration setListType(int listType) {

    this.listType = listType;
    return this;
  }

  public ListConfiguration setBinsPerLevel(int binsPerLevel) {

    this.binsPerLevel = binsPerLevel;
    return this;
  }

  public AbstractList createList() {

    AbstractList list = null;
    if (listType == LINEAR_LIST) {
      list = new LinearList();
    }
    if (listType == BINNED_LIST) {
      list = new BinnedList(binsPerLevel, binLevels);
    }

    return list;
  }

}
