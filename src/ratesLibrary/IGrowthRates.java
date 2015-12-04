/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IGrowthRates {

  public double getRate(int i, int j, double temperature);

  public double getDepositionRate();
  
  /**
   * Returns the island density mono layer depending on the temperature.
   * @param temperature
   * @return a double value
   */
  public double getIslandsDensityMl(double temperature);

  public double getEnergy(int i, int j);
}
