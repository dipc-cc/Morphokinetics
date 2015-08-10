/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticAlgorithmDatabase;

import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgGrowthThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching.SiEtchingBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching.SiEtchingThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.mutation.BgaBasedMutator;
import geneticAlgorithm.geneticOperators.populationInitialization.siEtching.SiEtchingInitialization;
import geneticAlgorithm.geneticOperators.populationInitialization.agAg.AgAgInitialization;
import geneticAlgorithm.geneticOperators.recombination.RealRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.siEtching.SiEtchingRestriction;
import geneticAlgorithm.geneticOperators.restrictions.agAg.AgAgRestriction;
import geneticAlgorithm.geneticOperators.selection.RandomSelection;
import geneticAlgorithm.geneticOperators.selection.RankingSelection;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmcConfig;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.list.ListConfiguration;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.lattice.AgAgLattice;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithmConfigFactory {

  public GeneticAlgorithmConfiguration create_silicon_convergence_configuration() {

    GeneticAlgorithmConfiguration config = new GeneticAlgorithmConfiguration();

    config.population_size = 100;
    config.offspring_size = 32;
    config.population_replacements = 5;
    config.initialization = new SiEtchingInitialization();
    config.mutation = new BgaBasedMutator();
    config.recombination = new RealRecombination();
    config.reinsertion = new ElitistReinsertion();
    config.restriction = new SiEtchingRestriction();
    config.selection = new RankingSelection();
    config.mainEvaluator = get_silicon_etching_main_evaluators();
    config.otherEvaluators = add_no_more_evaluators();

    return config;
  }

  public GeneticAlgorithmConfiguration create_Ag_Ag_convergence_configuration(double diffusionRate, double islandDensity, double depositionRate) {

    GeneticAlgorithmConfiguration config = new GeneticAlgorithmConfiguration();

    config.population_size = 100;
    config.offspring_size = 32;
    config.population_replacements = 5;
    config.initialization = new AgAgInitialization();
    config.mutation = new BgaBasedMutator();
    config.recombination = new RealRecombination();
    config.reinsertion = new ElitistReinsertion();
    config.restriction = new AgAgRestriction(diffusionRate);
    config.selection = new RankingSelection();
    config.mainEvaluator = get_Ag_Ag_growth_main_evaluator(depositionRate, islandDensity);
    config.otherEvaluators = add_no_more_evaluators();

    return config;
  }

  public GeneticAlgorithmConfiguration create_Ag_Ag_dcma_es_convergence_configuration(double diffusionRate, double islandDensity, double depositionRate) {

    GeneticAlgorithmConfiguration config = new GeneticAlgorithmConfiguration();

    //config.population_size = 100;
    config.population_size = 5;
    config.offspring_size = 32;
    config.population_replacements = 5;
    config.initialization = new AgAgInitialization();
        //config.mutation = new BgaBasedMutator();
    //config.recombination = new RealRecombination();
    //config.reinsertion = new ElitistReinsertion();
    config.restriction = new AgAgRestriction(diffusionRate);
    config.selection = new RandomSelection();
    config.mainEvaluator = get_Ag_Ag_growth_main_evaluator(depositionRate, islandDensity);
    config.otherEvaluators = add_no_more_evaluators();

    return config;
  }

  private AbstractPsdEvaluation get_silicon_etching_main_evaluators() {

    SiEtchingThreadedPsdEvaluation evaluator = new SiEtchingThreadedPsdEvaluation(SiEtchConfigKMC(), 30, 10000, 8);
    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);

    return evaluator;
  }

  private AbstractPsdEvaluation get_Ag_Ag_growth_main_evaluator(double depositionRate, double islandDensity) {

    //Ag_ag_growth_Threaded_PSD_Evaluation evaluator = new AgAgGrowthThreadedPsdEvaluation(AgAgConfigKMC(depositionRate,islandDensity), 30, Integer.MAX_VALUE, 2);
    AgAgBasicPsdEvaluation evaluator = new AgAgBasicPsdEvaluation(AgAgConfigKMC(depositionRate, islandDensity), 1, Integer.MAX_VALUE);

    evaluator.setWheight(1.0f);
    evaluator.setShowGraphics(true);

    return evaluator;
  }

  private List<IEvaluation> add_no_more_evaluators() {

    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }

  private static SiEtchingKmcConfig SiEtchConfigKMC() {
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

  private AgAgKmcConfig AgAgConfigKMC(double depositionRate, double islandDensity) {

    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20);

    return new AgAgKmcConfig(256, (int) (256 / AgAgLattice.YRatio), listConfig, depositionRate, islandDensity);

  }

}
