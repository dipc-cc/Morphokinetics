/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.process;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedProcess extends AbstractProcess {
  
  public static final byte ADSORB = 0;
  public static final byte SINGLE = 1;
  public static final byte CONCERTED = 2;
  public static final byte MULTI = 3;

  public ConcertedProcess() {
    super(6);
  }
}
