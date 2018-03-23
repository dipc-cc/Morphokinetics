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
package kineticMonteCarlo.activationEnergy;

import basic.Parser;
import java.io.PrintWriter;
import java.util.Iterator;
import kineticMonteCarlo.lattice.CatalysisLattice;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH3;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NO;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.OH;
import kineticMonteCarlo.site.CatalysisSite;
import kineticMonteCarlo.unitCell.CatalysisUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaActivationEnergy extends AbstractCatalysisActivationEnergy {
  private Double[] histogramPossibles;

  private final int numberOfNeighbours;
  
  public CatalysisAmmoniaActivationEnergy(Parser parser) {
    super(parser);
    histogramPossibles = new Double[19];
    numberOfNeighbours = 2;
    setLengthI(18);
    setLengthJ(1);
  }
  
  /**
   * Computes possibles for catalysis for all kind of processes
   *
   * @param lattice
   * @param elapsedTime
   * @param stationary
   */
  @Override
  public void updatePossibles(CatalysisLattice lattice, double elapsedTime, boolean stationary) {
    if (doActivationEnergyStudy() && stationary) {
      Double[][] histogramCounterTmp = initDouble();
      for (int i = 0; i < lattice.size(); i++) {
        CatalysisUc uc = (CatalysisUc) lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          CatalysisSite atom = (CatalysisSite) uc.getSite(j);
          // Adsorption
          if (!atom.isOccupied()) {
            histogramPossibles[1] += elapsedTime; // P1
            if (!atom.isIsolated()) {
              histogramPossibles[3] += elapsedTime; // P3
            }
          } else {
            for (int pos = 0; pos < numberOfNeighbours; pos += 2) {
              CatalysisSite neighbour = atom.getNeighbour(pos);
              // Desorption
              switch (atom.getType()) {
                case NH3:
                  histogramPossibles[2] +=elapsedTime / 2.0; // P2, it goes throw 2 times
                  break;
                case NO:
                  histogramPossibles[11] +=elapsedTime / 2.0; // P11, it goes throw 2 times
                  break;
                case O:
                  if (neighbour.getType()== O && neighbour.isOccupied()) { // Two O together
                    histogramPossibles[4] = elapsedTime * 0.5; // P4, it will be visited twice
                  }
                  break;
                case N:
                  if (neighbour.getType()== N && neighbour.isOccupied()) { // Two N together
                    histogramPossibles[10] = elapsedTime * 0.5; // P10, it will be visited twice
                  }
                  break;
              }
              
              
              // Diffusion
              if (!neighbour.isOccupied()) {
                switch (atom.getType()) {
                  case N:
                    histogramPossibles[12] += elapsedTime; // P12
                    break;
                  case O:
                    histogramPossibles[13] += elapsedTime; // P13
                    break;
                  case OH:
                    histogramPossibles[14] += elapsedTime; // P14
                    break;
                }
              }

              // Reaction
              if (atom.getType() == neighbour.getType() || !neighbour.isOccupied()) {
                continue;
              }
              if ((atom.getType() == NH3 && neighbour.getType() == O)
                      || (atom.getType() == O && neighbour.getType() == NH3)) {
                histogramPossibles[5] += elapsedTime / 2.0; // P5
              }
              if ((atom.getType() == NH2 && neighbour.getType() == OH)
                      || (atom.getType() == OH && neighbour.getType() == NH2)) {
                histogramPossibles[6] += elapsedTime / 2.0; // P6
                histogramPossibles[17] += elapsedTime / 2.0; // P17
              }
              if ((atom.getType() == NH && neighbour.getType() == OH)
                      || (atom.getType() == OH && neighbour.getType() == NH)) {
                histogramPossibles[7] += elapsedTime / 2.0; // P7
                histogramPossibles[16] += elapsedTime / 2.0; // P16
              }
              if ((atom.getType() == NH && neighbour.getType() == O)
                      || (atom.getType() == O && neighbour.getType() == NH)) {
                histogramPossibles[8] += elapsedTime / 2.0; // P8
              }
              if ((atom.getType() == N && neighbour.getType() == O)
                      || (atom.getType() == O && neighbour.getType() == N)) {
                histogramPossibles[9] += elapsedTime / 2.0; // P9
              }
              if ((atom.getType() == NH2 && neighbour.getType() == O)
                      || (atom.getType() == O && neighbour.getType() == NH2)) {
                histogramPossibles[15] += elapsedTime / 2.0; // P15
              }
              if ((atom.getType() == N && neighbour.getType() == OH)
                      || (atom.getType() == OH && neighbour.getType() == N)) {
                histogramPossibles[18] += elapsedTime / 2.0; // P18
              }
            }
          }
        }
      }
      updateCounter(histogramCounterTmp);
    }
  }
  
  @Override
  public void updatePossibles(Iterator<CatalysisSite> surface, double elapsedTime, boolean stationary) {
    if (doActivationEnergyStudy() && stationary) {
      System.out.println("This method should not be called");
      System.out.println("Call instead public void updatePossibles(CatalysisLattice lattice, double elapsedTime, boolean stationary)");
    }
  }
  
  @Override
  public void reset() {
    super.reset();
    histogramPossibles = initDouble1(19);
  }
  
  @Override
  void printPossibles(PrintWriter print, double time) {
    print.print(time + "\t");
    for (int i = 1; i <= 18 ; i++) {
      print.print(histogramPossibles[i] + "\t");
    }
  }
}
