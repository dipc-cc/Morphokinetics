/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ratesLibrary;

import static java.lang.Math.pow;
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
 * @author karmele
 */
public class CatalysisRates implements IRates {

  private final double[][][] diffusionEnergies;
  private final double[][] adsorptionDesorptionEnergies;
  private double diffusionMl;
  
  private final double prefactor;
  
  private final double[] mass;
  /**
   * Same as {@link BasicGrowthSyntheticRates}.
   */
  public CatalysisRates() {
    diffusionMl = 0.000035;
    
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
    mass [CO] = 28.01055;
    mass [O] =15.9994;
  }

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. 
   * These values are taken from many run of multi flake with 400x400 lattice points
   * 
   * @param temperature
   * @return a double density value
   */
  @Override
  public double getIslandDensity(double temperature) {
    double flux = diffusionMl;
    double c;
    double slope;
    double rtt;
    if (temperature > 250) {
      c = 220;
      slope = -(2.d / 3.d);
    } else {
      c = 0.25;
      slope = -(1.d / 3.d);
    }
    rtt = getRate(0, 0, 0, temperature);
     return pow(flux, 0.23d) * c * pow(rtt / pow(flux, 1.d / 3.d), slope);
  }

  @Override
  public double getEnergy(int i, int j) {
    return diffusionEnergies[i][j][0];
  }
  
  private double getAdsorptionRate(int sourceType, double pressure, double temperature) {
    return pressure * 10 / Math.sqrt(2 * Math.PI * mass[sourceType] * kB * temperature);
  }
  
  private double getRate(int sourceType, int sourceSite, int destinationSite, double temperature) {
    return prefactor * Math.exp(-diffusionEnergies[sourceType][sourceSite][destinationSite] / (kB * temperature));
  }

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. By default it F=0.000035 ML/s.
   * The perimeter deposition is calculated multiplying F (this) and island density.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[8];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          int index = (i * 2 * 2) + (j * 2) + k;
          rates[index] = (getRate(i, j, k, temperature));
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
}
