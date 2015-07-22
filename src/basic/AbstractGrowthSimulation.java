/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import kineticMonteCarlo.kmcCore.AbstractKmc;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSimulation {

  public AbstractGrowthSimulation(Parser myParser) {
    super(myParser);
  }
  
  
  @Override
  protected void initializeRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser) {
    double depositionRate = ratesFactory.getDepositionRate(myParser.getTemperature());
    double islandDensity = ratesFactory.getIslandDensity(myParser.getTemperature());
    this.kmc.setIslandDensityAndDepositionRate(depositionRate, islandDensity);
    this.kmc.initializeRates(ratesFactory.getRates(myParser.getTemperature()));

  }
  
  /**
   * Do nothing
   */
  @Override
  public void finishSimulation(){
    
  }
}
