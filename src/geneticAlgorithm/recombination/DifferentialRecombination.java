package geneticAlgorithm.recombination;

import java.util.Arrays;
import java.util.Collections;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;
import utils.akting.operations.OperationFactory;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

public class DifferentialRecombination implements IRecombination {

  private final  double cc;
  /** C_sigma. */
  private final double cSigma;
  private final double c1;
  private final double cmu;
  /** Damping for sigma. */
  private final double dampSigma;

  /** P_c of sigma. Evolution paths for C and sigma*/
  private RichArray pc;
  /** P of sigma. Used for step size control. */
  private RichArray pSigma;
  /** Step size in CMA-ES. It stores the standard deviation of all objective variables. */
  private double sigma;  
  
  /** B defines the coordinate system. B is an orthogonal matrix. Columns of B are eigenvectors of C
   * with unit length and correspond to the diagonal elements of D.
   */
  private RichMatrix B;
  /**
   * Covariance matrix C. Covariance matrix at generation g.
   */
  private RichMatrix C;
  /**
   * D contains the standard deviations. Diagonal D defines the scaling. A diagonal matrix (thus,
   * represented with an array). The diagonal elements of D are square roots of eigenvalues of C and
   * correspond to the respective columns of B.
   */
  private RichArray D;
  /**
   * Track update of B and D.
   */
  private double eigeneval;
  /** C^-1/2 */
  private RichMatrix invsqrtC;
  /** Expectation of ||N(0,I)|| == norm(randn(N,1)). */
  private final double chiN;
  /** Number of objective variables/problem dimension. */
  private final int dimensions;  
  /** Mean of objective variables. Recombination, new mean value in CMA-ES. */
  private RichArray xMean;  
  /** Number of parents for recombination in CMA-ES. */
  private final int mu;
  /** Variance effective selection mass. It holds 1 <= muEffective <= mu */
  private final double muEffective;
  /** Array for weighted recombination in CMA-ES. Positive weight coefficients for recombination. */
  private RichArray weights;
  
  /** Counter for the number of evaluations. A good optimisation method will minimise this number. */
  private int counteval;
  
  private final int errorsNumber;

  /**
   * 
   * @param populationSize Number of individuals of the population
   * @param dimensions Number of different genes
   */
  public DifferentialRecombination(int populationSize, int dimensions) {
    this.dimensions = dimensions;
    mu = populationSize / 2;
    eigeneval = 0;
    counteval = 0;
    
    createWeightsArray();
    chiN = Math.pow(dimensions, 0.5) * (1 - 1D / (4 * dimensions) + 1D / (21 * Math.pow(dimensions, 2)));
    muEffective = Math.pow(weights.sum(), 2) / weights.apply(OperationFactory.pow(2)).sum();

    pc = new RichArray(dimensions, 0);
    pSigma = new RichArray(dimensions, 0);

    cc = (4 + muEffective / dimensions) / (dimensions + 4 + 2 * muEffective / dimensions);
    cSigma = (muEffective + 2) / (dimensions + muEffective + 5);
    c1 = 2 / (Math.pow(dimensions + 1.3, 2) + muEffective);
    cmu = Math.min(1 - c1, 2 * (muEffective - 2 + 1D / muEffective) / (Math.pow(dimensions + 2, 2) + muEffective));
    dampSigma = 1 + 2 * Math.max(0, Math.sqrt((muEffective - 1) / (dimensions + 1)) - 1) + cSigma;

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
  
  /**
   * Initialises the first population and assigns values to sigma and xMean. 
   * @param population 
   */
  @Override
  public void initialise(Population population) {
    Integer[] offIndex = population.getOffFitness().sortedIndexes();
    Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, mu);
    population.newOffspringGenes();
    xMean = population.getOffspringGenes().recombinate(reducedIndex).multiply(weights);
    sigma = population.getOffspringGenes().recombinate(reducedIndex).transpose().std().std();
    System.out.println("\t\t\t Initialising sigma "+sigma);
  }
  
  /**
   * Creates a new offspring population. The new population size has to be equal to the original population size.
   * @param population original population. For the moment only used to get the fitness errors.
   * @param groups new individuals.
   * @return 
   */
  @Override
  public Population recombinate(Population population, IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);
    counteval += offspring.size(); // Adding to the evaluation counter the number of individuals of the population
    // Sort by fitness and compute weighted mean into xmean.
    Integer[] offIndex = population.getOffFitness().sortedIndexes();
    RichArray xold = xMean.copy();
    Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, mu);
    xMean = population.getOffspringGenes().recombinate(reducedIndex).multiply(weights);

    // Cumulation: Update evolution paths.
    pSigma = pSigma.apply(OperationFactory.multiply(1 - cSigma)).sum(
            invsqrtC.apply(
                    OperationFactory.multiply(Math.sqrt(cSigma * (2 - cSigma) * muEffective))).multiply(
                    xMean.deduct(xold).apply(OperationFactory.divide(sigma))));

    // The Heaviside function (hSigma) stalls the update of pc if ||pc|| is large.
    double hSigma = (pSigma.apply(OperationFactory.pow(2)).sum()
            / (1 - Math.pow(1 - cSigma, 2 * counteval / offspring.size())) / dimensions < 2 + 4 / (dimensions + 1)) ? 1 : 0;

    //Covariance matrix adaptation
    pc = pc.apply(OperationFactory.multiply(1 - cc)).sum(
            xMean.deduct(xold).apply(
            OperationFactory.divide(sigma)).apply(
            OperationFactory.multiply(hSigma * Math.sqrt(cc * (2 - cc) * muEffective))));

    // Adapt covariance matrix C.
    RichMatrix artmp = population.getOffspringGenes().recombinate(reducedIndex).deduct(
            RichMatrix.repmat(xold, mu)).apply(OperationFactory.multiply(1 / sigma));

    C = C.apply(OperationFactory.multiply(1 - c1 - cmu)).sum(
            pc.multiply(pc.transpose()).sum(
                    C.apply(OperationFactory.multiply((1 - hSigma) * cc * (2 - cc))
                    ).apply(OperationFactory.multiply(c1)))
    ).sum(artmp.multiply(RichMatrix.diag(weights)).multiply(artmp.transpose()).apply(OperationFactory.multiply(cmu)));

	// Adapt step size, sigma. Determine new overall variance (σ) = step size
    //sigma = sigma * Math.exp((cs / damps) * (ps.norm() / chiN - 1));
    sigma = Math.min(Math.max(0.1, sigma * Math.exp((cSigma / dampSigma) * (pSigma.norm() / chiN - 1))),1e50);
    System.out.println("\t\t\t sigma = "+sigma);
    // Update B and D from C.
    if (counteval - eigeneval > offspring.size() / (c1 + cmu) / dimensions / 10) { // to achieve O(N^2)
      eigeneval = counteval;

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
    offspring.setCrm(0.5);

    boolean cond1 = (population.getOffFitness().avg() - Collections.min(population.getOffFitness()) < 10) && (Collections.max(D) / Collections.min(D) < 10);
    boolean cond2 = ((sigma * Math.sqrt(Collections.max(C.diag()))) < 10) && (Collections.max(D) / Collections.min(D) > 10);
    if (cond1 || cond2) {
      p = 0.5;
      // Crossover mean.
      offspring.setCrm(0.85);
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
      auxInd = xMean.sum(
              B.apply(OperationFactory.multiply(sigma)).multiply(D.multiply(RichArray.randn(dimensions)))
      ).apply(OperationFactory.multiply(1 - p)).sum(
              auxInd.apply(OperationFactory.multiply(p))
      );

      offspring.setIndividual(auxInd.toIndividual(errorsNumber), k);
    }

    offspring.setOffspringGenes(population.getOffspringGenes());
    return offspring;
  }
  
  /**
   * Condition exceeds 1e14
   * @return 
   */
  @Override
  public boolean isDtooLarge() {
    return D.max() > 1e7 * D.min();
  }
}
