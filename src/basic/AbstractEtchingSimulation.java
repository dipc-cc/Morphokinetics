/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.AbstractKmc;
import ratesLibrary.IRatesFactory;
import ratesLibrary.SiRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractEtchingSimulation extends AbstractSimulation {

  public AbstractEtchingSimulation(Parser parser) {
    super(parser);
  }

  @Override
  protected void initialiseRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser parser) {
    getKmc().initialiseRates(new SiRatesFactory().getRates(getParser().getTemperature()));
  }

}
