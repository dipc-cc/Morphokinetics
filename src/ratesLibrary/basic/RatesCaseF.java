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
public class RatesCaseF extends RatesCase {

  public RatesCaseF() {
    double[] prefactors = new double[4];
    double[] energies = new double[4];

    energies[0] = 1.0;
    energies[1] = 1.0;
    energies[2] = 5.0e3;
    energies[3] = 5.0e5;

    prefactors[0] = 1.0;
    prefactors[1] = 1.0;
    prefactors[2] = 5.0e3;
    prefactors[3] = 5.0e5;

    setRates(energies, prefactors);
  }

}
