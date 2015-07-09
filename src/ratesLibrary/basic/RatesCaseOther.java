/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.basic;

/**
 *
 * @author Nestor, J. Alberdi-Rodriguez
 *
 * Etch rates data obtained from Gosalvez Et al - Physical Review E 68 (2003) 031604
 *
 */
public class RatesCaseOther extends RatesCase {

  public RatesCaseOther() {
    double[] prefactors = new double[4];
    double[] energies = new double[4];

    energies[0] = 0.0;
    energies[1] = 0.0;
    energies[2] = 0.0;
    energies[3] = 0.0;

    prefactors[0] = 1.0;
    prefactors[1] = 1.0;
    prefactors[2] = 1.0;
    prefactors[3] = 0.01;

    setRates(energies, prefactors);
  }
}
