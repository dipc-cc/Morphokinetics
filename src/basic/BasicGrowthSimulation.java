/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.growth.BasicGrowthKmc;
import ratesLibrary.BasicGrowth3Rates;
import ratesLibrary.BasicGrowthSyntheticRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSimulation extends AbstractGrowthSimulation{
  
  public BasicGrowthSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    if (getParser().getRatesLibrary().equals("version2")) {
      setRates(new BasicGrowth3Rates());
    } else {
      setRates(new BasicGrowthSyntheticRates());
    }
    setKmc(new BasicGrowthKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
}
