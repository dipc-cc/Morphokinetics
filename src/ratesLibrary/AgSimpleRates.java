/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import static ratesLibrary.IRates.kB;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimpleRates implements IRates {
  
  private final double[][] energies;
  private double diffusionMl;
  
  private final double prefactor;
  private final TreeMap densities;
  
  public AgSimpleRates() {
    diffusionMl = 0.000035;
    
    double e0 = 0.10;
    double e1 = 0.25;
    double e2 = 0.33;
    double e3 = 0.42;
    double eInf = 9999999;
    
    prefactor = 1e13;
    
    energies = new double[7][7];
    energies[0][0] = e0;
    energies[0][1] = e0;
    energies[0][2] = e0;
    energies[0][3] = e0;
    energies[0][4] = eInf;
    energies[0][5] = eInf;
    energies[0][6] = eInf;
    
    energies[1][0] = eInf;
    energies[1][1] = e1;
    energies[1][2] = e1;
    energies[1][3] = e1;
    energies[1][4] = e1;
    energies[1][5] = eInf;
    energies[1][6] = eInf;
    
    energies[2][0] = eInf;
    energies[2][1] = e2;
    energies[2][2] = e2;
    energies[2][3] = e2;
    energies[2][4] = e2;
    energies[2][5] = e2;
    energies[2][6] = eInf;
    
    energies[3][0] = eInf;
    energies[3][1] = eInf;
    energies[3][2] = eInf;
    energies[3][3] = e3;
    energies[3][4] = e3;
    energies[3][5] = e3;
    energies[3][6] = eInf;
    
    energies[4][0] = eInf;
    energies[4][1] = eInf;
    energies[4][2] = eInf;
    energies[4][3] = eInf;
    energies[4][4] = eInf;
    energies[4][5] = eInf;
    energies[4][6] = eInf;
    
    energies[5][0] = eInf;
    energies[5][1] = eInf;
    energies[5][2] = eInf;
    energies[5][3] = eInf;
    energies[5][4] = eInf;
    energies[5][5] = eInf;
    energies[5][6] = eInf;
    
    energies[6][0] = eInf;
    energies[6][1] = eInf;
    energies[6][2] = eInf;
    energies[6][3] = eInf;
    energies[6][4] = eInf;
    energies[6][5] = eInf;
    energies[6][6] = eInf;
    
    densities = new TreeMap();
    densities.put(50, 5.679e-02);
    densities.put(55, 5.686e-02);
    densities.put(60, 5.609e-02);
    densities.put(65, 5.170e-02);
    densities.put(70, 4.385e-02);
    densities.put(75, 3.581e-02);
    densities.put(80, 2.925e-02);
    densities.put(85, 2.399e-02);
    densities.put(90, 1.991e-02);
    densities.put(95, 1.660e-02);
    densities.put(100, 1.413e-02);
    densities.put(110, 1.051e-02);
    densities.put(120, 8.175e-03);
    densities.put(130, 6.563e-03);
    densities.put(140, 5.403e-03);
    densities.put(150, 4.558e-03);
    densities.put(200, 3.126e-03);
    densities.put(250, 2.197e-03);
    densities.put(300, 1.729e-03);
    densities.put(350, 1.304e-03);
    densities.put(400, 9.665e-04);
    densities.put(450, 6.782e-04);
    densities.put(500, 4.858e-04);
    densities.put(550, 3.522e-04);
    densities.put(600, 2.607e-04);
    densities.put(650, 2.043e-04);
    densities.put(700, 1.683e-04);
    densities.put(750, 1.357e-04);
    densities.put(800, 1.190e-04);
    densities.put(850, 1.045e-04);
    densities.put(900, 8.975e-05);
    densities.put(950, 8.850e-05);
    densities.put(1000, 8.275e-05);
    densities.put(1050, 7.105e-05);
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  /**
   * In principle, deposition rate is constant to 0.0035 ML/s. What changes is island density.
   * Consequently, deposition rate in practice varies with the temperature.
   *
   * @return diffusion mono layer (or deposition flux)
   */
  @Override
  public double getDepositionRatePerSite() {
    System.out.println("d "+diffusionMl);
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
  public double getIslandDensity(double temperature) {
    double density = -1;
    System.out.println("Tempe " + (int) temperature);
    try {
      density = (double) densities.get((int) temperature);
    } catch (NullPointerException e) {
      Set allKeys = densities.keySet();
      allKeys.iterator();

      for (Object i : allKeys) {
        System.out.println(i);
        if ((int) i > temperature) {
          density = (double) densities.get((int) i);
          break;
        }
        //density = 5;
      }
    }
    System.out.println("Density " + density);
    return density;
  }
  
  @Override
  public double getEnergy(int i, int j) {
    return energies[i][j];
  }

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. Cox et al. define to be 
   * F=0.0035 ML/s. The perimeter deposition is calculated multiplying F (this) and island density.
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[49];

    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        rates[i * 7 + j] = (getRate(i, j, temperature));
      }
    }
    return rates;
  }
}
