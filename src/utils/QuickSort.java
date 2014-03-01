package utils;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;



//QuickSort algorithm

public class QuickSort {
  public static void order_by_angle(Abstract_2D_diffusion_atom[] a, int size_evnt) {
    //quicksort(a, 0, a.length - 1);
      order_by_angle(a, 0, size_evnt);
  }

  private static void order_by_angle(Abstract_2D_diffusion_atom[] atom, int left, int right) {
    int i = left;
    int j = right;
    Abstract_2D_diffusion_atom aux;
    double pivote = atom[ (left + right) / 2].getAngle();
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
    }
    while (i <= j);
    if (left < j) {
      order_by_angle(atom, left, j);
    }
    if (i < right) {
      order_by_angle(atom, i, right);
    }
  }
}
