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
 * Farkas, Hess, Over del J Phys. Chem C (2011). Table 1.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisFarkasRates extends CatalysisRates {

  public CatalysisFarkasRates(int temperature) {
    super(temperature);
    
    double kJeV = 1.04e-2;
    
    double[] desorptionEnergiesCo = new double[2];
    desorptionEnergiesCo[BR] = 193 * kJeV;
    desorptionEnergiesCo[CUS] = 129 * kJeV;
    setDesorptionEnergiesCo(desorptionEnergiesCo);
  
    double[][] desorptionEnergiesO2 = new double[2][2];
    desorptionEnergiesO2[BR][BR]  = 414 * kJeV;
    desorptionEnergiesO2[BR][CUS] = 219 * kJeV; 
    desorptionEnergiesO2[CUS][BR] = 219 * kJeV;
    desorptionEnergiesO2[CUS][CUS] = 168 * kJeV;
    setDesorptionEnergiesO2(desorptionEnergiesO2);
    
    double[][] reactionEnergiesCoO = new double[2][2];
    reactionEnergiesCoO[BR][BR] = 133 * kJeV; // CO is in bridge and O in bridge
    reactionEnergiesCoO[BR][CUS] = 91 * kJeV; // CO is in bridge and O in CUS
    reactionEnergiesCoO[CUS][BR] = 89 * kJeV; // CO is in CUS and O in bridge
    reactionEnergiesCoO[CUS][CUS]= 89 * kJeV; // CO is in CUS and O in CUS
    setReactionEnergiesCoO(reactionEnergiesCoO);
    
    double[][][] diffusionEnergies = new double[2][2][2];
    diffusionEnergies[CO][BR][BR] = 87 * kJeV;
    diffusionEnergies[CO][BR][CUS] = 122 * kJeV;
    diffusionEnergies[CO][CUS][BR] = 58 * kJeV;
    diffusionEnergies[CO][CUS][CUS] = 106 * kJeV;
    diffusionEnergies[O][BR][BR] = 87 * kJeV;
    diffusionEnergies[O][BR][CUS] = 191 * kJeV;
    diffusionEnergies[O][CUS][BR] = 68 * kJeV;
    diffusionEnergies[O][CUS][CUS] = 106 * kJeV;
    setDiffusionEnergies(diffusionEnergies);
  }
}
