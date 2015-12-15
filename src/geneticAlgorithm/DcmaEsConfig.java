package geneticAlgorithm;

public class DcmaEsConfig {

  /**  Crossover mean. */
  private double crm;

  /**
   * 
   * @param populationSize Number of individuals of the population. Ideally: offSize = n * Math.round(28 / Math.sqrt(n));
   * @param dimension Number of objective variables/problem dimension.
   */
  public DcmaEsConfig(int populationSize, int dimension) {
  }

  public double getCrm() {
    return crm;
  }

  public void setCrm(double crm) {
    this.crm = crm;
  }
}
