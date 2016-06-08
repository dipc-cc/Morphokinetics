/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import ratesLibrary.IRates;
import ratesLibrary.SiRatesFromPreGosalvez;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractEtchingSimulation extends AbstractSimulation {

  public AbstractEtchingSimulation(Parser parser) {
    super(parser);
  }

  @Override
  protected void initialiseRates(IRates rates, Parser parser) {
    getKmc().initialiseRates(new SiRatesFromPreGosalvez().getRates(getParser().getTemperature()));
  }

}
