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
import basic.io.OutputType;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import kineticMonteCarlo.site.AbstractSite;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisSite.O;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.lattice.MultiAtom;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import kineticMonteCarlo.unitCell.CatalysisUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ActivationEnergy {
  /**
   * Attribute to count processes that happened. Used to compute activation energy per each rate.
   */
  private Integer[][] histogramSuccess;
  private Double[][] histogramPossible;
  private Double[] histogramPossibleReactionCoCus;
  private Double[] histogramPossibleAdsorption;
  private Double[][][] histogramPossibleDesorption;
  /** CO|O, from BR|CUS to BR|CUS. For Farkas, number of CO^C with CO^C neighbours */
  private Double[][][][] histogramPossibleDiffusion;
  private Long[][] histogramPossibleCounter;
  private Double[][] histogramPossibleTmp;
  private Long[][] histogramPossibleCounterTmp;
  private Double[] histogramPossibleIsland;
  private Double[] histogramPossibleIslandTmp;
  private Double[] histogramPossibleMultiAtom;
  private final ArrayList<AbstractSite> surface;
  private final boolean aeOutput;
  private boolean doActivationEnergyStudy;
  private double previousProbability;
  private int lengthI;
  private int lengthJ;
  private int numberOfNeighbours;
  private double[][] rates;
  
  private int[][] transitionsHistogram;

  public ActivationEnergy(Parser parser) {
    surface = new ArrayList();
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    doActivationEnergyStudy = false;
    if (aeOutput) {
      if (parser.getCalculationMode().equals("basic")) {
        doActivationEnergyStudy = true;
        lengthI = 4;
        lengthJ = lengthI;
        numberOfNeighbours = 4;
      }
      if (parser.getCalculationMode().equals("graphene")) {
        doActivationEnergyStudy = true;
        if (parser.getRatesLibrary().equals("GaillardSimple")) {
          lengthI = 4;
          lengthJ = lengthI;
          numberOfNeighbours = 3;
        } else {
          lengthI = 8;
          lengthJ = lengthI;
          numberOfNeighbours = 12;
        }
      }
      if (parser.getCalculationMode().equals("Ag") || 
          parser.getCalculationMode().equals("AgUc"))   {
        doActivationEnergyStudy = true;
        lengthI = 7;
        lengthJ = lengthI;
        numberOfNeighbours = 6;
      }
      if (parser.getCalculationMode().equals("catalysis")){
        doActivationEnergyStudy = true;
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
      if (parser.getCalculationMode().equals("concerted")) {
        doActivationEnergyStudy = true;
        lengthI = 12;
        lengthJ = 16;
        numberOfNeighbours = 6;
        histogramPossibleIsland = new Double[9];
        histogramPossibleIslandTmp = new Double[9];
        histogramPossibleMultiAtom = new Double[4];
        transitionsHistogram = new int[lengthI][lengthJ];
      }
      histogramPossible = new Double[lengthI][lengthJ];
      histogramPossibleCounter = new Long[lengthI][lengthJ];
      histogramPossibleTmp = new Double[lengthI][lengthJ];
      histogramPossibleCounterTmp = new Long[lengthI][lengthJ];
      histogramSuccess = new Integer[lengthI][lengthJ];
      for (int i = 0; i < lengthI; i++) {
        for (int j = 0; j < lengthJ; j++) {
          histogramPossible[i][j] = new Double(0);
          histogramPossibleCounter[i][j] = new Long(0);
          histogramPossibleTmp[i][j] = new Double(0);
          histogramPossibleCounterTmp[i][j] = new Long(0);
          histogramSuccess[i][j] = new Integer(0);
        }
      }
    }
    previousProbability = 0;
  }
  
  public void setRates(double[][] rates) {
    this.rates = rates;
  }
  
  public void addTransitions(AbstractGrowthSite atom) {
    updateTransitions(atom, 1);
  }

  public void removeTransitions(AbstractGrowthSite atom) {
    updateTransitions(atom, -1);
  }
  
  private void updateTransitions(AbstractGrowthSite atom, int add) {
    byte type;
    if (add == -1) {
      type = atom.getOldType();
    } else {
      type = atom.getRealType();
    }
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      byte neighbourType = atom.getEdgeType(SINGLE, i);
      if (neighbourType > -1) {
        transitionsHistogram[type][neighbourType] += add;
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
    if (doActivationEnergyStudy && stationary) {
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
    }
  }

  /**
   * Computes possibles for catalysis.
   *
   * @param surface
   * @param elapsedTime 
   * @param stationary
   */
  public void updatePossibles(Iterator<CatalysisSite> surface, double elapsedTime, boolean stationary) {
    if (doActivationEnergyStudy && stationary) {
      // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
      
      histogramPossibleTmp = initDouble();
      while (surface.hasNext()) {
        CatalysisSite atom = surface.next();
        for (int pos = 0; pos < numberOfNeighbours; pos++) {
          CatalysisSite neighbour = atom.getNeighbour(pos);
          if (atom.getType() == neighbour.getType() || !neighbour.isOccupied()) {
            continue;
          }
          // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
          if (atom.getType() == CO) {
            histogramPossible[atom.getLatticeSite()][neighbour.getLatticeSite()] += elapsedTime / 2.0;
            //histogramPossibleCounter[atom.getLatticeSite()][neighbour.getLatticeSite()]++;
            histogramPossibleTmp[atom.getLatticeSite()][neighbour.getLatticeSite()] += 0.5;
          } else {
            histogramPossible[neighbour.getLatticeSite()][atom.getLatticeSite()] += elapsedTime / 2.0;
            //histogramPossibleCounter[neighbour.getLatticeSite()][atom.getLatticeSite()]++;
            histogramPossibleTmp[neighbour.getLatticeSite()][atom.getLatticeSite()] += 0.5;
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
    }
  }
  
  public void updatePossiblesLocal(double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      if (previousProbability != totalAndDepositionProbability) {
        for (int i = 0; i < transitionsHistogram.length; i++) {
          for (int j = 0; j < transitionsHistogram[0].length; j++) {
            for (int k = 0; k < transitionsHistogram[i][j]; k++) {
              histogramPossible[i][j] += elapsedTime;
              histogramPossibleCounter[i][j]++;
              histogramPossibleTmp[i][j] += elapsedTime;
              histogramPossibleCounterTmp[i][j]++;
            }
          }
        }
      } else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < lengthI; i++) {
          for (int j = 0; j < lengthJ; j++) {
            histogramPossible[i][j] += histogramPossibleTmp[i][j];
            histogramPossibleCounter[i][j] += histogramPossibleCounterTmp[i][j];
          }
        }
      }
    }
  }
  
  public void updatePossibles(Iterator<AbstractSite> surface, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleTmp = initDouble();
        histogramPossibleCounterTmp = initLong();
        // iterate over ALL(!) atoms of the surface to get all possible hops (only to compute multiplicity)
        while (surface.hasNext()) {
          AbstractGrowthSite atom = (AbstractGrowthSite) surface.next();
          for (int pos = 0; pos < numberOfNeighbours; pos++) {
            AbstractGrowthSite neighbourAtom = atom.getNeighbour(pos);
            byte destination = neighbourAtom.getTypeWithoutNeighbour(pos);
            byte origin = atom.getRealType();
            if (atom.getBondsProbability(pos) > 0) {
              histogramPossible[origin][destination] += elapsedTime;
              histogramPossibleCounter[origin][destination]++;
              histogramPossibleTmp[origin][destination] += elapsedTime;
              histogramPossibleCounterTmp[origin][destination]++;
            }
          }
        }
        previousProbability = totalAndDepositionProbability;
      } else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < lengthI; i++) {
          for (int j = 0; j < lengthJ; j++) {
            histogramPossible[i][j] += histogramPossibleTmp[i][j];
            histogramPossibleCounter[i][j] += histogramPossibleCounterTmp[i][j];
          }
        }
      }
    }
  }

  public void updatePossiblesIslands(Iterator<Island> islands, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      //if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleIslandTmp = new Double[9];
        // iterate over all islands of the surface to get all possible hops (only to compute multiplicity)
        while (islands.hasNext()) {
          Island island = (Island) islands.next();
          if (island.getNumberOfAtoms() < 9) {
            histogramPossibleIsland[island.getNumberOfAtoms()] += elapsedTime;
          }
        }
      /*} else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < 9; i++) {
          histogramPossibleConcerted[i] += histogramPossibleConcertedTmp[i];
        }
      }*/
    }
  }

  public void updatePossiblesMultiAtoms(Iterator<MultiAtom> islands, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      // iterate over all MultiAtom of the surface to get all possible hops (only to compute multiplicity)
      while (islands.hasNext()) {
        Island island = (Island) islands.next();
        if (island.getNumberOfAtoms() < 9) {
          histogramPossibleMultiAtom[island.getNumberOfAtoms()] += elapsedTime;
        }
      }
    }
  }
  
  public void updateSuccess(int oldType, int newType) {
    histogramSuccess[oldType][newType]++;
  }
  
  public void update(ArrayList<AbstractSite> surface)  {
    
  }
  
  public void reset() {
    surface.clear();
    previousProbability = 0;
    if (aeOutput) {
      histogramPossible = initDouble();
      histogramPossibleCounter = initLong();
      histogramPossibleTmp = initDouble();
      histogramPossibleCounterTmp = initLong();
      histogramSuccess = initInt();
      
      histogramPossibleReactionCoCus = initDouble1(3);
      histogramPossibleAdsorption = initDouble1(lengthI);
      histogramPossibleDesorption = initDouble3();
      histogramPossibleDiffusion = initDouble4();
      
      histogramPossibleIsland = initDouble1(9);
      histogramPossibleIslandTmp = initDouble1(9);
      histogramPossibleMultiAtom = initDouble1(4);
    }
  }
  
  public void printAe(PrintWriter print, float coverage) {
    boolean printLineBreak = (coverage == -1);
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%f %s", coverage, "AeInstantaneousDiscrete ");
    printAeLow(print, "AeInstantaneousDiscrete ", printLineBreak, histogramPossibleCounterTmp);
    
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeSuccess ");
    printAeLow(print, "AeSuccess ", printLineBreak, histogramSuccess);
    
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AePossibleFromList ");
    printAeLow(print, "AePossibleFromList ", printLineBreak, histogramPossible);

    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AePossibleDiscrete ");
    printAeLow(print, "AePossibleDiscrete ", printLineBreak, histogramPossibleCounter);
    
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeRatioTimesPossible ");
    Double[][] ratioTimesPossible = new Double[histogramPossible.length][histogramPossible[0].length];
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        ratioTimesPossible[origin][destination] = rates[origin][destination] * histogramPossible[origin][destination];
      }
    }
    printAeLow(print, "AeRatioTimesPossible ", printLineBreak, ratioTimesPossible);
    
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeMultiplicity ");
    Double[][] multiplicity = new Double[histogramPossible.length][histogramPossible[0].length];
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        multiplicity[origin][destination] = histogramSuccess[origin][destination] / ratioTimesPossible[origin][destination];
      }
    }
    printAeLow(print, "AeMultiplicity ", printLineBreak, multiplicity);
    print.println();
    print.flush();
  }
  
  public void printAe(PrintWriter print[], double time) {
    boolean printLineBreak = false;
    
    print[0].print(time + "\t");
    printAeLow(print[0], "", printLineBreak, histogramPossibleCounterTmp); //AeInstantaneousDiscrete
    print[1].print(time + "\t");
    printAeLow(print[1], "", printLineBreak, histogramSuccess); //AeSuccess 
    print[2].print(time + "\t");
    printAeLow(print[2], "", printLineBreak, histogramPossible); //AePossibleFromList
    for (int i = 0; i < 9; i++) {
      print[2].print(histogramPossibleIsland[i] + "\t");
    }
    for (int i = 0; i < histogramPossibleMultiAtom.length; i++) {
      print[2].print(histogramPossibleMultiAtom[i] + "\t");
    }
    print[3].print(time + "\t");
    printAeLow(print[3], "", printLineBreak, histogramPossibleCounter); //AePossibleDiscrete

    print[4].print(time + "\t");
    Double[][] ratioTimesPossible = new Double[histogramPossible.length][histogramPossible[0].length];
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        ratioTimesPossible[origin][destination] = rates[origin][destination] * histogramPossible[origin][destination];
      }
    }
    printAeLow(print[4], "", printLineBreak, ratioTimesPossible); //AeRatioTimesPossible

    print[5].print(time + "\t");
    Double[][] multiplicity = new Double[histogramPossible.length][histogramPossible[0].length];
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        multiplicity[origin][destination] = histogramSuccess[origin][destination] / ratioTimesPossible[origin][destination];
      }
    }
    printAeLow(print[5], "", printLineBreak, multiplicity); //AeMultiplicity
    
    if (print.length > 6) {
      // new print with everything, only for catalysis
      print[6].print(time + "\t");
      printAeLow(print[6], "", printLineBreak, histogramPossible); //AePossibleFromList REACTION
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
    for (int i = 0; i < print.length; i++) {
      print[i].println();
      print[i].flush();
    }
  }
  
  private void printAeLow(PrintWriter print, String name, boolean printLineBreak, Object[][] histogram) {
    for (int origin = 0; origin < histogram.length; origin++) {
      if (printLineBreak) print.print(name);
      for (int destination = 0; destination < histogram[0].length; destination++) {
        print.print(histogram[origin][destination] + "\t");
      }
      if (printLineBreak) print.println();
    }
  }
    
  private Double[] initDouble1(int length) {
    Double[] histogram = new Double[length];
    for (int i = 0; i < length; i++) {
      histogram[i] = new Double(0);
    }
    return histogram;
  }
  
  private Double[][][] initDouble3() {
    Double[][][] histogram = new Double[lengthI][lengthJ][3];
    for (int i = 0; i < lengthI; i++) {
      for (int j = 0; j < lengthJ; j++) {
        for (int k = 0; k < 3; k++) {
          histogram[i][j][k] = new Double(0);
        }
      }
    }
    return histogram;
  }
  
  private Double[][][][] initDouble4() {
    Double[][][][] histogram = new Double[lengthI][lengthJ][lengthI][3];
    for (int i = 0; i < lengthI; i++) {
      for (int j = 0; j < lengthJ; j++) {
        for (int k = 0; k < lengthI; k++) {
          for (int l = 0; l < 3; l++) {
            histogram[i][j][k][l] = new Double(0);
          }
        }
      }
    }
    return histogram;
  }
  
  
  private Double[][] initDouble() {
    Double[][] histogram = new Double[lengthI][lengthJ];
    for (int i = 0; i < lengthI; i++) {
      for (int j = 0; j < lengthJ; j++) {
        histogram[i][j] = new Double(0);
      }
    }
    return histogram;
  }
  
  private Long[][] initLong() {
    Long[][] histogram = new Long[lengthI][lengthJ];
    for (int i = 0; i < lengthI; i++) {
      for (int j = 0; j < lengthJ; j++) {
        histogram[i][j] = new Long(0);
      }
    }
    return histogram;
  }
  
  private Integer[][] initInt() {
    Integer[][] histogram = new Integer[lengthI][lengthJ];
    for (int i = 0; i < lengthI; i++) {
      for (int j = 0; j < lengthJ; j++) {
        histogram[i][j] = new Integer(0);
      }
    }
    return histogram;
  }
}
