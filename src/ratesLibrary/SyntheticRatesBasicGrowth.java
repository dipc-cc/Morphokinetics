/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SyntheticRatesBasicGrowth implements IGrowthRates {

  private final double[][] energies;
  private double diffusionMl;
  private final double islandDensityPerSite;
  
  /**
   * Boltzmann constant.
   */
  private final double kB;
  private final double Ea;
  private final double Ed;
  private final double Ef;
  private final double Einf;
  private final double prefactor;

  public SyntheticRatesBasicGrowth()  { 
    kB = 8.617332e-5;
    diffusionMl = 0.000035;
    islandDensityPerSite = 1 / 60000f;
    
    Ed = 0.100;
    Ea = 0.275;
    Ef = 0.360;
    Einf = 9999999;
    
    prefactor = 1e13;
    
    energies = new double[4][4];
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][EDGE] = Ed;
    energies[TERRACE][KINK] = Ed;
    energies[TERRACE][ISLAND] = Ed;

    energies[EDGE][TERRACE] = Einf;
    energies[EDGE][EDGE] = Ef;
    energies[EDGE][KINK] = Ef;
    energies[EDGE][ISLAND] = Ef;

    energies[KINK][TERRACE] = Einf;
    energies[KINK][EDGE] = Ef;
    energies[KINK][KINK] = Ef;
    energies[KINK][ISLAND] = Ef;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
  }
  
  @Override
  public double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. 
   * These values are taken from section 4 of the paper of Cox et al.
   * 
   * (But are not consistent with, for example, the multi-flake
   * simulations: 180K, 250x250)
   * @param temperature
   * @return a double value from 1e-4 to 2e-5
   */
  @Override
  public double getIslandsDensityMl(double temperature) {
    if (temperature < 135) {//120 degrees Kelvin
      return 1e-4;
    }
    if (temperature < 150) {//135 degrees Kelvin
      return 5e-5;
    }
    if (temperature < 165) {//150 degrees Kelvin
      return 4e-5;
    }
    if (temperature < 180) {//165 degrees Kelvin
      return 3e-5;
    }
    return 2e-5; //180 degrees Kelvin
  }


  @Override
  public double getEnergy(int i, int j) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. By default it F=0.000035 ML/s.
   * The perimeter deposition is calculated multiplying F (this) and island density.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDiffusionMl(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }
}
