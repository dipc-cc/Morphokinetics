/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

/**
 *
 * This class just model a Genetic Algorithm population, it can be the main population or an
 * offspring
 *
 * @author Nestor
 */
public class Population {

  private Individual[] individuals;

  public Individual[] getIndividuals() {
    return individuals;
  }

  public Population(int size) {
    individuals = new Individual[size];
  }

  public Population(Individual[] ind) {
    individuals = new Individual[ind.length];
    for (int i = 0; i < ind.length; i++) {
      individuals[i] = ind[i];
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
   *
   * @param ind
   * @param left
   * @param right
   */
  private static void quicksort(Individual[] ind, int left, int right) {
    int i = left;
    int j = right;
    Individual aux;

    double pivote = ind[(left + right) / 2].getTotalError();

    do {
      while (ind[i].getTotalError() < pivote) {

        i++;
      }
      while (ind[j].getTotalError() > pivote) {
        j--;

      }
      if (i <= j) {
        aux = ind[i];
        ind[i] = ind[j];
        ind[j] = aux;
        i++;
        j--;
      }
    } while (i <= j);
    if (left < j) {
      quicksort(ind, left, j);
    }
    if (i < right) {
      quicksort(ind, i, right);
    }
  }
}
