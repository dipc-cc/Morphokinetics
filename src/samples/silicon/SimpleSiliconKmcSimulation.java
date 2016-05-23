package samples.silicon;

import basic.Parser;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleSiliconKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Silicon etching KMC");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(20);
    parser.setExtraLevels(1);
    parser.setMillerX(0);
    parser.setMillerY(1);
    parser.setMillerZ(1);
    parser.setCartSizeX(96);
    parser.setCartSizeY(96);
    parser.setCartSizeZ(16);

    SiKmc kmc = new SiKmc(parser);

    long start = System.nanoTime();
    kmc.reset();
    kmc.initialiseRates(new SiRatesFactory().getRates(350));
    kmc.depositSeed();
    kmc.simulate();

    System.out.println((System.nanoTime() - start) / 1000000);

    new SiFrame().drawKmc(kmc);
  }
}
