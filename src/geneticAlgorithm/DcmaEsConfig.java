package geneticAlgorithm;

import utils.akting.RichMatrix;

public class DcmaEsConfig {
  /** Offspring population. */
  private RichMatrix offX;

  /**  Crossover mean. */
  private double crm;

  /**
   * 
   * @param populationSize Number of individuals of the population. Ideally: offSize = n * Math.round(28 / Math.sqrt(n));
   * @param dimension Number of objective variables/problem dimension.
   */
  public DcmaEsConfig(int populationSize, int dimension) {
    offX = RichMatrix.zeros(dimension, populationSize);
  }

  public RichMatrix getOffX() {
    return offX;
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
