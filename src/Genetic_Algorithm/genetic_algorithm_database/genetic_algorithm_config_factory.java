/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.genetic_algorithm_database;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.IEvaluation;
import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.AbstractPSDEvaluation;
import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Ag_ag_growth.Ag_ag_growth_Threaded_PSD_Evaluation;
import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Si_etching.Si_etching_Basic_PSD_Evaluation;
import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Si_etching.Si_etching_Threaded_PSD_Evaluation;
import Genetic_Algorithm.Genetic_Operators.Mutation.BGA_based_mutator;
import Genetic_Algorithm.Genetic_Operators.Population_Initialization.Si_etching.Si_etching_initialization;
import Genetic_Algorithm.Genetic_Operators.Population_Initialization.ag_ag.Ag_Ag_initialization;
import Genetic_Algorithm.Genetic_Operators.Recombination.RealRecombination;
import Genetic_Algorithm.Genetic_Operators.Reinsertion.ElitistReinsertion;
import Genetic_Algorithm.Genetic_Operators.Restrictions.Si_etching.Si_etching_restriction;
import Genetic_Algorithm.Genetic_Operators.Restrictions.ag_ag.Ag_Ag_restriction;
import Genetic_Algorithm.Genetic_Operators.Selection.RankingSelection;
import Genetic_Algorithm.Genetic_algorithm_configuration;
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

    private AbstractPSDEvaluation get_silicon_etching_main_evaluators() {

        Si_etching_Threaded_PSD_Evaluation evaluator = new Si_etching_Threaded_PSD_Evaluation(SiEtchConfigKMC(), 30, 10000, 8);
        evaluator.setWheight(1.0f);
        evaluator.setShowGraphics(true);

        return evaluator;
    }

    private AbstractPSDEvaluation get_Ag_Ag_growth_main_evaluator(double deposition_rate, double island_density) {

        Ag_ag_growth_Threaded_PSD_Evaluation evaluator = new Ag_ag_growth_Threaded_PSD_Evaluation(AgAgConfigKMC(deposition_rate,island_density), 30, Integer.MAX_VALUE, 2);
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
