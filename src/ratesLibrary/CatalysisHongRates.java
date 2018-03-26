/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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

import static java.lang.Math.exp;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH3;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NO;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.OH;

/**
 * Rates from S. Hong et al. Journal of Catalysis 276 (2010) 371-381.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisHongRates extends CatalysisRates {

  /**
   * Mass of molecule (kg/molecule).
   */
  private final double[] mass;
  
  private final double[] desorptionEnergies;
  private final double[] diffusionEnergies;
  private final double[] reactionEnergies;
  private final double prefactor;
  private final double[] adsorptionRates;
  
  private final double[] epsilon;  
  private final double stickingCoefficient;
  /**
   * Partial pressures in Pascal (Pa).
   * Pa = atm * 101325 (Pa: kg/m/s^2)
   */
  private final double[] pressures;
  /** Temperature (K). */
  private final float temperature;
  
  public CatalysisHongRates(float temperature) {
    super(temperature);
    
    prefactor = 1e13;

    mass = new double[11]; // kg/molecule
    mass[NH3] = (14.006 + 1.007 * 3)/ Na; 
    mass[O2] = 2 * 15.9994 / Na;
    stickingCoefficient = 1;
    adsorptionRates = new double[11]; // Only NH3 and O can adsorb
    
    desorptionEnergies = new double[11]; // to big, but can keep all the type of particles
    desorptionEnergies[NH3] = 1.46; // P2
    desorptionEnergies[O] = 1.26; // P4
    desorptionEnergies[N2] = 0.27; // P10
    desorptionEnergies[N] = 0.27; // P10
    desorptionEnergies[NO] = 1.49; // P11
    
    epsilon = new double[11];
    epsilon[NH3] = 0.34;
    epsilon[NO] = 0.16;
    
    diffusionEnergies = new double[11];
    diffusionEnergies[N] = 0.96; // P12
    diffusionEnergies[O] = 0.93; // P13
    diffusionEnergies[OH] = 1.12; // P14

    reactionEnergies = new double[19];
    reactionEnergies[5] = 0.55; // P5
    reactionEnergies[6] = 0.27; // P6
    reactionEnergies[7] = 0; // P7
    reactionEnergies[8] = 0; // P8
    reactionEnergies[9] = 0.14; // P9
    reactionEnergies[15] = 1.0; // P15
    reactionEnergies[16] = 0; // P16
    reactionEnergies[17] = 0.26; // P17
    reactionEnergies[18] = 0.9; // P18

    pressures = new double[11];
    this.temperature = temperature;
  }
  /**
   * Sets pressure for NH3 given in atmospheres and saved in Pa. I know, the method's name is not
   * the best one.
   *
   * @param pressureCO in atm.
   */
  @Override
  public void setPressureCO(double pressureCO) {
    setPressure(NH3, pressureCO);
  }
  
  /**
   * Sets pressure for O2 given in atmospheres and saved in Pa.
   *
   * @param pressureO in atm.
   */
  @Override
  public void setPressureO2(double pressureO) {
    setPressure(O2, pressureO);
  }

  /**
   * Compute all adsorptions.
   */
  @Override
  public void computeAdsorptionRates() {
    adsorptionRates[NH3] = stickingCoefficient * computeAdsorptionRate(NH3);
    adsorptionRates[O] = stickingCoefficient * computeAdsorptionRate(O2);
  }
  /**
   * Adsorption rate for NH3 or O. Adsorption is only allowed in CUS.
   * 
   * @param atomType NH3 or O.
   * @return adsorption rate.
   */
  @Override
  public double getAdsorptionRate(int atomType) {
    return adsorptionRates[atomType];
  }
  
  private void setPressure(byte type, double pressure) {
    if (pressure < 1e-20) {
      pressure = 1e-20;
    }
    pressures[type] = pressure * 101325.0;
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
    double correction = 2.0; // This correction is need to reproduce fig 7 of the paper.
    return correction * pressures[sourceType] * areaHalfUc /
            (Math.sqrt(2.0 * Math.PI * mass[sourceType] * kBInt * temperature));
  }

  public double getDesorptionRate(int type) {
    return getRate(desorptionEnergies[type]);
  }
  
  /**
   * Coverage dependent interaction for desorption of NH3 and NO.
   * 
   * @param coverage coverage of NH3 or NO.
   * @param type NH3 or NO.
   * @return factor to be multiplied by the rate.
   */
  public double desorptionRepulsionRate(double coverage, byte type) {
    return exp(epsilon[type] * coverage / (kB * temperature));
  }
  
  public double getDiffusionRate(int type) {
    return getRate(diffusionEnergies[type]);
  }
  
  public double getReactionRate(int position) {
    return getRate(reactionEnergies[position]);
  }

  private double getRate(double energy) {
    return prefactor * exp(-energy / kB / temperature);
  }
}
