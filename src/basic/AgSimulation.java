/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.growth.AgKmc;
import ratesLibrary.AgRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimulation extends AbstractGrowthSimulation {

  public AgSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new AgRatesFactory());
    if (getParser().getHexaSizeI() == -1 || getParser().getHexaSizeJ() == -1) {
      double area = 1 / getRates().getIslandDensity(getParser().getTemperature()); // the inverse of the density is the area of the island
      int sizeX = (int) Math.ceil(Math.sqrt(area));
      int sizeY = (int) Math.ceil(Math.sqrt(area));
      getParser().setCartSizeX(sizeX);
      getParser().setCartSizeY(sizeY);
      System.out.println("Automatic size of the island is " + area + " " + sizeX + "x" + sizeY);
    }
    setKmc(new AgKmc(getConfig(),
            getParser().getHexaSizeI(), 
            getParser().getHexaSizeJ(),
            getParser().justCentralFlake(),
            (float) getParser().getCoverage()/100,
            getParser().useMaxPerimeter(),
            getParser().getPerimeterType(),
            getParser().depositInAllArea()));
    initialiseRates(getRates(), getKmc(), getParser());
  }
}
