/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

/**
 *
 * @author Nestor
 *
 * This class just model a Genetic Algorithm population, it can be the main population or an offspring
 *
 *
 */
public class Population {

  private Individual[] individuals;
  
  public Individual[] getIndividuals() {
    return individuals;
  }

  public Population(int size) {
    individuals = new Individual[size];
  }

  public Population(Individual[] p) {
    individuals = new Individual[p.length];
    for (int i = 0; i < p.length; i++) {
      individuals[i] = p[i];
    }

  }

  public Individual getIndividual(int pos) {
    return individuals[pos];
  }

  public void setIndividual(Individual ind, int pos) {
    individuals[pos] = ind;
  }

  public int size() {
    return individuals.length;
  }

  /**
   * Orders the population, from the least to the more error.
   */
  public void order() {

    quicksort(individuals, 0, individuals.length - 1);
  }

  /**
   * Quicksort-based ordering algorithm.
   * @param a
   * @param left
   * @param right 
   */
  private static void quicksort(Individual[] a, int left, int right) {
    int i = left;
    int j = right;
    Individual aux;

    double pivote = a[(left + right) / 2].getTotalError();

    do {
      while (a[i].getTotalError() < pivote) {

        i++;
      }
      while (a[j].getTotalError() > pivote) {
        j--;

      }
      if (i <= j) {

        aux = a[i];
        a[i] = a[j];
        a[j] = aux;
        i++;
        j--;
      }
    } while (i <= j);
    if (left < j) {
      quicksort(a, left, j);
    }
    if (i < right) {
      quicksort(a, i, right);
    }
  }
}
