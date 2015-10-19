/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticAlgorithmDatabase;

import basic.AgSimulation;
import basic.Parser;
import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AgAgBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AgAgGrowthThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiEtchingBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiEtchingThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.mutation.BgaBasedMutator;
import geneticAlgorithm.geneticOperators.populationInitialization.SiEtchingInitialization;
import geneticAlgorithm.geneticOperators.populationInitialization.AgAgInitialization;
import geneticAlgorithm.geneticOperators.recombination.RealRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.siEtching.SiEtchingRestriction;
import geneticAlgorithm.geneticOperators.restrictions.agAg.AgAgRestriction;
import geneticAlgorithm.geneticOperators.selection.RandomSelection;
import geneticAlgorithm.geneticOperators.selection.RankingSelection;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.list.ListConfiguration;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.lattice.AgAgLattice;
import ratesLibrary.AgAgRatesFactory;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithmConfigFactory {

  GeneticAlgorithmConfiguration config;
  private AgSimulation agSimulation;
  private Parser parser;
  private double depositionRate;
  private double islandDensity;
  private double diffusionRate;
  
  public GeneticAlgorithmConfigFactory() {
    config = new GeneticAlgorithmConfiguration();
  }
  
  public GeneticAlgorithmConfigFactory(Parser parser) {
    config = new GeneticAlgorithmConfiguration();
    this.parser = parser;
    this.agSimulation = new AgSimulation(parser);
    agSimulation.initialiseKmc();
    
    float experitentalTemp = parser.getTemperature();
    this.depositionRate = new AgAgRatesFactory().getDepositionRate(experitentalTemp);
    this.islandDensity = new AgAgRatesFactory().getIslandDensity(experitentalTemp);
    this.diffusionRate = new AgAgRatesFactory().getRates(experitentalTemp)[0];
    agSimulation.getKmc().setIslandDensityAndDepositionRate(depositionRate, islandDensity);
  }
  
  public GeneticAlgorithmConfiguration createSiConvergenceConfiguration() {

    config.setPopulationSize(100);
    config.setOffspringSize(32);
    config.setPopulationReplacements(5);
    config.setInitialization(new SiEtchingInitialization());
    config.setMutation(new BgaBasedMutator());
    config.setRecombination(new RealRecombination());
    config.setReinsertion(new ElitistReinsertion());
    config.setRestriction(new SiEtchingRestriction());
    config.setSelection(new RankingSelection());
    config.setMainEvaluator(getSiMainEvaluators());
    config.setOtherEvaluators(addNoMoreEvaluators());

    return config;
  }

  @Deprecated
  public GeneticAlgorithmConfiguration createAgAgConvergenceConfiguration(double diffusionRate, double islandDensity, double depositionRate) {

    config.setPopulationSize(100);
    config.setOffspringSize(32);
    config.setPopulationReplacements(5);
    config.setInitialization(new AgAgInitialization());
    config.setMutation(new BgaBasedMutator());
    config.setRecombination(new RealRecombination());
    config.setReinsertion(new ElitistReinsertion());
    config.setRestriction(new AgAgRestriction(diffusionRate));
    config.setSelection(new RankingSelection());
    config.setMainEvaluator(getAgAgMainEvaluator(depositionRate, islandDensity));
    config.setOtherEvaluators(addNoMoreEvaluators());

    return config;
  }

  public GeneticAlgorithmConfiguration createAgAgConvergenceConfiguration() {

    config.setPopulationSize(parser.getPopulationSize());
    config.setOffspringSize(parser.getOffspringSize());
    config.setPopulationReplacements(parser.getPopulationReplacement());
    config.setInitialization(new AgAgInitialization());
    config.setMutation(new BgaBasedMutator());
    config.setRecombination(new RealRecombination());
    config.setReinsertion(new ElitistReinsertion());
    config.setRestriction(new AgAgRestriction(diffusionRate));
    config.setSelection(new RankingSelection());
    config.setMainEvaluator(getAgAgMainEvaluator());
    config.setOtherEvaluators(addNoMoreEvaluators());

    return config;
  }

  @Deprecated
  public GeneticAlgorithmConfiguration createAgAgDcmaEsConvergenceConfiguration(double diffusionRate, double islandDensity, double depositionRate) {

    //config.populationSize(100;
    config.setPopulationSize(5);
    config.setOffspringSize(32);
    config.setPopulationReplacements(5);
    config.setInitialization(new AgAgInitialization());
        //config.mutation = new BgaBasedMutator();
    //config.recombination = new RealRecombination();
    //config.reinsertion = new ElitistReinsertion();
    config.setRestriction(new AgAgRestriction(diffusionRate));
    config.setSelection(new RandomSelection());
    config.setMainEvaluator(getAgAgMainEvaluator(depositionRate, islandDensity));
    config.setOtherEvaluators(addNoMoreEvaluators());

    return config;
  }
  
  public GeneticAlgorithmConfiguration createAgAgDcmaEsConvergenceConfiguration() {

    config.setPopulationSize(parser.getPopulationSize());
    config.setOffspringSize(parser.getOffspringSize());
    config.setPopulationReplacements(parser.getPopulationReplacement());
    config.setInitialization(new AgAgInitialization());
    config.setRestriction(new AgAgRestriction(diffusionRate));
    config.setSelection(new RandomSelection());
    config.setMainEvaluator(getAgAgMainEvaluator());
    config.setOtherEvaluators(addNoMoreEvaluators());

    return config;
  }

  private AbstractPsdEvaluation getSiMainEvaluators() {

    SiEtchingThreadedPsdEvaluation evaluator = new SiEtchingThreadedPsdEvaluation(localSiKmc(), 30, 10000, 8);
    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);

    return evaluator;
  }

  @Deprecated
  private AbstractPsdEvaluation getAgAgMainEvaluator(double depositionRate, double islandDensity) {
    //AgAgGrowthThreadedPsdEvaluation evaluator = new AgAgGrowthThreadedPsdEvaluation(localAgAgKmc(depositionRate,islandDensity), 30, Integer.MAX_VALUE, 2);
    AgAgBasicPsdEvaluation evaluator = new AgAgBasicPsdEvaluation(localAgAgKmc(depositionRate, islandDensity), 1, Integer.MAX_VALUE);

    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);

    return evaluator;
  }

  private AbstractPsdEvaluation getAgAgMainEvaluator() {
    AbstractPsdEvaluation evaluator = null;
    switch (parser.getEvaluator()) {
      case "serial":
        evaluator = new AgAgBasicPsdEvaluation((AgAgKmc) agSimulation.getKmc(), 1, Integer.MAX_VALUE);
        break;
      case "threaded":
        evaluator = new AgAgGrowthThreadedPsdEvaluation((AgAgKmc) agSimulation.getKmc(), 30, Integer.MAX_VALUE, 2);
        break;
      default:
        break;
    }
    
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

  @Deprecated
  private AgAgKmc localAgAgKmc(double depositionRate, double islandDensity) {
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20);
    
    return new AgAgKmc(listConfig, 256, (int) (256 / AgAgLattice.YRatio),depositionRate, islandDensity);

  }
}
