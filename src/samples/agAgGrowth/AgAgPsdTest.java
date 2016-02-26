/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.GrowthKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgRatesFactory;
import utils.MathUtils;
import utils.StaticRandom;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgPsdTest {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgRatesFactory ratesFactory = new AgRatesFactory();

    AgKmc kmc = initialiseKmc();

    //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
    PsdSignature2D PSD = new PsdSignature2D(128, 128);
    float[][] sampledSurface = null;

    initialiseRates(ratesFactory, kmc);
    for (int i = 0; i < 30; i++) {
      kmc.reset();
      kmc.depositSeed();
      kmc.simulate();

      sampledSurface = kmc.getSampledSurface(128, 128);

      PSD.addSurfaceSample(sampledSurface);
      System.out.println("flake " + i);
    }
    PSD.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    PSD.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

    new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(PSD.getPsd(), 1));

    new Frame2D("Sampled surface").setMesh(sampledSurface);

  }

  private static GrowthKmcFrame createGraphicsFrame(AgKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static AgKmc initialiseKmc() {

    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / AbstractGrowthLattice.Y_RATIO);

    AgKmc kmc = new AgKmc(config, sizeX, sizeY, true, false, (float) -1, false, RoundPerimeter.CIRCLE, false);

    return kmc;
  }

  private static void initialiseRates(AgRatesFactory reatesFactory, AgKmc kmc) {

    double depositionRatePerSite = reatesFactory.getDepositionRatePerSite();
    double islandDensity = reatesFactory.getIslandDensity(135);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.initialiseRates(reatesFactory.getRates(135));
  }

}
