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
package samples.agAgGrowth;

import basic.Parser;
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
 * @author N. Ferrando
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
    GrowthKmcFrame frame = new GrowthKmcFrame(kmc.getLattice(), kmc.getPerimeter(), 1);
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
