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
import kineticMonteCarlo.site.AbstractCatalysisSite;
import static kineticMonteCarlo.site.CatalysisCoSite.CO;
import static kineticMonteCarlo.site.AbstractCatalysisSite.CUS;
import ratesLibrary.CatalysisRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisFarkasKmc extends CatalysisCoKmc {
  
  
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
  double getDesorptionRate(AbstractCatalysisSite atom) {
    double rate = super.getDesorptionRate(atom);
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
  double getReactionRate(AbstractCatalysisSite atom, AbstractCatalysisSite neighbour) {
    AbstractCatalysisSite atomCo;
    if (atom.getType() == CO) {
      atomCo = atom;
    } else {
      atomCo = neighbour;
    }
    double rate;
    if (atomCo.getCoCusNeighbours() > 0) { // repulsion
      rate = reactionRateCoOCoCusCoCus[atomCo.getCoCusNeighbours() - 1];
    } else {
      rate = super.getReactionRate(atom, neighbour);
    }
    return rate;
  }

  @Override
  double getDiffusionRate(AbstractCatalysisSite atom, AbstractCatalysisSite neighbour) {
    if (atom.getType() == CO) {
      if (atom.getCoCusNeighbours() == 1) { // repulsion
        // C -> B, with one CO neighbour
        // C -> C, with one CO neighbour
        return diffusionRateCoCusCoCus[neighbour.getLatticeSite()];
      }
      if (atom.getCoCusNeighbours() == 2) // repulsion
        // C -> B, with two CO neighbours
        return diffusionRateCoCusCoCus[2];
    } 
    return super.getDiffusionRate(atom, neighbour);
  }
}
