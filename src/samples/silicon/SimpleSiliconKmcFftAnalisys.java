package samples.silicon;

import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFactory;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import utils.list.ListConfiguration;
import utils.MathUtils;
import utils.StaticRandom;
import utils.psdAnalysis.PsdSignature2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleSiliconKmcFftAnalisys {

  public static void main(String args[]) {

    System.out.println("Simple 2D FFT analisys of an etched silicon surface");

    SiKmcConfig config = configKmc();

    SiKmc kmc = new SiKmc(config);
    kmc.reset();
    kmc.initialiseRates(new SiRatesFactory().getRates(350));
    kmc.depositSeed();

    float[][] surface;;
    PsdSignature2D psd = new PsdSignature2D(128, 128, 1);

    System.out.println("Simulation -1");
    kmc.simulate();
    for (int i = 0; i < 100; i++) {
      System.out.println("Simulation " + i);
      kmc.simulate();
      surface = kmc.getSampledSurface(128, 128);
      psd.addSurfaceSample(surface);
    }

    psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

    Frame2D psdFrame = new Frame2D("PSD analysis")
            .setMesh(MathUtils.avgFilter(psd.getPsd(), 1))
            .setLogScale(true)
            .setShift(true);
    psdFrame.setVisible(true);
  }

  private static SiKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(16)
            .setExtraLevels(0);
    SiKmcConfig config = new SiKmcConfig()
            .setMillerX(1)
            .setMillerY(1)
            .setMillerZ(0)
            .setSizeX_UC(64)
            .setSizeY_UC(64)
            .setSizeZ_UC(256)
            .setListConfig(listConfig);
    return config;
  }
}
