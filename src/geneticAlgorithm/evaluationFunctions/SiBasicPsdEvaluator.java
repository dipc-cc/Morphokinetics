/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.Individual;
import graphicInterfaces.etching.SiFrame;
import java.util.Set;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class SiBasicPsdEvaluator extends AbstractPsdEvaluator {

  private SiKmc kmc;
  SiFrame frame;

  public SiBasicPsdEvaluator(SiKmcConfig config, int repeats, int measureInterval, Set flags) {

    super(repeats, measureInterval, flags, null);

    setPsdSizeX(config.sizeX_UC * 2);
    setPsdSizeY(config.sizeY_UC * 2);
    kmc = new SiKmc(config);
    psd = new PsdSignature2D(getPsdSizeY(), getPsdSizeX());
    difference = new float[getPsdSizeY()][getPsdSizeX()];
    frame = new SiFrame();
  }

  @Override
  public float[][] calculatePsdFromIndividual(Individual ind) {
    psd.reset();
    kmc.initialiseRates(ind.getGenes());
    for (int i = 0; i < repeats; i++) {
      kmc.reset();
      kmc.depositSeed();
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
    frame.drawKmc(kmc);
    return psd.getPsd();
  }

  @Override
  public void dispose() {
    psd = null;
    kmc = null;
    sampledSurface = null;
    difference = null;
  }
  
  /**
   * It is not implemented
   * @param i
   * @return 0
   */
  @Override
  protected double calculateHierarchyError(Individual i) {
    return 0;
  }

  /**
   * It is not implemented
   * @param i
   * @return 0
   */
  @Override
  protected double calculateHierarchyErrorFromReference(Individual i) {
    return 0;
  }

  @Override
  protected double calculateHierarchyErrorDiscrete(Individual ind) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
