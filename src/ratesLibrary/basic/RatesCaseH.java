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
public class RatesCaseH extends RatesCase {

  public RatesCaseH() {

    double[] prefactors = new double[4];
    double[] energies = new double[4];
    energies[0] = 0.0;
    energies[1] = 0.0;
    energies[2] = 0.4;
    energies[3] = 0.75;

    prefactors[0] = 1.0;
    prefactors[1] = 1.0;
    prefactors[2] = 8.0e4;
    prefactors[3] = 5.0e5;

    setRates(energies, prefactors);
  }

}

