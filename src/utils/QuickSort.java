/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils;

import java.util.List;
import kineticMonteCarlo.site.AbstractGrowthSite;

/** 
 * QuickSort algorithm
 */
public class QuickSort {

  public static void orderByAngle(List<AbstractGrowthSite> a, int sizeEvent) {
    //quicksort(a, 0, a.length - 1);
    orderByAngle(a, 0, sizeEvent);
  }

  private static void orderByAngle(List<AbstractGrowthSite> atom, int left, int right) {
    int i = left;
    int j = right;
    AbstractGrowthSite aux;
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
