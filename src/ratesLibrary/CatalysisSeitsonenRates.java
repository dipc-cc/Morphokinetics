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
package ratesLibrary;

import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;

/**
 * F. Hess, A. Farkas, AP. Seitsonen, H. Over, J. Comput. Chem. 2011, 33, 757-766. DOI:
 * 10.1002/jcc.22902. Table 1.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisSeitsonenRates extends CatalysisRates {

  public CatalysisSeitsonenRates(float temperature) {
    super(temperature);
    setCorrectionFactor(1.0);
    
    double[] desorptionEnergiesCo = new double[2];
    desorptionEnergiesCo[BR] = 1.85;
    desorptionEnergiesCo[CUS] = 1.32;
    setDesorptionEnergiesCo(desorptionEnergiesCo);
  
    double[][] desorptionEnergiesO2 = new double[2][2];
    desorptionEnergiesO2[BR][BR]  = 4.82;
    desorptionEnergiesO2[BR][CUS] = 3.3;
    desorptionEnergiesO2[CUS][BR] = 3.3;
    desorptionEnergiesO2[CUS][CUS] = 1.78;
    setDesorptionEnergiesO2(desorptionEnergiesO2);
    
    double[][] reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 1.4; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 0.6; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 0.74; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 0.71; // CO is in CUS and O in CUS
    setReactionEnergiesCoO(reactionEnergiesCoO);
    
    double[][][] diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = 0.7;
    diffusionEnergies[CO][BR][CUS] = 2.06;
    diffusionEnergies[CO][CUS][BR] = 1.4;
    diffusionEnergies[CO][CUS][CUS] = 1.57;
    diffusionEnergies[O][BR][BR] = 0.9;
    diffusionEnergies[O][BR][CUS] = 1.97;
    diffusionEnergies[O][CUS][BR] = 0.7;
    diffusionEnergies[O][CUS][CUS] = 1.53;
    setDiffusionEnergies(diffusionEnergies);
  }
}
