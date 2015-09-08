/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Nestor
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

  public static void applyGrowthAccordingDistanceToPerimeter(float[][] surface) {

    int modified;
    int currentHeight = 0;
    do {
      modified = 0;
      for (int i = 1; i < surface.length - 1; i++) {
        for (int j = 1; j < surface[0].length - 1; j++) {
          if (surface[i + 1][j] < currentHeight) {
            continue;
          }
          if (surface[i - 1][j] < currentHeight) {
            continue;
          }
          if (surface[i][j - 1] < currentHeight) {
            continue;
          }
          if (surface[i][j + 1] < currentHeight) {
            continue;
          }
          surface[i][j] = currentHeight + 1;
          modified++;
        }
      }
      currentHeight++;
    } while (modified > 0);
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
}
