/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IRatesFactory {

  public double[] getRates(double temperature);

  public double getDepositionRatePerSite(double temperature);
  
  public double getDepositionRatePerSite();

  public double getIslandDensity(double temperature);

}
