/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IRatesFactoryKK {

  public double[] getRates(double temperature);

  public double getDepositionRatePerSite();
  
  /**
   * Returns the island density mono layer depending on the temperature. How many islands per area
   * site are generated at current temperature. Usually with higher temperature less islands are
   * created, and thus, island density is lower. And the other way around.
   *
   * @param temperature temperature in Kelvin.
   * @return island density
   */
  public double getIslandDensity(double temperature);
 
  public void setDepositionFlux(double depositionFlux);
}
