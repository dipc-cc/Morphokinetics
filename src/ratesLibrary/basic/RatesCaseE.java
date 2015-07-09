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
public class RatesCaseE extends RatesCase {

  public RatesCaseE() {
    double[] prefactors = new double[4];
    double[] energies = new double[4];

    energies[0] = 0.0;
    energies[1] = 0.0;
    energies[2] = 0.3;
    energies[3] = 0.55;

    prefactors[0] = 5e+003;
    prefactors[1] = 5e+003;
    prefactors[2] = 5e+003;
    prefactors[3] = 5e+003;

    setRates(energies, prefactors);
  }

}
