/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodiguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package geneticAlgorithm;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.BasicGrowthSimulation;
import basic.Parser;
import basic.SiSimulation;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.AgThreadedPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.BasicEvaluator;
import geneticAlgorithm.evaluationFunctions.BasicGrowthPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.IEvaluation;
import geneticAlgorithm.evaluationFunctions.SiBasicPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.mutation.IMutation;
import geneticAlgorithm.populationInitialisation.AgReduced6Initialisator;
import geneticAlgorithm.populationInitialisation.BasicGrowthInitialisator;
import geneticAlgorithm.populationInitialisation.IInitialisator;
import geneticAlgorithm.populationInitialisation.SiInitialisator;
import geneticAlgorithm.recombination.IRecombination;
import geneticAlgorithm.reinsertion.IReinsertion;
import geneticAlgorithm.restrictions.RestrictionOperator;
import geneticAlgorithm.restrictions.SiRestriction;
import graphicInterfaces.MainInterface;
import geneticAlgorithm.restrictions.AgReduced6Restriction;
import geneticAlgorithm.restrictions.BasicGrowthRestriction;
import geneticAlgorithm.selection.ISelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.kmcCore.IKmc;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.kmcCore.growth.BasicGrowthKmc;
import ratesLibrary.AgRatesFromPrbCox;
import ratesLibrary.BasicGrowthSyntheticRates;
import utils.akting.operations.OperationFactory;

/**
 *
 * @author N. Ferrando
 */
public abstract class AbstractGeneticAlgorithm implements IGeneticAlgorithm{

  private Parser parser;
  
  private Population population;
  private BasicEvaluator evaluator;
  private final AbstractPsdEvaluator mainEvaluator;
  private List<IEvaluation> otherEvaluators;
  private IInitialisator initialisation;
  private ISelection selection;
  private IMutation mutation;
  private IRecombination recombination;
  private IReinsertion reinsertion;
  private RestrictionOperator restriction;
  private final int populationSize;
  private final int offspringSize;
  private final int populationReplacements;
  private double expectedSimulationTime;
  /** Number of different genes. */
  private final int dimensions;
  private final double minValueGene;
  private final double maxValueGene;
  private final boolean expDistribution;
  
  private int currentIteration = 0;
  private int totalIterations = 1;
  private MainInterface mainInterface;
  
  private AbstractSimulation simulation;
  
  private double depositionRatePerSite;
  private double islandDensity;
  
  private Updater updater;
  /** The stop error. If the current error is below this number, stop. */
  private final double stopError;
  private IgaProgressFrame graphics;

  public AbstractGeneticAlgorithm(Parser parser, ISelection selection, IMutation mutation, IRecombination recombination, IReinsertion reinsertion) {
    this.parser = parser;
    this.selection = selection;
    this.mutation = mutation;
    this.recombination = recombination;
    this.reinsertion = reinsertion;
    
    this.evaluator = new BasicEvaluator();
    otherEvaluators = addNoMoreEvaluators();
    
    
    populationSize = parser.getPopulationSize();
    
    if (parser.getEvolutionaryAlgorithm().equals("dcma")) {
      offspringSize = parser.getPopulationSize();
      populationReplacements = 0;
    } else {
      offspringSize = parser.getOffspringSize();
      populationReplacements = parser.getPopulationReplacement();
    }
    dimensions = 6;
    stopError = parser.getStopError();
    totalIterations = parser.getTotalIterations();
    minValueGene = parser.getMinValueGene();
    maxValueGene = parser.getMaxValueGene();
    expDistribution = parser.isExpDistribution();
    
    switch (parser.getCalculationMode()) {
      case "basic":
        simulation = new BasicGrowthSimulation(parser);
        simulation.initialiseKmc();
        depositionRatePerSite = new BasicGrowthSyntheticRates().getDepositionRatePerSite();
        islandDensity = new BasicGrowthSyntheticRates().getIslandDensity(parser.getTemperature());
        ((AbstractGrowthKmc) simulation.getKmc()).setDepositionRate(depositionRatePerSite, islandDensity);
        initialisation = new BasicGrowthInitialisator();
        restriction = new BasicGrowthRestriction(dimensions, 1e-6, 1e9, parser.isEnergySearch());
        break;
      case "Ag":
        simulation = new AgSimulation(parser);
        simulation.initialiseKmc();
        float experitentalTemp = parser.getTemperature();
        depositionRatePerSite = new AgRatesFromPrbCox().getDepositionRatePerSite();
        islandDensity = new AgRatesFromPrbCox().getIslandDensity(experitentalTemp);
        ((AbstractGrowthKmc) simulation.getKmc()).setDepositionRate(depositionRatePerSite, islandDensity);
        initialisation = new AgReduced6Initialisator();
        restriction = new AgReduced6Restriction(dimensions, parser.getMinValueGene(), parser.getMaxValueGene(), parser.isEnergySearch());
        if (parser.isDiffusionFixed()) ((AgReduced6Restriction) restriction).fixDiffusion();
        break;
      case "Si":
        simulation = new SiSimulation(parser);
        simulation.initialiseKmc();
        initialisation = new SiInitialisator();
        restriction = new SiRestriction();
        break;
      default:
        System.err.println("This calculation mode is not implemented for evolutionary algorithm");
        System.err.println("Current calculation mode is " + parser.getCalculationMode());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    mainEvaluator = createMainEvaluator();
  }
  
  @Override
  public IGeneticAlgorithm initialise() {
    population = initialisation.createRandomPopulation(populationSize, dimensions, minValueGene, maxValueGene, expDistribution);
    restriction.apply(population);
    evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    recombination.initialise(population);

    System.out.println("==================================");
    System.out.println("Finished initial random population");
    System.out.println("==================================");
    clearGraphics();
    
    return this;
  }

  @Override
  public void iterateOneStep() {
    IndividualGroup[] couples = selection.Select(population, offspringSize);
    Population offspringPopulation = recombination.recombinate(population, couples);
    offspringPopulation.setIterationNumber(currentIteration);

    int geneSize = population.getIndividual(0).getGeneSize();
    mutation.mutate(offspringPopulation, restriction.getNonFixedGenes(geneSize));
    restriction.apply(offspringPopulation);
    evaluator.evaluateAndOrder(offspringPopulation, mainEvaluator, otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (reevaluate()) {
      restriction.apply(population);
      evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    }

    reinsertion.Reinsert(population, offspringPopulation, populationReplacements);
  }
  
  /**
   * Evaluator evaluation
   *
   * @return threaded or basic evaluator
   */
  private AbstractPsdEvaluator createMainEvaluator() {
    AbstractPsdEvaluator evaluatorTmp;
    int sizeX = (int) (parser.getCartSizeX() * parser.getPsdScale());
    int sizeY = (int) (parser.getCartSizeY() * parser.getPsdScale());
    
    switch (parser.getCalculationMode()) {
      case "basic":
        evaluatorTmp = new BasicGrowthPsdEvaluator((BasicGrowthKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, sizeX, sizeY, parser.getEvaluatorTypes(), parser.getHierarchyEvaluator(), parser.getEvolutionarySearchType(), parser.getTemperature());
        break;
      case "Ag":
        if (parser.isEvaluatorParallel()) {
          evaluatorTmp = new AgThreadedPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, 2, sizeX, sizeY, parser.getEvaluatorTypes());
        } else {
          evaluatorTmp = new AgBasicPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, sizeX, sizeY, parser.getEvaluatorTypes(), parser.getHierarchyEvaluator(), parser.getEvolutionarySearchType(), parser.getTemperature());
        }
        break;
      case "Si":
        configureSiKmc(); // to be changed => directly select these parameters from the parser
        if (parser.isEvaluatorParallel()) {
          evaluatorTmp = new SiThreadedPsdEvaluator(parser, 10000, 8);
        } else {
          evaluatorTmp = new SiBasicPsdEvaluator(parser, 1000);
        }
        break;
      default:
        evaluatorTmp = null;// no error handling here. It is called from AbstractGeneticAlgorithm, where is checked that the calculation mode is correct.
        break;
    }

    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);

    return evaluatorTmp;
  }
  
  private void configureSiKmc() {
    parser.setListType("binned");
    parser.setBinsLevels(20);
    parser.setExtraLevels(1);
    parser.setMillerX(1);
    parser.setMillerY(0);
    parser.setMillerZ(0);
    parser.setCartSizeX(32);
    parser.setCartSizeY(32);
    parser.setCartSizeZ(48);
  }
  
  @Override 
  public void iterate() {
    for (int i = 1; i <= totalIterations; i++) {
      currentIteration = i;
      iterateOneStep();
      addToGraphics();
      System.out.println("For iteration " + currentIteration + " the best error is " + population.getBestError());
      if (exitCondition()) { // While using DCMA-ES this condition can occur and we must finish
        break;
      }
      if (population.getBestError() < stopError) {
        System.out.println("Stopping because the error is " + population.getBestError() + " (" + stopError + ")");
        break;
      }
    }
  }
  
  protected boolean isDtooLarge() {
    return recombination.isDtooLarge();
  }

  /**
   * Break if fitness is good enough or condition exceeds 1e14. Better termination methods are advisable.
   * 
   * @return exit search or not
   */
  @Override
  public boolean exitCondition() {
  /** Stop if mean(fitness) - min(fitness) < stopFitness (minimization). */
    double stopFitness = 1e-12;
    boolean cond1 = population.getOffFitness().apply(OperationFactory.deduct(population.getOffFitness().min())).allLessOrEqualThan(stopFitness);
    boolean cond2 = recombination.isDtooLarge();
    if (cond1 || cond2) {
      System.out.println("Exiting for an unknown reason " + cond1 + " " + cond2);
      return false;
    }
    return false;
  }    
    
  public void setExpectedSimulationTime(double expectedSimulationTime) {
    this.expectedSimulationTime = expectedSimulationTime;
    mainEvaluator.setExpectedSimulationTime(expectedSimulationTime);
  }

  public AbstractPsdEvaluator getMainEvaluator() {
    return mainEvaluator;
  }

  public AbstractGeneticAlgorithm setExperimentalPsd(float[][] experimentalPsd) {
    mainEvaluator.setPsd(experimentalPsd);
    return this;
  }
 
  public IKmc getKmc() {
    return simulation.getKmc();
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
  public Individual getIndividual(int pos) {
    return population.getIndividual(pos);
  }
  
  @Override
  public Individual getBestIndividual() {
    return population.getBestIndividual();
  }
  
  @Override
  public float[] getProgressPercent() {
    float[] progress = new float[3];
    progress[0] = currentIteration * 100.0f / totalIterations;
    progress[2] = mainEvaluator.getProgressPercent();

    return progress;
  }
  
  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }
  
  /**
   * Sets the main interface, starts a thread which will be responsible to update it and
   * assigns the main interface to the evaluator (to be able to also update it with 
   * the current surface and PSD).
   * @param mainInterface 
   */
  public void setMainInterface(MainInterface mainInterface) {
    this.mainInterface = mainInterface;
    updater = new Updater();
    updater.start();
  }  
  
  @Override
  public void setGraphics(IgaProgressFrame graphics) {
    this.graphics = graphics;
  }
  
  protected void addToGraphics() {
    if (mainInterface != null) {
      mainInterface.addNewBestIndividual(getBestIndividual());
    }
    if (graphics != null) {
      graphics.addNewBestIndividual(getBestIndividual());
    }
  }
  
  protected void clearGraphics() {
    if (graphics != null) {
      graphics.clear();
    }
  }
    
  public void setHierarchy(double[] rates) {
    mainEvaluator.setHierarchy(rates);
  }

  protected double getStopError() {
    return stopError;
  } 
  
  /**
   * Scales genes (ratios) with respect to the expected simulation time. If expected simulation time
   * is 0, no scaling is done
   *
   * @param population The population to be scaled.
   */
  @Deprecated
  protected void scaleIndividualRates(Population population) {
    for (int i = 0; i < population.size(); i++) {
      Individual individual = population.getIndividual(i);
      double factor;
      if (expectedSimulationTime == 0) {
        factor = 1;
      } else {
        factor = expectedSimulationTime / individual.getSimulationTime();
      }
      for (int j = 0; j < individual.getGeneSize(); j++) {
        individual.setGene(j, individual.getGene(j) / factor);
      }
    }
  }
  
  /**
   * Inner class responsible to update the interface.
   */
  final class Updater extends Thread {
    public Updater() {
    }

    /**
     * Every 100 ms updates the interface with the current progress.
     */
    @Override
    public void run() {
      while (true) {
        mainInterface.setProgress(getProgressPercent());
        mainInterface.paintCanvas();
        mainInterface.setSurface(mainEvaluator.getSurface());
        mainInterface.setError(mainEvaluator.getCurrentError());
        mainInterface.setSimulationMesh(mainEvaluator.getCurrentPsd());
        mainInterface.setDifference(mainEvaluator.getCurrentDifference());
        int individualCount  = mainEvaluator.getIndividualCount();
        int simulationCount  = mainEvaluator.getSimulationCount();;
        mainInterface.setStatusBar(
                "Population " + getCurrentIteration() + 
                " | Individual " + individualCount + 
                " | Simulation " + simulationCount + "/"+(parser.getRepetitions()-1));
        try {
          Updater.sleep(100);
        } catch (Exception e) {
        }
      }
    }
  }
}
