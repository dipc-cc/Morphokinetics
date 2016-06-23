/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static ratesLibrary.IRates.kB;

/**
 * Based on paper P. Gaillard, A. L. Schoenhalz, P. Moskovkin, S. Lucas, L. Henrard, Surface Science
 * (2016) 644, 102-108.
 *
 * @author Nestor, J. Alberdi-Rodriguez
 */
public class GrapheneSchoenhalzRates implements IRates {

  private final double[][] energies;
  private double diffusionMl;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;
  
  public GrapheneSchoenhalzRates() {
    diffusionMl = 5e-4;
    energies = new double[8][8];
    prefactor = 1e13; // s^-1
  }
  /**
   * Rate is calculated with the supporting info.
   *
   * @param originN1
   * @param originN2
   * @param destinationN1
   * @param destinationN2
   * @param temperature
   * @return
   */
  public double getRate(int originN1, int originN2, int destinationN1, int destinationN2, double temperature) {
    double energy = -1;
    int nn = originN1;
    int nnn = originN2;
    int event = 0;
    if (nn < 0 || nnn < 0) {
      throw new IllegalArgumentException("Number of occupied neighbours can't be negative. Exiting");
    }
    if (nn == 0 && destinationN1 == 0) {
      energy = 0.5; // event 0
      event = 0;
    } else if (nn == 1 && nnn == 2 && destinationN1 == 1 && destinationN2 == 2) { // my own invention
      energy = 0.61; // event 20
      event = 20;
    } else if (nn == 1 && nnn == 3 && destinationN1 == 1 && destinationN2 == 3) { // my own invention
      energy = 0.3; // event 22
      event = 22;
    } else if (nn == 1 && destinationN1 == 2) {
      energy = 1.8; // event 3
      event = 3;
    } else if (nn == 2 && destinationN1 == 0) {
      energy = 3.9; // event 5
      event = 5;
    } else if (nn == 1 && nnn > 0 && destinationN1 == 0) {
      energy = 2.7; // event 7
      event = 7;
    } else if (nn == 2 && destinationN1 == 1) {
      energy = 2.5; // event 9
      event = 9;
    } else if (nn == 0 && destinationN1 > 0) {
      energy = 0.1; // event 10
      event = 10;
    } else if (nn == 1 && nnn > 2 && destinationN1 == 1) {
      energy = 1.1; // event 11
      event = 11;
    } else if (nn == 1 && nnn <= 2 && destinationN1 == 1) {
      energy = 1.3; // event 13
      event = 13;
    } else if (nn == 2 && destinationN1 == 2) {
      energy = 2.3; // event 15
      event = 15;
    } else if (nn == 1 && nnn == 0 && destinationN1 == 0) {
      energy = 3.2; // event 17
      event = 17;
    }
    if (energy < 0) {
      System.err.println("nn "+nn+" nnn "+nnn+" destinationN1 "+destinationN1+" destinationN2 "+destinationN2);
      throw new IllegalArgumentException("Illegal energy. Exiting");
    }
    double rate = prefactor * Math.exp(-energy / (kB * temperature));
    return rate;
  }
  
  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. How many islands per area
   * site are generated at current temperature. Usually with higher temperature less islands are
   * created, and thus, island density is lower. And the other way around.
   *
   * @param temperature Not implemented yet: temperature in Kelvin.
   * @return island density
   */
  @Override
  public double getIslandDensity(double temperature) {
    return islandDensityPerSite;
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
    /**
   * We don't use the temperature by now.
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  /**
   * Does nothing
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  @Override
  public double[] getRates(double temperature) {
    double[] ratesVector = new double[64];
/*
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ratesVector[i * 8 + j] = (getRate(i, j, temperature));
      }
    }*/
    return ratesVector;
  }

  @Override
  public double getEnergy(int i, int j) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
