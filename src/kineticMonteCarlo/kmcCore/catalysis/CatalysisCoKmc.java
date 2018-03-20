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
package kineticMonteCarlo.kmcCore.catalysis;

import basic.Parser;
import basic.io.CatalysisCoRestart;
import basic.io.OutputType;
import kineticMonteCarlo.activationEnergy.ActivationEnergy;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.O;
import kineticMonteCarlo.unitCell.CatalysisUc;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoKmc extends CatalysisKmc {
  
  private final boolean doAdsorption;
  /*final boolean doDesorption;
  final boolean doReaction;
  final boolean doDiffusion;*/
  // Adsorption
  private double adsorptionRateCOPerSite;
  private double adsorptionRateOPerSite;
  // Desorption
  //double[] desorptionRateCOPerSite; // BRIDGE or CUS
  private double[] desorptionRateOPerSite;  // [BR][BR], [BR][CUS], [CUS][BR], [CUS][CUS]
  // Reaction
 /* double[] reactionRateCoO; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  double[] diffusionRateCO;
  double[] diffusionRateO;*/
  private final double goMultiplier;
  private int numGaps;
  /**
   * Activation energy output during the execution
   */
  private final boolean outputAe;
  private final boolean outputAeTotal;
  private final boolean doO2Dissociation;
  private long co2sum;
  /** Previous instant co2sum. For output */
  private long co2prv;
  private final int co2max;
  private long[] co2; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  
  public CatalysisCoKmc(Parser parser, String restartFolder) {
    super(parser, restartFolder);
    doAdsorption = parser.doCatalysisAdsorption();
    /*doDesorption = parser.doCatalysisDesorption();
    doReaction = parser.doCatalysisReaction();
    doDiffusion = parser.doCatalysisDiffusion();*/
    doO2Dissociation = parser.doCatalysisO2Dissociation();
    goMultiplier = parser.getGOMultiplier();
    outputAe = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    outputAeTotal = parser.getOutputFormats().contains(OutputType.formatFlag.AETOTAL);
    boolean outputData = parser.outputData();
    CatalysisCoRestart restart = new CatalysisCoRestart(outputData, restartFolder);
    setRestart(restart);
    co2max = parser.getNumberOfCo2();
    co2 = new long[4];
    co2sum = 0;
  }
  
  @Override
  int getNumberOfReactions() {
    return 4;
  }
  
  @Override
  public void setRates(CatalysisRates rates) {
    desorptionRateCOPerSite = new double[2]; // empty
    desorptionRateOPerSite = new double[4]; // empty 
    reactionRateCoO = new double[4]; // empty
    diffusionRateCO = new double[4]; // empty
    diffusionRateO = new double[4]; // empty
    if (doAdsorption) {
      adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
      adsorptionRateOPerSite = rates.getAdsorptionRate(O);
    }
    if (doDesorption) {
      desorptionRateCOPerSite = rates.getDesorptionRates(CO);
      desorptionRateOPerSite = rates.getDesorptionRates(O);
      for (int i = 0; i < desorptionRateOPerSite.length; i++) {
        desorptionRateOPerSite[i] = desorptionRateOPerSite[i] * goMultiplier;
      }
    }
    if (doReaction) {
      reactionRateCoO = rates.getReactionRates();
    }
    if (doDiffusion) {
      diffusionRateCO = rates.getDiffusionRates(CO);
      diffusionRateO = rates.getDiffusionRates(O);
    }
    
    if (outputAe || outputAeTotal) {
      double[][] processProbs2D = new double[2][2];

      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
          processProbs2D[i][j] = rates.getReactionRates()[i * 2 + j];
        }
      }
      getActivationEnergy().setRates(processProbs2D);
    }
    numGaps = getLattice().getHexaSizeI() * getLattice().getHexaSizeJ();
  }
  
  /**
   * Start with fully covered surface.
   * 
   * @param randomTypes if true, CO and O types randomly chosen. If false, only oxygen.
   */
  @Override
  void initCovered() {
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        switch (start) {
          case "O":
            a.setType(O);
            break;
          case "CO":
            a.setType(CO);
            break;
          default:
            a.setType((byte) StaticRandom.rawInteger(2));
            break;
        }
        getLattice().deposit(a, false);
      }
    }

    totalRate[DESORPTION] = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        a.setOnList(ADSORPTION, false);
        sites[ADSORPTION].insert(a);
        sites[DIFFUSION].insert(a);
        if (a.getType() == CO) {
          a.setRate(DESORPTION, desorptionRateCOPerSite[a.getLatticeSite()]);
          totalRate[DESORPTION] += a.getRate(DESORPTION);
        } else { //O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = desorptionRateOPerSite[index];
              a.addRate(DESORPTION, rate, k);
            }
          }
          totalRate[DESORPTION] += a.getRate(DESORPTION);
        }
        if (a.getRate(DESORPTION) > 0) {
          a.setOnList(DESORPTION, true);
        }
        sites[DESORPTION].insert(a);
      }
    }
    sites[DESORPTION].populate();
    totalRate[DESORPTION] = sites[DESORPTION].getTotalRate(DESORPTION);
    
    totalRate[REACTION] = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        if (a.getType() == CO) {
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              //               CO                   +         O
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        } else { // O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == CO) {
              //               CO                   +         O
              int index = 2 * neighbour.getLatticeSite() + a.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        }
        if (a.getRate(REACTION) > 0) {
          a.setOnList(REACTION, true);
        }
        sites[REACTION].insert(a);
        totalRate[REACTION] += a.getRate(REACTION);
      }
    }
    sites[REACTION].populate();
    
    getList().setRates(totalRate);
    getLattice().resetOccupied();
  }
  
  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  @Override
  void initAdsorptionRates() {
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        a.setRate(ADSORPTION, adsorptionRateCOPerSite + adsorptionRateOPerSite); // there is no neighbour
        a.setOnList(ADSORPTION, true);
        totalRate[ADSORPTION] += a.getRate(ADSORPTION);
        sites[ADSORPTION].insert(a);
        sites[DESORPTION].insert(a);
        sites[REACTION].insert(a);
        sites[DIFFUSION].insert(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORPTION].populate();
    sites[DESORPTION].populate();
    sites[REACTION].populate();
    sites[DIFFUSION].populate();
  }
  
  @Override
  void depositNewAtom() {
    CatalysisSite destinationAtom;
    byte atomType;
      
    CatalysisSite neighbourAtom = null;
    int random;
    
    if (sites[ADSORPTION].isEmpty()) {
      // can not deposit anymore
      return;
    }

    destinationAtom = (CatalysisSite) sites[ADSORPTION].randomElement();

    if (destinationAtom == null || destinationAtom.getRate(ADSORPTION) == 0 || destinationAtom.isOccupied()) {
      boolean isThereAnAtom = destinationAtom == null;
      System.out.println("Something is wrong " + isThereAnAtom);
    }
    double randomNumber = StaticRandom.raw() * destinationAtom.getRate(ADSORPTION);
    if (randomNumber < adsorptionRateCOPerSite) {
      atomType = CO;
    } else {
      atomType = O;
    }
    destinationAtom.setType(atomType);
    if (!doO2Dissociation || atomType == CO) {
      depositAtom(destinationAtom);
    } else { // it has to deposit two O (dissociation of O2 -> 2O)
      depositAtom(destinationAtom);
      random = StaticRandom.rawInteger(4);
      neighbourAtom = destinationAtom.getNeighbour(random);
      while (neighbourAtom.isOccupied()) {
        random = (random + 1) % 4;
        neighbourAtom = destinationAtom.getNeighbour(random);
      }
      neighbourAtom.setType(O);
      depositAtom(neighbourAtom);
    }
    
    updateRates(destinationAtom);
    if (neighbourAtom != null) {
      updateRates(neighbourAtom);
    }
  }
  
  @Override
  void desorbAtom() {    
    CatalysisSite atom = (CatalysisSite) sites[DESORPTION].randomElement();
    int atomsToDesorp = 1;
    CatalysisSite neighbour = null;
    if (atom.getType() == O) { // it has to desorp with another O to create O2
      neighbour = atom.getRandomNeighbour(DESORPTION);
      atomsToDesorp = 2;
      getLattice().extract(neighbour);
    }
    
    getLattice().extract(atom);
    numGaps += atomsToDesorp;
    if (neighbour != null) {
      updateRates(neighbour);
    }
    updateRates(atom);
  }
  
  @Override
  void reactAtom() {
    CatalysisSite atom = (CatalysisSite) sites[REACTION].randomElement();
    // it has to react with another atom
    CatalysisSite neighbour = atom.getRandomNeighbour(REACTION);
    getLattice().extract(neighbour);
    getLattice().extract(atom);
    numGaps = numGaps-2;
    // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
    int index;
    if (atom.getType() == CO) {
      index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    } else {
      index = 2 * neighbour.getLatticeSite() + atom.getLatticeSite();
    }
    co2[index]++;
    if (stationary)
      co2sum++;

    updateRates(neighbour);
    updateRates(atom);
  }
  
  /**
   * Moves an atom.
   */
  @Override
  void diffuseAtom() {
    CatalysisSite originAtom = (CatalysisSite) sites[DIFFUSION].randomElement();
    CatalysisSite destinationAtom = originAtom.getRandomNeighbour(DIFFUSION);
    destinationAtom.setType(originAtom.getType());
    getLattice().extract(originAtom);    
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    updateRates(originAtom);
    updateRates(destinationAtom);
  }

  private boolean depositAtom(CatalysisSite atom) {
    if (atom.isOccupied()) {
      return false;
    }
    getLattice().deposit(atom, false);
    
    numGaps--;
    return true;
  }
  
  /**
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(CatalysisSite atom) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    // recompute the probability of the current atom
    if (doAdsorption) recomputeAdsorptionRate(atom);
    if (doDesorption) recomputeDesorptionRate(atom);
    if (doReaction) recomputeReactionRate(atom);
    if (doDiffusion) recomputeDiffusionRate(atom);
    // recompute the probability of the neighbour atoms
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (doAdsorption) recomputeAdsorptionRate(neighbour);
      if (doDesorption) recomputeDesorptionRate(neighbour);
      if (doReaction) recomputeReactionRate(neighbour);
      if (doDiffusion) recomputeDiffusionRate(neighbour);
    }
    
    // recalculate total rate, if needed
    if (totalRate[ADSORPTION] / previousRate[ADSORPTION] < 1e-1) {
      updateRateFromList(ADSORPTION);
    }
    if (totalRate[DESORPTION] / previousRate[DESORPTION] < 1e-1 || 1.0 - totalRate[DESORPTION] / sites[DESORPTION].getTotalRate(DESORPTION) > 1e-3 || simulatedSteps % 10000000 == 0) {
      //System.out.println(simulatedSteps+" "+previousDesorptionRate + " " + totalDesorptionRate + " " + sites[DESORPTION].getDesorptionRate());
      updateRateFromList(DESORPTION);
    }
    if (totalRate[REACTION] / previousRate[REACTION] < 1e-1) {
      updateRateFromList(REACTION);
    }
    if (totalRate[DIFFUSION] / previousRate[DIFFUSION] < 1e-1) {
      updateRateFromList(DIFFUSION);
    }
    
    // tell to the list new rates
    getList().setRates(totalRate);
  }
  
  private void recomputeAdsorptionRate(CatalysisSite atom) {
    double oldAdsorptionRate = atom.getRate(ADSORPTION);
    totalRate[ADSORPTION] -= oldAdsorptionRate;
    if (atom.isOccupied()) {
      atom.setRate(ADSORPTION, 0);
    } else {
      int canAdsorbO2 = atom.isIsolated() && doO2Dissociation ? 0 : 1;
      atom.setRate(ADSORPTION, adsorptionRateCOPerSite + canAdsorbO2 * adsorptionRateOPerSite);
      if (atom.isIsolated()) {
        counterSitesWith4OccupiedNeighbours++;
      }
    }
    recomputeCollection(ADSORPTION, atom, oldAdsorptionRate);
  }
  
  private void recomputeDesorptionRate(CatalysisSite atom) {
    totalRate[DESORPTION] -= atom.getRate(DESORPTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DESORPTION)) {
        sites[DESORPTION].removeAtomRate(atom);
      }
      atom.setOnList(DESORPTION, false);
      return;
    }
    double oldDesorptionRate = atom.getRate(DESORPTION);
    atom.setRate(DESORPTION, 0);
    if (atom.getType() == CO) {
      double rate = getDesorptionRate(atom);
      atom.setRate(DESORPTION, rate);
    } else { // O
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          double rate = getDesorptionRate(atom, neighbour);
          atom.addRate(DESORPTION, rate, i);
        }
      }
    }
    recomputeCollection(DESORPTION, atom, oldDesorptionRate);
  }
  
  private void recomputeReactionRate(CatalysisSite atom) {
    totalRate[REACTION] -= atom.getRate(REACTION);
    double oldReactionRate = atom.getRate(REACTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(REACTION)) {
        sites[REACTION].removeAtomRate(atom);
      }
      atom.setOnList(REACTION, false);
      return;
    }
    atom.setRate(REACTION, 0);
    byte otherType = (byte) ((atom.getType() + 1) % 2);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (neighbour.isOccupied() && neighbour.getType() == otherType) {
        double rate = getReactionRate(atom, neighbour); 
        atom.addRate(REACTION, rate/2.0, i);
      }
    }
    recomputeCollection(REACTION, atom, oldReactionRate);
  }

  private void recomputeDiffusionRate(CatalysisSite atom) {
    totalRate[DIFFUSION] -= atom.getRate(DIFFUSION);
    double oldDiffusionRate = atom.getRate(DIFFUSION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DIFFUSION)) {
        sites[DIFFUSION].removeAtomRate(atom);
      }
      atom.setOnList(DIFFUSION, false);
      return;
    }
    atom.setRate(DIFFUSION, 0);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double rate = getDiffusionRate(atom, neighbour);
        atom.addRate(DIFFUSION, rate, i);
      }
    }
    recomputeCollection(DIFFUSION, atom, oldDiffusionRate);
  }
  
  /**
   * Computes desorption rate.
   * 
   * @param atom O atom.
   * @param neighbour O atom.
   * @return 
   */
  private double getDesorptionRate(CatalysisSite atom, CatalysisSite neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    return desorptionRateOPerSite[index];
  }
  
  @Override
  void initStationary() {
    co2prv = 0;
    co2 = new long[4];
  }

  @Override
  long[] getProduction() {
    return co2;
  }
  
  @Override
  long getSumProduction() {
    return co2sum;
  }

  @Override
  boolean writeNow() {
    return co2sum % 10 == 0 && co2prv != co2sum;
  }
  
  @Override
  void updatePrevious() {
    co2prv = co2sum;
  }
  
  @Override
  boolean maxProduction() {
    return co2sum != co2max;
  }
  
  @Override
  public void reset() {
    super.reset();
    co2 = new long[getNumberOfReactions()];
    co2sum = 0;
  }
  
  /**
   * Method to print rates. Equivalent to table 1 of Temel et al. J. Chem. Phys. 126 (2007).
   */
  @Override
  public void printRates() {
    System.out.println(" Process ");
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "CO adsorption\t", adsorptionRateCOPerSite);
    System.out.format("%s\t%1.1e\t%1.1e\n", " O adsorption\t",adsorptionRateOPerSite, adsorptionRateOPerSite * 2);
    System.out.println(" ------- ");
    System.out.println("Desorption");
    System.out.format("%s\t\t%1.1e\n","CO^BR", desorptionRateCOPerSite[0]);
    System.out.format("%s\t\t%1.1e\n", "CO^CUS", desorptionRateCOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^BR", desorptionRateOPerSite[0]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^CUS", desorptionRateOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^BR", desorptionRateOPerSite[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^CUS", desorptionRateOPerSite[3]);
    System.out.println(" ------- ");
    System.out.println("Reaction");
    System.out.format("%s\t%1.1e\n", "CO^BR + O^BR", reactionRateCoO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR + O^CUS", reactionRateCoO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^BR", reactionRateCoO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^CUS", reactionRateCoO[3]);
    System.out.println(" ------- ");
    System.out.println("Diffusion");
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^BR  ", diffusionRateCO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^CUS ", diffusionRateCO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^BR ", diffusionRateCO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^CUS", diffusionRateCO[3]);
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "O^BR -> O^BR    ", diffusionRateO[0]);
    System.out.format("%s\t%1.1e\n", "O^BR -> O^CUS   ", diffusionRateO[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^BR   ", diffusionRateO[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^CUS  ", diffusionRateO[3]);
  }
}
