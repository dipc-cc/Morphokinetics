/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ratesLibrary;

import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.BRBR;
import static kineticMonteCarlo.atom.CatalysisAtom.CUSCUS;
import static kineticMonteCarlo.atom.CatalysisAtom.BRCUS;
import static kineticMonteCarlo.atom.CatalysisAtom.CUSBR;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;

/**
 *
 * @author karmele, J. Alberdi-Rodriguez
 */
public class CatalysisRates implements IRates {

  private final double[][][] diffusionEnergies;
  private final double[][] adsorptionDesorptionEnergies;
  
  private final double prefactor;
  
  private final double[] mass;
  /**
   * Same as {@link BasicGrowthSyntheticRates}.
   */
  public CatalysisRates() {
    
    double E1 = 0.6;
    double E2 = 1.6;
    double E3 = 1.3;
    double E4 = 1.7;
    double E5 = 0.7;
    double E6 = 2.3;
    double E7 = 1.0;
    double E8 = 1.6;
    double Einf = 9999999;
    
    double E9 = 1.6;
    double E10 = 1.3;
    double E11 = 4.6;
    double E12 = 2.0;
    double E13 = 3.3;
    
    prefactor = 1e13;
    
    diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = E1;
    diffusionEnergies[CO][BR][CUS] = E2;
    diffusionEnergies[CO][CUS][BR] = E3;
    diffusionEnergies[CO][CUS][CUS] = E4;
    diffusionEnergies[O][BR][BR] = E5;
    diffusionEnergies[O][BR][CUS] = E6;
    diffusionEnergies[O][CUS][BR] = E7;
    diffusionEnergies[O][CUS][CUS] = E8;
    
    adsorptionDesorptionEnergies = new double[2][4];
    adsorptionDesorptionEnergies[CO][BR] = E9;
    adsorptionDesorptionEnergies[CO][CUS] = E10;
    adsorptionDesorptionEnergies[O][BRBR] = E11;
    adsorptionDesorptionEnergies[O][CUSCUS] = E12;
    adsorptionDesorptionEnergies[O][CUSBR] = E13;
    adsorptionDesorptionEnergies[O][BRCUS] = E13;
    
    mass = new double[2];
    mass[CO] = 28.01055;
    mass[O] = 15.9994;
  }

  @Override
  public double getDepositionRatePerSite() {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms. Use instead absortion methods.");
  }

  @Override
  public void setDepositionFlux(double diffusionMl) {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms. Use instead absortion methods.");
  }
  
  @Override
  public double getIslandDensity(double temperature) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }

  @Override
  public double getEnergy(int i, int j) {
    return diffusionEnergies[i][j][0];
  }

  public double[] getAdsorptionRates(int temperature, int presure) {
    double[] adsorptionRates = new double[2];
    for (int i = 0; i < adsorptionRates.length; i++) {
      adsorptionRates[i] = getAdsorptionRate(i, presure, temperature);
    }
    return adsorptionRates;
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[8];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          int index = (i * 2 * 2) + (j * 2) + k;
          rates[index] = getRate(i, j, k, temperature);
        }
      }
    }
    return rates;
  }
  
  public double[] getReduced5Energies() {
    double[] rates = new double[5];
    rates[0] = getEnergy(0, 0);
    rates[1] = getEnergy(1, 0);
    rates[2] = getEnergy(1, 1);
    rates[3] = getEnergy(1, 2);
    rates[4] = getEnergy(2, 1);
    return rates;
  }
  
  private double getRate(int sourceType, int sourceSite, int destinationSite, double temperature) {
    return prefactor * Math.exp(-diffusionEnergies[sourceType][sourceSite][destinationSite] / (kB * temperature));
  }
  
  private double getAdsorptionRate(int sourceType, double pressure, double temperature) {
    return pressure * 10 / Math.sqrt(2 * Math.PI * mass[sourceType] * kB * temperature);
  }
}
