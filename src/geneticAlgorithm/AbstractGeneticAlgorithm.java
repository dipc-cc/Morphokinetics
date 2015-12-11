/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.Parser;
import basic.SiSimulation;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.AgThreadedPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.BasicEvaluator;
import geneticAlgorithm.evaluationFunctions.IEvaluation;
import geneticAlgorithm.evaluationFunctions.SiBasicPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.mutation.IMutation;
import geneticAlgorithm.populationInitialisation.AgReduced6Initialisator;
import geneticAlgorithm.populationInitialisation.IInitialisator;
import geneticAlgorithm.populationInitialisation.SiInitialisator;
import geneticAlgorithm.recombination.IRecombination;
import geneticAlgorithm.reinsertion.IReinsertion;
import geneticAlgorithm.restrictions.RestrictionOperator;
import geneticAlgorithm.restrictions.SiRestriction;
import graphicInterfaces.MainInterface;
import geneticAlgorithm.restrictions.AgReduced6Restriction;
import geneticAlgorithm.selection.ISelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.kmcCore.IKmc;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import ratesLibrary.AgRatesFactory;
import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGeneticAlgorithm implements IGeneticAlgorithm{

  private Parser parser;
  
  private Population population;
  private BasicEvaluator evaluator;
  private AbstractPsdEvaluator mainEvaluator;
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
  
  private double depositionRate;
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
    offspringSize = parser.getOffspringSize();
    
    if (parser.getEvolutionaryAlgorithm().equals("dcma")) {
      populationReplacements = 0;
    } else {
      populationReplacements = parser.getPopulationReplacement();
    }
    dimensions = 6;
    stopError = parser.getStopError();
    totalIterations = parser.getTotalIterations();
    minValueGene = parser.getMinValueGene();
    maxValueGene = parser.getMaxValueGene();
    expDistribution = parser.isExpDistribution();
    
    switch (parser.getCalculationMode()) {
      case "Ag":
        this.simulation = new AgSimulation(parser);
        simulation.initialiseKmc();
        float experitentalTemp = parser.getTemperature();
        this.depositionRate = new AgRatesFactory().getDepositionRate(experitentalTemp);
        this.islandDensity = new AgRatesFactory().getIslandDensity(experitentalTemp);
        this.simulation.getKmc().setIslandDensityAndDepositionRate(depositionRate, islandDensity); 
        initialisation = new AgReduced6Initialisator();
        restriction = new AgReduced6Restriction(dimensions, parser.getMinValueGene(), parser.getMaxValueGene(), parser.isEnergySearch());
        if (parser.isDiffusionFixed()) ((AgReduced6Restriction) restriction).fixDiffusion();
        mainEvaluator = getAgMainEvaluator();
        break;
      case "Si":
        this.simulation = new SiSimulation(parser);
        simulation.initialiseKmc();
        initialisation = new SiInitialisator();
        restriction = new SiRestriction();
        mainEvaluator = getSiMainEvaluators();
        break;
      default:
        System.err.println("This calculation mode is not implemented for evolutionary algorithm");
        System.err.println("Current calculation mode is " + parser.getCalculationMode());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

  }
  
  @Override
  public IGeneticAlgorithm initialise() {
    population = getInitialisation().createRandomPopulation(getPopulationSize(), getDimensions(), getMinValueGene(), getMaxValueGene(), isExpDistribution());
    getRestriction().apply(population);
    this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    recombination.initialise(population);

    System.out.println("==================================");
    System.out.println("Finished initial random population");
    System.out.println("==================================");
    clearGraphics();
    
    return this;
  }

  @Override
  public void iterateOneStep() {
    IndividualGroup[] couples = selection.Select(population, getOffspringSize());
    Population offspringPopulation = recombination.recombinate(couples);
    offspringPopulation.setIterationNumber(getCurrentIteration());

    int geneSize = population.getIndividual(0).getGeneSize();
    mutation.mutate(offspringPopulation, getRestriction().getNonFixedGenes(geneSize));
    getRestriction().apply(offspringPopulation);
    evaluator.evaluateAndOrder(offspringPopulation, mainEvaluator, otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (reevaluate()) {
      getRestriction().apply(population);
      this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    }

    reinsertion.Reinsert(population, offspringPopulation, getPopulationReplacements());

  }
  /**
   * Evaluator evaluation
   *
   * @return
   */
  private AbstractPsdEvaluator getAgMainEvaluator() {
    AbstractPsdEvaluator evaluatorTmp;
    int sizeX = parser.getCartSizeX() / 2;
    int sizeY = parser.getCartSizeY() / 2;
    if (parser.isEvaluatorParallel()) {
      evaluatorTmp = new AgThreadedPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, 2, sizeX, sizeY, parser.getEvaluatorTypes());
    } else {
      evaluatorTmp = new AgBasicPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, sizeX, sizeY, parser.getEvaluatorTypes(), parser.getHierarchyEvaluator(), parser.getEvolutionarySearchType());
    }

    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);

    return evaluatorTmp;
  }
    
  private AbstractPsdEvaluator getSiMainEvaluators() {
    AbstractPsdEvaluator evaluatorTmp;
    if (parser.isEvaluatorParallel()) {
      evaluatorTmp = new SiThreadedPsdEvaluator(localSiKmc(), parser.getRepetitions(), 10000, 8, parser.getEvaluatorTypes());
    } else {
      evaluatorTmp = new SiBasicPsdEvaluator(localSiKmc(), parser.getRepetitions(), 1000, parser.getEvaluatorTypes());
    }

    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);

    return evaluatorTmp;
  }
  
  private static SiKmcConfig localSiKmc() {
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20)
            .setExtraLevels(1);

    SiKmcConfig config = new SiKmcConfig()
            .setMillerX(1)
            .setMillerY(0)
            .setMillerZ(0)
            .setSizeX_UC(32)
            .setSizeY_UC(32)
            .setSizeZ_UC(48)
            .setListConfig(listConfig);
    return config;
  }
  
  @Override 
  public void iterate() {
    for (int i = 1; i <= getTotalIterations(); i++) {
      setCurrentIteration(i);
      iterateOneStep();
      addToGraphics();
      System.out.println("For iteration " + currentIteration + " the best error is " + population.getBestError());
      if (exitCondition()) { // While using DCMA-ES this condition can occur and we must finish
        break;
      }
      if (population.getBestError() < getStopError()) {
        System.out.println("Stopping because the error is " + population.getBestError() + " (" + stopError + ")");
        break;
      }
    }
  }
  
  protected boolean isDtooLarge() {
    return recombination.isDtooLarge();
  }
  
  public void setCurrentIteration(int i) {
    currentIteration = i;
  }
  
  public void setInitialisation(IInitialisator initialization) {
    this.initialisation = initialization;
  }

  public void setExpectedSimulationTime(double expectedSimulationTime) {
    this.expectedSimulationTime = expectedSimulationTime;
    mainEvaluator.setExpectedSimulationTime(expectedSimulationTime);
  }

  public AbstractPsdEvaluator getMainEvaluator() {
    return mainEvaluator;
  }

  public IInitialisator getInitialisation() {
    return initialisation;
  }


  public final int getPopulationSize() {
    return populationSize;
  }

  public int getOffspringSize() {
    return offspringSize;
  }

  public int getPopulationReplacements() {
    return populationReplacements;
  }

  public double getExpectedSimulationTime() {
    return expectedSimulationTime;
  }

  public RestrictionOperator getRestriction() {
    return restriction;
  }

  public AbstractGeneticAlgorithm setExperimentalPsd(float[][] experimentalPsd) {
    mainEvaluator.setPsd(experimentalPsd);
    return this;
  }
 
  public IKmc getKmc() {
    return simulation.getKmc();
  }

  public final int getDimensions() {
    return dimensions;
  }
  
  public double getMinValueGene() {
    return minValueGene;
  }
  
  public double getMaxValueGene() {
    return maxValueGene;
  }
  public boolean isExpDistribution() {
    return expDistribution;
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
    progress[0] = currentIteration * 100.0f / getTotalIterations();
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
    mainEvaluator.setMainInterface(mainInterface);
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
    ((AgBasicPsdEvaluator) mainEvaluator).setHierarchy(rates);
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
        try {
          Updater.sleep(100);
        } catch (Exception e) {
        }
      }
    }
  }
}
