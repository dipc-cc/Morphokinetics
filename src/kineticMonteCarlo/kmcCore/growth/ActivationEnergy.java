/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import java.util.ArrayList;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ActivationEnergy {
  private double[][] histogramPossible;
  private long[][] histogramPossibleCounter;
  private double[][] histogramPossibleTmp;
  private long[][] histogramPossibleCounterTmp;
  private final ArrayList<AbstractAtom> surface;
  private final boolean aeOutput;
  private boolean doActivationEnergyStudy;
  private double previousProbability;
  private int length;
  private int numberOfNeighbours;

  public ActivationEnergy(Parser parser) {
    surface = new ArrayList();
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    doActivationEnergyStudy = false;
    if (aeOutput) {
      if (parser.getCalculationMode().equals("basic")) {
        doActivationEnergyStudy = true;
        length = 4;
        numberOfNeighbours = 4;
      }
      if (parser.getCalculationMode().equals("graphene")) {
        doActivationEnergyStudy = true;
        length = 8;
        numberOfNeighbours = 12;
      }
      if (parser.getCalculationMode().equals("Ag") || parser.getCalculationMode().equals("AgUc")) {
        doActivationEnergyStudy = true;
        length = 7;
        numberOfNeighbours = 6;
      }
      histogramPossible = new double[length][length];
      histogramPossibleCounter = new long[length][length];
      histogramPossibleTmp = new double[length][length];
      histogramPossibleCounterTmp = new long[length][length];
    }
    previousProbability = 0;
  }

  public double[][] getHistogramPossible() {
    return histogramPossible;
  }

  public long[][] getHistogramPossibleCounter() {
    return histogramPossibleCounter;
  }

  public void updatePossibles(ArrayList<AbstractAtom> surface, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleTmp = new double[length][length];
        histogramPossibleCounterTmp = new long[length][length];
        // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
        for (int i = 0; i < surface.size(); i++) {
          AbstractGrowthAtom atom = (AbstractGrowthAtom) surface.get(i);
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
        for (int i = 0; i < length; i++) {
          for (int j = 0; j < length; j++) {
            histogramPossible[i][j] += histogramPossibleTmp[i][j];
            histogramPossibleCounter[i][j] += histogramPossibleCounterTmp[i][j];
          }
        }
      }
    }
  }

  public void update(ArrayList<AbstractAtom> surface)  {
    
  }
  
  public void reset() {
    surface.clear();
    previousProbability = 0;
    if (aeOutput) {
      histogramPossible = new double[length][length];
      histogramPossibleCounter = new long[length][length];
      histogramPossibleTmp = new double[length][length];
      histogramPossibleCounterTmp = new long[length][length];
    }
  }
}
