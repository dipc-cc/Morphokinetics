/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 * Etch rates data obtained from Gosalvez Et al - Physical Review E 68 (2003) 031604.
 *
 * @author Nestor
 */
public class SiRatesFromPreGosalvez implements IRates {

  private final double[][] prefactors = new double[4][16];
  private final double[][] energies = new double[4][16];

  public SiRatesFromPreGosalvez() {
    double E0 = 0;
    double E1 = 0.526;
    double E2A = 0.42;//0.525;
    double E2B = 0.0;
    double E2C = 0.35;//0.395;
    double E3A = 0.541;
    double E3B = 0.382;

    double P0 = 1;
    double P1 = 1.47e+004;
    double P2A = 3.43e+004;
    double P2B = 0.11;
    double P2C = 3.059e+004;
    double P3A = 3.63;
    double P3B = 113.7;
    
    energies[0][0] = E0;
    energies[0][1] = E0;
    energies[0][2] = E0;
    energies[0][3] = E0;
    energies[0][4] = E0;
    energies[0][5] = E0;
    energies[0][6] = E0;
    energies[0][7] = E0;
    energies[0][8] = E0;
    energies[0][9] = E0;
    energies[0][10] = E0;
    energies[0][11] = E0;
    energies[0][12] = E0;
    energies[0][13] = E0;
    energies[0][14] = E0;
    energies[0][15] = E0;

    energies[1][0] = 0;
    energies[1][1] = E1;
    energies[1][2] = E1;
    energies[1][3] = E1;
    energies[1][4] = E1;
    energies[1][5] = E1;
    energies[1][6] = E1;
    energies[1][7] = E1;
    energies[1][8] = E1;
    energies[1][9] = E1;
    energies[1][10] = E1;
    energies[1][11] = E1;
    energies[1][12] = E1;
    energies[1][13] = E1;
    energies[1][14] = E1;
    energies[1][15] = E1;

    energies[2][0] = 0;
    energies[2][1] = E2C;
    energies[2][2] = E2C;
    energies[2][3] = E2C;
    energies[2][4] = E2C;
    energies[2][5] = E2C;
    energies[2][6] = E2C;
    energies[2][7] = E2B;
    energies[2][8] = E2A;
    energies[2][9] = E2C;
    energies[2][10] = E2C;
    energies[2][11] = E2C;
    energies[2][12] = E2C;
    energies[2][13] = E2C;
    energies[2][14] = E2C;
    energies[2][15] = E2C;

    energies[3][0] = 0;
    energies[3][1] = E3B;
    energies[3][2] = E3B;
    energies[3][3] = E3B;
    energies[3][4] = E3B;
    energies[3][5] = E3B;
    energies[3][6] = E3B;
    energies[3][7] = E3B;
    energies[3][8] = E3B;
    energies[3][9] = E3A;
    energies[3][10] = E3B;
    energies[3][11] = E3B;
    energies[3][12] = E3B;
    energies[3][13] = E3B;
    energies[3][14] = E3B;
    energies[3][15] = E3B;

    prefactors[0][0] = P0;
    prefactors[0][1] = P0;
    prefactors[0][2] = P0;
    prefactors[0][3] = P0;
    prefactors[0][4] = P0;
    prefactors[0][5] = P0;
    prefactors[0][6] = P0;
    prefactors[0][7] = P0;
    prefactors[0][8] = P0;
    prefactors[0][9] = P0;
    prefactors[0][10] = P0;
    prefactors[0][11] = P0;
    prefactors[0][12] = P0;
    prefactors[0][13] = P0;
    prefactors[0][14] = P0;
    prefactors[0][15] = P0;

    prefactors[1][0] = 1;
    prefactors[1][1] = P1;
    prefactors[1][2] = P1;
    prefactors[1][3] = P1;
    prefactors[1][4] = P1;
    prefactors[1][5] = P1;
    prefactors[1][6] = P1;
    prefactors[1][7] = P1;
    prefactors[1][8] = P1;
    prefactors[1][9] = P1;
    prefactors[1][10] = P1;
    prefactors[1][11] = P1;
    prefactors[1][12] = P1;
    prefactors[1][13] = P1;
    prefactors[1][14] = P1;
    prefactors[1][15] = P1;

    prefactors[2][0] = 1;
    prefactors[2][1] = P2C;
    prefactors[2][2] = P2C;
    prefactors[2][3] = P2C;
    prefactors[2][4] = P2C;
    prefactors[2][5] = P2C;
    prefactors[2][6] = P2C;
    prefactors[2][7] = P2B;
    prefactors[2][8] = P2A;
    prefactors[2][9] = P2C;
    prefactors[2][10] = P2C;
    prefactors[2][11] = P2C;
    prefactors[2][12] = P2C;
    prefactors[2][13] = P2C;
    prefactors[2][14] = P2C;
    prefactors[2][15] = P2C;

    prefactors[3][0] = 1;
    prefactors[3][1] = P3B;
    prefactors[3][2] = P3B;
    prefactors[3][3] = P3B;
    prefactors[3][4] = P3B;
    prefactors[3][5] = P3B;
    prefactors[3][6] = P3B;
    prefactors[3][7] = P3B;
    prefactors[3][8] = P3B;
    prefactors[3][9] = P3A;
    prefactors[3][10] = P3B;
    prefactors[3][11] = P3B;
    prefactors[3][12] = P3B;
    prefactors[3][13] = P3B;
    prefactors[3][14] = P3B;
    prefactors[3][15] = P3B;
  }

  @Override
  public double getEnergy(int i, int j) {
    return energies[i][j];
  }
  
  @Override
  public double getDepositionRatePerSite() {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms.");
  }

  @Override
  public double getIslandDensity(double temperature) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }
 
  @Override
  public void setDepositionFlux(double depositionFlux) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[64];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 16; j++) {
        rates[i * 16 + j] = (prefactors[i][j] * Math.exp(-energies[i][j] / (kB * temperature)));
      }
    }
    return rates;
  }
}
