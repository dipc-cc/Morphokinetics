/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import static kineticMonteCarlo.atom.AbstractAtom.CORNER;
import static kineticMonteCarlo.atom.AbstractAtom.EDGE_A;
import static kineticMonteCarlo.atom.AbstractAtom.EDGE_B;
import static kineticMonteCarlo.atom.AbstractAtom.KINK_A;
import static kineticMonteCarlo.atom.AbstractAtom.KINK_B;
import static kineticMonteCarlo.atom.AbstractAtom.ISLAND;

/**
 *
 * Etch rates data obtained from Cox et al. - PHYSICAL REVIEW B 71, 11541 2005
 *
* @author Nestor
 */
public class RatesFromPrbCox implements IDiffusionRates {

  /**
   * Boltzmann constant
   */
  private double kB = 8.617332e-5;
  /**
   * Diffusion Mono Layer. Utilised to calculate absorption rate
   */
  private double diffusion_ML = 0.0035;
  double P = 10e13;
  double Pd = 10e11; //no lo sabemos
  /**
   * Energy from edge A to edge A
   */
  double Eaa = 0.275;
  /**
   * Energy from edge B to edge B
   */
  double Ebb = 0.310;
  /**
   * Energy from corner to edge A
   */
  double Eca = 0.075;
  /**
   * Energy from corner to edge B
   */
  double Ecb = 0.15;
  /**
   * Energy from corner to corner
   */
  double Ecc = 0.075; //consideramos la misma barrera para corner a corner, a diferencia de Cox et al.
  /**
   * Energy from edge A to edge B, from corner
   */
  double Eacb = 0.36;
  /**
   * Energy from edge B to edge A, from corner
   */
  double Ebca = 0.36;
  /**
   * Energy from edge A to kink, from corner
   */
  double Eack = 0.36;
  /**
   * Energy from edge B to kink, from corner
   */
  double Ebck = 0.36;
  /**
   * Energy from corner to kink, from edge A
   */
  double Ecak = 0.15;  
  /**
   * Energy from corner to kink, from edge B
   */
  double Ecbk = 0.075;
  double Ed = 0.1;  //no lo sabemos seguro
  /**
   * Infinite energy. To make impossible the transition.
   */
  double Einf = 9999999;
  private double[][] prefactors = new double[7][7];
  private double[][] energies = new double[7][7];

  /**
   * Atom types are documented in class AbstractAtom. In any case are;
   *
   * 0: terrace | 1: corner | 2: A side | 3: kink A | 4: bulk | 5: B side | 6: Kink B
   */
  public RatesFromPrbCox() {

    //[source type][destination type]
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][CORNER] = Ed;
    energies[TERRACE][EDGE_A] = Ed;
    energies[TERRACE][KINK_A] = Ed;
    energies[TERRACE][ISLAND] = Ed;
    energies[TERRACE][EDGE_B] = Ed;
    energies[TERRACE][KINK_B] = Ed;

    energies[CORNER][TERRACE] = Einf;//
    energies[CORNER][CORNER] = Ecc;
    energies[CORNER][EDGE_A] = Eca;
    energies[CORNER][KINK_A] = Ecak;
    energies[CORNER][ISLAND] = Math.max(Ecak, Ecbk);
    energies[CORNER][EDGE_B] = Ecb;
    energies[CORNER][KINK_B] = Ecbk;

    energies[EDGE_A][TERRACE] = Einf;
    energies[EDGE_A][CORNER] = Einf;
    energies[EDGE_A][EDGE_A] = Eaa;
    energies[EDGE_A][KINK_A] = Eack;
    energies[EDGE_A][ISLAND] = Eack;
    energies[EDGE_A][EDGE_B] = Eacb;
    energies[EDGE_A][KINK_B] = Eack;

    energies[KINK_A][TERRACE] = Einf;
    energies[KINK_A][CORNER] = Einf;
    energies[KINK_A][EDGE_A] = Einf;
    energies[KINK_A][KINK_A] = Einf;
    energies[KINK_A][ISLAND] = Einf;
    energies[KINK_A][EDGE_B] = Einf;
    energies[KINK_A][KINK_B] = Einf;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][CORNER] = Einf;
    energies[ISLAND][EDGE_A] = Einf;
    energies[ISLAND][KINK_A] = Einf;
    energies[ISLAND][ISLAND] = Einf;
    energies[ISLAND][EDGE_B] = Einf;
    energies[ISLAND][KINK_B] = Einf;

    energies[EDGE_B][TERRACE] = Einf;
    energies[EDGE_B][CORNER] = Einf;
    energies[EDGE_B][EDGE_A] = Ebca;
    energies[EDGE_B][KINK_A] = Ebck;
    energies[EDGE_B][ISLAND] = Ebck;
    energies[EDGE_B][EDGE_B] = Ebb;
    energies[EDGE_B][KINK_B] = Ebck;

    energies[KINK_B][TERRACE] = Einf;
    energies[KINK_B][CORNER] = Einf;
    energies[KINK_B][EDGE_A] = Einf;
    energies[KINK_B][KINK_A] = Einf;
    energies[KINK_B][ISLAND] = Einf;
    energies[KINK_B][EDGE_B] = Einf;
    energies[KINK_B][KINK_B] = Einf;

    prefactors[TERRACE][TERRACE] = Pd;
    prefactors[TERRACE][CORNER] = Pd;
    prefactors[TERRACE][EDGE_A] = Pd;
    prefactors[TERRACE][KINK_A] = Pd;
    prefactors[TERRACE][ISLAND] = Pd;
    prefactors[TERRACE][EDGE_B] = Pd;
    prefactors[TERRACE][KINK_B] = Pd;

    prefactors[CORNER][TERRACE] = P;
    prefactors[CORNER][CORNER] = P;
    prefactors[CORNER][EDGE_A] = P;
    prefactors[CORNER][KINK_A] = P;
    prefactors[CORNER][ISLAND] = P;
    prefactors[CORNER][EDGE_B] = P;
    prefactors[CORNER][KINK_B] = P;

    prefactors[EDGE_A][TERRACE] = P;
    prefactors[EDGE_A][CORNER] = P;
    prefactors[EDGE_A][EDGE_A] = P;
    prefactors[EDGE_A][KINK_A] = P;
    prefactors[EDGE_A][ISLAND] = P;
    prefactors[EDGE_A][EDGE_B] = P;
    prefactors[EDGE_A][KINK_B] = P;

    prefactors[KINK_A][TERRACE] = P;
    prefactors[KINK_A][CORNER] = P;
    prefactors[KINK_A][EDGE_A] = P;
    prefactors[KINK_A][KINK_A] = P;
    prefactors[KINK_A][ISLAND] = P;
    prefactors[KINK_A][EDGE_B] = P;
    prefactors[KINK_A][KINK_B] = P;

    prefactors[ISLAND][TERRACE] = P;
    prefactors[ISLAND][CORNER] = P;
    prefactors[ISLAND][EDGE_A] = P;
    prefactors[ISLAND][KINK_A] = P;
    prefactors[ISLAND][ISLAND] = P;
    prefactors[ISLAND][EDGE_B] = P;
    prefactors[ISLAND][KINK_B] = P;

    prefactors[EDGE_B][TERRACE] = P;
    prefactors[EDGE_B][CORNER] = P;
    prefactors[EDGE_B][EDGE_A] = P;
    prefactors[EDGE_B][KINK_A] = P;
    prefactors[EDGE_B][ISLAND] = P;
    prefactors[EDGE_B][EDGE_B] = P;
    prefactors[EDGE_B][KINK_B] = P;

    prefactors[KINK_B][TERRACE] = P;
    prefactors[KINK_B][CORNER] = P;
    prefactors[KINK_B][EDGE_A] = P;
    prefactors[KINK_B][KINK_A] = P;
    prefactors[KINK_B][ISLAND] = P;
    prefactors[KINK_B][EDGE_B] = P;
    prefactors[KINK_B][KINK_B] = P;
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
