/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Individual;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmc;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmcConfig;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgBasicPsdEvaluation extends AbstractPsdEvaluation {

    private AgAgKmc KMC;

    public AgAgBasicPsdEvaluation(AgAgKmcConfig config, int repeats, int measureInterval) {

        super(repeats, measureInterval);

        PSD_size_X = 64;
        PSD_size_Y = 64;
        
        KMC = new AgAgKmc(config,true);
        PSD = new PsdSignature2D(PSD_size_Y,PSD_size_X);
        sampledSurface = new float[PSD_size_Y][PSD_size_X];
        difference = new float[PSD_size_Y][PSD_size_X];
        
        DiffusionKmcFrame frame = create_graphics_frame(KMC);
        frame.setVisible(true);   
    }
    
        private static DiffusionKmcFrame create_graphics_frame(AgAgKmc kmc) {
        DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
        return frame;
    }

    @Override
    public float[][] calculate_PSD_from_individual(Individual i) {

        this._calculate_PSD_from_individual(i);
        return PSD.getPSD();
    }

    @Override
    public void dispose() {
        PSD=null;
        KMC=null;
        sampledSurface=null;
        difference=null;   
    }

    private void _calculate_PSD_from_individual(Individual ind) {
        PSD.reset();
        double time = 0.0;
        for (int i = 0; i < repeats; i++) {
            KMC.initializeRates(ind.getGenes());
            while (true) {
                KMC.simulate(measureInterval);
                KMC.getSampledSurface(sampledSurface);
                PSD.addSurfaceSample(sampledSurface);
                if (KMC.getIterations() < measureInterval) {
                	time += KMC.getTime();
                    break;
                }
            }
            currentSimulation++;
        }
        ind.setSimulationTime(time / repeats);
        PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
    }
    
    
}
