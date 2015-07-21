package samples.silicon;

import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import ratesLibrary.SiRatesFactory;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.list.ListConfiguration;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SiliconPsdDifferencesBetweenTemperatures {

  public static void main(String args[]) {

    System.out.println("Showing PSD differences between two temperatures ");

    SiEtchingKmcConfig config = configKmc();

    SiEtchingKmc kmc = new SiEtchingKmc(config);

    float[][] PSD300_1
            = getPsdFromSimulation(kmc, 300);
    float[][] PSD300_2
            = getPsdFromSimulation(kmc, 300);
    float[][] PSD400_1
            = getPsdFromSimulation(kmc, 400);

    float[][] Relative_error_1
            = new float[PSD300_1.length][PSD300_1[0].length];
    float[][] Relative_error_2
            = new float[PSD300_1.length][PSD300_1[0].length];

    for (int i = 0; i < PSD300_1.length; i++) {
      for (int j = 0; j < PSD300_1[0].length; j++) {
        Relative_error_1[i][j] = ((PSD400_1[i][j] - PSD300_1[i][j]) / PSD300_1[i][j]);
        Relative_error_2[i][j] = ((PSD300_2[i][j] - PSD300_1[i][j]) / PSD300_1[i][j]);
      }
    }

    Frame2D frame = new Frame2D("Relative difference between PSDs 400K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMesh(MathUtils.avgFilter(Relative_error_1, 1));

    frame.setLocation(frame.getWidth(), 0);

    if (Math.abs(frame.getMax()) > Math.abs(frame.getMin())) {
      frame.setMin(-frame.getMax());
    } else {
      frame.setMax(-frame.getMin());
    }

    new Frame2D("Relative difference between PSDs 300K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMax(frame.getMax())
            .setMin(frame.getMin())
            .setMesh(MathUtils.avgFilter(Relative_error_2, 1));
  }

  private static float[][] getPsdFromSimulation(SiEtchingKmc kmc, int temperature) {

    PsdSignature2D psd = new PsdSignature2D(kmc.getLattice().getSizeY() * 2, kmc.getLattice().getSizeX() * 2);
    float[][] surface = new float[kmc.getLattice().getSizeY() * 2][kmc.getLattice().getSizeX() * 2];

    for (int a = 0; a < 30; a++) {

      kmc.initializeRates(new SiRatesFactory()
              .getRates(temperature));

      kmc.simulate(5000);
      for (int i = 0; i < 10; i++) {
        kmc.simulate(10000);
        kmc.getSampledSurface(surface);
        psd.addSurfaceSample(surface);
      }
    }

    psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    return psd.getPsd();
  }

  private static SiEtchingKmcConfig configKmc() {
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(100)
            .setExtraLevels(0);
    SiEtchingKmcConfig config = new SiEtchingKmcConfig()
            .setMillerX(1)
            .setMillerY(0)
            .setMillerZ(0)
            .setSizeX_UC(48)
            .setSizeY_UC(48)
            .setSizeZ_UC(16)
            .setListConfig(listConfig);
    return config;
  }
}
