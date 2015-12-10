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
import geneticAlgorithm.evaluationFunctions.SiBasicPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.populationInitialisation.AgReduced6Initialisator;
import geneticAlgorithm.populationInitialisation.IInitialisator;
import geneticAlgorithm.populationInitialisation.SiInitialisator;
import geneticAlgorithm.restrictions.RestrictionOperator;
import geneticAlgorithm.restrictions.SiRestriction;
import graphicInterfaces.MainInterface;
import geneticAlgorithm.restrictions.AgReduced6Restriction;
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

  protected Parser parser;
  
  protected BasicEvaluator evaluator;
  protected AbstractPsdEvaluator mainEvaluator;
  private IInitialisator initialisation;
  private RestrictionOperator restriction;
  private final int populationSize;
  private final int offspringSize;
  private final int populationReplacements;
  private double expectedSimulationTime;
  /** Number of different genes. */
  private int dimensions;
  
  private int currentIteration = 0;
  private int totalIterations = 1;
  protected MainInterface mainInterface;
  
  private AbstractSimulation simulation;
  
  private double depositionRate;
  private double islandDensity;
  
  private Updater updater;
  /** The stop error. If the current error is below this number, stop. */
  private final double stopError;

  public AbstractGeneticAlgorithm(Parser parser) {
    
    this.evaluator = new BasicEvaluator();
    
    this.parser = parser;
    
    populationSize = parser.getPopulationSize();
    offspringSize = parser.getOffspringSize();
    populationReplacements = parser.getPopulationReplacement();
    dimensions = 6;
    stopError = parser.getStopError();
    totalIterations = parser.getTotalIterations();
    
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

  /**
   * Evaluator evaluation
   *
   * @return
   */
  private AbstractPsdEvaluator getAgMainEvaluator() {
    AbstractPsdEvaluator evaluatorTmp = null;
    int sizeX = parser.getCartSizeX() / 2;
    int sizeY = parser.getCartSizeY() / 2;
    if (parser.isEvaluatorParallel()) {
	evaluatorTmp = new AgThreadedPsdEvaluator((AgKmc) simulation.getKmc(), 30, Integer.MAX_VALUE, 2, sizeX, sizeY, parser.getEvaluatorTypes());  
    } else {
        evaluatorTmp = new AgBasicPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, sizeX, sizeY, parser.getEvaluatorTypes(), parser.getHierarchyEvaluator(), parser.getEvolutionarySearchType());
    }
    
    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);
    
    return evaluatorTmp;             
  }
    
  private AbstractPsdEvaluator getSiMainEvaluators() {
    AbstractPsdEvaluator evaluatorTmp = null;
    if (parser.isEvaluatorParallel()) {
      evaluatorTmp = new SiThreadedPsdEvaluator(localSiKmc(), 30, 10000, 8, parser.getEvaluatorTypes());
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
  
  public void setMainEvaluator(AbstractPsdEvaluator mainEvaluator) {
    this.mainEvaluator = mainEvaluator;
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
    progress[0] = currentIteration * 100.0f / getTotalIterations();
    progress[2] = mainEvaluator.getProgressPercent();

    return progress;
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
