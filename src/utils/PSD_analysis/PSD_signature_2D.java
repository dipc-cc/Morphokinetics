/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.PSD_analysis;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nestor
 */
public class PSD_signature_2D {

    private FloatFFT_2D FFT_Core;
    private float[][] PSD;
    private float[][] buffer;
    private int measures;
    private Semaphore semaphore;
    private boolean averaged = false;
    public static final int HORIZONTAL_SIMMETRY = 0;
    public static final int VERTICAL_SIMMETRY = 1;

    public PSD_signature_2D(int binsX, int binsY) {

        FFT_Core = new FloatFFT_2D(binsY, binsX);
        PSD = new float[binsY][binsX];
        buffer = new float[binsY][binsX * 2];
        measures = 0;
        semaphore=new Semaphore(1);
    }

    public void addSurfaceSample(float[][] surface) {

        if (averaged) {
            throw new RuntimeException("PSD measures averaged, new samples cannot be added without signature reset.");
        }

        try{semaphore.acquire();}catch(InterruptedException e){System.err.println("Thread interrupted while writting PSD signature");}
        
        for (int i = 0; i < surface.length; i++) {
            System.arraycopy(surface[i], 0, buffer[i], 0, surface[0].length);
        }

        FFT_Core.realForwardFull(buffer);

        for (int i = 0; i < PSD.length; i++) {
            for (int j = 0; j < PSD[0].length; j++) {
                PSD[i][j] += buffer[i][j * 2] * buffer[i][j * 2] + buffer[i][j * 2 + 1] * buffer[i][j * 2 + 1];
            }
        }
        measures++;
        semaphore.release();
        
    }

    public float[][] getPSD() {
                
        if (!averaged) {
            for (int i = 0; i < PSD.length; i++) {
                for (int j = 0; j < PSD[0].length; j++) {
                    PSD[i][j] /= measures;
                }
            }
            averaged = true;
        }
        return PSD;
    }

    public void reset() {
        averaged = false;
        measures = 0;
        PSD=new float[PSD.length][PSD[0].length];
    }

    public void apply_simmetry_fold(int simmetry_type) {

        switch (simmetry_type) {
            case HORIZONTAL_SIMMETRY:
                for (int i = 0; i < PSD.length; i++) {
                    for (int j = 1; j < PSD[0].length / 2; j++) {



                        float temp = (PSD[i][j] + PSD[i][PSD[0].length - j - 1]) * 0.5f;
                        PSD[i][j] = PSD[i][PSD[0].length - j - 1] = temp;
                    }
                }
                break;

            case VERTICAL_SIMMETRY:
                for (int i = 1; i < PSD.length / 2; i++) {
                    for (int j = 0; j < PSD[0].length; j++) {
                        float temp = (PSD[i][j] + PSD[PSD.length - i - 1][j]) * 0.5f;
                        PSD[i][j] = PSD[PSD.length - i - 1][j] = temp;
                    }
                }
                break;
        }
    }




}
