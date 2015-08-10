/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Individual;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgBasicPsdEvaluation extends AbstractPsdEvaluation {

  private AgAgKmc kmc;

  public AgAgBasicPsdEvaluation(AgAgKmc kmc, int repeats, int measureInterval) {

    super(repeats, measureInterval);

    psdSizeX = 64;
    psdSizeY = 64;

    this.kmc = kmc;
    psd = new PsdSignature2D(psdSizeY, psdSizeX);
    sampledSurface = new float[psdSizeY][psdSizeX];
    difference = new float[psdSizeY][psdSizeX];

    DiffusionKmcFrame frame = createGraphicsFrame(kmc);
    frame.setVisible(true);
  }

  private static DiffusionKmcFrame createGraphicsFrame(AgAgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
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
    for (int i = 0; i < repeats; i++) {
      kmc.initializeRates(ind.getGenes());
      while (true) {
        kmc.simulate(measureInterval);
        kmc.getSampledSurface(sampledSurface);
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
  }

}
