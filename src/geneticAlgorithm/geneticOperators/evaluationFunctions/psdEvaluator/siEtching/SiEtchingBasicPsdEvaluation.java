/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Individual;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class SiEtchingBasicPsdEvaluation extends AbstractPsdEvaluation {

    private SiEtchingKmc KMC;
    
    public SiEtchingBasicPsdEvaluation(SiEtchingKmcConfig config, int repeats, int measureInterval) {

        super(repeats, measureInterval);

        PSD_size_X = config.sizeX_UC * 2;
        PSD_size_Y = config.sizeY_UC * 2;
        KMC = new SiEtchingKmc(config);
        PSD = new PsdSignature2D(PSD_size_Y, PSD_size_X);
        sampledSurface = new float[PSD_size_Y][PSD_size_X];
        difference = new float[PSD_size_Y][PSD_size_X];
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
        for (int i = 0; i < repeats; i++) {
            KMC.initializeRates(ind.getGenes());
            KMC.simulate(measureInterval / 2);
            while (true) {
                KMC.simulate(measureInterval);
                KMC.getSampledSurface(sampledSurface);
                PSD.addSurfaceSample(sampledSurface);
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
