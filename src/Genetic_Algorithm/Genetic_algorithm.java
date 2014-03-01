/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.BasicEvaluator;
import Graphic_interfaces.GA_convergence.IGA_progress_frame;

/**
 *
 * @author Nestor
 */
public class Genetic_algorithm implements IGenetic_algorithm {

    private Population population;
    private Genetic_algorithm_configuration config;
    private BasicEvaluator evaluator;
    private int currentIteration = 0;
    private int totalIterations = 1;
    private IGA_progress_frame graphics;

    public Genetic_algorithm(Genetic_algorithm_configuration configuration) {
        this.config = configuration;
        this.evaluator = new BasicEvaluator();
    }

    public IGenetic_algorithm initialize() {
        this.population = config.initialization.createRandomPopulation(config.population_size);
        this.config.restriction.apply(this.population);
        this.evaluator.evaluate_and_order(this.population, this.config.mainEvaluator, this.config.otherEvaluators);
        
         System.out.println("=============");
         for (int i = 0; i < 50; i++) {
        System.out.print(population.getIndividual(i).getTotalError()+"| \t");
        for (int a=0;a<population.getIndividual(i).getGeneSize();a++) System.out.print(population.getIndividual(i).getGene(a)+" \t");
         System.out.println();
         }
        
        this.scaleIndividualRates(this.population);
        
        if (graphics != null) {
            graphics.clear();
        }
        
        

        
        return this;
    }
    
    
    

    private void iterateOneStep() {
        Couple[] couples = this.config.selection.Select(this.population, this.config.offspring_size);
        Population offspring = this.config.recombination.recombinate(couples);

        int geneSize = population.getIndividual(0).getGeneSize();
        this.config.mutation.mutate(offspring, this.config.restriction.getNonFixedGenes(geneSize));
        this.config.restriction.apply(offspring);
        this.evaluator.evaluate_and_order(offspring, this.config.mainEvaluator, this.config.otherEvaluators);

        //sometimes it is good to reevaluate the whole population
        if (currentIteration > 0 && currentIteration % 25 == 0) {
            this.scaleIndividualRates(this.population);   
            this.config.restriction.apply(this.population);
            this.evaluator.evaluate_and_order(this.population, this.config.mainEvaluator, this.config.otherEvaluators);;   
        }
              
        
       this.config.reinsertion.Reinsert(population, offspring, this.config.population_replacements);

    }

    public void iterate(int steps) {
        totalIterations = steps;
        for (int i = 0; i < steps; i++) {
            currentIteration = i;
            iterateOneStep();
            addToGraphics();

        }
    }

    public double getBestError() {
        return population.getIndividual(0).getTotalError();
    }

    public Individual getIndividual(int pos) {
        return population.getIndividual(pos);
    }

    public Individual getBestIndividual() {
        return population.getIndividual(0);
    }

    public float[] getProgressPercent() {

        float[] progress = new float[3];

        progress[0] = currentIteration * 100.0f / totalIterations;

        float[] subprogress = evaluator.getProgressPercent();
        progress[1] = subprogress[0];
        progress[2] = subprogress[1];

        return progress;
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
    public void setGraphics(IGA_progress_frame graphics) {
        this.graphics = graphics;
    }

    private void addToGraphics() {
        if (graphics != null) {
            graphics.addNewBestIndividual(getBestIndividual());
        }
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
