/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.MultithreadedPsdEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmc;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmcConfig;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgGrowthThreadedPsdEvaluation extends MultithreadedPsdEvaluation implements IFinishListener, IIntervalListener {

    private static final int FPS_GRAPHICS = 2;

    private PsdSignature2D[] PSDs;
    private double[] times;
    private int PSD_size_X;
    private int PSD_size_Y;

    private long time_last_render;

    public AgAgGrowthThreadedPsdEvaluation(AgAgKmcConfig config, int repeats, int measureInterval, int num_threads) {

        super(repeats, measureInterval, num_threads);

        for (int i = 0; i < num_threads; i++) {
            AgAgKmc kmc = new AgAgKmc(config, true);
            DiffusionKmcFrame frame = create_graphics_frame(kmc);
            frame.setVisible(true);

            workers[i] = new KmcWorker(kmc, i);
            workers[i].start();
        }

        PSD_size_X = 64;
        PSD_size_Y = 64;
    }

    private static DiffusionKmcFrame create_graphics_frame(AgAgKmc kmc) {
        DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
        return frame;
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

        PSDs[0].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        PSDs[0].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

        return PSDs[0].getPSD();
    }

    private double evaluate_individual(int individual_pos) {

        double error = 0;
        float[][] difference = new float[PSD_size_Y][PSD_size_X];

        PSDs[individual_pos].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        PSDs[individual_pos].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

        calculateRelativeDifference(difference, PSDs[individual_pos]);

        difference = MathUtils.avg_Filter(difference, 5);

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
        times[workID] += workers[workerID].getKMC().getTime();
        addToPSD(workID, surface);

        System.out.println("Worker " + workerID + " finished a simulation :(" + workID + ")");
    }

    private void addToPSD(int workID, float[][] surface) {
        PSDs[workID].addSurfaceSample(surface);
    }

    private void calculate_PSD_of_population(Population p) {
        PSDs = new PsdSignature2D[p.size()];

        times = new double[p.size()];
        for (int i = 0; i < p.size(); i++) {
            PSDs[i] = new PsdSignature2D(PSD_size_Y, PSD_size_X);
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

        return this;
    }

    @Override
    public synchronized void handleSimulationFinish(int workerID, int workID) {
        if (showGraphics && (System.currentTimeMillis() - time_last_render) > 1000.0f / FPS_GRAPHICS) {
            time_last_render = System.currentTimeMillis();
        }

        super.handleSimulationFinish(workerID, workID);
    }

    private void store_simulation_times(Population p) {
        for (int i = 0; i < p.size(); i++) {
            times[i] /= repeats;
            p.getIndividual(i).setSimulationTime(times[i]);
        }
    }

}
