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

import static kineticMonteCarlo.site.AbstractCatalysisSite.BR;
import static kineticMonteCarlo.site.AbstractCatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisCoSite.O;
import static kineticMonteCarlo.site.CatalysisCoSite.CO;


/**
 * F. Hess, A. Farkas, AP. Seitsonen, H. Over, J. Comput. Chem. 2011, 33, 757-766. DOI:
 * 10.1002/jcc.22902. Table 1.
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisReuterOverRates extends CatalysisRates {
  
  public CatalysisReuterOverRates(float temperature) {
    super(temperature);
    setCorrectionFactor(1.0);
    
    double[] desorptionEnergiesCo = new double[2];
    desorptionEnergiesCo[BR] = 1.6;
    desorptionEnergiesCo[CUS] = 1.3;
    setDesorptionEnergiesCo(desorptionEnergiesCo);
  
    double[][] desorptionEnergiesO2 = new double[2][2];
    desorptionEnergiesO2[BR][BR]  = 4.6;
    desorptionEnergiesO2[BR][CUS] = 3.3;
    desorptionEnergiesO2[CUS][BR] = 3.3;
    desorptionEnergiesO2[CUS][CUS] = 2.0;
    setDesorptionEnergiesO2(desorptionEnergiesO2);
    
    double[][] reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 1.54; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 0.76; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 1.25; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 0.89; // CO is in CUS and O in CUS
    setReactionEnergiesCoO(reactionEnergiesCoO);
    
    double[][][] diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = 0.6;
    diffusionEnergies[CO][BR][CUS] = 1.6;
    diffusionEnergies[CO][CUS][BR] = 1.3;
    diffusionEnergies[CO][CUS][CUS] = 1.7;
    diffusionEnergies[O][BR][BR] = 0.7;
    diffusionEnergies[O][BR][CUS] = 2.3;
    diffusionEnergies[O][CUS][BR] = 1.0;
    diffusionEnergies[O][CUS][CUS] = 1.6;
    setDiffusionEnergies(diffusionEnergies);
  }
}
