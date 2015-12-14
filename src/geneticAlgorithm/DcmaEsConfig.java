package geneticAlgorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

public class DcmaEsConfig {
  /** Offspring population. */
  private RichMatrix offX;
  /** Objective function values. */
  private RichArray offFitness;

  /**  Crossover mean. */
  private double crm;

  /**
   * 
   * @param populationSize Number of individuals of the population. Ideally: offSize = n * Math.round(28 / Math.sqrt(n));
   * @param dimension Number of objective variables/problem dimension.
   */
  public DcmaEsConfig(int populationSize, int dimension) {
    offX = RichMatrix.zeros(dimension, populationSize);
    offFitness = new RichArray(populationSize, 1D).apply(new Operation() {
      @Override
      public double apply(double value) {
        return 1e8 * value;
      }
    });

  }

  public RichMatrix getOffX() {
    return offX;
  }

  public RichArray getOffFitness() {
    return offFitness;
  }

  public double getCrm() {
    return crm;
  }

  public void setOffX(RichMatrix offX) {
    this.offX = offX;
  }

  public void setCrm(double crm) {
    this.crm = crm;
  }
}
