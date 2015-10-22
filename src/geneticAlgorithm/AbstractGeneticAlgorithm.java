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
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.geneticOperators.mutation.BgaBasedMutator;
import geneticAlgorithm.geneticOperators.mutation.IMutation;
import geneticAlgorithm.geneticOperators.populationInitialization.AgAgInitialization;
import geneticAlgorithm.geneticOperators.populationInitialization.IInitializator;
import geneticAlgorithm.geneticOperators.populationInitialization.SiEtchingInitialization;
import geneticAlgorithm.geneticOperators.recombination.IRecombination;
import geneticAlgorithm.geneticOperators.recombination.RealRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistReinsertion;
import geneticAlgorithm.geneticOperators.reinsertion.IReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.RestrictionOperator;
import geneticAlgorithm.geneticOperators.restrictions.agAg.AgAgRestriction;
import geneticAlgorithm.geneticOperators.restrictions.siEtching.SiEtchingRestriction;
import geneticAlgorithm.geneticOperators.selection.ISelection;
import geneticAlgorithm.geneticOperators.selection.RankingSelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import ratesLibrary.AgAgRatesFactory;
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
  protected IInitializator initialization;
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
  
  private AbstractSimulation simulation;
  
  private double depositionRate;
  private double islandDensity;
  private double diffusionRate;  
  

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
        this.depositionRate = new AgAgRatesFactory().getDepositionRate(experitentalTemp);
        this.islandDensity = new AgAgRatesFactory().getIslandDensity(experitentalTemp);
        this.diffusionRate = new AgAgRatesFactory().getRates(experitentalTemp)[0];
        this.simulation.getKmc().setIslandDensityAndDepositionRate(depositionRate, islandDensity); 
        initialization = new AgAgInitialization();
        restriction = new AgAgRestriction(diffusionRate);
        mainEvaluator = getAgAgMainEvaluator();
        break;
      case "Si":
        this.simulation = new SiSimulation(parser);
        initialization = new SiEtchingInitialization();
        restriction = new SiEtchingRestriction();
        mainEvaluator = getSiMainEvaluators();
        break;
      default:
        System.err.println("This calculation mode is not implemented for evolutionary algorithm");
        System.err.println("Current calculation mode is " + parser.getCalculationMode());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    simulation.initialiseKmc();
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
  private AbstractPsdEvaluator getAgAgMainEvaluator() {
    AbstractPsdEvaluator evaluator = null;
    int sizeX = parser.getCartSizeX() / 2;
    int sizeY = parser.getCartSizeY() / 2;
    switch (parser.getEvaluator()) {
      case "serial":
        evaluator = new AgBasicPsdEvaluator((AgAgKmc) simulation.getKmc(), 1, Integer.MAX_VALUE, sizeX, sizeY);
        break;
      case "threaded":
        evaluator = new AgThreadedPsdEvaluator((AgAgKmc) simulation.getKmc(), 30, Integer.MAX_VALUE, 2, sizeX, sizeY);
        break;
      default:
        break;
    }
    
    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);
    
    return evaluator;             
  }
    
  private AbstractPsdEvaluator getSiMainEvaluators() {
    SiThreadedPsdEvaluator evaluator = new SiThreadedPsdEvaluator(localSiKmc(), 30, 10000, 8);
    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);

    return evaluator;
  }
  
  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }
  
  private static SiEtchingKmcConfig localSiKmc() {
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20)
            .setExtraLevels(1);

    SiEtchingKmcConfig config = new SiEtchingKmcConfig()
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

  public void setInitialization(IInitializator initialization) {
    this.initialization = initialization;
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

  public IInitializator getInitialization() {
    return initialization;
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
}
