/* 
 * Copyright (C) 2018 N. Ferrando
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package samples.silicon;

import basic.Parser;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFromPreGosalvez;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import utils.MathUtils;
import utils.StaticRandom;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author N. Ferrando
 */
public class SiliconPsdDifferencesBetweenTemperatures {

  public static void main(String args[]) {

    System.out.println("Showing PSD differences between two temperatures ");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(100);
    parser.setExtraLevels(0);
    parser.setMillerX(1);
    parser.setMillerY(0);
    parser.setMillerZ(0);
    parser.setCartSizeX(48);
    parser.setCartSizeY(48);
    parser.setCartSizeZ(16);

    SiKmc kmc = new SiKmc(parser);
    

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

    kmc.initialiseRates(new SiRatesFromPreGosalvez().getRates(temperature));
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

    psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

    return psd.getPsd();
  }
}
