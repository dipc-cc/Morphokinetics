/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ratesLibrary;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.O2;

/**
 *
 * @author karmele, J. Alberdi-Rodriguez
 */
public class CatalysisRates implements IRates {

  private final double[][][] diffusionEnergies;
  private final double[] desorptionEnergiesCo;
  private final double[][] desorptionEnergiesO2;
  private final double[][] reactionEnergiesCoO;
  
  private final double prefactor;
  
  /**
   * Mass of molecule (kg/molecule).
   */
  private final double[] mass;
  /**
   * Partial pressures (atm). It is not in Pascal.
   * Pa = p * 101325 (Pa: kg/m/s^2)
   */
  private final double[] pressures;
  private double totalAdsorptionRate;
  private final double[] adsorptionRates;
  /** Chemical potential (eV). */
  private final double[] mu;
  /** Molecular interdistance (m). Distance between atoms in the molecule.*/
  private final double[] R;
  /** Vibrational frequency (Hz). */
  private final double[] V;
  private final double[] reducedMass;
  /**
   * Symmetry number.
   */
  private final double[] sigma;
  /** Temperature (K). */
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
  
    desorptionEnergiesO2 = new double[2][2];
    desorptionEnergiesO2[BR][BR]  = 4.6;
    desorptionEnergiesO2[BR][CUS] = 3.3;
    desorptionEnergiesO2[CUS][BR] = 3.3;
    desorptionEnergiesO2[CUS][CUS] = 2.0;
    
    reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 1.5; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 0.8; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 1.2; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 0.9; // CO is in CUS and O in CUS
      
    mass = new double[2]; // kg/molecule
    mass[CO] = 28.01055 / Na;
    mass[O2] = 2 * 15.9994 / Na;
    pressures = new double[2];
    adsorptionRates = new double[2];
    totalAdsorptionRate = -1; // it needs to be initialised
    mu = new double[2];
    mu[CO] = 1;
    mu[O2] = 1;
    R = new double[2]; // m
    R[CO] = 1.128e-10;
    R[O2] = 1.21e-10;
    V = new double[2]; // Hz
    V[CO] = 6.5e13;
    V[O2] = 4.7e13;
    reducedMass = new double[2];
    reducedMass[CO] = (12.01115 * 15.9994) / ((12.01115 + 15.9994) * Na);
    reducedMass[O] = 15.9994 / (2.0 * Na);
    sigma = new double[2];
    sigma[CO] = 0.98;
    sigma[O2] = 0.66;
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
  
  public void setPressureO2(double pressureO) {
    setPressure(O2, pressureO);
  }
  
  public void setPressureCO(double pressureCO) {
    setPressure(CO, pressureCO);
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
      rates[0] = getDesorptionRate(O, desorptionEnergiesO2[BR][BR]);
      rates[1] = getDesorptionRate(O, desorptionEnergiesO2[BR][CUS]);
      rates[2] = getDesorptionRate(O, desorptionEnergiesO2[CUS][BR]);
      rates[3] = getDesorptionRate(O, desorptionEnergiesO2[CUS][CUS]);
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
  
  private void setPressure(byte type, double pressure) {
    if (pressure < 1e-10) {
      pressure = 1e-10;
    }
    pressures[type] = pressure;
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
            (Math.sqrt(2 * Math.PI * mass[sourceType] * kBInt * temperature));
  }
  
  /**
   * Compute all adsorptions. 
   */
  private void computeAdsorptionRates() {
    adsorptionRates[O] = 0.5 * computeAdsorptionRate(O2);
    adsorptionRates[CO] = computeAdsorptionRate(CO);
    totalAdsorptionRate = adsorptionRates[O] + adsorptionRates[CO];
  }
  
  /**
   * mu_{A} = -K_{B} T log( k_{B} T / p_{A} * (2 pi m_{A} k_{B} T /
h^2)^(3/2) * (8 pi^2 I k_{B} T / sigma h^2) * 1/
( 1 - exp( -h v / k_{B} T ) )) )
   * @param type
   * @param energy
   * @return 
   */
  private double getDesorptionRate(byte type, double energy) {
    // translationPartitionFunction
    double[] qt = new double[2];
    qt[CO] = pow(2.0 * PI * mass[CO] * kBInt * temperature / pow(h, 2.0), (3.0 / 2.0));
    qt[O2] = pow(2.0 * PI * mass[O2] * kBInt * temperature / pow(h, 2.0), (3.0 / 2.0)); //O2

    // rotational partition function
    double[] qr = new double[2];
    qr[CO] = 8.0 * pow(PI, 2.0) * reducedMass[CO] * pow(R[CO], 2.0) * kBInt * temperature / (sigma[CO] * pow(h, 2.0));
    qr[O] = 8.0 * pow(PI, 2.0) * reducedMass[O2] * pow(R[O], 2.0) * kBInt * temperature / (sigma[O] * pow(h, 2.0));

    // vibrational partition function
    double[] qv = new double[2];
    qv[CO] = 1.0 / (1.0 - exp(-h * V[CO] / (kBInt * temperature)));
    qv[O2] = 1.0 / (1.0 - exp(-h * V[O2] / (kBInt * temperature)));

    mu[CO] = -kB * temperature * log(kBInt * temperature / (pressures[CO] * 101325.0) * qt[CO] * qr[CO] * qv[CO]);
    mu[O2] = -kB * temperature * log(kBInt * temperature / (pressures[O2] * 101325.0) * qt[O2] * qr[O2] * qv[O2]);
    double correction = type + 1; // adsorption rate for O is for an atom, this is for a O2 molecule.
    return correction * adsorptionRates[type] * exp(-(energy + mu[type]) / (kB * temperature));
  }
}
