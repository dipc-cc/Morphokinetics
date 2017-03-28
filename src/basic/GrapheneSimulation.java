/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.growth.GrapheneKmc;
import ratesLibrary.GrapheneGaillardOneNeighbourRates;
import ratesLibrary.GrapheneGaillardRates;
import ratesLibrary.GrapheneGaillardSimpleRates;
import ratesLibrary.GrapheneSchoenhalzRates;
import ratesLibrary.GrapheneSyntheticRates;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneSimulation extends AbstractGrowthSimulation {

  private final String ratesLibrary;
  
  public GrapheneSimulation(Parser parser) {
    super(parser);
    
    ratesLibrary = parser.getRatesLibrary();
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    IRates rates;
    switch (ratesLibrary) {
      case "GaillardSimple":
        rates = new GrapheneGaillardSimpleRates();
        break;
      case "Gaillard1Neighbour":
        rates = new GrapheneGaillardOneNeighbourRates();
        break;
      case "Gaillard2Neighbours":
        rates = new GrapheneGaillardRates();
        break;
      case "Schoenhalz":
        rates = new GrapheneSchoenhalzRates();
        break;
      default:
        rates = new GrapheneSyntheticRates();
        break;
    }
        
    setRates(rates);
    setKmc(new GrapheneKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }

}
