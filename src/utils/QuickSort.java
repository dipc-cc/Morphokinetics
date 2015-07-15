package utils;

import kineticMonteCarlo.atom.diffusion.Abstract2DDiffusionAtom;

/** 
 * QuickSort algorithm
 */
public class QuickSort {

  public static void orderByAngle(Abstract2DDiffusionAtom[] a, int sizeEvent) {
    //quicksort(a, 0, a.length - 1);
    orderByAngle(a, 0, sizeEvent);
  }

  private static void orderByAngle(Abstract2DDiffusionAtom[] atom, int left, int right) {
    int i = left;
    int j = right;
    Abstract2DDiffusionAtom aux;
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
