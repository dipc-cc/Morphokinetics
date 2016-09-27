package samples.basic;

import basic.Parser;
import graphicInterfaces.basic.BasicFrame;
import kineticMonteCarlo.kmcCore.etching.BasicKmc;
import ratesLibrary.basic.RatesCaseOther;
import utils.StaticRandom;
import utils.Wait;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleBasicKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Basic KMC");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setCartSizeX(512);
    parser.setCartSizeY(128);
    parser.setListType("binned");
    parser.setBinsLevels(100);
    parser.setExtraLevels(0);

    BasicKmc kmc = new BasicKmc(parser);

    BasicFrame panel = new BasicFrame(3);

    kmc.reset();
    kmc.initialiseRates(new RatesCaseOther().getRates(350));
    kmc.depositSeed();

    for (int i = 0; i < 1000; i++) {
      kmc.simulate(500);
      panel.drawKmc(kmc);
      Wait.manyMilliSec(300);
    }
  }
}
