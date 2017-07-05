/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class CatalysisKiejnaRates extends CatalysisRates {
  
  public CatalysisKiejnaRates(int temperature) {
    super(temperature);
    
    double[] desorptionEnergiesCo = new double[2];
    desorptionEnergiesCo[BR] = 1.69;
    desorptionEnergiesCo[CUS] = 1.31;
    setDesorptionEnergiesCo(desorptionEnergiesCo);
  
    double[][] desorptionEnergiesO2 = new double[2][2];
    desorptionEnergiesO2[BR][BR]  = 4.66;
    desorptionEnergiesO2[BR][CUS] = 3.16; // TODO: It is not in the article
    desorptionEnergiesO2[CUS][BR] = 3.16; // TODO: It is not in the article
    desorptionEnergiesO2[CUS][CUS] = 1.72;
    setDesorptionEnergiesO2(desorptionEnergiesO2);
    
    double[][] reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 1.48; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 0.61; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 0.99; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 0.78; // CO is in CUS and O in CUS
    setReactionEnergiesCoO(reactionEnergiesCoO);
    
    double[][][] diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = 0.6;
    diffusionEnergies[CO][BR][CUS] = 1.6; // TODO: It is not in the article, but it is correct
    diffusionEnergies[CO][CUS][BR] = 1.3;
    diffusionEnergies[CO][CUS][CUS] = 1.7;
    diffusionEnergies[O][BR][BR] = 0.7;
    diffusionEnergies[O][BR][CUS] = 2.3; // TODO: It is not in the article, but it is correct
    diffusionEnergies[O][CUS][BR] = 1.0;
    diffusionEnergies[O][CUS][CUS] = 1.6;
    setDiffusionEnergies(diffusionEnergies);
  }
}
