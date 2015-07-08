/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import Graphic_interfaces.Difussion2D_Growth.AgAg_growth.AgAgKMC_canvas;
import Graphic_interfaces.Difussion2D_Growth.DifussionKMC_frame;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC_config;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import utils.MathUtils;
import utils.PSD_analysis.PSD_signature_2D;

/**
 *
 * @author Nestor
 */
public class AgAgBasicPsdEvaluation extends AbstractPSDEvaluation {

    private Ag_Ag_KMC KMC;
    private PSD_signature_2D PSD ;
    private float[][] sampledSurface ;
    private float[][] difference;

    public AgAgBasicPsdEvaluation(Ag_Ag_KMC_config config, int repeats, int measureInterval) {

        super(repeats, measureInterval);

        KMC = new Ag_Ag_KMC(config,true);
        PSD = new PSD_signature_2D(64,64);
        sampledSurface = new float[64][64];
        difference = new float[64][64];
        
        DifussionKMC_frame frame = create_graphics_frame(KMC);
        frame.setVisible(true);   
    }
    
        private static DifussionKMC_frame create_graphics_frame(Ag_Ag_KMC kmc) {
        DifussionKMC_frame frame = new DifussionKMC_frame(new AgAgKMC_canvas((Abstract_2D_diffusion_lattice) kmc.getLattice()));
        return frame;
    }

    @Override
    public double[] evaluate(Population p) {

        this.currentPopulation = p;
        this.currentSimulation = 0;
        double[] results = new double[p.size()];

        for (int i = 0; i < p.size(); i++) {
            results[i] = evaluate_individual(p.getIndividual(i));
           
        }

        return results;
    }

    private double evaluate_individual(Individual ind) {
        
        calculate_PSD_from_individual(ind);
        calculateRelativeDifference(difference, PSD);

        difference=MathUtils.avg_Filter(difference, 5);
        double error = 0;
        for (int a = 0; a < difference.length; a++) {
            for (int b = 0; b < difference[0].length; b++) {
                
                error += Math.abs(difference[a][b]);
            }
        }        
        return error * wheight;
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
        PSD.apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
        PSD.apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);
    }
    
    
}
