/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IRates {
  /**
   * Boltzmann constant (eV/K).
   */
  static double kB = 8.617332e-5;
  /**
   * Boltzmann constant (J/K).
   */
  static double kBInt = 1.381e-23;
  /**
   * Planck constant (J·s).
   */
  static double h = 6.6260695729e-34;
  /**
   * Avogadro constant (1/mol) * 1000 (g -> kg). 6.022e23·1000.
   */
  static double Na = 6.022e26;
  
  public double[] getRates(double temperature);

  public double getEnergy(int i, int j);  
  
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
  
  public void setDepositionFlux(double diffusionMl);
}
