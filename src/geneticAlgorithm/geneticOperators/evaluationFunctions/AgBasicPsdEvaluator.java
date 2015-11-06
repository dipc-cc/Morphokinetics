/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import basic.io.Restart;
import geneticAlgorithm.Individual;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
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
  }

  @Override
  public float[][] calculatePsdFromIndividual(Individual ind) {
    psd.reset();
    double time = 0.0;
    String folderName = "gaResults/population"+currentPopulation.getIterationNumber()+"/individual"+currentSimulation/repeats;
    int sizes[] = new int[2];
    Restart restart = new Restart(folderName);
    psd.setRestart(restart);      
    kmc.initialiseRates(ind.getGenes());
    for (int i = 0; i < repeats; i++) {
      kmc.reset();
      kmc.depositSeed();
      while (true) {
        kmc.simulate(measureInterval);
        sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());   
        psd.addSurfaceSample(sampledSurface);
        sizes[0] = sampledSurface.length;
        sizes[1] = sampledSurface[0].length;
        restart.writeSurfaceBinary(2, sizes, sampledSurface, i);
        if (kmc.getIterations() < measureInterval) {
          time += kmc.getTime();
          break;
        }
      }
      currentSimulation++;
    }
 
    ind.setSimulationTime(time / repeats);
    psd.printAvgToFile();
    psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
    return psd.getPsd();
  }

  @Override
  public void dispose() {
    psd = null;
    kmc = null;
    sampledSurface = null;
    difference = null;
  }
}
