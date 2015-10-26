/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import ratesLibrary.AgAgRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimulation extends AbstractGrowthSimulation {

  public AgSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    this.rates = new AgAgRatesFactory();
    this.kmc = new AgAgKmc(config,
            parser.getHexaSizeI(), 
            parser.getHexaSizeJ(),
            parser.justCentralFlake(),
            (float) parser.getCoverage()/100,
            parser.useMaxPerimeter(),
            parser.getPerimeterType());
  }
  
  public void initialiseKmc(double[] rates) {
    initialiseKmc();
    kmc.initializeRates(rates);
    System.out.println("This");
    for (int i = 0; i < rates.length; i++) {
      System.out.println(" " + rates[i]);
    }
  }
}
