/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Si_etching;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.AbstractEvaluation;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Multithreaded_PSD_Evaluation;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;
import Graphic_interfaces.Silicon_etching.Silicon_frame;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.KMC_core.worker.IFinish_listener;
import Kinetic_Monte_Carlo.KMC_core.worker.IInterval_listener;
import Kinetic_Monte_Carlo.KMC_core.worker.KMC_worker;
import utils.MathUtils;
import utils.PSD_analysis.PSD_signature_2D;

/**
 *
 * @author Nestor
 */
public class Si_etching_Threaded_PSD_Evaluation extends Multithreaded_PSD_Evaluation implements IFinish_listener, IInterval_listener {

    private static final int FPS_GRAPHICS = 2;
    
    private PSD_signature_2D[] PSDs;
    private double[] times;
    private int PSD_size_X;
    private int PSD_size_Y;
    private Silicon_frame frame;
    private long time_last_render;

    public Si_etching_Threaded_PSD_Evaluation(Si_etching_KMC_config config, int repeats, int measureInterval, int num_threads) {

        super(repeats, measureInterval, num_threads);

        for (int i = 0; i < num_threads; i++) {
            workers[i] = new KMC_worker(new Si_etching_KMC(config), i);
            workers[i].start();
        }
        PSD_size_X = config.sizeX_UC * 2;
        PSD_size_Y = config.sizeY_UC * 2;
    }

    @Override
    public double[] evaluate(Population p) {
        calculate_PSD_of_population(p);
        double[] results = calculate_difference_with_RealPSD();
        return results;
    }

    @Override
    public float[][] calculate_PSD_from_individual(Individual i) {

        Population p = new Population(1);
        p.setIndividual(i, 0);
        this.calculate_PSD_of_population(p);

        PSDs[0].apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
        PSDs[0].apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);

        return PSDs[0].getPSD();
    }

    private double evaluate_individual(int individual_pos) {

        double error = 0;
        float[][] difference = new float[PSD_size_Y][PSD_size_X];

        PSDs[individual_pos].apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
        PSDs[individual_pos].apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);

        calculateRelativeDifference(difference, PSDs[individual_pos]);
        
        difference=MathUtils.avg_Filter(difference, 5);

                
  
        for (int a = 0; a < PSD_size_Y; a++) {
            for (int b = 0; b < PSD_size_X; b++) {
                error += Math.abs(difference[a][b]);
            }
        }
        return error * wheight;
    }

    @Override
    public void handleSimulationIntervalFinish(int workerID, int workID) {

        float[][] surface = new float[PSD_size_Y][PSD_size_X];
        workers[workerID].getSampledSurface(surface);
        times[workID]+=workers[workerID].getKMC().getTime();
        addToPSD(workID, surface);
    }

    private void addToPSD(int workID, float[][] surface) {
        PSDs[workID].addSurfaceSample(surface);
    }

    private void calculate_PSD_of_population(Population p) {
        PSDs = new PSD_signature_2D[p.size()];
        
        
        times=new double[p.size()];
        for (int i = 0; i < p.size(); i++) {
            PSDs[i] = new PSD_signature_2D(PSD_size_Y, PSD_size_X);
        }

        currentPopulation = p;
        currentSimulation = 0;
        finishedSimulation = 0;

        for (int i = 0; i < this.num_threads; i++) {
            assignNewWork(i);
        }

        try {
            evalation_complete.acquire();
        } catch (Exception e) {
        }
        store_simulation_times(p);
        
    }

    private double[] calculate_difference_with_RealPSD() {
        double[] results = new double[currentPopulation.size()];
        for (int i = 0; i < currentPopulation.size(); i++) {
            results[i] = evaluate_individual(i);
        }
        return results;
    }

    @Override
    public AbstractEvaluation setShowGraphics(boolean showGraphics) {
        super.setShowGraphics(showGraphics);
        if (showGraphics && frame == null) {
            frame = new Silicon_frame();
        }
        if (!showGraphics && frame != null) {
            frame.dispose();
            frame = null;
        }
        return this;
    }

    @Override
    public synchronized void handleSimulationFinish(int workerID, int workID) {
        if (showGraphics && (System.currentTimeMillis() - time_last_render) > 1000.0f / FPS_GRAPHICS) {
            frame.drawKMC(workers[workerID].getKMC());
            time_last_render = System.currentTimeMillis();
        }
        
        super.handleSimulationFinish(workerID, workID);
    }

    
    private void store_simulation_times(Population p) {
        for (int i = 0; i < p.size(); i++) {times[i]/=repeats;
                                            p.getIndividual(i).setSimulationTime(times[i]);}
    }
    
    
    
    
}
