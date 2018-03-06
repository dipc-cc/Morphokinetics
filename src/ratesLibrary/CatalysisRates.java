/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package ratesLibrary;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static kineticMonteCarlo.atom.CatalysisSite.BR;
import static kineticMonteCarlo.atom.CatalysisSite.CUS;
import static kineticMonteCarlo.atom.CatalysisSite.O;
import static kineticMonteCarlo.atom.CatalysisSite.CO;
import static kineticMonteCarlo.atom.CatalysisSite.O2;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public abstract class CatalysisRates implements IRates {

  private double[][][] diffusionEnergies;
  private double[] desorptionEnergiesCo;
  private double[][] desorptionEnergiesO2;
  private double[][] reactionEnergiesCoO;
  
  // Only for Farkas
  private double[] desorptionEnergiesCoCusCoCus;
  private double[] reactionEnergiesCoOCoCusCoCus;
  private double[] diffusionEnergiesCoCusCoCus;
  
  private final double prefactor;
  private double correctionFactor;
  
  /**
   * Mass of molecule (kg/molecule).
   */
  private final double[] mass;
  /**
   * Partial pressures in Pascal (Pa).
   * Pa = atm * 101325 (Pa: kg/m/s^2)
   */
  private final double[] pressures;
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
  private final float temperature;
  
  public CatalysisRates(float temperature) {
    this.temperature = temperature;
    
    prefactor = kB * temperature / hEv;
    correctionFactor = 0.5;///

    mass = new double[2]; // kg/molecule
    mass[CO] = 28.01055 / Na;
    mass[O2] = 2 * 15.9994 / Na;
    pressures = new double[2];
    adsorptionRates = new double[2];
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
    sigma[O2] = 1.32;
  }
  
  /**
   * Scales prefactor. By default is kB * temperature / hEv
   *
   * @param factor 1.0 or 0.5
   */
  public final void setCorrectionFactor(double factor) {
    this.correctionFactor = factor;
  }

  public final void setDiffusionEnergies(double[][][] diffusionEnergies) {
    this.diffusionEnergies = diffusionEnergies;
  }
  
  public final void setDiffusionEnergiesCoCusCoCus(double[] diffusionEnergiesCoCusCoCus) {
    this.diffusionEnergiesCoCusCoCus = diffusionEnergiesCoCusCoCus;
  }

  public final void setDesorptionEnergiesCo(double[] desorptionEnergiesCo) {
    this.desorptionEnergiesCo = desorptionEnergiesCo;
  }
  
  public final void setDesorptionEnergiesCoCusCoCus(double[] desorptionEnergiesCoCusCoCus) {
    this.desorptionEnergiesCoCusCoCus= desorptionEnergiesCoCusCoCus;
  }

  public final void setDesorptionEnergiesO2(double[][] desorptionEnergiesO2) {
    this.desorptionEnergiesO2 = desorptionEnergiesO2;
  }

  public final void setReactionEnergiesCoO(double[][] reactionEnergiesCoO) {
    this.reactionEnergiesCoO = reactionEnergiesCoO;
  }
  
  public final void setReactionEnergiesCoOcoCusCoCus(double[] reactionEnergiesCoOCoCusCoCus) {
    this.reactionEnergiesCoOCoCusCoCus = reactionEnergiesCoOCoCusCoCus;
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
  
  /**
   * Sets pressure for O2 given in atmospheres and saved in Pa.
   *
   * @param pressureO in atm.
   */
  public void setPressureO2(double pressureO) {
    setPressure(O2, pressureO);
  }
  
  /**
   * Sets pressure for CO given in atmospheres and saved in Pa.
   *
   * @param pressureCO in atm.
   */
  public void setPressureCO(double pressureCO) {
    setPressure(CO, pressureCO);
  }

  /**
   * Adsorption rate for CO or O.
   * 
   * @param atomType CO or O.
   * @return adsorption rate.
   */
  public double getAdsorptionRate(int atomType) {
    return adsorptionRates[atomType];
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
  
  /**
   * Only for Farkas and repulsion.
   *
   * @return rates.
   */
  public double[] getDesorptionRates() {
    double[] rates = new double[2];
    rates[0] = getDesorptionRate(CO, desorptionEnergiesCoCusCoCus[0]);
    rates[1] = getDesorptionRate(CO, desorptionEnergiesCoCusCoCus[1]);
    return rates;
  }
  
  public double[] getReactionRates() {
    double[] rates;
    double constant = correctionFactor * prefactor;
    rates = new double[4];
    rates[0] = constant * Math.exp(-reactionEnergiesCoO[BR][BR] / (kB * temperature));
    rates[1] = constant * Math.exp(-reactionEnergiesCoO[BR][CUS] / (kB * temperature));
    rates[2] = constant * Math.exp(-reactionEnergiesCoO[CUS][BR] / (kB * temperature));
    rates[3] = constant * Math.exp(-reactionEnergiesCoO[CUS][CUS] / (kB * temperature));
    return rates;
  }
  
  /**
   * Only for Farkas and repulsion.
   * 
   * @param fake fake parameter.
   * @return rates.
   */
  public double[] getReactionRates(boolean fake) {
    double[] rates;
    double constant = correctionFactor * prefactor;
    rates = new double[2];
    // only one CO cus neighbour
    rates[0] = constant * Math.exp(-reactionEnergiesCoOCoCusCoCus[0] / (kB * temperature));
    // two CO neighbours
    rates[1] = constant * Math.exp(-reactionEnergiesCoOCoCusCoCus[1] / (kB * temperature));
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
  
  /**
   * Only for Farkas and repulsion.
   *
   * @return rates.
   */
  public double[] getDiffusionRates()  {
    double[] rates = new double[3];
    rates[0] = getRate(0);
    rates[1] = getRate(1);
    rates[2] = getRate(2);
    return rates;
  }
  
  /**
   * Compute all adsorptions.
   */
  public void computeAdsorptionRates() {
    double stickingCoefficient = correctionFactor;
    adsorptionRates[CO] = stickingCoefficient * computeAdsorptionRate(CO);
    double dissociativeAdsorption = correctionFactor;
    adsorptionRates[O] = dissociativeAdsorption * computeAdsorptionRate(O2);
  }
  
  private void setPressure(byte type, double pressure) {
    if (pressure < 1e-20) {
      pressure = 1e-20;
    }
    pressures[type] = pressure * 101325.0;
  }

  private double getRate(int sourceType, int sourceSite, int destinationSite) {
    return prefactor * Math.exp(-diffusionEnergies[sourceType][sourceSite][destinationSite] / (kB * temperature));
  }
  
  /**
   * Only for Farkas and repulsion.
   *
   * @return rate.
   */
  private double getRate(int index) {
    return prefactor * Math.exp(-diffusionEnergiesCoCusCoCus[index] / (kB * temperature));
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
    double areaHalfUc = 10.0308e-20; // Angstrom²
    return pressures[sourceType] * areaHalfUc /
            (Math.sqrt(2.0 * Math.PI * mass[sourceType] * kBInt * temperature));
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
    qr[O2] = 8.0 * pow(PI, 2.0) * reducedMass[O2] * pow(R[O2], 2.0) * kBInt * temperature / (sigma[O2] * pow(h, 2.0));

    // vibrational partition function
    double[] qv = new double[2];
    qv[CO] = 1.0 / (1.0 - exp(-h * V[CO] / (kBInt * temperature)));
    qv[O2] = 1.0 / (1.0 - exp(-h * V[O2] / (kBInt * temperature)));

    mu[CO] = -kB * temperature * log(kBInt * temperature / pressures[CO] * qt[CO] * qr[CO] * qv[CO]);
    mu[O2] = -kB * temperature * log(kBInt * temperature / pressures[O2] * qt[O2] * qr[O2] * qv[O2]);
    double correction = type + 1; // adsorption rate for O is for an atom, this is for a O2 molecule.
    return correction * adsorptionRates[type] * exp(-(energy + mu[type]) / (kB * temperature));
  }
}
