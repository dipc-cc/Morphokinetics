/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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
import kineticMonteCarlo.lattice.CatalysisAmmoniaLattice;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import kineticMonteCarlo.site.CatalysisAmmoniaSite;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.N;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH2;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NH3;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.NO;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.O;
import static kineticMonteCarlo.site.CatalysisAmmoniaSite.OH;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import kineticMonteCarlo.unitCell.CatalysisUc;
import ratesLibrary.CatalysisHongRates;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;

/**
 * Catalysis simulation of ammonia oxidation based on S. Hong et atl. Journal of Catalysis 276
 * (2010) 371-381.
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaKmc extends CatalysisKmc {

  private static final byte P5 = 4;
  private static final byte P6 = 5;
  private static final byte P7 = 6;
  private static final byte P8 = 7;
  private static final byte P9 = 8;
  private static final byte P15 = 9;
  private static final byte P16 = 10;
  private static final byte P17 = 11;
  private static final byte P18 = 12;
  // Adsorption
  private double adsorptionRateNH3PerSite;
  private double adsorptionRateOPerSite;
  private final double[] desorptionRatePerSite;
  private final double[] diffusionRates;
  
  private double NH3_O_reaction_NH2_OH;
  private double NH2_OH_reaction_NH_H2O;
  private double NH_OH_reaction_N_H2O;
  private double NH_O_reaction_N_OH;
  private double N_O_reaction_NO;
  private double NH2_O_reaction_NH_OH;
  private double NH_OH_reaction_NH2_O;
  private double NH2_OH_reaction_NH3_O;
  private double N_OH_reaction_NH_O;

  private long h2oCounter;
  private long noCounter;
  private long nCounter;

  public CatalysisAmmoniaKmc(Parser parser, String restartFolder) {
    super(parser, restartFolder);
    desorptionRatePerSite = new double[11];
    diffusionRates = new double[11];
    boolean useTree = parser.useCatalysisTree(REACTION);
    sites[P5] = col.getCollection(useTree, P5);
    sites[P6] = col.getCollection(useTree, P6);
    sites[P7] = col.getCollection(useTree, P7);
    sites[P8] = col.getCollection(useTree, P8);
    sites[P9] = col.getCollection(useTree, P9);
    sites[P15] = col.getCollection(useTree, P15);
    sites[P16] = col.getCollection(useTree, P16);
    sites[P17] = col.getCollection(useTree, P17);
    sites[P18] = col.getCollection(useTree, P18);
  }
  
  @Override
  int getNumberOfReactions() {
    return P18 + 1;
  }

  @Override
  void init(Parser parser) {
    CatalysisAmmoniaLattice catalysisLattice = new CatalysisAmmoniaLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), parser.getRatesLibrary());
    catalysisLattice.init();
    setLattice(catalysisLattice);
  }
  
  @Override
  public void setRates(CatalysisRates rates) {
    CatalysisHongRates r = (CatalysisHongRates) rates;
    if (doAdsorption) {
      adsorptionRateNH3PerSite = rates.getAdsorptionRate(NH3);
      adsorptionRateOPerSite = rates.getAdsorptionRate(O);
    }
    if (doDesorption) {
      desorptionRatePerSite[NH3] = r.getDesorptionRate(NH3);
      desorptionRatePerSite[O] = r.getDesorptionRate(O);
      desorptionRatePerSite[N] = r.getDesorptionRate(N);
      desorptionRatePerSite[NO] = r.getDesorptionRate(NO);
    }
    if (doDiffusion) {
      diffusionRates[N] = r.getDiffusionRate(N);
      diffusionRates[O] = r.getDiffusionRate(O);
      diffusionRates[OH] = r.getDiffusionRate(OH);
    }
    if (doReaction) {
      NH3_O_reaction_NH2_OH = r.getReactionRate(5);
      NH2_OH_reaction_NH_H2O = r.getReactionRate(6);
      NH_OH_reaction_N_H2O = r.getReactionRate(7);
      NH_O_reaction_N_OH = r.getReactionRate(8);
      N_O_reaction_NO = r.getReactionRate(9);
      NH2_O_reaction_NH_OH = r.getReactionRate(15);
      NH_OH_reaction_NH2_O = r.getReactionRate(16);
      NH2_OH_reaction_NH3_O = r.getReactionRate(17);
      N_OH_reaction_NH_O = r.getReactionRate(18);
    }
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
    if (randomNumber < adsorptionRateNH3PerSite) {
      atomType = NH3;
    } else {
      atomType = O;
    }
    destinationAtom.setType(atomType);
    if (atomType == NH3) {
      depositAtom(destinationAtom);
    } else { // it has to deposit two O (dissociation of O2 -> 2O). Both in CUS
      depositAtom(destinationAtom);
      
      random = StaticRandom.rawInteger(2)*2;
      neighbourAtom = destinationAtom.getNeighbour(random);
      while (neighbourAtom.isOccupied()) {
        random = (random + 2) % 4;
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
  private boolean depositAtom(CatalysisSite atom) {
    if (atom.isOccupied()) {
      return false;
    }
    getLattice().deposit(atom, false);
    
    return true;
  }

  @Override
  void desorpAtom() { 
    CatalysisSite atom = (CatalysisSite) sites[DESORPTION].randomElement();
    CatalysisSite neighbour = null;
    if (atom.getType() == O || atom.getType() == N) { // it has to desorp with another O to create O2
      neighbour = atom.getRandomNeighbour(DESORPTION);
      getLattice().extract(neighbour);
      if (atom.getType() == N) {
        nCounter++;
      }
    }
    
    getLattice().extract(atom);
    if (neighbour != null) {
      updateRates(neighbour);
    }
    updateRates(atom);
  }

  @Override
  //void reactAtom(int reaction) {
  void reactAtom() {
    double totalReactionRate = sites[REACTION].getTotalRate(REACTION);
    double randomNumber = StaticRandom.raw();
    double random = randomNumber * totalReactionRate;
 
    double sum = 0.0;
    byte i;
    for (i = P5; i <= P18 ; i++) {
      sum += sites[i].getTotalRate(i);
      if (sum > random) {
        //System.out.println(i+" it's the chosen one");
        break;
      }
    }
    
    CatalysisSite atom = (CatalysisSite) sites[i].randomElement();
    // it has to react with another atom
    CatalysisSite neighbour = atom.getRandomNeighbour(i);
    switch (i) {
      case P5:
        reactP5(atom, neighbour);
        break;
      case P6:
        reactP6(atom, neighbour);
        break;
      case P7:
        reactP7(atom, neighbour);
        break;
      case P8:
        reactP8(atom, neighbour);
        break;
      case P9:
        reactP9(atom, neighbour);
        break;
      case P15:
        reactP15(atom, neighbour);
        break;
      case P16:
        reactP16(atom, neighbour);
        break;
      case P17:
        reactP17(atom, neighbour);
        break;
      case P18:
        reactP18(atom, neighbour);
        break;
      default:
        System.out.println("Error in reaction");
    }
    updateRates(neighbour);
    updateRates(atom);
  }
  
  /**
   * NH3 + O -> NH2 + OH.
   */
  private void reactP5(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH3) {
      atom.setType(NH2);
      neighbour.setType(OH);
    } else {
      atom.setType(OH);
      neighbour.setType(NH2);
    }
  }
  
  /**
   * NH2 + OH -> NH + H2O (g)
   */
  private void reactP6(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH2) {
      atom.setType(NH);
      getLattice().extract(neighbour); // H2O
    } else {
      getLattice().extract(atom); // H2O
      neighbour.setType(NH);
    }
    h2oCounter++;
  }

  /**
   * NH + OH -> N + H2O (g).
   */
  private void reactP7(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH) {
      atom.setType(N);
      getLattice().extract(neighbour); // H2O
    } else {
      getLattice().extract(atom); // H2O
      neighbour.setType(N);
    }
    h2oCounter++;
  }
  /**
   * NH + O -> N + OH.
   */
  private void reactP8(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH) {
      atom.setType(N);
      neighbour.setType(OH);
    } else {
      atom.setType(OH);
      neighbour.setType(N);
    }
  }
  /**
   * N + O -> NO.
   */
  private void reactP9(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == N) {
      atom.setType(NO);
      getLattice().extract(neighbour);
    } else {
      getLattice().extract(atom);
      neighbour.setType(NO);
    }
    noCounter++;
  }
  /**
   * NH2 + O -> NH + OH.
   */
  private void reactP15(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH2) {
      atom.setType(NH);
      neighbour.setType(OH);
    } else {
      atom.setType(OH);
      neighbour.setType(NH);
    }
  }
  /**
   * NH + OH -> NH2 + O.
   */
  private void reactP16(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH) {
      atom.setType(NH2);
      neighbour.setType(O);
    } else {
      atom.setType(O);
      neighbour.setType(NH2);
    }
  }
  /**
   * NH2 + OH -> NH3 + O.
   */
  private void reactP17(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == NH2) {
      atom.setType(NH3);
      neighbour.setType(O);
    } else {
      atom.setType(O);
      neighbour.setType(NH3);
    }
  }
  /**
   * N + OH -> NH + O.
   */
  private void reactP18(CatalysisSite atom, CatalysisSite neighbour) {
    if (atom.getType() == N) {
      atom.setType(NH);
      neighbour.setType(O);
    } else {
      atom.setType(O);
      neighbour.setType(NH);
    }
  }
    
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


  @Override
  void initCovered() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void printRates() {
    System.out.println("Rates are not known yet");
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  @Override
  void initAdsorptionProbability() {
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite s = uc.getSite(j);
        if (s.getLatticeSite() == CUS) {
          s.setRate(ADSORPTION, adsorptionRateNH3PerSite + adsorptionRateOPerSite); // there is no neighbour
          s.setOnList(ADSORPTION, true);
          totalRate[ADSORPTION] += s.getRate(ADSORPTION);
          sites[ADSORPTION].insert(s);
        }
        sites[DESORPTION].insert(s);
        sites[REACTION].insert(s);
        sites[DIFFUSION].insert(s);
        for (int k = P5; k < P18+1; k++) {
          sites[k].insert(s);
        }
      }
    }
    getList().setRates(totalRate);
    sites[ADSORPTION].populate();
    sites[DESORPTION].populate();
    sites[REACTION].populate();
    sites[DIFFUSION].populate();
    for (int k = P5; k < P18+1; k++) {
      sites[k].populate();
    }
  }
  
  /**
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(CatalysisSite site) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    CatalysisAmmoniaSite s = (CatalysisAmmoniaSite) site;
    // recompute the probability of the current atom
    if (doAdsorption) recomputeAdsorptionProbability(s);
    if (doDesorption) recomputeDesorptionProbability(s);
    if (doReaction) recomputeReactionProbability(s);
    if (doDiffusion) recomputeDiffusionProbability(s);
    // recompute the probability of the neighbour atoms
    for (int i = 0; i < s.getNumberOfNeighbours(); i++) {
      CatalysisAmmoniaSite neighbour = (CatalysisAmmoniaSite) s.getNeighbour(i);
      if (doAdsorption) recomputeAdsorptionProbability(neighbour);
      if (doDesorption) recomputeDesorptionProbability(neighbour);
      if (doReaction) recomputeReactionProbability(neighbour);
      if (doDiffusion) recomputeDiffusionProbability(neighbour);
    }
    
    // recalculate total probability, if needed
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
    
    // tell to the list new probabilities
    getList().setRates(totalRate);
  }
  
  private void recomputeAdsorptionProbability(CatalysisAmmoniaSite site) {
    double oldAdsorptionRate = site.getRate(ADSORPTION);
    totalRate[ADSORPTION] -= oldAdsorptionRate;
    if (site.isOccupied()) {
      site.setRate(ADSORPTION, 0);
    } else {
      int canAdsorbO2 = site.isIsolated() ? 0 : 1;
      if (site.getLatticeSite() == CUS) {
        site.setRate(ADSORPTION, adsorptionRateNH3PerSite + canAdsorbO2 * adsorptionRateOPerSite);
      }
    }
    recomputeCollection(ADSORPTION, site, oldAdsorptionRate);
  }
  
  private void recomputeDesorptionProbability(CatalysisSite atom) {
    totalRate[DESORPTION] -= atom.getRate(DESORPTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DESORPTION)) {
        sites[DESORPTION].removeAtomRate(atom);
      }
      atom.setOnList(DESORPTION, false);
      return;
    }
    double oldDesorptionProbability = atom.getRate(DESORPTION);
    atom.setRate(DESORPTION, 0);
    double rate = desorptionRatePerSite[atom.getType()];
    if (atom.getType() == NH3 || atom.getType() == NO) {
      atom.setRate(DESORPTION, rate);
    } else { // O or N
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          atom.addRate(DESORPTION, rate, i);
        }
      }
    }
    recomputeCollection(DESORPTION, atom, oldDesorptionProbability);
  }
  
  private void recomputeReactionProbability(CatalysisAmmoniaSite atom) {
    totalRate[REACTION] -= atom.getRate(REACTION);
    for (byte i = 0; i < 13; i++) {
      totalRate[i] -= atom.getRate(i);
    }
    double oldReactionRate = atom.getRate(REACTION);
    double[] oldReactionRates = new double[13];
    for (byte i = 0; i < oldReactionRates.length; i++) {
      oldReactionRates[i] = atom.getRate(i);
    }
    if (!atom.isOccupied()) {
      if (atom.isOnList(REACTION)) {
        sites[REACTION].removeAtomRate(atom);
      }
      atom.setOnList(REACTION, false);
      return;
    }
    atom.setRate(REACTION, 0);
    atom.setRate(P5, 0);
    atom.setRate(P6, 0);
  
    recomputeReactionP(atom, P5, NH3_O_reaction_NH2_OH, NH3, O);
    recomputeReactionP(atom, P6, NH2_OH_reaction_NH_H2O, NH2, OH);
    recomputeReactionP(atom, P7, NH_OH_reaction_N_H2O, NH, OH);
    recomputeReactionP(atom, P8, NH_O_reaction_N_OH, NH, O);
    recomputeReactionP(atom, P9, N_O_reaction_NO, N, O);
    recomputeReactionP(atom, P15, NH2_O_reaction_NH_OH, NH2, O);
    recomputeReactionP(atom, P16, NH_OH_reaction_NH2_O, NH, OH);
    recomputeReactionP(atom, P17, NH2_OH_reaction_NH3_O, NH2, OH);
    recomputeReactionP(atom, P18, N_OH_reaction_NH_O, N, OH);
    
    recomputeCollection(REACTION, atom, oldReactionRate);
    for (byte i= P5; i<=P18; i++) {
      recomputeCollection(i, atom, oldReactionRates[i]);
    }
  }
  
  private void recomputeReactionP(CatalysisAmmoniaSite atom, byte p, double rate, byte prod1, byte prod2) {
    if (atom.getType() == prod1) {
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisAmmoniaSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == prod2) {
          atom.addRate(REACTION, rate/2.0, i);
          atom.addRate(p, rate/2.0, i);
        }
      }
    }
    
    if (atom.getType() == prod2) {
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisAmmoniaSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == prod1) {
          atom.addRate(REACTION, rate/2.0, i);
          atom.addRate(p, rate/2.0, i);
        }
      }
    }
  }
  
  private void recomputeDiffusionProbability(CatalysisSite atom) {
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
      if (!neighbour.isOccupied() && neighbour.getLatticeSite() == CUS) {
        double probability = diffusionRates[atom.getType()];
        atom.addRate(DIFFUSION, probability, i);
      }
    }
    recomputeCollection(DIFFUSION, atom, oldDiffusionRate);
  }
}
