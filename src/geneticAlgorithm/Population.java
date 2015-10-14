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

  public Individual[] getIndividuals() {
    return Individuals;
  }

  private Individual[] Individuals;

  public Population(int size) {
    Individuals = new Individual[size];
  }

  public Population(Individual[] p) {
    Individuals = new Individual[p.length];
    for (int i = 0; i < p.length; i++) {
      Individuals[i] = p[i];
    }

  }

  public Individual getIndividual(int pos) {
    return Individuals[pos];
  }

  public void setIndividual(Individual ind, int pos) {
    Individuals[pos] = ind;
  }

  public int size() {
    return Individuals.length;
  }

  /**
   * Orders the population, from the least to the more error.
   */
  public void order() {

    quicksort(Individuals, 0, Individuals.length - 1);
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
