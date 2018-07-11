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
package kineticMonteCarlo.lattice;

import static utils.MathUtils.rotateAngle;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Stencil {
  private int init;
  private int end;
  private int size;
  private int movingIndex; //either x or y
  private int direction;
  private boolean rotated;
  private boolean far;
  private int sign;

  private int xIndex;
  private int yIndex;
  private int iIndex;
  private final int latticeSizeX;
  private final int latticeSizeY;
  private int[] index;

  private int[] initFixed;
  
  public Stencil(int x, int y) {
    latticeSizeX = x;
    latticeSizeY = y;
    index = new int[2]; 
  }

  /**
   * 
   * @param x
   * @param y
   * @param direction direction of the movement.
   * @param add if true, those sites must become unavailable.
   * @param far Ag lattice positions for far sites (to control the adsorption).
   * @param rotated if the molecule is 90º rotated.
   */
  public void init(int x, int y, int direction, boolean add, boolean far, boolean rotated) {
    xIndex = x;
    yIndex = y;
    iIndex = 0;
    this.direction = direction;
    this.rotated = rotated;
    this.far = far;
    if (!far) {
      if (add){
        initFixed = new int[]{-1, 3, 2, -3};
      } else {
        initFixed = new int[]{1, -2, 0, 2};
      }
      // sets fixed index
      if (direction % 2 == 0) { // x travelling
        if (!rotated) {
          index[1] = getYIndex(y + initFixed[direction]); // y fixed
          initLargeSide();
        } else {
          int[] fixed = rotateAngle(initFixed[direction + 1], 0, 90);
          index[1] = getYIndex(y + fixed[1]); // y fixed
          initSmallSide();
        }
      } else { // y travelling
        if (!rotated) {
          index[0] = getXIndex(x + initFixed[direction]); // x fixed
          initSmallSide();
        } else {
          int[] fixed = rotateAngle(0, initFixed[(direction + 1) % 4], 90);
          index[0] = getXIndex(x + fixed[0]); // x fixed
          initLargeSide();
        }
      }
    } else {// far sites
      sign = direction % 3 == 0 ? 1 : -1;
      sign = add ? -sign : sign;
      int distance = add ? 5 : 4;
      size = 9;
      init = -4;
      // sets fixed index
      if (direction % 2 == 0) { // x travelling
        index[1] = getYIndex(y + sign * distance); // y fixed
      } else { // y travelling
        index[0] = getXIndex(x + sign * distance); // x fixed
      }
    }
  }
  
  public int size() {
    return size;
  }
  
  public int[] getNextIndex() {
    if (direction % 2 == 0) {
      index[0] = getXIndex(xIndex + init + iIndex);
    } else {
      index[1] = getYIndex(yIndex + init + iIndex);
    }

    iIndex++;
    return index;
  }  
  
  private int getXIndex(int x) {
    if (x < 0) {
      return latticeSizeX + x;
    }
    if (x >= latticeSizeX) {
      return x % latticeSizeX;
    }
    return x;
  }
  
  private int getYIndex(int y) {
    if (y < 0) {
      return latticeSizeY + y;
    }
    if (y >= latticeSizeY) {
      return y % latticeSizeY;
    }
    return y;
  }
  
  private void initSmallSide() {
    init = 0;
    end = 1;
    size = 2;
  }
  
  private void initLargeSide() {
    init = -2;
    end = 2;
    size = 5;
  }
}
