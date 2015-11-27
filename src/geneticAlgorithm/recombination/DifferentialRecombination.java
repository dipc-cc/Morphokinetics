package geneticAlgorithm.recombination;

import java.util.Arrays;
import java.util.Collections;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;
import utils.akting.operations.OperationFactory;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

public class DifferentialRecombination implements IRecombination {

  private DcmaEsConfig config;

  private double mueff;

  private double cc;
  private double cs;
  private double c1;
  private double cmu;
  private double damps;

  private RichArray pc;
  private RichArray ps;
  
  /** B defines the coordinate system. */
  private RichMatrix B;
  /** Covariance matrix C. */
  private RichMatrix C;  
  /** D contains the standard deviations. */
  private RichArray D;
  /** Track update of B and D. */
  private double eigeneval;
  /** C^-1/2 */
  private RichMatrix invsqrtC;
  /** Step size in CMA-ES. */
  private double sigma;  
  /** Expectation of ||N(0,I)|| == norm(randn(N,1)). */
  private final double chiN;
  /** Number of objective variables/problem dimension. */
  private int dimensions;  
  /** Recombination, new mean value in CMA-ES. */
  private RichArray xmean;  
  /** Number of parents for recombination in CMA-ES. */
  private int mu;
  /** Array for weighted recombination in CMA-ES. */
  private RichArray weights;
  
  private final int errorsNumber;

  public DifferentialRecombination(DcmaEsConfig config, Population population) {
    this.config = config;
    dimensions = population.getIndividual(0).getGeneSize();
    mu = population.size() / 2;
    eigeneval = 0;
    
    createWeightsArray();
    chiN = Math.pow(dimensions, 0.5) * (1 - 1D / (4 * dimensions) + 1D / (21 * Math.pow(dimensions, 2)));
    mueff = Math.pow(weights.sum(), 2) / weights.apply(OperationFactory.pow(2)).sum();

    pc = new RichArray(dimensions, 0);
    ps = new RichArray(dimensions, 0);

    cc = (4 + mueff / dimensions) / (dimensions + 4 + 2 * mueff / dimensions);
    cs = (mueff + 2) / (dimensions + mueff + 5);
    c1 = 2 / (Math.pow(dimensions + 1.3, 2) + mueff);
    cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1D / mueff) / (Math.pow(dimensions + 2, 2) + mueff));
    damps = 1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (dimensions + 1)) - 1) + cs;

    B = RichMatrix.eye(dimensions);
    D = new RichArray(dimensions, 1);
    C = RichMatrix.covariance(B, D);
    invsqrtC = RichMatrix.invsqrtCovariance(B, D);
    
    errorsNumber = 4;
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
  
  public void initialise(Population population) {
    Integer[] offIndex = config.getOffFitness().sortedIndexes();
    Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, mu);
    config.setOffX(new RichMatrix(population));
    xmean = config.getOffX().recombinate(reducedIndex).multiply(weights);
    sigma = config.getOffX().recombinate(reducedIndex).transpose().std().std();
    
  }
  
  @Override
  public Population recombinate(IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);

    // Sort by fitness and compute weighted mean into xmean.
    Integer[] offIndex = config.getOffFitness().sortedIndexes();
    RichArray xold = xmean.copy();
    Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, mu);
    xmean = config.getOffX().recombinate(reducedIndex).multiply(weights);

    // Cumulation: Update evolution paths.
    ps = ps.apply(OperationFactory.multiply(1 - cs)).sum(
            invsqrtC.apply(
                    OperationFactory.multiply(Math.sqrt(cs * (2 - cs) * mueff))).multiply(
                    xmean.deduct(xold).apply(OperationFactory.divide(sigma))));

    double hsig = (ps.apply(OperationFactory.pow(2)).sum()
            / (1 - Math.pow(1 - cs, 2 * config.getCounteval() / offspring.size())) / dimensions < 2 + 4 / (dimensions + 1)) ? 1 : 0;

    pc = pc.apply(OperationFactory.multiply(1 - cc)).sum(
            xmean.deduct(xold).apply(
            OperationFactory.divide(sigma)).apply(
            OperationFactory.multiply(hsig * Math.sqrt(cc * (2 - cc) * mueff))));

    // Adapt covariance matrix C.
    RichMatrix artmp = config.getOffX().recombinate(reducedIndex).deduct(
            RichMatrix.repmat(xold, mu)).apply(OperationFactory.multiply(1 / sigma));

    C = C.apply(OperationFactory.multiply(1 - c1 - cmu)).sum(
            pc.multiply(pc.transpose()).sum(
                    C.apply(OperationFactory.multiply((1 - hsig) * cc * (2 - cc))
                    ).apply(OperationFactory.multiply(c1)))
    ).sum(artmp.multiply(RichMatrix.diag(weights)).multiply(artmp.transpose()).apply(OperationFactory.multiply(cmu)));

	// Adapt step size sigma.
    //config.sigma = config.sigma * Math.exp((cs / damps) * (ps.norm() / config.chiN - 1));
    sigma = Math.max(0.1, sigma * Math.exp((cs / damps) * (ps.norm() / chiN - 1)));

    // Update B and D from C.
    if (config.getCounteval() - eigeneval > offspring.size() / (c1 + cmu) / dimensions / 10) {
      eigeneval = config.getCounteval();

      // Enforce symmetry.
      C = C.triu(0).sum(C.triu(1).transpose());

      // Eigen decomposition, B==normalized eigenvectors.
      EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(new DenseDoubleMatrix2D(C.getPureMatrix()));

      B = new RichMatrix(eigenvalueDecomposition.getV().toArray());

      RichMatrix auxD = new RichMatrix(eigenvalueDecomposition.getD().toArray());
      // D contains standard deviations now.
      D = auxD.diag().apply(OperationFactory.sqrt());

      invsqrtC = RichMatrix.invsqrtCovariance(B, D);
    }

	// Update P and F:
    // P = 0.5 * ( 1 + G / Gmax )
    double p = 1;
    // Crossover mean.
    config.setCrm(0.5);

    boolean cond1 = (config.getOffFitness().avg() - Collections.min(config.getOffFitness()) < 10) && (Collections.max(D) / Collections.min(D) < 10);
    boolean cond2 = ((sigma * Math.sqrt(Collections.max(C.diag()))) < 10) && (Collections.max(D) / Collections.min(D) > 10);
    if (cond1 || cond2) {
      p = 0.5;
      // Crossover mean.
      config.setCrm(0.85);
	  // Crossover standard deviation.
      //config.crs = 0.1;
    }
    System.out.println("P=" + p);
    RichArray f = RichArray.rand(Double.valueOf(groups.length).intValue()).apply(new Operation() {
      @Override
      public double apply(double value) {
        return 0.5 + 0.5 * value;
      }
    });

    for (int k = 0; k < groups.length; k++) {
      RichArray k1 = new RichArray(groups[k].get(0));
      RichArray k2 = new RichArray(groups[k].get(1));
      RichArray k3 = new RichArray(groups[k].get(2));

      // Apply recombination according to DE.
      RichArray auxInd = k1.sum(
              k2.deduct(k3).apply(OperationFactory.multiply(f.get(k)))
      );

	  // Weighted sum of CMA-ES and DE values. CMA-ES value taken into 
      // account when P < 1.
      auxInd = xmean.sum(
              B.apply(OperationFactory.multiply(sigma)).multiply(D.multiply(RichArray.randn(dimensions)))
      ).apply(OperationFactory.multiply(1 - p)).sum(
              auxInd.apply(OperationFactory.multiply(p))
      );

      offspring.setIndividual(auxInd.toIndividual(errorsNumber), k);
    }

    return offspring;
  }
  
  /**
   * Condition exceeds 1e14
   * @return 
   */
  public boolean isDtooLarge() {
    return D.max() > 1e7 * D.min();
  }
}
