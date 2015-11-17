package geneticAlgorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

public class DcmaEsConfig {
  /** Number of objective variables/problem dimension. */
  private int n;
  /** Stop if mean(fitness) - min(fitness) < stopFitness (minimization). */
  private double stopFitness;
  /** Number of function evaluations to stop. */
  private double stopEval;
  /** Maximum number of generations. */
  private double gMax;
  /** Population size, offspring number in DE and CMA-ES. */
  private double offSize;
  /** Number of parents for recombination in CMA-ES. */
  private double mu;
  /** Array for weighted recombination in CMA-ES. */
  private RichArray weights;
  /** B defines the coordinate system. */
  private RichMatrix B;
  /** D contains the standard deviations. */
  private RichArray D;
  /** Covariance matrix C. */
  private RichMatrix C;
  /** C^-1/2 */
  private RichMatrix invsqrtC;
  /** Track update of B and D. */
  private double eigeneval;
  /** Expectation of ||N(0,I)|| == norm(randn(N,1)) */
  private double chiN;
  /** Counter for the number of evaluations. A good optimisation method will minimise this number. */
  private int counteval;
  /** Offspring population. */
  private RichMatrix offX;
  /** Trial/test offspring population. */
  private RichMatrix offV;
  /** Objective function values. */
  private RichArray offFitness;
  /** Recombination, new mean value in CMA-ES. */
  private RichArray xmean;
  /** Step size in CMA-ES. */
  private double sigma;

  private boolean performCmaEs = true;

  /**  Crossover mean. */
  private double crm; 
  /** Crossover standard deviation. */
  private double crs; 

  private int errorsNumber = 4;

  public DcmaEsConfig(AbstractGeneticAlgorithm configuration, int dimension) {
    n = dimension;

    stopFitness = 1e-12;
    stopEval = 1e8;
    gMax = 1e6;

    offSize = configuration.getPopulationSize(); //offSize = n * Math.round(28 / Math.sqrt(n));

    mu = offSize / 2;

    B = RichMatrix.eye(n);
    D = new RichArray(n, 1);
    C = RichMatrix.covariance(B, D);
    invsqrtC = RichMatrix.invsqrtCovariance(B, D);
    eigeneval = 0;
    chiN = Math.pow(n, 0.5) * (1 - 1D / (4 * n) + 1D / (21 * Math.pow(n, 2)));

    counteval = 0;

    offX = RichMatrix.zeros(Double.valueOf(n).intValue(), Double.valueOf(offSize).intValue());
    offV = RichMatrix.zeros(Double.valueOf(n).intValue(), Double.valueOf(offSize).intValue());
    offFitness = new RichArray(Double.valueOf(offSize).intValue(), 1D).apply(new Operation() {
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
    }).normalize();
  }
  
  public int getN() {
    return n;
  }

  public double getStopFitness() {
    return stopFitness;
  }

  public double getStopEval() {
    return stopEval;
  }

  public double getgMax() {
    return gMax;
  }

  public double getOffSize() {
    return offSize;
  }

  public double getMu() {
    return mu;
  }

  public RichArray getWeights() {
    return weights;
  }

  public RichMatrix getB() {
    return B;
  }

  public RichArray getD() {
    return D;
  }

  public RichMatrix getC() {
    return C;
  }

  public RichMatrix getInvsqrtC() {
    return invsqrtC;
  }

  public double getEigeneval() {
    return eigeneval;
  }

  public double getChiN() {
    return chiN;
  }

  public int getCounteval() {
    return counteval;
  }

  public RichMatrix getOffX() {
    return offX;
  }

  public RichMatrix getOffV() {
    return offV;
  }

  public RichArray getOffFitness() {
    return offFitness;
  }

  public RichArray getXmean() {
    return xmean;
  }

  public double getSigma() {
    return sigma;
  }

  public boolean isPerformCmaEs() {
    return performCmaEs;
  }

  public double getCrm() {
    return crm;
  }

  public double getCrs() {
    return crs;
  }

  public int getErrorsNumber() {
    return errorsNumber;
  }

  public void setStopFitness(double stopFitness) {
    this.stopFitness = stopFitness;
  }

  public void setStopEval(double stopEval) {
    this.stopEval = stopEval;
  }

  public void setgMax(double gMax) {
    this.gMax = gMax;
  }

  public void setOffSize(double offSize) {
    this.offSize = offSize;
  }

  public void setMu(double mu) {
    this.mu = mu;
  }

  public void setWeights(RichArray weights) {
    this.weights = weights;
  }

  public void setB(RichMatrix B) {
    this.B = B;
  }

  public void setD(RichArray D) {
    this.D = D;
  }

  public void setC(RichMatrix C) {
    this.C = C;
  }

  public void setInvsqrtC(RichMatrix invsqrtC) {
    this.invsqrtC = invsqrtC;
  }

  public void setEigeneval(double eigeneval) {
    this.eigeneval = eigeneval;
  }

  public void setChiN(double chiN) {
    this.chiN = chiN;
  }

  public void setCounteval(int counteval) {
    this.counteval = counteval;
  }

  public void setOffX(RichMatrix offX) {
    this.offX = offX;
  }

  public void setOffV(RichMatrix offV) {
    this.offV = offV;
  }

  public void setOffFitness(RichArray offFitness) {
    this.offFitness = offFitness;
  }

  public void setXmean(RichArray xmean) {
    this.xmean = xmean;
  }

  public void setSigma(double sigma) {
    this.sigma = sigma;
  }

  public void setPerformCmaEs(boolean performCmaEs) {
    this.performCmaEs = performCmaEs;
  }

  public void setCrm(double crm) {
    this.crm = crm;
  }

  public void setCrs(double crs) {
    this.crs = crs;
  }

  public void setErrorsNumber(int errorsNumber) {
    this.errorsNumber = errorsNumber;
  }

  void addCountEval() {
    this.counteval++;
  }
}
