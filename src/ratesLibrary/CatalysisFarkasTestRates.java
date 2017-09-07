/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.CatalysisAtom.CO;

/**
 * Farkas, Hess, Over J Phys. Chem C (2011). Supporting Information.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisFarkasTestRates extends CatalysisRates {
  
  private double K = 2;
  
  public CatalysisFarkasTestRates(float temperature) {
    super(temperature);
    setPrefactor(0.5);
  }
  
  @Override
  public double[] getDesorptionRates(int type) {
    double[] rates;
    if (type == CO) {
      rates = new double[2];
      rates[0] = 0.00;
      rates[1] = 0.00;
    } else {
      rates = new double[4];
      double kDes = 1;
      rates[0] = kDes;
      rates[1] = kDes;
      rates[2] = kDes;
      rates[3] = kDes;
    }
    return rates;
  }
  
  /**
   * Adsorption rate of 0.29, 0.15 or 0.03.
   * 
   * @param atomType CO or O. ignored.
   * @return adsorption rate.
   */
  @Override
  public double getAdsorptionRate(int atomType) {
    if (atomType == CO) {
      return 0.00;
    } else {
      return K;
    }
  }
}
