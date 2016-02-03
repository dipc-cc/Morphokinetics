/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType;
import kineticMonteCarlo.kmcCore.growth.GrapheneKmc;
import ratesLibrary.GrapheneRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneSimulation extends AbstractGrowthSimulation {

  public GrapheneSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new GrapheneRatesFactory());
    boolean extraOutput = getParser().getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    setKmc(new GrapheneKmc(getConfig(),
            getParser().getHexaSizeI(),
            getParser().getHexaSizeJ(),
            getParser().justCentralFlake(),
            (float) getParser().getCoverage() / 100,
            getParser().useMaxPerimeter(),
            getParser().getPerimeterType(),
            getParser().depositInAllArea(),
            extraOutput));
    initialiseRates(getRates(), getKmc(), getParser());
  }

}
