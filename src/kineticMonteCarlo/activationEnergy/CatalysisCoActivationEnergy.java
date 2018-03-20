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
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisSite.O;
import kineticMonteCarlo.unitCell.CatalysisUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoActivationEnergy extends ActivationEnergy {
  
  private Double[] histogramPossibleReactionCoCus;
  private Double[] histogramPossibleAdsorption;
  private Double[][][] histogramPossibleDesorption;
  /** CO|O, from BR|CUS to BR|CUS. For Farkas, number of CO^C with CO^C neighbours */
  private Double[][][][] histogramPossibleDiffusion;
  private int lengthI;
  private int lengthJ;
  private int numberOfNeighbours;
  
  public CatalysisCoActivationEnergy(Parser parser) {
    super(parser);
    lengthI = 2;
    numberOfNeighbours = 4;
    histogramPossibleAdsorption = new Double[2];
    histogramPossibleDesorption = new Double[2][2][3];
    histogramPossibleDiffusion = new Double[2][2][2][3];
    histogramPossibleReactionCoCus = new Double[3];
    for (int i = 0; i < 2; i++) {
      histogramPossibleAdsorption[i] = new Double(0);
      histogramPossibleReactionCoCus[i] = new Double(0);
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          histogramPossibleDesorption[i][j][k] = new Double(0);
          for (int l = 0; l < 3; l++) {
            histogramPossibleDiffusion[i][j][k][l] = new Double(0);
          }
        }
      }
    }
  }
  
  /**
   * Computes possibles for catalysis for all kind of processes
   *
   * @param lattice
   * @param elapsedTime
   * @param stationary
   */
  public void updatePossibles(CatalysisLattice lattice, double elapsedTime, boolean stationary) {
    /*if (doActivationEnergyStudy() && stationary) {
      histogramPossibleTmp = initDouble();
      for (int i = 0; i < lattice.size(); i++) {
        CatalysisUc uc = (CatalysisUc) lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          CatalysisSite atom = (CatalysisSite) uc.getSite(j);
          int numberOfCoNeighbours = 0;
          if (atom.getLatticeSite() == CUS) {
            numberOfCoNeighbours = atom.getCoCusNeighbours();
          }
          // Adsorption
          if (!atom.isOccupied()) {
            histogramPossibleAdsorption[CO] += elapsedTime;
            if (!atom.isIsolated()) {
              histogramPossibleAdsorption[O] += elapsedTime;
            }
          } else {
            for (int pos = 0; pos < numberOfNeighbours; pos++) {
              CatalysisSite neighbour = atom.getNeighbour(pos);
              // Desorption
              if (atom.getType() == CO) {
                histogramPossibleDesorption[CO][atom.getLatticeSite()][numberOfCoNeighbours] += elapsedTime / 4.0; // it goes throw 4 times
              } else if (neighbour.getType() == O && neighbour.isOccupied()) { // Two O together
                histogramPossibleDesorption[O][atom.getLatticeSite()][neighbour.getLatticeSite()] += elapsedTime * 0.5; // it will be visited twice
              }

              // Diffusion
              if (!neighbour.isOccupied()) {
                histogramPossibleDiffusion[atom.getType()][atom.getLatticeSite()][neighbour.getLatticeSite()][numberOfCoNeighbours] += elapsedTime;
              }

              // Reaction
              if (atom.getType() == neighbour.getType() || !neighbour.isOccupied()) {
                continue;
              }
              // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
              if (atom.getType() == CO) {
                if (numberOfCoNeighbours > 0) {
                  int index = 2 * neighbour.getLatticeSite() + numberOfCoNeighbours - 1;
                  histogramPossibleReactionCoCus[index] += elapsedTime / 2.0;
                } else {
                  histogramPossible[atom.getLatticeSite()][neighbour.getLatticeSite()] += elapsedTime / 2.0;
                  //histogramPossibleCounter[atom.getLatticeSite()][neighbour.getLatticeSite()]++;
                  histogramPossibleTmp[atom.getLatticeSite()][neighbour.getLatticeSite()] += 0.5;
                }
              } else {
                numberOfCoNeighbours = neighbour.getCoCusNeighbours();
                if (numberOfCoNeighbours > 0) {
                  int index = 2 * atom.getLatticeSite() + numberOfCoNeighbours - 1;
                  histogramPossibleReactionCoCus[index] += elapsedTime / 2.0;
                } else {
                  histogramPossible[neighbour.getLatticeSite()][atom.getLatticeSite()] += elapsedTime / 2.0;
                  //histogramPossibleCounter[neighbour.getLatticeSite()][atom.getLatticeSite()]++;
                  histogramPossibleTmp[neighbour.getLatticeSite()][atom.getLatticeSite()] += 0.5;
                }
              }
            }
          }
        }
      }
      // it is counting twice each reaction, so dividing by 2
      for (int i = 0; i < histogramPossibleCounter.length; i++) {
        for (int j = 0; j < histogramPossibleCounter[0].length; j++) {
          histogramPossibleCounter[i][j] += histogramPossibleTmp[i][j].longValue();
          histogramPossibleCounterTmp[i][j] = histogramPossibleTmp[i][j].longValue();
        }
      }
    }//*/
  }
  
  /**
   * New print with everything, only for catalysis.
   * @param print
   * @param time
   */
  @Override
  public void printAe(PrintWriter print[], double time) {
    super.printAe(print, time);
    print[6].print(time + "\t");
    //printAeLow(print[6], "", false, histogramPossible); //AePossibleFromList REACTION
    for (int i = 0; i < histogramPossibleAdsorption.length; i++) {
      print[6].print(histogramPossibleAdsorption[i] + "\t");
    }
    print[6].print(histogramPossibleDesorption[CO][BR][0] + "\t");
    print[6].print(histogramPossibleDesorption[CO][CUS][0] + "\t");
    for (int i = 0; i < histogramPossibleDesorption[O].length; i++) {
      for (int j = 0; j < 2; j++) {
        print[6].print(histogramPossibleDesorption[O][i][j] + "\t");
      }
    }
    for (int i = 0; i < histogramPossibleDiffusion.length; i++) {
      for (int j = 0; j < histogramPossibleDiffusion[0].length; j++) {
        for (int k = 0; k < histogramPossibleDiffusion[0][0].length; k++) {
          print[6].print(histogramPossibleDiffusion[i][j][k][0] + "\t");
        }
      }
    }
    // Extra for Farkas
    print[6].print(histogramPossibleDesorption[CO][CUS][1] + "\t");
    print[6].print(histogramPossibleDesorption[CO][CUS][2] + "\t");
    print[6].print(histogramPossibleReactionCoCus[0] + "\t");
    print[6].print(histogramPossibleReactionCoCus[1] + "\t");
    print[6].print(histogramPossibleReactionCoCus[2] + "\t");
    print[6].print(histogramPossibleDiffusion[CO][CUS][BR][1] + "\t");
    print[6].print(histogramPossibleDiffusion[CO][CUS][BR][2] + "\t");
    print[6].print(histogramPossibleDiffusion[CO][CUS][CUS][1] + "\t");
  }
  
  @Override
  public void reset() {
    super.reset();
    /*histogramPossibleReactionCoCus = initDouble1(3);
    histogramPossibleAdsorption = initDouble1(lengthI);
    histogramPossibleDesorption = initDouble3();
    histogramPossibleDiffusion = initDouble4();//*/
  }
    
}
