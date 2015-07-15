/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.psdAnalysis;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nestor
 */
public class PsdSignature2D {

  private FloatFFT_2D fftCore;
  private float[][] psd;
  private float[][] buffer;
  private int measures;
  private Semaphore semaphore;
  private boolean averaged = false;
  public static final int HORIZONTAL_SIMMETRY = 0;
  public static final int VERTICAL_SIMMETRY = 1;

  public PsdSignature2D(int binsY, int binsX) {

    fftCore = new FloatFFT_2D(binsY, binsX);
    psd = new float[binsY][binsX];
    buffer = new float[binsY][binsX * 2];
    measures = 0;
    semaphore = new Semaphore(1);
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

    for (int i = 0; i < psd.length; i++) {
      for (int j = 0; j < psd[0].length; j++) {
        psd[i][j] += buffer[i][j * 2] * buffer[i][j * 2] + buffer[i][j * 2 + 1] * buffer[i][j * 2 + 1];
      }
    }
    measures++;
    semaphore.release();

  }

  public float[][] getPSD() {

    if (!averaged) {
      for (int i = 0; i < psd.length; i++) {
        for (int j = 0; j < psd[0].length; j++) {
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
    psd = new float[psd.length][psd[0].length];
  }

  public void applySimmetryFold(int simmetryType) {

    switch (simmetryType) {
      case HORIZONTAL_SIMMETRY:
        for (int i = 0; i < psd.length; i++) {
          for (int j = 1; j < psd[0].length / 2; j++) {

            float temp = (psd[i][j] + psd[i][psd[0].length - j - 1]) * 0.5f;
            psd[i][j] = psd[i][psd[0].length - j - 1] = temp;
          }
        }
        break;

      case VERTICAL_SIMMETRY:
        for (int i = 1; i < psd.length / 2; i++) {
          for (int j = 0; j < psd[0].length; j++) {
            float temp = (psd[i][j] + psd[psd.length - i - 1][j]) * 0.5f;
            psd[i][j] = psd[psd.length - i - 1][j] = temp;
          }
        }
        break;
    }
  }

}
