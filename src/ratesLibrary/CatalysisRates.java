/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ratesLibrary;

import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;

/**
 *
 * @author karmele, J. Alberdi-Rodriguez
 */
public class CatalysisRates implements IRates {

  private final double[][][] diffusionEnergies;
  private final double[] desorptionEnergiesCo;
  private final double[][] desorptionEnergiesO;
  private final double[][] reactionEnergiesCoO;
  
  private final double prefactor;
  
  private final double[] mass;
  private final double[] pressures;
  private double totalAdsorptionRate;
  private final double[] adsorptionRates;

  
  private final int temperature;
  
  public CatalysisRates(int temperature) {
    this.temperature = temperature;
    
    double Einf = 9999999;
    prefactor = 1e13;
    
    diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = 0.6;
    diffusionEnergies[CO][BR][CUS] = 1.6;
    diffusionEnergies[CO][CUS][BR] = 1.3;
    diffusionEnergies[CO][CUS][CUS] = 1.7;
    diffusionEnergies[O][BR][BR] = 0.7;
    diffusionEnergies[O][BR][CUS] = 2.3;
    diffusionEnergies[O][CUS][BR] = 1.0;
    diffusionEnergies[O][CUS][CUS] = 1.6;
    
    desorptionEnergiesCo = new double[2];
    desorptionEnergiesCo[BR] = 1.6;
    desorptionEnergiesCo[CUS] = 1.3;
  
    desorptionEnergiesO = new double[2][2];
    desorptionEnergiesO[BR][BR]  = 4.6;
    desorptionEnergiesO[BR][CUS] = 3.3;
    desorptionEnergiesO[CUS][BR] = 3.3;
    desorptionEnergiesO[CUS][CUS] = 2.0;
    
    reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 1.5; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 0.8; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 1.2; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 0.9; // CO is in CUS and O in CUS
      
    mass = new double[2]; // g/mol
    mass[CO] = 28.01055;
    mass[O] = 15.9994*2;
    pressures = new double[2];
    adsorptionRates = new double[2];
    totalAdsorptionRate = -1; // it needs to be initialised
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
  
  public void setPressureO(double pressureO) {
    pressures[O] = pressureO;
  }
  
  public void setPressureCO(double pressureCO) {
    pressures[CO] = pressureCO;
  }

  /**
   * Sum of adsorption rates of CO and O.
   * 
   * @return total adsorption rate.
   */
  public double getTotalAdsorptionRate() {
    if (totalAdsorptionRate == -1) {
      computeAdsorptionRates();
    }
    return totalAdsorptionRate;
  }

  /**
   * Adsorption rate for CO or O.
   * 
   * @param atomType CO or O.
   * @return adsorption rate.
   */
  public double getAdsorptionRate(int atomType) {
    if (totalAdsorptionRate == -1) {
      computeAdsorptionRates();
    }
    return adsorptionRates[atomType];
  }

  /**
   * All adsorption rates.
   * 
   * @return all adsorption rates.
   */
  public double[] getAdsorptionRates() {
    if (totalAdsorptionRate == -1) {
      computeAdsorptionRates();
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
  
  public double[] getDesorptionRates(int type) {
    double[] rates;
    if (type == CO) {
      rates = new double[2];
      rates[0] = prefactor * Math.exp(-desorptionEnergiesCo[0] / (kB * temperature));
      rates[1] = prefactor * Math.exp(-desorptionEnergiesCo[1] / (kB * temperature));
    } else {
      rates = new double[4];
      rates[0] = prefactor * Math.exp(-desorptionEnergiesO[BR][BR] / (kB * temperature));
      rates[1] = prefactor * Math.exp(-desorptionEnergiesO[BR][CUS] / (kB * temperature));
      rates[2] = prefactor * Math.exp(-desorptionEnergiesO[CUS][BR] / (kB * temperature));
      rates[3] = prefactor * Math.exp(-desorptionEnergiesO[CUS][CUS] / (kB * temperature));
    }
    return rates;
  }
  
  public double[] getReactionRates() {
    double[] rates;
    double planckConstant = 4.136e-15; // eV⋅s
    double constant = 0.5 * kB * temperature / planckConstant;
    rates = new double[4];
    rates[0] = constant * Math.exp(-reactionEnergiesCoO[BR][BR] / (kB * temperature));
    rates[1] = constant * Math.exp(-reactionEnergiesCoO[BR][CUS] / (kB * temperature));
    rates[2] = constant * Math.exp(-reactionEnergiesCoO[CUS][BR] / (kB * temperature));
    rates[3] = constant * Math.exp(-reactionEnergiesCoO[CUS][CUS] / (kB * temperature));
    return rates;
  }
    
  
  private double getRate(int sourceType, int sourceSite, int destinationSite, double temperature) {
    return prefactor * Math.exp(-diffusionEnergies[sourceType][sourceSite][destinationSite] / (kB * temperature));
  }
  
  /**
   * Equation (3) of Reuter & Scheffler, PRB 73, 2006.
   * k_i = \frac{p_A A_s}{\sqrt{2\pi m_A K_B T}}
   * 
   * @param sourceType
   * @param pressures
   * @param temperature
   * @return 
   */
  private double getAdsorptionRate(int sourceType, double pressures[], double temperature) {
    return pressures[sourceType] * 101132 * 5.0145e-20 /
            (Math.sqrt(2 * Math.PI * mass[sourceType] * 1.381e-23 * temperature /(1000 * 6.022e23)));
  }
  
  /**
   * Compute all adsorptions. 
   */
  private void computeAdsorptionRates() {
    adsorptionRates[O] = 0.25 * getAdsorptionRate(O, pressures, temperature);
    adsorptionRates[CO] = getAdsorptionRate(CO, pressures, temperature);
    totalAdsorptionRate = adsorptionRates[O] + adsorptionRates[CO];
  }
}
