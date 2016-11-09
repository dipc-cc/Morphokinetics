/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFromPreGosalvez;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulation extends AbstractEtchingSimulation {

  public SiSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new SiRatesFromPreGosalvez());
    setKmc(new SiKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }

  /**
   * Does nothing.
   */
  @Override
  public void createFrame() {
  }

  /**
   * Show the result of the simulation in a frame.
   */
  @Override
  public void finishSimulation() {
    if (getParser().visualise()) {
      try {
        new SiFrame().drawKmc(getKmc());
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
    }
  } 
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());

    for (int i = 0; i < rates.length; i++) {
      System.out.printf("%1.3E  ", rates[i]);
    }
    System.out.println("");
  }
}
