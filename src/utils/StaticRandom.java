/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package utils;

import utils.edu.cornell.lassp.houle.rngPack.Ranecu;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class StaticRandom {

  private static Ranecu rand;

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

  }

  public static double raw() {
    return rand.raw();
  }

  /**
   * Returns an integer random number between 0 (inclusive) and given number (exclusive)
   *
   * @param max maximum integer to be considered
   * @return 0 <= result < max
   */
  public static int rawInteger(int max) {
    return (int) (rand.raw() * max);
  }

}
