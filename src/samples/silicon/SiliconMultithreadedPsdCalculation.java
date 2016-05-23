/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.silicon;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AbstractEvaluator;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.Individual;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
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
            new SiRatesFactory().getRates(350)));

    evaluation.dispose();

    new Frame2D("Multi-threaded calculated PSD")
            .setLogScale(true)
            .setShift(true)
            .setMesh(psd);
  }
}
