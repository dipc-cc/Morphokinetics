/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * Etch rates data obtained from Cox et al. - PHYSICAL REVIEW B 71, 11541 2005
 *
* @author Nestor
 */
public class RatesFromPrbCox implements IDiffusionRates {

  private double kB = 8.617332e-5;
  private double diffusion_ML = 0.0035;
  double P = 10e13;
  double Pd = 10e11; //no lo sabemos
  double Eaa = 0.275;
  double Ebb = 0.310;
  double Eca = 0.075;
  double Ecb = 0.15;
  double Ecc = 0.075; //consideramos la misma barrera para corner a corner, a diferencia de Cox et al.
  double Eacb = 0.36;
  double Ebca = 0.36;
  double Eack = 0.36;
  double Ebck = 0.36;
  double Eck_a = 0.15;
  double Eck_b = 0.075;
  double Ed = 0.1;  //no lo sabemos seguro
  double E_inf = 9999999;
  private double[][] prefactors = new double[7][7];
  private double[][] energies = new double[7][7];

  //0: terrace
  //1: corner
  //2: A side
  //3: kink
  //4: bulk
  //5: B side
  public RatesFromPrbCox() {

    //[source type][destination type]
    energies[0][0] = Ed;
    energies[0][1] = Ed;
    energies[0][2] = Ed;
    energies[0][3] = Ed;
    energies[0][4] = Ed;
    energies[0][5] = Ed;
    energies[0][6] = Ed;

    energies[1][0] = E_inf;//
    energies[1][1] = Ecc;
    energies[1][2] = Eca;
    energies[1][3] = Eck_a;
    energies[1][4] = Math.max(Eck_a, Eck_b);
    energies[1][5] = Ecb;
    energies[1][6] = Eck_b;

    energies[2][0] = E_inf;
    energies[2][1] = E_inf;
    energies[2][2] = Eaa;
    energies[2][3] = Eack;
    energies[2][4] = Eack;
    energies[2][5] = Eacb;
    energies[2][6] = Eack;

    energies[3][0] = E_inf;
    energies[3][1] = E_inf;
    energies[3][2] = E_inf;
    energies[3][3] = E_inf;
    energies[3][4] = E_inf;
    energies[3][5] = E_inf;
    energies[3][6] = E_inf;

    energies[4][0] = E_inf;
    energies[4][1] = E_inf;
    energies[4][2] = E_inf;
    energies[4][3] = E_inf;
    energies[4][4] = E_inf;
    energies[4][5] = E_inf;
    energies[4][6] = E_inf;

    energies[5][0] = E_inf;
    energies[5][1] = E_inf;
    energies[5][2] = Ebca;
    energies[5][3] = Ebck;
    energies[5][4] = Ebck;
    energies[5][5] = Ebb;
    energies[5][6] = Ebck;

    energies[6][0] = E_inf;
    energies[6][1] = E_inf;
    energies[6][2] = E_inf;
    energies[6][3] = E_inf;
    energies[6][4] = E_inf;
    energies[6][5] = E_inf;
    energies[6][6] = E_inf;

    prefactors[0][0] = Pd;
    prefactors[0][1] = Pd;
    prefactors[0][2] = Pd;
    prefactors[0][3] = Pd;
    prefactors[0][4] = Pd;
    prefactors[0][5] = Pd;
    prefactors[0][6] = Pd;

    prefactors[1][0] = P;
    prefactors[1][1] = P;
    prefactors[1][2] = P;
    prefactors[1][3] = P;
    prefactors[1][4] = P;
    prefactors[1][5] = P;
    prefactors[1][6] = P;

    prefactors[2][0] = P;
    prefactors[2][1] = P;
    prefactors[2][2] = P;
    prefactors[2][3] = P;
    prefactors[2][4] = P;
    prefactors[2][5] = P;
    prefactors[2][6] = P;

    prefactors[3][0] = P;
    prefactors[3][1] = P;
    prefactors[3][2] = P;
    prefactors[3][3] = P;
    prefactors[3][4] = P;
    prefactors[3][5] = P;
    prefactors[3][6] = P;

    prefactors[4][0] = P;
    prefactors[4][1] = P;
    prefactors[4][2] = P;
    prefactors[4][3] = P;
    prefactors[4][4] = P;
    prefactors[4][5] = P;
    prefactors[4][6] = P;

    prefactors[5][0] = P;
    prefactors[5][1] = P;
    prefactors[5][2] = P;
    prefactors[5][3] = P;
    prefactors[5][4] = P;
    prefactors[5][5] = P;
    prefactors[5][6] = P;

    prefactors[6][0] = P;
    prefactors[6][1] = P;
    prefactors[6][2] = P;
    prefactors[6][3] = P;
    prefactors[6][4] = P;
    prefactors[6][5] = P;
    prefactors[6][6] = P;
  }

  @Override
  public double getRate(int i, int j, double temperature) {
    return (prefactors[i][j] * Math.exp(-energies[i][j] / (kB * temperature)));
  }

  @Override
  public double getDepositionRate() {
    return diffusion_ML;

  }

  @Override
  public double getIslandsDensityML(double temperature) {
    if (temperature < 135) {//120 degrees
      return 1e-4;
    }
    if (temperature < 150) {//135 degrees
      return 5e-5;
    }
    if (temperature < 165) {//150 degrees
      return 4e-5;
    }
    if (temperature < 180) {//165 degrees
      return 3e-5;
    }
    return 2e-5; //180 degrees
  }

}
