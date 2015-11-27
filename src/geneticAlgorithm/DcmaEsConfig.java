package geneticAlgorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

public class DcmaEsConfig {
  /** Number of parents for recombination in CMA-ES. */
  private double mu;
  /** Array for weighted recombination in CMA-ES. */
  private RichArray weights;
  /** D contains the standard deviations. */
  private RichArray D;
  /** Track update of B and D. */
  private double eigeneval;
  /** Counter for the number of evaluations. A good optimisation method will minimise this number. */
  private int counteval;
  /** Offspring population. */
  private RichMatrix offX;
  /** Objective function values. */
  private RichArray offFitness;

  /**  Crossover mean. */
  private double crm; 
  /** Crossover standard deviation. */
  private double crs; 

  /**
   * 
   * @param configuration
   * @param dimension Number of objective variables/problem dimension.
   */
  public DcmaEsConfig(AbstractGeneticAlgorithm configuration, int dimension) {
    int offSize = configuration.getPopulationSize(); //Ideally: offSize = n * Math.round(28 / Math.sqrt(n));
    mu = offSize / 2;
    D = new RichArray(dimension, 1);
    eigeneval = 0;
    counteval = 0;

    offX = RichMatrix.zeros(dimension, offSize);
    offFitness = new RichArray(offSize, 1D).apply(new Operation() {
      @Override
      public double apply(double value) {
        return 1e8 * value;
      }
    });

    createWeightsArray();
  }

  private void createWeightsArray() {
    weights = RichArray.createArrayInRange(mu);
    weights = weights.apply(new Operation() {
      @Override
      public double apply(double value) {
        return Math.log(mu + 1D / 2) - Math.log(value);
      }
    }).normalise();
  }
  
  public RichArray getWeights() {
    return weights;
  }

  public RichArray getD() {
    return D;
  }

  public double getEigeneval() {
    return eigeneval;
  }

  public int getCounteval() {
    return counteval;
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

  public double getCrs() {
    return crs;
  }
  
  public void setD(RichArray D) {
    this.D = D;
  }

  public void setEigeneval(double eigeneval) {
    this.eigeneval = eigeneval;
  }

  public void setOffX(RichMatrix offX) {
    this.offX = offX;
  }

  public void setCrm(double crm) {
    this.crm = crm;
  }

  public void setCrs(double crs) {
    this.crs = crs;
  }

  void addCountEval() {
    this.counteval++;
  }
}
