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
  
  public StaticRandom(boolean randomSeed){
     if (randomSeed) {
      rand = new Ranecu(System.nanoTime());
    } else {
      // for testing purposes
      rand = new Ranecu(1234512345,678967890); // Joseba: To create allways the same "Randoom" numbers
    }
  }
  
  public static double raw() {

    return rand.raw();

  }

  public static int rawInteger(int max) {

    return (int) (rand.raw() * max);
  }

}
