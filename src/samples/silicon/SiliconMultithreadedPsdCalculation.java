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
import geneticAlgorithm.evaluationFunctions.AbstractEvaluator;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.Individual;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import ratesLibrary.SiRatesFromPreGosalvez;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class SiliconMultithreadedPsdCalculation {

  public static void main(String[] args) {

    System.out.println("Multithreaded PSD calculation from a KMC configuration");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(12);
    parser.setExtraLevels(1);
    parser.setMillerX(1);
    parser.setMillerY(0);
    parser.setMillerZ(0);
    parser.setCartSizeX(32);
    parser.setCartSizeY(32);
    parser.setCartSizeZ(64);

    AbstractEvaluator evaluation = new SiThreadedPsdEvaluator(parser, 10000, 4);
    evaluation.setWheight(1.0f);
    evaluation.setShowGraphics(false);

    float[][] psd = ((AbstractPsdEvaluator) evaluation).calculatePsdFromIndividual(new Individual(
            new SiRatesFromPreGosalvez().getRates(350)));

    evaluation.dispose();

    new Frame2D("Multi-threaded calculated PSD")
            .setLogScale(true)
            .setShift(true)
            .setMesh(psd);
  }
}
