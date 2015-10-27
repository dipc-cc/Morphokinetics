package utils;

import kineticMonteCarlo.atom.AbstractGrowthAtom;

/** 
 * QuickSort algorithm
 */
public class QuickSort {

  public static void orderByAngle(AbstractGrowthAtom[] a, int sizeEvent) {
    //quicksort(a, 0, a.length - 1);
    orderByAngle(a, 0, sizeEvent);
  }

  private static void orderByAngle(AbstractGrowthAtom[] atom, int left, int right) {
    int i = left;
    int j = right;
    AbstractGrowthAtom aux;
    double pivote = atom[(left + right) / 2].getAngle();
    do {
      while (atom[i].getAngle() < pivote) {
        i++;
      }
      while (atom[j].getAngle() > pivote) {
        j--;
      }
      if (i <= j) {
        aux = atom[i];
        atom[i] = atom[j];
        atom[j] = aux;
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
