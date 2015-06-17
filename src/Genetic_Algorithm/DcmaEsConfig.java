package Genetic_Algorithm;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.Operation;

public class DcmaEsConfig {

	public int n;
	//public RichArray xMin = new RichArray(n, -500);
	//public RichArray xMax = new RichArray(n, 500);
	
	public double stopFitness;
	public double stopEval;
	public double gMax;
	
	public double offSize;
	
	public double mu;
	public RichArray weights;	
	
	public RichMatrix B;
	public RichArray D;
	public RichMatrix C;
	public RichMatrix invsqrtC;
	public double eigeneval;
	public double chiN;
	
	public int counteval;
	
	public RichMatrix offX;
	public RichMatrix offV;
	public RichArray offFitness;
	
	public RichArray xmean;
	public double sigma;
	
	public boolean performCmaEs = true;
	
	public double p;
	public double crm;
	public double crs;
	
	public int errorsNumber = 4;
	
	//public ArrayList<List<Double>> dat = new ArrayList<List<Double>>();
	//public ArrayList<List<Double>> datx = new ArrayList<List<Double>>();
	
	public DcmaEsConfig(Genetic_algorithm_configuration configuration, int dimension) {
		n = dimension;
		
		stopFitness = 1e-8;
		stopEval =  1e8;
		gMax = 1e4;
		
		offSize = configuration.population_size; //offSize = n * Math.round(28 / Math.sqrt(n));
		
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
}
