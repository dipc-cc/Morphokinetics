/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticAlgorithmDatabase;

import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgGrowthThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching.SiEtchingBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching.SiEtchingThreadedPsdEvaluation;
import geneticAlgorithm.geneticOperators.mutation.BGA_based_mutator;
import geneticAlgorithm.geneticOperators.populationInitialization.Si_etching.Si_etching_initialization;
import geneticAlgorithm.geneticOperators.populationInitialization.ag_ag.Ag_Ag_initialization;
import geneticAlgorithm.geneticOperators.recombination.RealRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.Si_etching.Si_etching_restriction;
import geneticAlgorithm.geneticOperators.restrictions.ag_ag.Ag_Ag_restriction;
import geneticAlgorithm.geneticOperators.selection.RandomSelection;
import geneticAlgorithm.geneticOperators.selection.RankingSelection;
import geneticAlgorithm.Genetic_algorithm_configuration;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC_config;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.list.List_configuration;
import static Samples.AgAg_growth.AgAgMulthreadedEvaluatorTest.constant_Y;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class genetic_algorithm_config_factory {

    public Genetic_algorithm_configuration create_silicon_convergence_configuration() {

        Genetic_algorithm_configuration config = new Genetic_algorithm_configuration();

        config.population_size = 100;
        config.offspring_size = 32;
        config.population_replacements = 5;
        config.initialization = new Si_etching_initialization();
        config.mutation = new BGA_based_mutator();
        config.recombination = new RealRecombination();
        config.reinsertion = new ElitistReinsertion();
        config.restriction = new Si_etching_restriction();
        config.selection = new RankingSelection();
        config.mainEvaluator = get_silicon_etching_main_evaluators();
        config.otherEvaluators = add_no_more_evaluators();

        return config;
    }

    public Genetic_algorithm_configuration create_Ag_Ag_convergence_configuration(double diffusion_rate, double island_density, double deposition_rate) {

        Genetic_algorithm_configuration config = new Genetic_algorithm_configuration();

        config.population_size = 100;
        config.offspring_size = 32;
        config.population_replacements = 5;
        config.initialization = new Ag_Ag_initialization();
        config.mutation = new BGA_based_mutator();
        config.recombination = new RealRecombination();
        config.reinsertion = new ElitistReinsertion();
        config.restriction = new Ag_Ag_restriction(diffusion_rate);
        config.selection = new RankingSelection();
        config.mainEvaluator = get_Ag_Ag_growth_main_evaluator(deposition_rate,island_density);
        config.otherEvaluators = add_no_more_evaluators();

        return config;
    }
    
    public Genetic_algorithm_configuration create_Ag_Ag_dcma_es_convergence_configuration(double diffusion_rate, double island_density, double deposition_rate) {

        Genetic_algorithm_configuration config = new Genetic_algorithm_configuration();

        //config.population_size = 100;
        config.population_size = 5;
        config.offspring_size = 32;
        config.population_replacements = 5;
        config.initialization = new Ag_Ag_initialization();
        //config.mutation = new BGA_based_mutator();
        //config.recombination = new RealRecombination();
        //config.reinsertion = new ElitistReinsertion();
        config.restriction = new Ag_Ag_restriction(diffusion_rate);
        config.selection = new RandomSelection();
        config.mainEvaluator = get_Ag_Ag_growth_main_evaluator(deposition_rate,island_density);
        config.otherEvaluators = add_no_more_evaluators();

        return config;
    }

    private AbstractPSDEvaluation get_silicon_etching_main_evaluators() {

        SiEtchingThreadedPsdEvaluation evaluator = new SiEtchingThreadedPsdEvaluation(SiEtchConfigKMC(), 30, 10000, 8);
        evaluator.setWheight(1.0f);
        evaluator.setShowGraphics(true);

        return evaluator;
    }

    private AbstractPSDEvaluation get_Ag_Ag_growth_main_evaluator(double deposition_rate, double island_density) {

        //Ag_ag_growth_Threaded_PSD_Evaluation evaluator = new AgAgGrowthThreadedPsdEvaluation(AgAgConfigKMC(deposition_rate,island_density), 30, Integer.MAX_VALUE, 2);
    	AgAgBasicPsdEvaluation evaluator = new AgAgBasicPsdEvaluation(AgAgConfigKMC(deposition_rate,island_density), 1, Integer.MAX_VALUE);
    	
        evaluator.setWheight(1.0f);
        evaluator.setShowGraphics(true);

        return evaluator;
    }

    private List<IEvaluation> add_no_more_evaluators() {

        List<IEvaluation> evaluation = new ArrayList();
        return evaluation;
    }

    private static Si_etching_KMC_config SiEtchConfigKMC() {
        List_configuration listConfig = new List_configuration()
                .setList_type(List_configuration.BINNED_LIST)
                .setBins_per_level(20)
                .set_extra_levels(1);

        Si_etching_KMC_config config = new Si_etching_KMC_config()
                .setMillerX(1)
                .setMillerY(0)
                .setMillerZ(0)
                .setSizeX_UC(32)
                .setSizeY_UC(32)
                .setSizeZ_UC(48)
                .setListConfig(listConfig);
        return config;
    }

    private Ag_Ag_KMC_config AgAgConfigKMC(double deposition_rate, double island_density) {

        List_configuration listConfig = new List_configuration()
                .setList_type(List_configuration.BINNED_LIST)
                .setBins_per_level(20);

        return new Ag_Ag_KMC_config(256, (int) (256 / constant_Y), listConfig, deposition_rate, island_density);

    }

}
