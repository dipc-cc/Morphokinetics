/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import basic.Parser;
import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.GrowthKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFromPrbCox;
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

    AgRatesFromPrbCox ratesFactory = new AgRatesFromPrbCox();

    AgKmc kmc = initialiseKmc();

    //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
    PsdSignature2D PSD = new PsdSignature2D(128, 128, 1);
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

    Frame2D psdFrame = new Frame2D("PSD analysis");
    psdFrame.setMesh(MathUtils.avgFilter(PSD.getPsd(), 1));
    psdFrame.setLogScale(true)
            .setShift(true);
    psdFrame.setVisible(true);

    Frame2D surfaceFrame =new Frame2D("Sampled surface").setMesh(sampledSurface);
    surfaceFrame.setVisible(true);
  }

  private static GrowthKmcFrame createGraphicsFrame(AgKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()), 1);
    return frame;
  }

  private static AgKmc initialiseKmc() {

    new StaticRandom();

    int sizeX = 256;
    int sizeY = (int) (sizeX / AbstractGrowthLattice.Y_RATIO);
    Parser parser = new Parser();
    parser.setCartSizeX(sizeX);
    parser.setCartSizeY(sizeY);
    parser.setListType("linear");
    
    AgKmc kmc = new AgKmc(parser);

    return kmc;
  }

  private static void initialiseRates(AgRatesFromPrbCox rates, AgKmc kmc) {
    double depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(135);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.initialiseRates(rates.getRates(135));
  }

}
