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
package kineticMonteCarlo.site;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * Heaps de arrays de probabilidades ([12] doubles)
 *
 * Tras una inicializacióin, se van sirviendo o realmacenando a petición de los átomos del KMC
 * evaluado.
 *
 *
 */
public class ArrayStack {

  private int arraySize;
  private Deque<double[]> stack;

  public ArrayStack(int arraySize) {
    this.arraySize = arraySize;
    stack = new ArrayDeque();
  }

  public double[] getProbArray() {

    if (stack.isEmpty()) {
      return new double[arraySize];
    } else {
      return stack.pop();
    }
  }

  public void returnProbArray(double[] array) {
    stack.push(array);
  }
}
