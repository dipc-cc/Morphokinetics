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
public class SimpleSiliconKmcFftAnalisys {

  public static void main(String args[]) {

    System.out.println("Simple 2D FFT analisys of an etched silicon surface");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(16);
    parser.setExtraLevels(0);
    parser.setMillerX(1);
    parser.setMillerY(1);
    parser.setMillerZ(0);
    parser.setCartSizeX(64);
    parser.setCartSizeY(64);
    parser.setCartSizeZ(256);

    SiKmc kmc = new SiKmc(parser);
    kmc.reset();
    kmc.initialiseRates(new SiRatesFromPreGosalvez().getRates(350));
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
}
