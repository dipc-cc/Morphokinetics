/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import static kineticMonteCarlo.atom.AgAtom.CORNER;
import static kineticMonteCarlo.atom.AgAtom.EDGE_A;
import static kineticMonteCarlo.atom.AgAtom.EDGE_B;
import static kineticMonteCarlo.atom.AgAtom.KINK_A;
import static kineticMonteCarlo.atom.AgAtom.KINK_B;
import static kineticMonteCarlo.atom.AgAtom.ISLAND;

/**
 *
 * Etch rates data obtained from Cox et al. - PHYSICAL REVIEW B 71, 11541 2005
 *
 * @author Nestor
 */
public class RatesFromPrbCox implements IGrowthRates {

  /**
   * Boltzmann constant
   */
  private final double kB;
  /**
   * Diffusion Mono Layer. Utilised to calculate absorption rate
   */
  private final double diffusionMl;
  private final double P;
  private final double Pd;
  /**
   * Energy from edge A to edge A
   */
  private final double Eaa;
  /**
   * Energy from edge B to edge B
   */
  private final double Ebb;
  /**
   * Energy from corner to edge A
   */
  private final double Eca;
  /**
   * Energy from corner to edge B
   */
  private final double Ecb;
  /**
   * Energy from corner to corner
   */
  private final double Ecc; //consideramos la misma barrera para corner a corner, a diferencia de Cox et al.
  /**
   * Energy from edge A to edge B, across corner
   */
  private final double Eacb;
  /**
   * Energy from edge B to edge A, across corner
   */
  private final double Ebca;
  /**
   * Energy from edge A to kink, across corner
   */
  private final double Eack;
  /**
   * Energy from edge B to kink, across corner
   */
  private final double Ebck;
  /**
   * Energy from corner to kink A
   */
  private final double Ecak;  
  /**
   * Energy from corner to kink B
   */
  private final double Ecbk;
  private final double Ed;
  /**
   * Infinite energy. To make impossible the transition.
   */
  private final double Einf;
  private final double[][] prefactors;
  private final double[][] energies;

  /**
   * Atom types are documented in class AbstractAtom. In any case are;
   *
   * 0: terrace | 1: corner | 2: A side | 3: kink A | 4: bulk | 5: B side | 6: kink B
   *                               edge                 island       edge
   */
  public RatesFromPrbCox() { 
    kB = 8.617332e-5;
    diffusionMl = 0.0035;
    P = 1e13;
    Pd = 1e11; //no lo sabemos
    // Gene 0; no lo sabemos seguro
    Ed = 0.1;
    // Gene 1 (Ec); consideramos la misma barrera para corner a corner, a diferencia de Cox et al.
    Ecc = Eca = Ecbk = 0.075; 
    // Gene 2 (Ee)
    Ecak = Ecb = 0.15;
    // Gene 3 (Ef)
    Eack = Ebca = Ebck = Eacb = 0.36;
    // Gene 4 (Ea)
    Eaa = 0.275;
    // Gene 5 (Eb)
    Ebb = 0.310; 
    Einf = 9999999;
    prefactors = new double[7][7];
    energies = new double[7][7];

    //[source type][destination type]
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][CORNER] = Ed;
    energies[TERRACE][EDGE_A] = Ed;
    energies[TERRACE][KINK_A] = Ed;
    energies[TERRACE][ISLAND] = Ed;
    energies[TERRACE][EDGE_B] = Ed;
    energies[TERRACE][KINK_B] = Ed;

    energies[CORNER][TERRACE] = Einf; // Impossible. Forbid it
    energies[CORNER][CORNER] = Ecc;
    energies[CORNER][EDGE_A] = Eca;
    energies[CORNER][KINK_A] = Ecak;
    energies[CORNER][ISLAND] = Math.max(Ecak, Ecbk);
    energies[CORNER][EDGE_B] = Ecb;
    energies[CORNER][KINK_B] = Ecbk;

    energies[EDGE_A][TERRACE] = Einf; // Impossible. Forbid it
    energies[EDGE_A][CORNER] = Einf; // Impossible. Forbid it
    energies[EDGE_A][EDGE_A] = Eaa;
    energies[EDGE_A][KINK_A] = Eack;
    energies[EDGE_A][ISLAND] = Eack;
    energies[EDGE_A][EDGE_B] = Eacb;
    energies[EDGE_A][KINK_B] = Eack;

    energies[KINK_A][TERRACE] = Einf; // Impossible. Forbid it
    energies[KINK_A][CORNER] = Einf; // Impossible. Forbid it
    energies[KINK_A][EDGE_A] = Einf; // Impossible. Forbid it
    energies[KINK_A][KINK_A] = Einf; // Impossible. Forbid it
    energies[KINK_A][ISLAND] = Einf; // Impossible. Forbid it
    energies[KINK_A][EDGE_B] = Einf; // Impossible. Forbid it
    energies[KINK_A][KINK_B] = Einf; // Impossible. Forbid it

    energies[ISLAND][TERRACE] = Einf; // Impossible. Forbid it
    energies[ISLAND][CORNER] = Einf; // Impossible. Forbid it
    energies[ISLAND][EDGE_A] = Einf; // Impossible. Forbid it
    energies[ISLAND][KINK_A] = Einf; // Impossible. Forbid it
    energies[ISLAND][ISLAND] = Einf; // Impossible. Forbid it
    energies[ISLAND][EDGE_B] = Einf; // Impossible. Forbid it
    energies[ISLAND][KINK_B] = Einf; // Impossible. Forbid it

    energies[EDGE_B][TERRACE] = Einf; // Impossible. Forbid it
    energies[EDGE_B][CORNER] = Einf; // Impossible. Forbid it
    energies[EDGE_B][EDGE_A] = Ebca;
    energies[EDGE_B][KINK_A] = Ebck;
    energies[EDGE_B][ISLAND] = Ebck;
    energies[EDGE_B][EDGE_B] = Ebb;
    energies[EDGE_B][KINK_B] = Ebck;

    energies[KINK_B][TERRACE] = Einf; // Impossible. Forbid it
    energies[KINK_B][CORNER] = Einf; // Impossible. Forbid it
    energies[KINK_B][EDGE_A] = Einf; // Impossible. Forbid it
    energies[KINK_B][KINK_A] = Einf; // Impossible. Forbid it
    energies[KINK_B][ISLAND] = Einf; // Impossible. Forbid it
    energies[KINK_B][EDGE_B] = Einf; // Impossible. Forbid it
    energies[KINK_B][KINK_B] = Einf; // Impossible. Forbid it

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
    return diffusionMl;
  }

  /**
   * Returns the island density mono layer depending on the temperature.
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
    // TERRECE to anywhere is an special case. In the code in general it is used a different prefactor (1e11).
    // This number come from applying the same prefactor
    if (i == TERRACE) return 0.15357378552368;
    // Return the table value otherwise
    return energies[i][j];
  }

}
