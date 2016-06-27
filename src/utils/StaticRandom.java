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
  private static double[] randomNumbersVector;
  private static int currentPosition;
  private static int limit;

  public StaticRandom() {
    rand = new Ranecu(System.nanoTime());
  }

  public StaticRandom(boolean randomSeed) {
    if (randomSeed) {
      rand = new Ranecu(System.nanoTime());
    } else {
      // for testing purposes
      rand = new Ranecu(1234512345, 678967890); // Joseba: To create allways the same "Random" numbers
    }
    limit = 10000000;// 10M
    initVector();
  }

  public static double raw() {
    //return rand.raw();/*
    if (currentPosition < limit - 1) {
      currentPosition++;
      return randomNumbersVector[currentPosition];
    } else {
      initVector();
      return randomNumbersVector[currentPosition];
    }//*/
  }

  /**
   * Returns an integer random number between 0 (inclusive) and given number (exclusive)
   *
   * @param max maximum integer to be considered
   * @return 0 <= result < max
   */
  public static int rawInteger(int max) {
    return (int) (raw() * max);
  }

  private static void initVector() {
    randomNumbersVector = new double[limit];
    rand.raw(randomNumbersVector, limit);
    currentPosition = 0;
  }
    
}
