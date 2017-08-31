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
public class CatalysisSeitsonenRates extends CatalysisRates {

  public CatalysisSeitsonenRates(float temperature) {
    super(temperature);
    
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
