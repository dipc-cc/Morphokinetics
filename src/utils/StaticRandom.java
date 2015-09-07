/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import utils.edu.cornell.lassp.houle.rngPack.Ranecu;

/**
 *
 * @author Nestor
 */
public class StaticRandom {

  private static Ranecu rand;

  public StaticRandom() {
    rand = new Ranecu(System.nanoTime());
  }
  
  public static double raw() {

    return rand.raw();

  }

  public static int rawInteger(int max) {

    return (int) (rand.raw() * max);
  }

}
