package geneticAlgorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

public class DcmaEsConfig {

  private int n;
	//public RichArray xMin = new RichArray(n, -500);
  //public RichArray xMax = new RichArray(n, 500);

  private double stopFitness;
  private double stopEval;
  private double gMax;

  private double offSize;

  private double mu;
  private RichArray weights;

  private RichMatrix B;
  /**
   * D contains the standard deviations
   */
  private RichArray D;
  private RichMatrix C;
  private RichMatrix invsqrtC;
  private double eigeneval;
  private double chiN;

  private int counteval;

  private RichMatrix offX;
  private RichMatrix offV;
  private RichArray offFitness;

  private RichArray xmean;
  private double sigma;

  private boolean performCmaEs = true;

  private double p;
  private double crm; /**  Crossover mean. */
  private double crs; /** Crossover standard deviation. */

  private int errorsNumber = 4;

  //public ArrayList<List<Double>> dat = new ArrayList<List<Double>>();
  //public ArrayList<List<Double>> datx = new ArrayList<List<Double>>();
  public DcmaEsConfig(GeneticAlgorithmConfiguration configuration, int dimension) {
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
      public double apply(double value) {
        return 1e8 * value;
      }
    });

    createWeightsArray();

  }

  private void createWeightsArray() {
    weights = RichArray.createArrayInRange(mu);
    weights = weights.apply(new Operation() {

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

  public double getP() {
    return p;
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

  public void setN(int n) {
    this.n = n;
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

  public void setP(double p) {
    this.p = p;
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
