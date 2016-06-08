/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.growth.GrapheneKmc;
import ratesLibrary.GrapheneSyntheticRates;

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

    setRates(new GrapheneSyntheticRates());
    setKmc(new GrapheneKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }

}
