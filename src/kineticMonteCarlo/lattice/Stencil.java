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
  private int size;
  private int direction;
  private boolean rotated;
  private int shifted;
  private boolean add;
  private int sign;

  private int xIndex;
  private int yIndex;
  private int iIndex;
  private final int latticeSizeX;
  private final int latticeSizeY;
  private final int[] index;

  private int[] centre;
  
  private static final int[] TOP = {0, -1};
  private static final int[] RIGHT = {1, 0};
  private static final int[] BOTTOM = {0, 1};
  private static final int[] LEFT = {-1, 0};
  private static final int[][] MOVE = {TOP, RIGHT, BOTTOM, LEFT};
  /** [TYPE (shifted or not)][side (bottom...)][i,j].*/
  private final int[][][][] stencil;
  
  public Stencil(int x, int y) {
    latticeSizeX = x;
    latticeSizeY = y;
    index = new int[2];
    centre = new int[]{x, y};
    // not shifted
    int[][] top = {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {2, 0}};
    int[][] right = {{2, 0}, {2, 1}};
    int[][] bottom = {{-2, 1}, {-1, 1}, {0, 1}, {1, 1}, {2, 1}};
    int[][] left = {{-2, 0}, {-2, 1}};
    int[][][] stencil1 = new int[][][]{top, right, bottom, left};
    top = new int[][]{{-2, 0}, {-1, 0}, {0, -1}, {1, -1}, {2, -1}};
    right = new int[][]{{2, 0}, {2, -1}, {0, 1}};
    bottom = new int[][]{{-2, 1}, {-1, 1}, {0, 1}, {1, 0}, {2, 0}};
    left = new int[][]{{-2, 0}, {-2, 1}, {0, -1}};
    int[][][] stencil2 = new int[][][]{top, right, bottom, left};
    stencil = new int[][][][]{stencil1, stencil2};
  }

  /**
   * Init the iteration for a diffusion check.
   * 
   * @param x
   * @param y
   * @param direction 
   * @param rotated 
   * @param shifted 
   */
  public void init(int x, int y, int direction, boolean rotated, int shifted) {
    xIndex = x;
    yIndex = y;
    centre = new int[]{x, y};
    this.direction = direction;
    this.rotated = rotated;
    this.shifted = shifted;
    add = true;
    iIndex = 0;
  }
  
  /**
   * 
   * @param x
   * @param y
   * @param direction direction of the movement.
   * @param add if true, those sites must become unavailable.
   * @param far Ag lattice positions for far sites (to control the adsorption).
   * @param rotated if the molecule is 90º rotated.
   * @param shifted if the molecule is 22.5º rotated.
   */
  public void init(int x, int y, int direction, boolean add, boolean far, boolean rotated, int shifted) {
    xIndex = x;
    yIndex = y;
    centre = new int[]{x, y};
    iIndex = 0;
    this.direction = direction;
    this.rotated = rotated;
    this.shifted = shifted;
    this.add = add;
    if (far) {// far sites
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
  
  public boolean hasNext(){
    int dir = direction;
    if (rotated) {
      dir = (dir + 1) % 4;
    }
    return iIndex < stencil[shifted][dir].length;
  }
  
  public int[] getNextIndexClose() {
    int[] move = {0, 0};
    int dir = direction;
    if (add) {
      move = MOVE[direction];
    } else {
      dir = (direction + 2) % 4; // remove the opposite side
    }
    int[] pos;
    if (rotated) { // rotate 90º the stencil
      dir = (dir + 1) % 4;
      pos = stencil[shifted][dir][iIndex];
      pos = rotateAngle(pos[0], pos[1], 90);
    } else {
      pos = stencil[shifted][dir][iIndex];
    }
  
    index[0] = getXIndex(centre[0] + pos[0] + move[0]);
    index[1] = getYIndex(centre[1] + pos[1] + move[1]);

    iIndex++;
    return index;
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
  
  int getXIndex(int x) {
    if (x < 0) {
      return latticeSizeX + x;
    }
    if (x >= latticeSizeX) {
      return x % latticeSizeX;
    }
    return x;
  }
  
  int getYIndex(int y) {
    if (y < 0) {
      return latticeSizeY + y;
    }
    if (y >= latticeSizeY) {
      return y % latticeSizeY;
    }
    return y;
  }
}
