/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
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
