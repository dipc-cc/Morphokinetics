/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.psdAnalysis;

import basic.io.Restart;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import java.awt.geom.Point2D;
import java.util.concurrent.Semaphore;

import java.util.ArrayList;
import utils.MathUtils;

/**
 * Every surface is associated with a 2D map where each location represents a harmonic frequency and
 * the associated/displayed value represents the squared sum of the real and imaginary amplitudes
 * for that harmonic component (i.e., the power for that frequency). Low frequencies are correlated
 * to large structures, such as hills or valleys, while large frequencies are related to small
 * features, such as meandering steps. PSD maps are statistically equivalent for different images
 * obtained under the same experimental conditions, while the maps change significantly when the
 * conditions are modified. (From Ferrando, Gosalvez, Ayuela, JPC C, 2014, DOI: 10.1021/jp409812x)
 *
 * @author Nestor
 */
public class PsdSignature2D {

  private FloatFFT_2D fftCore;
  private float[][] psd;
  private ArrayList<float[][]> psdVector;
  private float[][] psdTmp;
  private float[][] buffer;
  private int measures;
  private Semaphore semaphore;
  private boolean averaged = false;
  private final int psdSizeY;
  private final int psdSizeX;
  private final int surfaceSizeY;
  private final int surfaceSizeX;
  private final int[] sizes;
  private final int dimensions;
  private Restart restart;
  public static final int HORIZONTAL_SYMMETRY = 0;
  public static final int VERTICAL_SYMMETRY = 1;

  public PsdSignature2D(int surfaceSizeY, int surfaceSizeX, double extent) {
    psdSizeY = (int) (surfaceSizeY * extent);
    psdSizeX = (int) (surfaceSizeX * extent);
    fftCore = new FloatFFT_2D(psdSizeY, psdSizeX);
    psd = new float[psdSizeY][psdSizeX];
    psdTmp = new float[psdSizeY][psdSizeX];
    buffer = new float[psdSizeY][psdSizeX * 2];
    measures = 0;
    semaphore = new Semaphore(1);
    psdVector = new ArrayList<>();
    this.surfaceSizeY = surfaceSizeY;
    this.surfaceSizeX = surfaceSizeX;
    sizes = new int[2];
    sizes[0] = psdSizeY;
    sizes[1] = psdSizeX;
    dimensions = 2;
    restart = new Restart();
  }

  public void addSurfaceSample(float[][] sampledSurface) {
    if (averaged) {
      throw new RuntimeException("PSD measures averaged, new samples cannot be added without signature reset.");
    }

    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      System.err.println("Thread interrupted while writting PSD signature");
    }

    for (int i = 0; i < sampledSurface.length; i++) {
      System.arraycopy(sampledSurface[i], 0, buffer[i], 0, sampledSurface[0].length);
    }

    // Do DFT (discrete Fourier Transfrom). [Equation 1 of Czifra Á. Sensitivity of PSD... 2009 (pp. 505-517). Springer].
    fftCore.realForwardFull(buffer);

    // Do the PSD. [Equation 2 of Czifra Á. Sensitivity of PSD... 2009 (pp. 505-517). Springer].
    for (int i = 0; i < psdSizeY; i++) {
      for (int j = 0; j < psdSizeX; j++) {
        psdTmp[i][j] = (buffer[i][j * 2] * buffer[i][j * 2] + buffer[i][j * 2 + 1] * buffer[i][j * 2 + 1]) / (surfaceSizeX * surfaceSizeY);
        psd[i][j] += psdTmp[i][j];
      }
    }
    psdVector.add(psdTmp);
    measures++;
    semaphore.release();

  }
  
  /**
   * Does the PSD of the given surface.
   * @param sampledSurface given surface
   * @return PSD 
   */
  public static float[][] doOnePsd(float[][] sampledSurface) {
    int sizeY = sampledSurface[0].length;
    int sizeX = sampledSurface.length;
    float[][] bufferTmp = new float[sizeY][sizeX * 2];
    float[][] result = new float[sizeY][sizeX];
    FloatFFT_2D fftCoreTmp = new FloatFFT_2D(sizeY, sizeX);

    for (int i = 0; i < sampledSurface.length; i++) {
      System.arraycopy(sampledSurface[i], 0, bufferTmp[i], 0, sizeY);
    } // Do DFT (discrete Fourier Transfrom). [Equation 1 of Czifra Á. Sensitivity of PSD... 2009 (pp. 505-517). Springer].
    fftCoreTmp.realForwardFull(bufferTmp);

    // Do the PSD. [Equation 2 of Czifra Á. Sensitivity of PSD... 2009 (pp. 505-517). Springer].
    for (int i = 0; i < sampledSurface[0].length; i++) {
      for (int j = 0; j < sampledSurface.length; j++) {
        result[i][j] = (bufferTmp[i][j * 2] * bufferTmp[i][j * 2] + bufferTmp[i][j * 2 + 1] * bufferTmp[i][j * 2 + 1]) / (sizeX * sizeY);
      }
    }
    return result;
  }
 
  public float[][] getPsd() {
    if (!averaged) {
      for (int i = 0; i < psdSizeY; i++) {
        for (int j = 0; j < psdSizeX; j++) {
          psd[i][j] /= measures;
        }
      }
      averaged = true;
    }
    return psd;
  }

  public void reset() {
    averaged = false;
    measures = 0;
    psd = new float[psdSizeY][psdSizeX];
  }

  /**
   * It does the average of two points (either vertically or horizontally) and assigns that value to
   * both points. Those two points are separated from the centre by the same amount and it assumes
   * that the centre is the Cartesian centre.
   *
   * @param symmetryType horizontal or vertical (HORIZONTAL_SYMMETRY or VERTICAL_SYMMETRY)
   */
  public void applySymmetryFold(int symmetryType) {
    switch (symmetryType) {
      case HORIZONTAL_SYMMETRY:
        for (int i = 0; i < psdSizeY; i++) {
          for (int j = 1; j < psdSizeX / 2; j++) {
            float temp = (psd[i][j] + psd[i][psdSizeX - j - 1]) * 0.5f;
            psd[i][j] = psd[i][psdSizeX - j - 1] = temp;
          }
        }
        break;

      case VERTICAL_SYMMETRY:
        for (int i = 1; i < psdSizeY / 2; i++) {
          for (int j = 0; j < psdSizeX; j++) {
            float temp = (psd[i][j] + psd[psdSizeY - i - 1][j]) * 0.5f;
            psd[i][j] = psd[psdSizeY - i - 1][j] = temp;
          }
        }
        break;
    }
  }

  /**
   * Writes the current PSD to a mko (binary) file. The file name will be given by the restart
   * folder and simulationNumber.
   *
   * @param simulationNumber the number of simulation
   */
  public void writePsdBinary(int simulationNumber) {
    restart.writePsdBinary(dimensions, sizes, psdVector.get(simulationNumber), simulationNumber);
  }
    
  /**
   * Writes the current PSD to a text file. The file name will be given by the restart
   * folder and simulationNumber.
   *
   * @param simulationNumber the number of simulation
   */
  public void writePsdText(int simulationNumber) {
    restart.writePsdText2D(dimensions, sizes, psdVector.get(simulationNumber), simulationNumber);
  }

  public void printAvgToFile(){
    restart.writePsdBinary(dimensions, sizes, MathUtils.avgFilter(getPsd(), 1), "psdAvgFil");
    restart.writePsdText2D(dimensions, sizes, MathUtils.avgFilter(getPsd(), 1), "psdAvgFil");
    restart.writePsdBinary(dimensions, sizes, getPsd(), "psdAvgRaw");
    restart.writePsdText2D(dimensions, sizes, getPsd(), "psdAvgRaw");
    
    float[] psd1D = new float[getPsd().length];
    Point2D centre = new Point2D.Double(getPsd().length / 2, getPsd()[0].length / 2);
     // Do the average per each radius from the centre, and create 1D PSD
    for (int i = 0; i < getPsd().length; i++) {
      for (int j = 0; j < getPsd()[0].length; j++) {
        int posX = (i + getPsd().length / 2) % getPsd().length;
        int posY = (j + getPsd()[0].length / 2) % getPsd()[0].length;
        Point2D point = new Point2D.Double((double) posX, (double) posY);
        int radius = (int) Math.ceil(centre.distance(point));
        psd1D[radius] += getPsd()[i][j];
      }
    }
    
    restart.writePsdText1D(psd1D, "psd1D");
  }
  
  public void setRestart(Restart restart) {
    this.restart = restart;
  }
}
