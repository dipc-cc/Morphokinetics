/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.unitCell.SimpleUc;

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
  private Double[] histogramPossibleConcerted;
  private Double[] histogramPossibleConcertedTmp;
  private final ArrayList<AbstractAtom> surface;
  private final boolean aeOutput;
  private boolean doActivationEnergyStudy;
  private double previousProbability;
  private int lengthI;
  private int lengthJ;
  private int numberOfNeighbours;
  private double[][] rates;

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
        histogramPossibleConcerted = new Double[8];
        histogramPossibleConcertedTmp = new Double[8];
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
        SimpleUc uc = (SimpleUc) lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
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
              CatalysisAtom neighbour = atom.getNeighbour(pos);
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
  public void updatePossibles(Iterator<CatalysisAtom> surface, double elapsedTime, boolean stationary) {
    if (doActivationEnergyStudy && stationary) {
      // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
      
      histogramPossibleTmp = initDouble();
      while (surface.hasNext()) {
        CatalysisAtom atom = surface.next();
        for (int pos = 0; pos < numberOfNeighbours; pos++) {
          CatalysisAtom neighbour = atom.getNeighbour(pos);
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
  
  public void updatePossibles(Iterator<AbstractAtom> surface, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleTmp = initDouble();
        histogramPossibleCounterTmp = initLong();
        // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
        while (surface.hasNext()) {
          AbstractGrowthAtom atom = (AbstractGrowthAtom) surface.next();
          for (int pos = 0; pos < numberOfNeighbours; pos++) {
            AbstractGrowthAtom neighbourAtom = atom.getNeighbour(pos);
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
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleConcertedTmp = new Double[8];
        // iterate over all islands of the surface to get all possible hops (only to compute multiplicity)
        while (islands.hasNext()) {
          Island island = (Island) islands.next();
          histogramPossibleConcerted[island.getNumberOfAtoms()]++;
        }
      } else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < 8; i++) {
          histogramPossibleConcerted[i] += histogramPossibleConcertedTmp[i];
        }

      }
    }
  }
  public void updateSuccess(int oldType, int newType) {
    histogramSuccess[oldType][newType]++;
  }
  
  public void update(ArrayList<AbstractAtom> surface)  {
    
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
      
      histogramPossibleConcerted = initDouble1(8);
      histogramPossibleConcertedTmp = initDouble1(8);
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
