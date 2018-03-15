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
import kineticMonteCarlo.lattice.CatalysisCoFarkasLattice;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import ratesLibrary.CatalysisRates;

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
  void init(Parser parser) {
    CatalysisLattice catalysisLattice = new CatalysisCoFarkasLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), parser.getRatesLibrary());
    catalysisLattice.init();
    setLattice(catalysisLattice);
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
  double getDesorptionRate(CatalysisSite atom) {
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
  double getReactionRate(CatalysisSite atom, CatalysisSite neighbour) {
    int index;
    CatalysisSite atomCo;
    CatalysisSite atomO;
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
  double getDiffusionRate(CatalysisSite atom, CatalysisSite neighbour) {
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
