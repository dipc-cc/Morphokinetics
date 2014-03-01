/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * Heaps de arrays de probabilidades ([12] doubles)
 *
 * Tras una inicializacióin, se van sirviendo o realmacenando a petición de los
 * átomos del KMC evaluado.
 *
 *
 */
public class Array_stack {


   private int arraySize; 
    private Deque<double[]> stack;


    public Array_stack(int arraySize) {
       this.arraySize=arraySize;
       stack = new ArrayDeque();
    }

    public double[] getProbArray() {

        if (stack.isEmpty()) return new double[arraySize];
        else return stack.pop();   
    }

    public void returnProbArray(double[] array) {
       stack.push(array);
    }
}
