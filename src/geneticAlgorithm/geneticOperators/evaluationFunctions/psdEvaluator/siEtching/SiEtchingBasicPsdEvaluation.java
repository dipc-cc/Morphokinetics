/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Individual;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class SiEtchingBasicPsdEvaluation extends AbstractPsdEvaluation {

    private SiEtchingKmc KMC;
    
    public SiEtchingBasicPsdEvaluation(SiEtchingKmcConfig config, int repeats, int measureInterval) {

        super(repeats, measureInterval);

        psdSizeX = config.sizeX_UC * 2;
        psdSizeY = config.sizeY_UC * 2;
        KMC = new SiEtchingKmc(config, true);
        psd = new PsdSignature2D(psdSizeY, psdSizeX);
        difference = new float[psdSizeY][psdSizeX];
    } 
    
    @Override
    public float[][] calculatePsdFromIndividual(Individual i) {

        this._calculate_PSD_from_individual(i);
        return psd.getPsd();
    }

    @Override
    public void dispose() {
        psd=null;
        KMC=null;
        sampledSurface=null;
        difference=null;   
    }

    private void _calculate_PSD_from_individual(Individual ind) {
        psd.reset();
        for (int i = 0; i < repeats; i++) {
            KMC.initializeRates(ind.getGenes());
            KMC.simulate(measureInterval / 2);
            while (true) {
                KMC.simulate(measureInterval);
                sampledSurface = KMC.getSampledSurface(psdSizeY, psdSizeX);
                psd.addSurfaceSample(sampledSurface);
                if (KMC.getIterations() < measureInterval) {
                    break;
                }
            }
            currentSimulation++;
        }

        psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
    }
    
    
}
