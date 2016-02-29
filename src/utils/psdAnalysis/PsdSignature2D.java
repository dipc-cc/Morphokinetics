/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.psdAnalysis;

import basic.io.Restart;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
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
  private final int binsY;
  private final int binsX;
  private final int[] sizes;
  private final int dimensions;
  private Restart restart;
  public static final int HORIZONTAL_SYMMETRY = 0;
  public static final int VERTICAL_SYMMETRY = 1;

  public PsdSignature2D(int binsY, int binsX) {

    fftCore = new FloatFFT_2D(binsY, binsX);
    psd = new float[binsY][binsX];
    psdTmp = new float[binsY][binsX];
    buffer = new float[binsY][binsX * 2];
    measures = 0;
    semaphore = new Semaphore(1);
    psdVector = new ArrayList<>();
    this.binsY = binsY;
    this.binsX = binsX;
    sizes = new int[2];
    sizes[0] = binsY;
    sizes[1] = binsX;
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

    fftCore.realForwardFull(buffer);

    for (int i = 0; i < binsY; i++) {
      for (int j = 0; j < binsX; j++) {
        psdTmp[i][j] = buffer[i][j * 2] * buffer[i][j * 2] + buffer[i][j * 2 + 1] * buffer[i][j * 2 + 1];
        psd[i][j] += psdTmp[i][j];
      }
    }
    psdVector.add(psdTmp);
    measures++;
    semaphore.release();

  }

  public float[][] getPsd() {

    if (!averaged) {
      for (int i = 0; i < binsY; i++) {
        for (int j = 0; j < binsX; j++) {
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
    psd = new float[binsY][binsX];
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
        for (int i = 0; i < binsY; i++) {
          for (int j = 1; j < binsX / 2; j++) {
            float temp = (psd[i][j] + psd[i][binsX - j - 1]) * 0.5f;
            psd[i][j] = psd[i][binsX - j - 1] = temp;
          }
        }
        break;

      case VERTICAL_SYMMETRY:
        for (int i = 1; i < binsY / 2; i++) {
          for (int j = 0; j < binsX; j++) {
            float temp = (psd[i][j] + psd[binsY - i - 1][j]) * 0.5f;
            psd[i][j] = psd[binsY - i - 1][j] = temp;
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
    restart.writePsdBinary(dimensions, sizes, MathUtils.avgFilter(this.getPsd(), 1), "psdAvgFil");
    restart.writePsdText2D(dimensions, sizes, MathUtils.avgFilter(this.getPsd(), 1), "psdAvgFil");
    restart.writePsdBinary(dimensions, sizes, this.getPsd(), "psdAvgRaw");
    restart.writePsdText2D(dimensions, sizes, this.getPsd(), "psdAvgRaw");
  }
  
  public void setRestart(Restart restart) {
    this.restart = restart;
  }
}
