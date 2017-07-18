package kineticMonteCarlo.kmcCore.catalysis;

import basic.Parser;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import ratesLibrary.CatalysisRates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisFarkasKmc extends CatalysisKmc {
  
  
  private double[] desorptionRateCoCusCoCus; // Only for Farkas
  private double[] reactionRateCoOCoCusCoCus; // Only for Farkas
  private double[] diffusionRateCoCusCoCus; // Only for Farkas
  
  public CatalysisFarkasKmc(Parser parser) {
    super(parser);
  }
  
  @Override
  public void setRates(CatalysisRates rates) {
    super.setRates(rates);
    if (doDesorption) {
      desorptionRateCoCusCoCus = rates.getDesorptionRates();
    }
    if (doReaction) {
      reactionRateCoOCoCusCoCus = rates.getReactionRates(true);
    }
    if (doDiffusion) {
      diffusionRateCoCusCoCus = rates.getDiffusionRates();
    }

    //activationEnergy.setRates(processProbs2D);
  }
  
  @Override
  double getDesorptionProbability(CatalysisAtom atom) {
    double rate = desorptionRateCOPerSite[atom.getLatticeSite()];
    if (atom.getLatticeSite() == CUS) {
      if (atom.getCoCusNeighbours() > 0) {
        rate = desorptionRateCoCusCoCus[atom.getCoCusNeighbours() - 1];
      }
    }
    return rate;
  }

  /**
   * One atom is O and the other CO, for sure.
   *
   * @param atom
   * @param neighbour
   * @return
   */
  @Override
  double getReactionProbability(CatalysisAtom atom, CatalysisAtom neighbour) {
    int index;
    CatalysisAtom atomCo;
    CatalysisAtom atomO;
    if (atom.getType() == CO) {
      atomCo = atom;
      atomO = neighbour;
    } else {
      atomCo = neighbour;
      atomO = atom;
    }
    double probability;
    if (atomCo.getCoCusNeighbours() > 0) { // repulsion
      probability = reactionRateCoOCoCusCoCus[atomCo.getCoCusNeighbours() - 1];
    } else {
      index = 2 * atomCo.getLatticeSite() + atomO.getLatticeSite();
      probability = reactionRateCoO[index];
    }
    return probability;
  }

  @Override
  double getDiffusionProbability(CatalysisAtom atom, CatalysisAtom neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    double probability;
    if (atom.getType() == CO) {
      probability = diffusionRateCO[index];
      if (atom.getCoCusNeighbours() > 0) { // repulsion
        probability = diffusionRateCoCusCoCus[atom.getCoCusNeighbours() - 1];
      }
    } else {
      probability = diffusionRateO[index];
    }
    return probability;
  }
}
