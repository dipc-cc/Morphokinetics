package geneticAlgorithm;

import java.util.Arrays;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.OperationFactory;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.geneticOperators.evaluationFunctions.BasicEvaluator;
import geneticAlgorithm.geneticOperators.mutation.CrossoverMutator;
import geneticAlgorithm.geneticOperators.recombination.DifferentialRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistAllReinsertion;
import graphicInterfaces.gaConvergence.IgaProgressFrame;

public class GeneticAlgorithmDcmaEs implements IGeneticAlgorithm {
	
	private Population population;
    private GeneticAlgorithmConfiguration config;
    private BasicEvaluator evaluator;
    private int currentIteration = 0;
    private int totalIterations = 1;
    private IgaProgressFrame graphics;
    
    private DcmaEsConfig dcmaEsConfig;
    
    Integer[] offIndex;
    Integer[] reducedIndex;
    
    public GeneticAlgorithmDcmaEs(GeneticAlgorithmConfiguration configuration) {
    	config = configuration;
    	evaluator = new BasicEvaluator();
    }

	@Override
	public IGeneticAlgorithm initialize() {
		population = config.initialization.createRandomPopulation(config.population_size);
		
		// Inicializamos la clase que contiene variables globales del algoritmo.
		dcmaEsConfig = new DcmaEsConfig(config, population.getIndividual(0).getGeneSize());
		config.recombination = new DifferentialRecombination(dcmaEsConfig);
		config.mutation = new CrossoverMutator(dcmaEsConfig);
		config.reinsertion = new ElitistAllReinsertion(dcmaEsConfig);
        
		config.restriction.apply(this.population);
        
		//evaluator.evaluate_and_order(this.population, config.mainEvaluator, config.otherEvaluators);
		double[] fitness = config.mainEvaluator.evaluate(population);
		//double[] fitness = myEvaluate(population);
		for (int i = 0; i < fitness.length; i++) {
			dcmaEsConfig.offFitness.set(i, fitness[i]);
			population.getIndividual(i).setError(0, fitness[i]);
			dcmaEsConfig.counteval++;
		}
		
		offIndex = dcmaEsConfig.offFitness.sortedIndexes();
		reducedIndex = Arrays.copyOfRange(offIndex, 0, Double.valueOf(dcmaEsConfig.mu).intValue());
		dcmaEsConfig.offX = new RichMatrix(population);
		dcmaEsConfig.xmean = dcmaEsConfig.offX.recombinate(reducedIndex).multiply(dcmaEsConfig.weights);
		dcmaEsConfig.sigma = dcmaEsConfig.offX.recombinate(reducedIndex).transpose().std().std();
		
		Population p= population;
		p.order();
        System.out.println("=============");
        for (int i = 0; i < p.size(); i++) {
        	System.out.print(p.getIndividual(i).getTotalError()+"| \t");
        
        	for (int a=0;a<p.getIndividual(i).getGeneSize();a++) {
        		System.out.print(p.getIndividual(i).getGene(a)+" \t");
        	}
        	
        	System.out.println();
        }
        
        if (graphics != null) {
            graphics.clear();
        }
        
        return this;
	}
	
	private void iterateOneStep() {		
		IndividualGroup[] trios = config.selection.Select(population, config.population_size);
        Population offspring = config.recombination.recombinate(trios);

        config.mutation.mutate(offspring, null);
        config.restriction.apply(offspring);
        /*for (int i = 0; i < offspring.size(); i++) {
        	Individual ind = offspring.getIndividual(i);
        	for (int j = 0; j < ind.getGeneSize(); j++) {
        		double val = ind.getGene(j);
        		ind.setGene(j, Math.max(-500, Math.min(500, val)));
        	}
        }*/
        //this.evaluator.evaluate_and_order(offspring, this.config.mainEvaluator, this.config.otherEvaluators);
        double[] fitness = config.mainEvaluator.evaluate(offspring);
        //double[] fitness = myEvaluate(offspring);
		for (int i = 0; i < fitness.length; i++) {
			//dcmaEsConfig.offFitness.set(i, fitness[i]);
			offspring.getIndividual(i).setError(0, fitness[i]);
			dcmaEsConfig.counteval++;
		}

        //sometimes it is good to reevaluate the whole population
       // if (currentIteration > 0 && currentIteration % 25 == 0) {
            config.restriction.apply(population);
        	/*for (int i = 0; i < population.size(); i++) {
            	Individual ind = population.getIndividual(i);
            	for (int j = 0; j < ind.getGeneSize(); j++) {
            		double val = ind.getGene(j);
            		ind.setGene(j, Math.max(-500, Math.min(500, val)));
            	}
            }*/
            //this.evaluator.evaluate_and_order(this.population, this.config.mainEvaluator, this.config.otherEvaluators);;
        
		//fitness = config.mainEvaluator.evaluate(population);
           
		//fitness = myEvaluate(population);
           
	/*	
		 for (int i = 0; i < fitness.length; i++) {
    			dcmaEsConfig.offFitness.set(i, fitness[i]);
    			population.getIndividual(i).setError(0, fitness[i]);
    			dcmaEsConfig.counteval++;
    		}
        }*/
                   
       population = config.reinsertion.Reinsert(population, offspring, 0);
       
       offIndex = dcmaEsConfig.offFitness.sortedIndexes();
       dcmaEsConfig.offX = new RichMatrix(population);
    }
	
	@Override
	public void iterate(int steps) {
		totalIterations = steps;
        
		while (dcmaEsConfig.counteval < dcmaEsConfig.stopEval && currentIteration < steps) {
			currentIteration++;
			
			iterateOneStep();
			
			addToGraphics();
			
			if (dcmaEsConfig.offFitness.apply(OperationFactory.deduct(dcmaEsConfig.offFitness.min())).allLessOrEqualThan(dcmaEsConfig.stopFitness)
					|| dcmaEsConfig.D.max() > 1e7 * dcmaEsConfig.D.min()) {
				break;
			}
		     if(this.getBestError()<180)
		    	 break;
		}
		
		offIndex = dcmaEsConfig.offFitness.sortedIndexes();
		dcmaEsConfig.offX = dcmaEsConfig.offX.recombinate(offIndex);
		double fmin = dcmaEsConfig.offFitness.get(0);
		RichArray xmin = dcmaEsConfig.offX.get(offIndex[0]);
		
		System.out.println(dcmaEsConfig.counteval + ": " + fmin);
		System.out.println(xmin);
	}
	
	private double[] myEvaluate(Population population) {
		double[] values = new double[population.size()];
		
		for (int i = 0; i < population.size(); i++) {
			values[i] = TestSuite.fschwefel(new RichArray(population.getIndividual(i)));
		}
		
		return values;
	}

	@Override
	public int getCurrentIteration() {
		return currentIteration;
	}

	@Override
	public int getTotalIterations() {
		return totalIterations;
	}
	
	@Override
	public float[] getProgressPercent() {
		float[] progress = new float[3];

        progress[0] = currentIteration * 100.0f / totalIterations;

        float[] subprogress = evaluator.getProgressPercent();
        progress[1] = subprogress[0];
        progress[2] = subprogress[1];

        return progress;
	}

	@Override
	public void setGraphics(IgaProgressFrame graphics) {
		this.graphics = graphics;
	}
	
	private void addToGraphics() {
        if (graphics != null) {
            graphics.addNewBestIndividual(getBestIndividual());
        }
    }
	
	public Individual getBestIndividual() {
		Population p =  new Population(population.getIndividuals());
		p.order();
		return p.getIndividual(0);
    }

	@Override
	public double getBestError() {
		Population p =  new Population(population.getIndividuals());
		p.order();
		return p.getIndividual(0).getTotalError();
	}

	@Override
	public Individual getIndividual(int pos) {
		return population.getIndividual(pos);
	}
	
	private void scaleIndividualRates(Population population) {

        for (int i = 0; i < population.size(); i++) {
            Individual individual = population.getIndividual(i);
            double factor = config.expected_simulation_time / individual.getSimulationTime();
            for (int j = 0; j < individual.getGeneSize(); j++) {
                individual.setGene(j, individual.getGene(j) / factor);
            }
        }
    }
}
