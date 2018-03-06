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
package kineticMonteCarlo.atom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisSiteAttributes extends AbstractGrowthAtomAttributes{
  
  /**
   * CO or O.
   */
  private byte typeOfAtom;
  
  public byte getType() {
    return typeOfAtom;
  }
  
  public void setType(byte newType) {
    typeOfAtom = newType;
  }
}
