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
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmcConfig;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgBasicPsdEvaluation extends AbstractPsdEvaluation {

  private AgAgKmc KMC;

  public AgAgBasicPsdEvaluation(AgAgKmcConfig config, int repeats, int measureInterval) {

    super(repeats, measureInterval);

    psdSizeX = 64;
    psdSizeY = 64;

    KMC = new AgAgKmc(config.getListConfig(), 
            config.getHexaSizeI(), 
            config.getHexaSizeJ(), 
            config.getDepositionRate(),
            config.getIslandDensity());
    psd = new PsdSignature2D(psdSizeY, psdSizeX);
    sampledSurface = new float[psdSizeY][psdSizeX];
    difference = new float[psdSizeY][psdSizeX];

    DiffusionKmcFrame frame = createGraphicsFrame(KMC);
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
    KMC = null;
    sampledSurface = null;
    difference = null;
  }

  private void _calculatePsdFromIndividual(Individual ind) {
    psd.reset();
    double time = 0.0;
    for (int i = 0; i < repeats; i++) {
      KMC.initializeRates(ind.getGenes());
      while (true) {
        KMC.simulate(measureInterval);
        KMC.getSampledSurface(sampledSurface);
        psd.addSurfaceSample(sampledSurface);
        if (KMC.getIterations() < measureInterval) {
          time += KMC.getTime();
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
