package basic;

import kineticMonteCarlo.kmcCore.growth.AgUcKmc;
import ratesLibrary.AgRatesFromPrbCox;
import ratesLibrary.AgSimpleRates;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcSimulation  extends AbstractGrowthSimulation {

  public AgUcSimulation(Parser parser) {
    super(parser);
  }
  
 @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    switch (getParser().getRatesLibrary()) {
      case "simple":
        setRates(new AgSimpleRates());
        break;
      default:
        setRates(new AgRatesFromPrbCox());
    }
    if (getParser().getHexaSizeI() == -1 || getParser().getHexaSizeJ() == -1) {
      double area = 1 / getRates().getIslandDensity(getParser().getTemperature()); // the inverse of the density is the area of the island
      int sizeX = (int) Math.ceil(Math.sqrt(area));
      int sizeY = (int) Math.ceil(Math.sqrt(area));
      getParser().setCartSizeX(sizeX);
      getParser().setCartSizeY(sizeY);
      System.out.println("Automatic size of the island is " + area + " " + sizeX + "x" + sizeY);
    }
    setKmc(new AgUcKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
}
