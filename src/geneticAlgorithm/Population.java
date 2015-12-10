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
  private int iterationNumber;
  private boolean ordered;

  public Individual[] getIndividuals() {
    return individuals;
  }

  public Population(int size) {
    individuals = new Individual[size];
    ordered = false;
  }

  public Population(Individual[] ind) {
    individuals = new Individual[ind.length];
    for (int i = 0; i < ind.length; i++) {
      individuals[i] = ind[i];
    }
    ordered = false;
  }

  /**
   * Return an population individual from position pos
   * @param pos position
   * @return Individual
   */
  public Individual getIndividual(int pos) {
    return individuals[pos];
  }

  public void setIndividual(Individual ind, int pos) {
    individuals[pos] = ind;
    ordered = false;
  }

  public int size() {
    return individuals.length;
  }

  /**
   * Orders the population, from the least to the more error.
   */
  public void order() {
    if (!ordered) {
      System.out.println("ordering");
      quicksort(individuals, 0, individuals.length - 1);
    } else {
      System.out.println("not ordering");
    }
    ordered = true;
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
  
  public void setIterationNumber(int iterationNumber) {
    this.iterationNumber = iterationNumber;
  }
  
  public int getIterationNumber() {
    return iterationNumber;
  }
  
  /**
   * Prints to the standard output the current individuals, one line per individual with all its genes.
   * @param header An string to be added at the beginning of the line.
   */
  public void print(String header) {
    for (int i = 0; i < individuals.length; i++) {
      Individual individual = individuals[i];
      System.out.print("\t\t" + header + "\tPopulation " + iterationNumber + " individual " + i + " genes ");
      for (int j = 0; j < individual.getGeneSize(); j++) {
        System.out.print(" " + individual.getGene(j));
      }
      System.out.println("");
    }
  }
}
