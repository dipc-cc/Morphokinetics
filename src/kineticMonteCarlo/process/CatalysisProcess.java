/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.process;

/**
 * Class to store a process of catalysis. Possible processes are adsorption, desorption, reaction
 * and diffusion.
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisProcess extends AbstractProcess {

  public static final byte ADSORPTION = 0;
  public static final byte DESORPTION = 1;
  public static final byte REACTION = 2;
  public static final byte DIFFUSION = 3;
  
  public CatalysisProcess() {
    super(4);
  }
}
