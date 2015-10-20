/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.Individual;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class SiEtchingBasicPsdEvaluation extends AbstractPsdEvaluation {

  private SiEtchingKmc kmc;

  public SiEtchingBasicPsdEvaluation(SiEtchingKmcConfig config, int repeats, int measureInterval) {

    super(repeats, measureInterval);

    setPsdSizeX(config.sizeX_UC * 2);
    setPsdSizeY(config.sizeY_UC * 2);
    kmc = new SiEtchingKmc(config);
    psd = new PsdSignature2D(getPsdSizeY(), getPsdSizeX());
    difference = new float[getPsdSizeY()][getPsdSizeX()];
  }

  @Override
  public float[][] calculatePsdFromIndividual(Individual i) {

    this._calculatePsdFromIndividual(i);
    return psd.getPsd();
  }

  @Override
  public void dispose() {
    psd = null;
    kmc = null;
    sampledSurface = null;
    difference = null;
  }

  private void _calculatePsdFromIndividual(Individual ind) {
    psd.reset();
    for (int i = 0; i < repeats; i++) {
      kmc.initializeRates(ind.getGenes());
      kmc.simulate(measureInterval / 2);
      while (true) {
        kmc.simulate(measureInterval);
        sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());
        psd.addSurfaceSample(sampledSurface);
        if (kmc.getIterations() < measureInterval) {
          break;
        }
      }
      currentSimulation++;
    }

    psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
  }

}
