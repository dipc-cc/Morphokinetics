package Genetic_Algorithm.Genetic_Operators.Recombination;

import java.util.Arrays;
import java.util.Collections;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;
import utils.akting.operations.OperationFactory;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import Genetic_Algorithm.DcmaEsConfig;
import Genetic_Algorithm.IndividualGroup;
import Genetic_Algorithm.Population;

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
		
		mueff = Math.pow(config.weights.sum(), 2) / config.weights.apply(OperationFactory.pow(2)).sum();
		
		pc = new RichArray(config.n, 0);
		ps = new RichArray(config.n, 0);
		
		cc = (4 + mueff / config.n) / (config.n + 4 + 2 * mueff / config.n);
		cs = (mueff + 2) / (config.n + mueff + 5);
		c1 = 2 / (Math.pow(config.n + 1.3, 2) + mueff);
		cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1D / mueff) / (Math.pow(config.n + 2, 2) + mueff));
		damps = 1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (config.n + 1)) - 1) + cs;
		
		invsqrtC = RichMatrix.invsqrtCovariance(config.B, config.D);
	}

	@Override
	public Population recombinate(IndividualGroup[] groups) {
		Population offspring = new Population(groups.length);
		
		// Sort by fitness and compute weighted mean into xmean.
		Integer[] offIndex = config.offFitness.sortedIndexes();
		RichArray xold = config.xmean.copy();
		Integer[] reducedIndex = Arrays.copyOfRange(offIndex, 0, Double.valueOf(config.mu).intValue());
		config.xmean = config.offX.recombinate(reducedIndex).multiply(config.weights);
		
		// Cumulation: Update evolution paths.
		ps = ps.apply(OperationFactory.multiply(1 - cs)).sum(
				invsqrtC.apply(
						OperationFactory.multiply(Math.sqrt(cs * (2 - cs) * mueff))).multiply(
						config.xmean.deduct(xold).apply(OperationFactory.divide(config.sigma))));
		
		double hsig = (ps.apply(OperationFactory.pow(2)).sum()
				/ (1 - Math.pow(1 - cs, 2 * config.counteval / config.offSize)) / config.n < 2 + 4 / (config.n + 1)) ? 1 : 0; 
		
		pc = pc.apply(OperationFactory.multiply(1 - cc)).sum(
				config.xmean.deduct(xold).apply(
						OperationFactory.divide(config.sigma)).apply(
								OperationFactory.multiply(hsig * Math.sqrt(cc * (2 - cc) * mueff))));
		
		// Adapt covariance matrix C.
		RichMatrix artmp = config.offX.recombinate(reducedIndex).deduct(
				RichMatrix.repmat(xold, Double.valueOf(config.mu).intValue())).apply(OperationFactory.multiply(1 / config.sigma));
		
		config.C = config.C.apply(OperationFactory.multiply(1 - c1 - cmu)).sum(
				pc.multiply(pc.transpose()).sum(
						config.C.apply(OperationFactory.multiply((1 - hsig) * cc * (2 - cc))
				).apply(OperationFactory.multiply(c1))
			).sum(artmp.multiply(RichMatrix.diag(config.weights)).multiply(artmp.transpose()).apply(OperationFactory.multiply(cmu))));
		
		// Adapt step size sigma.
		config.sigma = config.sigma * Math.exp((cs / damps) * (ps.norm() / config.chiN - 1));
		
		// Update B and D from C.
		if (config.counteval - config.eigeneval > config.offSize / (c1 + cmu) / config.n / 10) {
			config.eigeneval = config.counteval;
			
			// Enforce symmetry.
			config.C = config.C.triu(0).sum(config.C.triu(1).transpose());
			
			// Eigen decomposition, B==normalized eigenvectors.
			EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(new DenseDoubleMatrix2D(config.C.getPureMatrix()));
			
			config.B = new RichMatrix(eigenvalueDecomposition.getV().toArray());
			
			RichMatrix auxD = new RichMatrix(eigenvalueDecomposition.getD().toArray());
			// D contains standard deviations now.
			config.D = auxD.diag().apply(OperationFactory.sqrt());
			
			invsqrtC = RichMatrix.invsqrtCovariance(config.B, config.D);
		}
		
		// Update P and F:
		// P = 0.5 * ( 1 + G / Gmax )
		config.p = 1;
		// Crossover mean.
		config.crm = 0.5;
		// Crossover standard deviation.
		config.crs = 0.1;
		
		if (config.performCmaEs && ( config.offFitness.avg() - Collections.min(config.offFitness) < 10)) {
			config.p = 0.5;
			// Crossover mean.
			config.crm = 1.0;
			// Crossover standard deviation.
			config.crs = 0.1;
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
			auxInd = config.xmean.sum(
					config.B.apply(OperationFactory.multiply(config.sigma)).multiply(config.D.multiply(RichArray.randn(config.n)))
			).apply(OperationFactory.multiply(1 - config.p)).sum(
					auxInd.apply(OperationFactory.multiply(config.p))
			);
			
			offspring.setIndividual(auxInd.toIndividual(config.errorsNumber), k);
		}
		
		return offspring;
	}

}
