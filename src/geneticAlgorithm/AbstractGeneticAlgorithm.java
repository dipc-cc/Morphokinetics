/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.Parser;
import basic.SiSimulation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AgThreadedPsdEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.BasicEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiBasicPsdEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.geneticOperators.mutation.BgaBasedMutator;
import geneticAlgorithm.geneticOperators.mutation.IMutation;
import geneticAlgorithm.geneticOperators.populationInitialisation.AgInitialisator;
import geneticAlgorithm.geneticOperators.populationInitialisation.IInitialisator;
import geneticAlgorithm.geneticOperators.populationInitialisation.SiInitialisator;
import geneticAlgorithm.geneticOperators.recombination.IRecombination;
import geneticAlgorithm.geneticOperators.recombination.RealRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistReinsertion;
import geneticAlgorithm.geneticOperators.reinsertion.IReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.RestrictionOperator;
import geneticAlgorithm.geneticOperators.restrictions.AgRestriction;
import geneticAlgorithm.geneticOperators.restrictions.SiRestriction;
import geneticAlgorithm.geneticOperators.selection.ISelection;
import graphicInterfaces.MainInterface;
import graphicInterfaces.gaConvergence.IgaProgressFrame;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.kmcCore.IKmc;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import ratesLibrary.AgRatesFactory;
import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGeneticAlgorithm implements IGeneticAlgorithm{

  private Parser parser;
  
  protected BasicEvaluator evaluator;
  protected AbstractPsdEvaluator mainEvaluator;
  protected List<IEvaluation> otherEvaluators;
  protected IMutation mutation;
  protected IInitialisator initialisation;
  protected IRecombination recombination;
  protected IReinsertion reinsertion;
  protected RestrictionOperator restriction;
  protected ISelection selection;
  protected int populationSize;
  protected int offspringSize;
  protected int populationReplacements;
  protected double expectedSimulationTime;
  
  protected Population population;
  protected int currentIteration = 0;
  protected int totalIterations = 1;
  protected IgaProgressFrame graphics;
  protected MainInterface mainInterface;
  
  private AbstractSimulation simulation;
  
  private double depositionRate;
  private double islandDensity;
  private double diffusionRate;  
  
  private Updater updater;

  public AbstractGeneticAlgorithm(Parser parser) {
    
    this.evaluator = new BasicEvaluator();
    
    this.parser = parser;
    
    populationSize = parser.getPopulationSize();
    offspringSize = parser.getOffspringSize();
    populationReplacements = parser.getPopulationReplacement();
    
    switch (parser.getCalculationMode()) {
      case "Ag":
        this.simulation = new AgSimulation(parser);
        simulation.initialiseKmc();
        float experitentalTemp = parser.getTemperature();
        this.depositionRate = new AgRatesFactory().getDepositionRate(experitentalTemp);
        this.islandDensity = new AgRatesFactory().getIslandDensity(experitentalTemp);
        this.diffusionRate = new AgRatesFactory().getRates(experitentalTemp)[0];
        this.simulation.getKmc().setIslandDensityAndDepositionRate(depositionRate, islandDensity); 
        initialisation = new AgInitialisator();
        restriction = new AgRestriction(diffusionRate);
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
    mutation = new BgaBasedMutator();
    recombination = new RealRecombination();
    reinsertion = new ElitistReinsertion();
    otherEvaluators = addNoMoreEvaluators();

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
    switch (parser.getEvaluator()) {
      case "serial":
        evaluatorTmp = new AgBasicPsdEvaluator((AgKmc) simulation.getKmc(), parser.getRepetitions(), Integer.MAX_VALUE, sizeX, sizeY);
        break;
      case "threaded":
        evaluatorTmp = new AgThreadedPsdEvaluator((AgKmc) simulation.getKmc(), 30, Integer.MAX_VALUE, 2, sizeX, sizeY);
        break;
      default:
        System.err.println("Error: Default evolutor. This evoluator is not implemented!");
        System.err.println("Current value: " + parser.getEvaluator() + ". Possible values are serial or threaded");
        throw new IllegalArgumentException("Evaluator mode is not implemented");
    }
    
    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);
    
    return evaluatorTmp;             
  }
    
  private AbstractPsdEvaluator getSiMainEvaluators() {
    AbstractPsdEvaluator evaluatorTmp = null;
    switch (parser.getEvaluator()) {
      case "serial":
        evaluatorTmp = new SiBasicPsdEvaluator(localSiKmc(), parser.getRepetitions(), 1000);
        break;
      case "threaded":
      evaluatorTmp = new SiThreadedPsdEvaluator(localSiKmc(), 30, 10000, 8);
        break;
      default:
        System.err.println("Error: Default evolutor. This evoluator is not implemented!");
        System.err.println("Current value: " + parser.getEvaluator() + ". Possible values are serial or threaded");
        throw new IllegalArgumentException("Evaluator mode is not implemented");
    }
    
    evaluatorTmp.setWheight(1.0f);
    evaluatorTmp.setShowGraphics(true);

    return evaluatorTmp;
  }
  
  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
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

  public void setOtherEvaluators(List<IEvaluation> otherEvaluators) {
    this.otherEvaluators = otherEvaluators;
  }

  public void setMutation(IMutation mutation) {
    this.mutation = mutation;
  }

  public void setInitialization(IInitialisator initialization) {
    this.initialisation = initialization;
  }

  public void setRecombination(IRecombination recombination) {
    this.recombination = recombination;
  }

  public void setReinsertion(IReinsertion reinsertion) {
    this.reinsertion = reinsertion;
  }

  public void setRestriction(RestrictionOperator restriction) {
    this.restriction = restriction;
  }

  public void setSelection(ISelection selection) {
    this.selection = selection;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public void setOffspringSize(int offspringSize) {
    this.offspringSize = offspringSize;
  }

  public void setPopulationReplacements(int populationReplacements) {
    this.populationReplacements = populationReplacements;
  }

  public void setExpectedSimulationTime(double expectedSimulationTime) {
    this.expectedSimulationTime = expectedSimulationTime;
  }

  public AbstractPsdEvaluator getMainEvaluator() {
    return mainEvaluator;
  }

  public List<IEvaluation> getOtherEvaluators() {
    return otherEvaluators;
  }

  public IMutation getMutation() {
    return mutation;
  }

  public IInitialisator getInitialization() {
    return initialisation;
  }

  public IRecombination getRecombination() {
    return recombination;
  }

  public IReinsertion getReinsertion() {
    return reinsertion;
  }

  public RestrictionOperator getRestriction() {
    return restriction;
  }

  public ISelection getSelection() {
    return selection;
  }

  public int getPopulationSize() {
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

  public AbstractGeneticAlgorithm setExperimentalPsd(float[][] experimentalPsd) {
    mainEvaluator.setPsd(experimentalPsd);
    return this;
  }
  
  public IKmc getKmc() {
    return simulation.getKmc();
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
