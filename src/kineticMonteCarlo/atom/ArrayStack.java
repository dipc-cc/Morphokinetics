/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

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
public class ArrayStack {


   private int arraySize; 
    private Deque<double[]> stack;


    public ArrayStack(int arraySize) {
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
