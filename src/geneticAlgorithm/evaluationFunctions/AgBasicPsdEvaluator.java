/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import basic.io.Restart;
import geneticAlgorithm.Individual;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgBasicPsdEvaluator extends AbstractPsdEvaluator {

  private AgKmc kmc;

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
    int individualCount = currentSimulation/repeats;
    psd.reset();
    double time = 0.0;
    String folderName = "gaResults/population"+getCurrentIteration()+"/individual"+individualCount;
    int sizes[] = new int[2];
    Restart restart = new Restart(folderName);
    psd.setRestart(restart);
    kmc.initialiseRates(getRates6(ind.getGenes()));
    for (int i = 0; i < repeats; i++) {
      mainInterface.setStatusBar("Population "+getCurrentIteration()+" | Individual "+individualCount+" | Simulation "+i+"/"+(repeats-1));
      kmc.reset();
      kmc.depositSeed();
      while (true) {
        kmc.simulate(measureInterval);
        sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());
        psd.addSurfaceSample(sampledSurface);
        sizes[0] = sampledSurface.length;
        sizes[1] = sampledSurface[0].length;
        restart.writeSurfaceBinary(2, sizes, sampledSurface, i);
        if (kmc.getCoverage() < 0.05) continue;
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
    psd.printAvgToFile();
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
   * Calculates rates from the genes. Most of the rates are 0, the rest is calculated from the given
   * genes.
   *
   * @param genes
   * @return
   */
  public double[] getRates(double[] genes) {
    double[] rates = new double[49];

    for (int i = 0; i < rates.length; i++) {
      rates[i] = 0; // All rates to 0 (actually, %80 are 0)
    }
    for (int i = 0; i < 7; i++) {
      rates[i] = genes[0]; // Terrace to any
    }

    System.arraycopy(genes, 1, rates, 8, 6); // Corner rates
    
    rates[2 * 7 + 2] = genes[7]; // Rate corresponding to E_aa
    rates[5 * 7 + 5] = genes[9]; // Rate corresponding to E_bb

    //We set the following atomistic configurations to the same rate (according to the Ag/Ag diffusion paper):
    //(2,3)=(2,4)=(2,5)=(2,6)=(5,2)=(5,3)=(5,4)=(5,6)
    rates[2 * 7 + 3] = genes[8];
    rates[2 * 7 + 4] = genes[8];
    rates[2 * 7 + 5] = genes[8];
    rates[2 * 7 + 6] = genes[8];
    rates[5 * 7 + 2] = genes[8];
    rates[5 * 7 + 3] = genes[8];
    rates[5 * 7 + 4] = genes[8];
    rates[5 * 7 + 6] = genes[8];
    return rates;
  }
  
  /**
   * Calculates rates from the genes. Most of the rates are 0, the rest is calculated from the given
   * genes.
   *
   * Ratio (energy type) | ratio index
   * 0) E_d                    (0,j)
   * 1) E_c                    (1,1)(1,2)(1,6)
   * 2) E_e                    (1,3)(1,5)
   * 3) E_f                    (2,3)=(2,4)=(2,5)=(2,6)=(5,2)=(5,3)=(5,4)=(5,6)
   * 4) E_a                    (2,2)
   * 5) E_b                    (5,5)
   * @param genes
   * @return
   */
  public double[] getRates6(double[] genes) {
    System.out.print("population"+getCurrentIteration()+"/individual"+currentSimulation/repeats);
    for (int i = 0; i < 6; i++) {
      System.out.print(" "+genes[i]);;
    }
    System.out.println(" ");
    double[] rates = new double[49];

    for (int i = 0; i < rates.length; i++) {
      rates[i] = 0; // All rates to 0 (actually, %80 are 0)
    }
    for (int i = 0; i < 7; i++) {
      rates[i] = genes[0]; // Terrace to any
    }

    rates[1 * 7 + 1] = genes[1]; // E_c
    rates[1 * 7 + 2] = genes[1]; // E_c
    rates[1 * 7 + 6] = genes[1]; // E_c
    
    rates[1 * 7 + 3] = genes[2]; // E_e
    rates[1 * 7 + 5] = genes[2]; // E_e

    // The maximum energy has to be chosen, so the minimum ratio.
    rates[1 * 7 + 4] = Math.min(genes[1], genes[2]);
    
    // E_f
    rates[2 * 7 + 3] = genes[3];
    rates[2 * 7 + 4] = genes[3];
    rates[2 * 7 + 5] = genes[3];
    rates[2 * 7 + 6] = genes[3];
    rates[5 * 7 + 2] = genes[3];
    rates[5 * 7 + 3] = genes[3];
    rates[5 * 7 + 4] = genes[3];
    rates[5 * 7 + 6] = genes[3];

    rates[2 * 7 + 2] = genes[4]; // Rate corresponding to E_a
    rates[5 * 7 + 5] = genes[5]; // Rate corresponding to E_b

    return rates;
  }
}
