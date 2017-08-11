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
  
  public CatalysisFarkasKmc(Parser parser, String restartFolder) {
    super(parser, restartFolder);
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
  double getDesorptionRate(CatalysisAtom atom) {
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
  double getReactionRate(CatalysisAtom atom, CatalysisAtom neighbour) {
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
    double rate;
    if (atomCo.getCoCusNeighbours() > 0) { // repulsion
      rate = reactionRateCoOCoCusCoCus[atomCo.getCoCusNeighbours() - 1];
    } else {
      index = 2 * atomCo.getLatticeSite() + atomO.getLatticeSite();
      rate = reactionRateCoO[index];
    }
    return rate;
  }

  @Override
  double getDiffusionRate(CatalysisAtom atom, CatalysisAtom neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    double rate;
    if (atom.getType() == CO) {
      rate = diffusionRateCO[index];
      if (atom.getCoCusNeighbours() == 1) { // repulsion
        // C -> B, with one CO neighbour
        // C -> C, with one CO neighbour
        rate = diffusionRateCoCusCoCus[neighbour.getLatticeSite()];
      }
      if (atom.getCoCusNeighbours() == 2) // repulsion
        // C -> B, with two CO neighbours
        rate = diffusionRateCoCusCoCus[2];
    } else {
      rate = diffusionRateO[index];
    }
    return rate;
  }
}
