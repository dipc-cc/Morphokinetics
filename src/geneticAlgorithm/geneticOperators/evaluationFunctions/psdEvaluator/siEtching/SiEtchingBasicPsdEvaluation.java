/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class SiEtchingBasicPsdEvaluation extends AbstractPsdEvaluation {

    private SiEtchingKmc KMC;
    private PsdSignature2D PSD = new PsdSignature2D(128, 128);
    private float[][] surface = new float[128][128];
    private float[][] difference = new float[128][128];

    public SiEtchingBasicPsdEvaluation(SiEtchingKmcConfig config, int repeats, int measureInterval) {

        super(repeats, measureInterval);

        KMC = new SiEtchingKmc(config);
        PSD = new PsdSignature2D(config.sizeY_UC * 2, config.sizeX_UC * 2);
        surface = new float[config.sizeY_UC * 2][config.sizeX_UC * 2];
        difference = new float[config.sizeY_UC * 2][config.sizeX_UC * 2];
        PSD_size_X = config.sizeX_UC * 2;
        PSD_size_Y = config.sizeY_UC * 2;
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
        for (int a = 0; a < PSD_size_Y; a++) {
            for (int b = 0; b < PSD_size_X; b++) {
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
        surface=null;
        difference=null;   
    }

    private void _calculate_PSD_from_individual(Individual ind) {
        PSD.reset();
        for (int i = 0; i < repeats; i++) {
            KMC.initializeRates(ind.getGenes());
            KMC.simulate(measureInterval / 2);
            while (true) {
                KMC.simulate(measureInterval);
                KMC.getSampledSurface(surface);
                PSD.addSurfaceSample(surface);
                if (KMC.getIterations() < measureInterval) {
                    break;
                }
            }
            currentSimulation++;
        }

        PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
    }
    
    
}
