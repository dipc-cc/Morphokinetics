/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType;
import graphicInterfaces.basic.BasicFrame;
import kineticMonteCarlo.kmcCore.etching.BasicKmc;
import kineticMonteCarlo.kmcCore.growth.BasicGrowthKmc;
import ratesLibrary.BasicGrowthRatesFactory;
import ratesLibrary.IRatesFactory;
import ratesLibrary.basic.RatesCaseOther;

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

    setRates(new BasicGrowthRatesFactory());
    boolean extraOutput = getParser().getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    setKmc(new BasicGrowthKmc(getConfig(),
            getParser().getHexaSizeI(), 
            getParser().getHexaSizeJ(),
            getParser().justCentralFlake(),
            getParser().isPeriodicSingleFlake(),
            (float) getParser().getCoverage()/100,
            getParser().useMaxPerimeter(),
            getParser().getPerimeterType(),
            extraOutput));
    initialiseRates(getRates(), getParser());
  }
}
