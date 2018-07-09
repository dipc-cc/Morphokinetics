/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.process;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaProcess extends AbstractProcess {
  
  public static final byte ADSORPTION = 0;
  public static final byte DESORPTION = 1;
  public static final byte REACTION = 2;
  public static final byte DIFFUSION = 3;
  public static final byte ROTATION = 4;
  public static final byte TRANSFORM = 5;
  public static final byte SHIFT = 6;
  
  public BdaProcess() {
    super(6);
  }
  
}
