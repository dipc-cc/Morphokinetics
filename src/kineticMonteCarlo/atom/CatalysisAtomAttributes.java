/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAtomAttributes extends AbstractGrowthAtomAttributes{
  
  /**
   * CO or O.
   */
  private byte typeOfAtom;
  private double adsorptionProbability;
  
  public byte getType() {
    return typeOfAtom;
  }
  
  public void setType(byte newType) {
    typeOfAtom = newType;
  }
  
  public double getAdsorptionProbability() {
    return adsorptionProbability;
  }

  public void setAdsorptionProbability(double adsorptionProbability) {
    this.adsorptionProbability = adsorptionProbability;
  }
}
