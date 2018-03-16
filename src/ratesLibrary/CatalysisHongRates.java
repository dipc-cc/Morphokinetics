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
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.H2O;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH3;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NO;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.OH;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.VAC;

/**
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
  private double NH3_O_reaction_NH2_OH;
  private double NH2_OH_reaction_NH_H2O;
  private double NH_OH_reaction_N_H2O;
  private double NH_O_reaction_N_OH;
  private double N_O_reaction_NO;
  private double NH2_O_reaction_NH_OH;
  private double NH_OH_reaction_NH2_O;
  private double NH2_OH_reaction_NH3_O;
  private double N_OH_reaction_NH_O;
  private final double prefactor;
  private final double[] adsorptionRates;
  
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
    mass[NH3] = (14.006 * 3 + 1.007)/ Na; 
    mass[O2] = 2 * 15.9994 / Na;
    stickingCoefficient = 1;
    adsorptionRates = new double[11]; // Only NH3 and O can adsorb
    
    desorptionEnergies = new double[11]; // to big, but can keep all the type of particles
    desorptionEnergies[NH3] = 1.46; // P2
    desorptionEnergies[O] = 1.26; // P4
    desorptionEnergies[N2] = 0.27; // P10
    desorptionEnergies[NO] = 1.49; // P11
    
    diffusionEnergies = new double[11];
    diffusionEnergies[N] = 0.96; // P12
    diffusionEnergies[O] = 0.93; // P13
    diffusionEnergies[OH] = 1.12; // P14
    
    NH3_O_reaction_NH2_OH = 0.01;//0.55; // P5;
    NH2_OH_reaction_NH_H2O = 0.001;//0.27; // P6;
    NH_OH_reaction_N_H2O= 0; // P7;
    NH_O_reaction_N_OH = 0; // P8;
    N_O_reaction_NO = 0.14; // P9;
    NH2_O_reaction_NH_OH = 1.0; // P15;
    NH_OH_reaction_NH2_O = 0; // P16;
    NH2_OH_reaction_NH3_O = 0.26; // P17;
    N_OH_reaction_NH_O = 0.9; // P18;

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
    /*reactionEnergies[NH3][O][NH2][OH] = 0.55; // P5
    reactionEnergies[NH2][OH][NH][H2O] = 0.27; // P6
    reactionEnergies[NH2][OH][NH3][O] = 0.26; // P17
    reactionEnergies[NH2][O][NH][OH] = 1.0; // P15
    reactionEnergies[NH][OH][N][H2O] = 0; // P7
    reactionEnergies[NH][OH][NH2][O] = 0; // P16
    reactionEnergies[NH][O][N][OH] = 0; // P8
    reactionEnergies[N][O][NO][VAC] = 0.14; // P9
    reactionEnergies[N][OH][NH][O] = 0.9; // P18*/

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
    return pressures[sourceType] * areaHalfUc /
            (Math.sqrt(2.0 * Math.PI * mass[sourceType] * kBInt * temperature));
  }

  public double getDesorptionRate(int type) {
    return getRate(desorptionEnergies[type]);
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
