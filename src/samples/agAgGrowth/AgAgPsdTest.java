/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.GrowthKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AgLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgAgRatesFactory;
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

    AgAgRatesFactory ratesFactory = new AgAgRatesFactory();

    AgKmc kmc = initialize_kmc();

    //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
    PsdSignature2D PSD = new PsdSignature2D(128, 128);
    float[][] sampledSurface = null;

    initializeRates(ratesFactory, kmc);
    for (int i = 0; i < 30; i++) {
      kmc.reset();
      kmc.depositSeed();
      kmc.simulate();

      sampledSurface = kmc.getSampledSurface(128, 128);

      PSD.addSurfaceSample(sampledSurface);
      System.out.println("flake " + i);
    }
    PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(PSD.getPsd(), 1));

    new Frame2D("Sampled surface").setMesh(sampledSurface);

  }

  private static GrowthKmcFrame create_graphics_frame(AgKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static AgKmc initialize_kmc() {

    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / AgLattice.YRatio);

    AgKmc kmc = new AgKmc(config, sizeX, sizeY, true, (float) -1, false, RoundPerimeter.CIRCLE);

    return kmc;
  }

  private static void initializeRates(AgAgRatesFactory reatesFactory, AgKmc kmc) {

    double deposition_rate = reatesFactory.getDepositionRate(135);
    double island_density = reatesFactory.getIslandDensity(135);
    kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
    kmc.initialiseRates(reatesFactory.getRates(135));
  }

}
