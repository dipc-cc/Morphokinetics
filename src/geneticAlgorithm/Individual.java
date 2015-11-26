/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

/**
 *
 * This class models an individual of a Genetic algorithm. The genes consists on floating point
 * numbers.
 *
 * @author Nestor
 */
public class Individual {

  /**
   * Individual genes.
   */
  private double[] genes;

  /**
   * List of errors associated to this individual. We keep them in an array for debugging purposes,
   * the GA just uses the global accumulation for selection purposes.
   */
  private double[] errors;

  private double simulationTime;

  public Individual(int genesNumber, int errorsNumber) {
    genes = new double[genesNumber];
    errors = new double[errorsNumber];
  }

  public Individual(double[] genes, double[] errors) {
    this.genes = genes;
    this.errors = errors;
  }

  public Individual(double[] genes) {
    this.genes = genes;
    this.errors = new double[]{};
  }

  public void setGene(int pos, double gene) {
    genes[pos] = gene;
  }

  public double[] getGenes() {
    return genes;
  }

  public double getGene(int pos) {
    return genes[pos];
  }

  public void scaleGene(double scale, int pos) {
    genes[pos] *= scale;
  }

  public void setError(int pos, double error) {
    errors[pos] = error;
  }

  public double[] getErrors() {
    return errors;
  }

  public double getError(int pos) {
    return errors[pos];
  }

  public double getTotalError() {
    double temp = 0;

    for (int i = 0; i < errors.length; i++) {
      temp += errors[i];
    }

    return temp;
  }

  /**
   * Returns the number of genes 
   * @return number of genes
   */
  public int getGeneSize() {
    return genes.length;
  }

  public int getErrorsSize() {
    return errors.length;
  }

  public double getSimulationTime() {
    return simulationTime;
  }

  public void setSimulationTime(double simulationTime) {
    this.simulationTime = simulationTime;
  }

}
