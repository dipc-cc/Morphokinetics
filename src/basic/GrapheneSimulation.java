/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.diffusion.GrapheneKmc;
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

    this.rates = new GrapheneRatesFactory();
    this.kmc = new GrapheneKmc(config,
            parser.getHexaSizeI(), 
            parser.getHexaSizeJ(),
            parser.justCentralFlake(),
            (float) parser.getCoverage()/100,
            parser.useMaxPerimeter(),
            parser.getPerimeterType());
    initializeRates(rates, kmc, parser);
  }

}
