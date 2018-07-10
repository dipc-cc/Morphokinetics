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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class MathUtils {

  public static int mod(int x, int m) {
    int r = x%m;
    return r<0 ? r+m : r;
  }
  
  public static double truncate(double value, int decimals) {
    int desp = 1;
    for (int i = 0; i < decimals; i++) {
      desp *= 10;
    }
    long temp = Math.round(value * desp);
    return (((double) temp) / desp);
  }

  public static void fillSurfaceHoles(float[][] surface) {
    boolean holesRemaining = true;
    while (holesRemaining) {
      holesRemaining = false;
      for (int i = 0; i < surface.length; i++) {
        for (int j = 0; j < surface[0].length; j++) {
          if (surface[i][j] == 0) {
            int cont = 0;
            if (i > 0 && surface[i - 1][j] > 0) {
              cont++;
              surface[i][j] += surface[i - 1][j];
            }
            if (j > 0 && surface[i][j - 1] > 0) {
              cont++;
              surface[i][j] += surface[i][j - 1];
            }
            if (i < surface.length - 1 && surface[i + 1][j] > 0) {
              cont++;
              surface[i][j] += surface[i + 1][j];
            }
            if (j < surface[0].length - 1 && surface[i][j + 1] > 0) {
              cont++;
              surface[i][j] += surface[i][j + 1];
            }
            if (cont == 0) {
              holesRemaining = true;
            } else {
              surface[i][j] /= cont;
            }
          }
        }
      }
    }
  }

  /**
   * Creates tents for the surface. Thus, will create more sinusoidal like input for the PSD, which
   * will easier identify the frequencies of those tents.
   *
   * @param surface island surface
   */
  public static void applyGrowthAccordingDistanceToPerimeter(float[][] surface) {
    int modified;
    int currentHeight = 0;
    int maxI = surface.length;
    int maxJ = surface[0].length;
    int maxHeight = 1000;
    do {
      modified = 0;
      for (int i = 0; i < maxI; i++) {
        for (int j = 0; j < maxJ; j++) {
          if (surface[getIndex(i + 1, maxI)][j] < currentHeight) {
            continue;
          }
          if (surface[getIndex(i - 1, maxI)][j] < currentHeight) {
            continue;
          }
          if (surface[i][getIndex(j - 1, maxJ)] < currentHeight) {
            continue;
          }
          if (surface[i][getIndex(j + 1, maxJ)] < currentHeight) {
            continue;
          }
          surface[i][j] = currentHeight + 1;
          modified++;
        }
      }
      currentHeight++;
    } while (modified > 0 && currentHeight < maxHeight);
  }
    
  public static void planeSurface(float[][] surface) {
    int maxI = surface.length;
    int maxJ = surface[0].length;
    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        if (surface[i][j] > -1) {
          surface[i][j] = 0;
        }
      }
    }
  } 
  
  /**
   * Normalises the surface, to be able to easily compare surfaces with different tent heights.
   * @param surface 
   */
  public static void normalise(float[][] surface) {
    int binX = surface.length;
    int binY = surface[0].length;
    float max = 0;
    float sum = 0;
        
    // Get the sum and max
    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        sum += surface[i][j];
        if (max < surface[i][j]) {
          max = surface[i][j];
        }
      }
    }
    
    // Calculate mean and update max
    float mean = sum / (binX * binY);
    max = max - mean; 
    // Substract mean height and normalise the height field
    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        surface[i][j] = (surface[i][j] - mean) / max;
      }
    }
  }
    

  private static int getIndex(int x, int maxX) {
    if (x == maxX) {
      return 0;
    }
    if (x < 0) {
      return maxX - 1;
    }
    return x;
  }
  
  public static float[][] avgFilter(float[][] surface, int radio) {
    int binY = surface.length;
    int binX = surface[0].length;
    float[][] filtered = new float[binY][binX];

    for (int a = 0; a < binY; a++) {
      for (int b = 0; b < binX; b++) {
        int cont = 0;
        filtered[a][b] = 0;
        for (int c = a - radio; c <= a + radio; c++) {
          for (int d = b - radio; d <= b + radio; d++) {

            int row = c;
            int col = d;

            if (row < 0) {
              row += binY;
            }
            if (row >= binY) {
              row -= binY;
            }
            if (col < 0) {
              col += binX;
            }
            if (col >= binX) {
              col -= binX;
            }

            if (row == 0 && col == 0) {
              continue;
            }
            filtered[a][b] += surface[row][col];
            cont++;
          }
        }
        filtered[a][b] /= cont;
      }
    }
    return filtered;
  }
  
  /**
   * Adds an empty area, maintaining the island size. It is useful to compare different size
   * simulations.
   *
   * Converts this island:
   * <pre> {@code 
:::::::::::::::::::::::::
:::::::::::::::::::::::::
::::@::@.::::::::::::::::
::::`@@@:::@@@:::::::::::
:::::;@@@:::@';::::::;@::
::::::,@@@;;.@+;:#`@@@,::
::::::::+@@@ +@:.@@';@@':
:::::::::+@@@@@@@#@:::..:
:::::::::+ ,@'+# @@+:::::
::::::::::@@@@,+.::::::::
:::::::::::@+#:::::::::::
::::::::;@@@@::::::::::::
::::::::::;@@::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
} </pre>
    into this filled one:
    * <pre> {@code
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::@::::::::::::::::::::::::::::::
::::::::::::::::,@:;@:::::::::::::::::::::::::::::
:::::::::::::::::'@@.:::+@::::::::,.::::::::::::::
:::::::::::::::::::@@@@::@+@:::@@@@@::::::::::::::
::::::::::::::::::::'@@@+ @.:@@@@'@.::::::::::::::
::::::::::::::::::::::@@@@@;@@@:::#@::::::::::::::
:::::::::::::::::::::.@ @@@@ @@@::::::::::::::::::
::::::::::::::::::::::#@@@:@@::;::::::::::::::::::
::::::::::::::::::::::::@@::::::::::::::::::::::::
::::::::::::::::::::.@+@@:::::::::::::::::::::::::
:::::::::::::::::::::'@@@,::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
  }</pre>
   * @param inputArea input area
   * @param scale how much is going to increase (must be > 1)
   * @return output bigger area
   */
  public static float[][] increaseEmptyArea(float[][] inputArea, double scale){
    // ensure that scale is bigger than one
    if (scale <= 1 ) {
      return inputArea;
    }
    
    int sizeX = inputArea.length;
    int sizeY = inputArea[0].length;
    float[][] outputArea = new float[(int) (sizeX * scale)][(int) (sizeY * scale)];
    for (int x = 0; x < outputArea.length; x++) {
      for (int y = 0; y < outputArea[0].length; y++) {
        outputArea[x][y] = -1;
      }
    }
    int padX = (int) ((outputArea.length - sizeX) / 2);
    int padY = (int) ((outputArea[0].length - sizeY) / 2);
    for (int x = 0; x < sizeX; x++) {
      System.arraycopy(inputArea[x], 0, outputArea[x + padX], padY, sizeY);
    }
    return outputArea;
  }

  /**
   * Given an original surface it reduces by the given factor.
   *
   * @param originalSurface
   * @param factor
   * @return reduced surface
   */
  public static float[][] scale(float[][] originalSurface, int factor) {
    double scale = (double) 1 / (double) factor;
    return scale(originalSurface, scale);
  }
  
  /**
   * See {@link #scale(float[][], int) }
   * 
   * @param originalSurface
   * @param scale
   * @return 
   */
  public static float[][] scale(float[][] originalSurface, double scale) {
    int originalSizeX = originalSurface.length;
    int originalSizeY = originalSurface[0].length;
    int reducedSizeX = (int) Math.ceil(originalSizeX * scale); // This is required to ensure that data properly fits afterwards
    int reducedSizeY = (int) Math.ceil(originalSizeY * scale);

    float[][] reducedSurface = new float[reducedSizeX][reducedSizeY];
    if (scale > 1) {
      System.err.println("Error:scale must be less or equal to 1.");
      return originalSurface;
    }
    // This is really simple interpolation; last position is visited, this value is assigned
    for (int x = 0; x < originalSizeX; x++) {
      for (int y = 0; y < originalSizeY; y++) {
        int reducedX = (int) (x * scale);
        int reducedY = (int) (y * scale);
        reducedSurface[reducedX][reducedY] = originalSurface[x][y];
      }
    }
    return reducedSurface;
  }

  
  public static int[] rotateAngle(int x, int y, double angle) {
    double[] result = rotateAngle((double) x, (double) y, angle);
    return new int[]{(int) round(result[0]), (int) round(result[1])};
  }
 
  public static double[] rotateAngle(double x, double y, double angle) {
    angle = -angle * 2 * PI / 360;
    double xRotated = x * cos(angle) - y * sin(angle);
    y = x * sin(angle) + y * cos(angle);
    return new double[]{xRotated, y};
  }
}
