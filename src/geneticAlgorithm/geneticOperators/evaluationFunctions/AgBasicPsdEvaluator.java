/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.Individual;
import graphicInterfaces.growth.DiffusionKmcFrame;
import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgBasicPsdEvaluator extends AbstractPsdEvaluator {

  private AgKmc kmc;
  private Frame2D psdFrame;

  public AgBasicPsdEvaluator(AgKmc kmc, int repeats, int measureInterval, int psdSizeX, int psdSizeY) {

    super(repeats, measureInterval);

    setPsdSizeX(psdSizeX);
    setPsdSizeY(psdSizeY);

    this.kmc = kmc;
    psd = new PsdSignature2D(getPsdSizeY(), getPsdSizeX());
    difference = new float[getPsdSizeY()][getPsdSizeX()];

    DiffusionKmcFrame frame = createGraphicsFrame(kmc);
    frame.setVisible(true);
    psdFrame =  new Frame2D("Calculated PSD analysis")
            .setLogScale(true)
            .setShift(true)
            .setMin(2.02634)
            .setMax(19.34551);
  }

  private static DiffusionKmcFrame createGraphicsFrame(AgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
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
    double time = 0.0;
    kmc.initialiseRates(ind.getGenes());
    for (int i = 0; i < repeats; i++) {
      kmc.reset();
      kmc.depositSeed();
      while (true) {
        kmc.simulate(measureInterval);
        sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());
        psd.addSurfaceSample(sampledSurface);
        if (kmc.getIterations() < measureInterval) {
          time += kmc.getTime();
          break;
        }
      }
      currentSimulation++;
    }
    ind.setSimulationTime(time / repeats);
    psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
    psdFrame.setMesh(psd.getPsd());
  }

}
