/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.growth.AgKmc;
import ratesLibrary.AgRatesFactory;

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

    setRates(new AgRatesFactory());
    setKmc(new AgKmc(getConfig(),
            getParser().getHexaSizeI(), 
            getParser().getHexaSizeJ(),
            getParser().justCentralFlake(),
            (float) getParser().getCoverage()/100,
            getParser().useMaxPerimeter(),
            getParser().getPerimeterType(),
            getParser().depositInAllArea()));
    initialiseRates(getRates(), getKmc(), getParser());
  }
}
