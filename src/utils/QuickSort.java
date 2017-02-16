package utils;

import java.util.List;
import kineticMonteCarlo.atom.AbstractGrowthAtom;

/** 
 * QuickSort algorithm
 */
public class QuickSort {

  public static void orderByAngle(List<AbstractGrowthAtom> a, int sizeEvent) {
    //quicksort(a, 0, a.length - 1);
    orderByAngle(a, 0, sizeEvent);
  }

  private static void orderByAngle(List<AbstractGrowthAtom> atom, int left, int right) {
    int i = left;
    int j = right;
    AbstractGrowthAtom aux;
    double pivote = atom.get((left + right) / 2).getAngle();
    do {
      while (atom.get(i).getAngle() < pivote) {
        i++;
      }
      while (atom.get(j).getAngle() > pivote) {
        j--;
      }
      if (i <= j) {
        aux = atom.get(i);
        atom.set(i, atom.get(j));
        atom.set(j, aux);
        i++;
        j--;
      }
    } while (i <= j);
    if (left < j) {
      orderByAngle(atom, left, j);
    }
    if (i < right) {
      orderByAngle(atom, i, right);
    }
  }
}
