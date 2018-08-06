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
package ratesLibrary.bda;

import kineticMonteCarlo.unitCell.BdaMoleculeUc;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractBdaRates implements IRates {
  
  private double diffusionMl;
  private final double prefactor;
  /** Temperature (K). */
  private final float temperature;
  
  public AbstractBdaRates(float temperature) {
    this.temperature = temperature;
    prefactor = 1e13;
  }

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }
  
  /**
   * In principle, deposition rate is constant.
   *
   * @return diffusion mono layer (or deposition flux)
   */
  @Override
  public double getDepositionRatePerSite() {
    return getRate(getDepositionEnergy());
  }
  
  abstract double getDepositionEnergy();
  

  public double[] getDesorptionRates() {
    double[] rates = new double[12];
    for (int i = 0; i < rates.length; i++) {
      rates[i] = 1e-5;      
    }
    return rates;
  }

  /**
   * Base energy of the molecule - the actual energy (depending on the neighbourhood).
   * 
   * @param bdaUc unit cell that contains the BDA molecule.
   * @param type alpha or beta.
   * @return rate.
   */
  public double getDiffusionRate(BdaMoleculeUc bdaUc, byte type) {
    return getRate(getDiffusionEnergy(type) - bdaUc.getEnergy());
  }
  
  private double getRate(double energy) {
    return prefactor * Math.exp(-energy / (kB * temperature));
  }
 
  abstract double getDiffusionEnergy(byte type);
  
  public double getRotationRate(BdaMoleculeUc bdaUc) {
    return getRate(getRotationEnergy() - bdaUc.getEnergy());
  }
  
  abstract double getRotationEnergy();
  
  abstract double getShiftEnergy();
  
  public double getShiftRate(BdaMoleculeUc bdaUc) {
    return getRate(getShiftEnergy() - bdaUc.getEnergy());
  }
  
  abstract double getTransformEnergy();
  
  public double getTransformRate() {
    return getRate(getTransformEnergy());
  }
}
