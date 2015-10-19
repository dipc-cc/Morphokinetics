package geneticAlgorithm.geneticOperators.recombination;

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

  public double mueff;

  public double cc;
  private double cs;
  public double c1;
  public double cmu;
  public double damps;

  public RichArray pc;
  private RichArray ps;

  private RichMatrix invsqrtC;

  public DifferentialRecombination(DcmaEsConfig config) {
    this.config = config;

    mueff = Math.pow(config.getWeights().sum(), 2) / config.getWeights().apply(OperationFactory.pow(2)).sum();

    pc = new RichArray(config.getN(), 0);
    ps = new RichArray(config.getN(), 0);

    cc = (4 + mueff / config.getN()) / (config.getN() + 4 + 2 * mueff / config.getN());
    cs = (mueff + 2) / (config.getN() + mueff + 5);
    c1 = 2 / (Math.pow(config.getN() + 1.3, 2) + mueff);
    cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1D / mueff) / (Math.pow(config.getN() + 2, 2) + mueff));
    damps = 1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (config.getN() + 1)) - 1) + cs;

    invsqrtC = RichMatrix.invsqrtCovariance(config.getB(), config.getD());
  }

  @Override
  public Population recombinate(IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);

    // Sort by fitness and compute weighted mean into xmean.
    Integer[] offIndex = config.getOffFitness().sortedIndexes();
    RichArray xold = config.getXmean().copy();
    Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, Double.valueOf(config.getMu()).intValue());
    config.setXmean(config.getOffX().recombinate(reducedIndex).multiply(config.getWeights()));

    // Cumulation: Update evolution paths.
    ps = ps.apply(OperationFactory.multiply(1 - cs)).sum(
            invsqrtC.apply(
                    OperationFactory.multiply(Math.sqrt(cs * (2 - cs) * mueff))).multiply(
                    config.getXmean().deduct(xold).apply(OperationFactory.divide(config.getSigma()))));

    double hsig = (ps.apply(OperationFactory.pow(2)).sum()
            / (1 - Math.pow(1 - cs, 2 * config.getCounteval() / config.getOffSize())) / config.getN() < 2 + 4 / (config.getN() + 1)) ? 1 : 0;

    pc = pc.apply(OperationFactory.multiply(1 - cc)).sum(
            config.getXmean().deduct(xold).apply(
                    OperationFactory.divide(config.getSigma())).apply(
                    OperationFactory.multiply(hsig * Math.sqrt(cc * (2 - cc) * mueff))));

    // Adapt covariance matrix C.
    RichMatrix artmp = config.getOffX().recombinate(reducedIndex).deduct(
            RichMatrix.repmat(xold, Double.valueOf(config.getMu()).intValue())).apply(OperationFactory.multiply(1 / config.getSigma()));

    config.setC(config.getC().apply(OperationFactory.multiply(1 - c1 - cmu)).sum(
            pc.multiply(pc.transpose()).sum(
                    config.getC().apply(OperationFactory.multiply((1 - hsig) * cc * (2 - cc))
                    ).apply(OperationFactory.multiply(c1)))
    ).sum(artmp.multiply(RichMatrix.diag(config.getWeights())).multiply(artmp.transpose()).apply(OperationFactory.multiply(cmu))));

	// Adapt step size sigma.
    //config.sigma = config.sigma * Math.exp((cs / damps) * (ps.norm() / config.chiN - 1));
    config.setSigma(Math.max(0.1, config.getSigma() * Math.exp((cs / damps) * (ps.norm() / config.getChiN() - 1))));

    // Update B and D from C.
    if (config.getCounteval() - config.getEigeneval() > config.getOffSize() / (c1 + cmu) / config.getN() / 10) {
      config.setEigeneval(config.getCounteval());

      // Enforce symmetry.
      config.setC(config.getC().triu(0).sum(config.getC().triu(1).transpose()));

      // Eigen decomposition, B==normalized eigenvectors.
      EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(new DenseDoubleMatrix2D(config.getC().getPureMatrix()));

      config.setB(new RichMatrix(eigenvalueDecomposition.getV().toArray()));

      RichMatrix auxD = new RichMatrix(eigenvalueDecomposition.getD().toArray());
      // D contains standard deviations now.
      config.setD(auxD.diag().apply(OperationFactory.sqrt()));

      invsqrtC = RichMatrix.invsqrtCovariance(config.getB(), config.getD());
    }

	// Update P and F:
    // P = 0.5 * ( 1 + G / Gmax )
    config.setP(1);
    // Crossover mean.
    config.setCrm(0.5);
    // Crossover standard deviation.
    config.setCrs(0.1);

    boolean cond1 = (config.getOffFitness().avg() - Collections.min(config.getOffFitness()) < 10) && (Collections.max(config.getD()) / Collections.min(config.getD()) < 10);
    boolean cond2 = ((config.getSigma() * Math.sqrt(Collections.max(config.getC().diag()))) < 10) && (Collections.max(config.getD()) / Collections.min(config.getD()) > 10);
    boolean cond = (cond1 || cond2);
    if (config.isPerformCmaEs() && cond) {
      config.setP(0.5);
      // Crossover mean.
      config.setCrm(0.85);
			// Crossover standard deviation.
      //config.crs = 0.1;
    }

    RichArray f = RichArray.rand(Double.valueOf(groups.length).intValue()).apply(new Operation() {
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
      auxInd = config.getXmean().sum(
              config.getB().apply(OperationFactory.multiply(config.getSigma())).multiply(config.getD().multiply(RichArray.randn(config.getN())))
      ).apply(OperationFactory.multiply(1 - config.getP())).sum(
              auxInd.apply(OperationFactory.multiply(config.getP()))
      );

      offspring.setIndividual(auxInd.toIndividual(config.getErrorsNumber()), k);
    }

    return offspring;
  }

}
