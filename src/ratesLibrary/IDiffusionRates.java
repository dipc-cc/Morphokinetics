/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IDiffusionRates {

  public double getRate(int i, int j, double temperature);

  public double getDepositionRate();

  public double getIslandsDensityML(double temperature);
}
