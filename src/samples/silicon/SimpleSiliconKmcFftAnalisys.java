package samples.silicon;

import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFactory;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
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

    SiEtchingKmcConfig config = configKmc();

    SiKmc kmc = new SiKmc(config);
    kmc.reset();
    kmc.initialiseRates(new SiRatesFactory().getRates(350));
    kmc.depositSeed();

    float[][] surface;;
    PsdSignature2D PSD = new PsdSignature2D(128, 128);

    kmc.simulate(5000);
    for (int i = 0; i < 100; i++) {
      kmc.simulate(5000);
      surface = kmc.getSampledSurface(128, 128);
      PSD.addSurfaceSample(surface);
    }

    PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    new Frame2D("PSD analysis")
            .setMesh(MathUtils.avgFilter(PSD.getPsd(), 1));
  }

  private static SiEtchingKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(16)
            .setExtraLevels(0);
    SiEtchingKmcConfig config = new SiEtchingKmcConfig()
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
