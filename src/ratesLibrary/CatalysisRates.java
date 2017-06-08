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
  /** Chemical potential. */
  private final double[] R;
  private final double[] V;
  private final double[] preI;
  private final double[] sigma;
  
  private final int temperature;
  
  public CatalysisRates(int temperature) {
    this.temperature = temperature;
    
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
    mass[O] = 2 * 15.9994;
    pressures = new double[2];
    adsorptionRates = new double[2];
    totalAdsorptionRate = -1; // it needs to be initialised
    R = new double[2];
    R[CO] = 1.128e-10;
    R[O] = 1.21e-10;
    V = new double[2];
    V[CO] = 6.5e13;
    V[O] = 4.7e13;
    preI = new double[2];
    preI[CO] = (12.01115 * 15.9994) / ((12.01115 + 15.9994)*(1000 * Na));
    preI[O] = 15.9994  / (2000 * Na);
    sigma = new double[2];
    sigma[CO] = 0.98;
    sigma[O] = 0.66;
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
          rates[index] = getRate(i, j, k);
        }
      }
    }
    return rates;
  }
  
  public double[] getDesorptionRates(int type) {
    double[] rates;
    if (type == CO) {
      rates = new double[2];
      rates[0] = getDesorptionRate(CO, desorptionEnergiesCo[BR]);
      rates[1] = getDesorptionRate(CO, desorptionEnergiesCo[CUS]);
    } else {
      rates = new double[4];
      rates[0] = getDesorptionRate(O, desorptionEnergiesO[BR][BR]);
      rates[1] = getDesorptionRate(O, desorptionEnergiesO[BR][CUS]);
      rates[2] = getDesorptionRate(O, desorptionEnergiesO[CUS][BR]);
      rates[3] = getDesorptionRate(O, desorptionEnergiesO[CUS][CUS]);
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
  
  public double[] getDiffusionRates(int type) {
    double[] rates = new double[4];
    rates[0] = getRate(type, BR, BR);
    rates[1] = getRate(type, BR, CUS);
    rates[2] = getRate(type, CUS, BR);
    rates[3] = getRate(type, CUS, CUS);
    return rates;
  }    
  
  private double getRate(int sourceType, int sourceSite, int destinationSite) {
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
  private double computeAdsorptionRate(int sourceType) {
    return pressures[sourceType] * 101132 * 5.0145e-20 /
            (Math.sqrt(2 * Math.PI * mass[sourceType] * kBInt * temperature /(1000 * Na)));
  }
  
  /**
   * Compute all adsorptions. 
   */
  private void computeAdsorptionRates() {
    adsorptionRates[O] = 0.5 * computeAdsorptionRate(O);
    adsorptionRates[CO] = computeAdsorptionRate(CO);
    totalAdsorptionRate = adsorptionRates[O] + adsorptionRates[CO];
  }
  
  private double getDesorptionRate(byte site, double energy) {
    double _desRate =  adsorptionRates[site] * Math.exp((energy * 1.602176565e-19 + (-kBInt * temperature * Math.log10((kBInt * temperature / (pressures[site] * 101325))* Math.pow(2 * Math.PI * (mass[site] / (1000 * Na)) * kBInt * temperature / Math.pow(h, 2), 3/2) * (8 * Math.pow(Math.PI,2) * (preI[site] * Math.pow(R[site],2))  * kBInt * temperature / (sigma[site] * Math.pow(h,2))) * (Math.exp(-h * V[site] / (2 * kBInt * temperature))/(1-Math.exp(-h * V[site] / (kBInt * temperature))))))) / (kBInt * temperature));
    System.out.println((-kBInt * temperature * Math.log10((kBInt * temperature / (pressures[site] * 101325))* Math.pow(2 * Math.PI * (mass[site] / (1000 * Na)) * kBInt * temperature / Math.pow(h, 2), 3/2) * (8 * Math.pow(Math.PI,2) * (preI[site] * Math.pow(R[site],2))  * kBInt * temperature / (sigma[site] * Math.pow(h,2))))));
    return _desRate;
  }
  
  
}
