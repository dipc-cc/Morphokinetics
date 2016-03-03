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
public class SiliconPsdDifferencesBetweenTemperatures {

  public static void main(String args[]) {

    System.out.println("Showing PSD differences between two temperatures ");

    SiKmcConfig config = configKmc();

    SiKmc kmc = new SiKmc(config);

    float[][] psd300_1 = getPsdFromSimulation(kmc, 300);
    Frame2D frame300_1 = new Frame2D("PSD 300K first").setMesh(psd300_1)
            .setLogScale(true)
            .setShift(true);
    frame300_1.setVisible(true);
    float[][] psd300_2 = getPsdFromSimulation(kmc, 300);
    Frame2D frame300_2 = new Frame2D("PSD 300K second").setMesh(psd300_2)
            .setLogScale(true)
            .setShift(true);;
    frame300_2.setLocation(frame300_1.getWidth(), 0);
    frame300_2.setVisible(true);
    float[][] psd400_1 = getPsdFromSimulation(kmc, 400);
    Frame2D frame400_1 = new Frame2D("PSD 400K first").setMesh(psd400_1)
            .setLogScale(true)
            .setShift(true);;
    frame400_1.setLocation(2 * frame300_1.getWidth(), 0);
    frame400_1.setVisible(true);

    float[][] relativeError1
            = new float[psd300_1.length][psd300_1[0].length];
    float[][] relativeError2
            = new float[psd300_1.length][psd300_1[0].length];

    for (int i = 0; i < psd300_1.length; i++) {
      for (int j = 0; j < psd300_1[0].length; j++) {
        relativeError1[i][j] = ((psd400_1[i][j] - psd300_1[i][j]) / psd300_1[i][j]);
        relativeError2[i][j] = ((psd300_2[i][j] - psd300_1[i][j]) / psd300_1[i][j]);
      }
    }

    Frame2D frame400vs300 = new Frame2D("Relative difference between PSDs 400K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMesh(MathUtils.avgFilter(relativeError1, 1));

    frame400vs300.setLocation(frame300_1.getWidth(), frame300_1.getHeight());

    if (Math.abs(frame400vs300.getMax()) > Math.abs(frame400vs300.getMin())) {
      frame400vs300.setMin(-frame400vs300.getMax());
    } else {
      frame400vs300.setMax(-frame400vs300.getMin());
    }
    frame400vs300.setVisible(true);

    Frame2D frame300vs300 = new Frame2D("Relative difference between PSDs 300K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMax(frame400vs300.getMax())
            .setMin(frame400vs300.getMin())
            .setMesh(MathUtils.avgFilter(relativeError2, 1));
    frame300vs300.setLocation(0, frame300_1.getHeight());
    frame300vs300.setVisible(true);
  }

  private static float[][] getPsdFromSimulation(SiKmc kmc, int temperature) {

    PsdSignature2D psd = new PsdSignature2D(kmc.getLattice().getHexaSizeJ() * 2, kmc.getLattice().getHexaSizeI() * 2, 1);
    float[][] surface;

    kmc.initialiseRates(new SiRatesFactory().getRates(temperature));
    for (int a = 0; a < 30; a++) {
      kmc.reset();
      kmc.depositSeed();
      kmc.simulate();
      for (int i = 0; i < 10; i++) {
        System.out.println(temperature + "K simulation " + a + " " + i);
        kmc.simulate();
        surface = kmc.getSampledSurface(kmc.getLattice().getHexaSizeJ() * 2, kmc.getLattice().getHexaSizeI() * 2);
        psd.addSurfaceSample(surface);
      }
    }

    psd.doPsd();
    psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

    return psd.getPsd();
  }

  private static SiKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(100)
            .setExtraLevels(0);
    SiKmcConfig config = new SiKmcConfig()
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
