/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

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
  /** Objective function values. */
  private RichArray offFitness;
  /** Offspring population. */
  private RichMatrix offspringGenes;  
  /**  Crossover mean. */
  private double crm;
  
  public Individual[] getIndividuals() {
    return individuals;
  }
  
  /**
   * 
   * @param dimensions Number of genes per individual
   * @param size Number of individuals
   */
  public Population(int dimensions, int size) {
    individuals = new Individual[size];
    ordered = false;
    // Set big error for all the individuals
    offFitness = new RichArray(size, 1D).apply(new Operation() {
      @Override
      public double apply(double value) {
        return 1e8 * value;
      }
    });
    offspringGenes = RichMatrix.zeros(dimensions, size);
  }

  public Population(int size) {
    individuals = new Individual[size];
    ordered = false;
  }
  
  /**
   * Return an population individual from position pos
   *
   * @param pos position must be less than number of individuals (population size)
   * @return Individual
   */
  public Individual getIndividual(int pos) {
    if (pos >= individuals.length) {
      throw new IllegalArgumentException("Required individual does not exist. Required position " + pos + " total number of individuals " + individuals.length);
    }
    return individuals[pos];
  }

  /**
   * Sets an individual to position pos
   *
   * @param ind Individual to be inserted
   * @param pos position must be less than number of individuals (population size)
   */
  public void setIndividual(Individual ind, int pos) {
    if (pos >= individuals.length) {
      throw new IllegalArgumentException("Required individual does not exist. Required position " + pos + " total number of individuals " + individuals.length);
    }
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
      quicksort(individuals, 0, individuals.length - 1);
    }
    ordered = true;
  }

  /**
   * Objective function values for DCMA-ES algorithm. Otherwise, it has huge values that never
   * change
   *
   * @return Objective function values
   */
  public RichArray getOffFitness() {
    return offFitness;
  }
  
  public RichMatrix getOffspringGenes() {
    return offspringGenes;
  }
  
  public void newOffspringGenes() {
    offspringGenes = new RichMatrix(this);
  }
  
  public void setOffspringGenes(RichMatrix offX) {
    this.offspringGenes = offX;
  }
  
  public double getCrm() {
    return crm;
  }

  public void setCrm(double crm) {
    this.crm = crm;
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
  
  /**
   * Returns the best error of the current population. It orders the population (if required) and
   * returns the sum of the best individual
   *
   * @return the lowest error
   */
  public double getBestError() {
    return getBestIndividual().getTotalError();
  }
  
  /**
   * Returns the best individual of the current population. It orders the population (if required) and
   * returns the best individual
   * @return the best individual
   */
  public Individual getBestIndividual() {
     if (!ordered) {
      this.order();
    }
    return this.individuals[0];
  }

}
