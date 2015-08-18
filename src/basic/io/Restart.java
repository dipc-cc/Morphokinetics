/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.StringTokenizer;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Restart {

  public static final int MAX_DIMS = 3;
  private static String folder; 

  public Restart() {
    folder = "results/";
    createFolder(folder);
  }
  
  public Restart(String restartFolder){ 
    folder = restartFolder;
    createFolder(restartFolder);
  }
  
  private void createFolder(String restartFolder){
    try {
      File file = new File(restartFolder);
      file.mkdir();
    } catch (Exception e) {
      System.err.println("Error creating folder: " + restartFolder);
    }
  }

  /**
   * Writes float data to a file called "psd[number].mko". First of all calls to the header
   * writing: look documentation there.
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param simulationNumber
   */
  public void writePsdBinary(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = folder + "psd" + simulationNumber + ".mko";

    writePsdText2D(dimensions, sizes, data, simulationNumber);

    writeLowBinary(dimensions, sizes, data, fileName);
  }

  public void writePsdBinary(int dimensions, int[] sizes, float[][] data, String fileName) {
    if (!fileName.startsWith(folder)){
      fileName = folder + fileName;
    }
    writePsdText2D(dimensions, sizes, data, fileName);

    String subfix = ".mko";
    if (!fileName.endsWith(subfix)) {
      fileName = fileName + ".mko";
    }
    writeLowBinary(dimensions, sizes, data, fileName);
  }

  /**
   * Function to print to text file each PSD result. Mainly thought to plot it with gnuplot
   * Instructions to plot: > set dgrid3d 30,30 > splot "psd0.txt" u 1:2:3 w l
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param simulationNumber
   */
  public void writePsdText2D(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = folder + "psd" + simulationNumber + ".txt";
    writeLowText2D(data, fileName, true);
  }

  public void writePsdText2D(int dimensions, int[] sizes, float[][] data, String fileName) {
    String subfix = ".txt";
    if (!fileName.endsWith(subfix)) {
      fileName = fileName + ".txt";
    }
    writeLowText2D(data, fileName, true);
  }

  /**
   * Writes float data to a file called "surface[number].mko". First of all calls to the header
   * writing: look documentation there.
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param simulationNumber
   */
  public void writeSurfaceBinary(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = folder + "surface" + simulationNumber + ".mko";
    writeLowBinary(dimensions, sizes, data, fileName);

    writeSurfaceText2D(dimensions, sizes, data, simulationNumber);
  }

  public void writeSurfaceText2D(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = folder + "surface" + simulationNumber + ".txt";
    writeLowText2D(data, fileName, false);
  }

  public float[][] readSurfaceText2D(int dimensions, int[] sizes, String fileName) throws FileNotFoundException {
    return readLowText2D(fileName, sizes);
  }

  /**
   * This method does the actual writing to the file
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param fileName
   */
  private void writeLowBinary(int dimensions, int[] sizes, float[][] data, String fileName) {
    System.out.println("Writting matrix of size " + sizes[0] + "x" + sizes[1] + " to disk. "
            + "File: " + fileName);

    FileOutputStream fos;
    DataOutputStream dos;

    try {
      writeHeaderBinary(dimensions, sizes, fileName);
      // create file output stream
      fos = new FileOutputStream(fileName, true);
      // create data output stream
      dos = new DataOutputStream(fos);

      // for each byte in the buffer
      for (int i = 0; i < sizes[0]; i++) {
        for (int j = 0; j < sizes[1]; j++) {
          // write float to the dos
          dos.writeFloat(data[i][j]);
        }
      }

      // force bytes to the underlying stream
      dos.flush();
      // releases all system resources from the streams
      fos.close();
      dos.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  /**
   * The header of the file has to have the following structure: - The "Morphokinetics" keyword -
   * The number of dimensions (int) - The size of each dimension (3 x int) - Trash to fill 64 bytes
   * (in case we need some more space for the future) Right now is 32. Has to be adapted in the
   * future
   *
   * @param dimensions
   * @param sizes
   * @param simulationNumber
   */
  private static void writeHeaderBinary(int dimensions, int[] sizes, String fileName) {
    FileOutputStream fos;
    DataOutputStream dos;
    try {
      // create file output stream
      fos = new FileOutputStream(fileName);
      // create data output stream
      dos = new DataOutputStream(fos);

      // write header to the dos
      dos.writeUTF("Morphokinetics");
      dos.writeInt(dimensions);
      int i;
      for (i = 0; i < dimensions; i++) {
        dos.writeInt(sizes[i]);
      }
      for (int j = i; j < MAX_DIMS; j++) {
        dos.writeInt(0);
      }

      for (int j = 0; j < 8; j++) {
        dos.writeInt(-1);
      }
      dos.size();
      // force bytes to the underlying stream
      dos.flush();
      // releases all system resources from the streams
      fos.close();
      dos.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  private static void writeLowText2D(float[][] data, String fileName, boolean shift) {
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);

      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[0].length; j++) {
          int posX = i;
          int posY = j;
          if (shift) {
            posX = (posX + data.length / 2) % data.length;
            posY = (posY + data[0].length / 2) % data[0].length;
          }
          String s = format("%.3f", data[i][j]);
          String sLog = format("%.3f", Math.log(data[i][j]));
          printWriter.write(posX + " " + posY + " " + sLog + " " + s + "\n");
        }

        //printWriter.write("\n");
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  private float[][] readLowText2D(String fileName, int[] sizes) throws FileNotFoundException {
    float[][] data = new float[sizes[0]][sizes[1]];
    String subfix = ".txt";
    if (!fileName.endsWith(subfix)) {
      fileName = fileName + ".txt";
    }
    fileName = folder + fileName;
    System.out.println("Trying to read " + fileName + " file of size " + sizes[0] + "x" + sizes[1]);
    int i = -1;
    int j = -1;
    float trash;
    try {

      BufferedReader in = new BufferedReader(new FileReader(fileName));
      String line;
      // <-- read whole line
      line = in.readLine();
      while (line != null) {
        StringTokenizer tk = new StringTokenizer(line);
        i = Integer.parseInt(tk.nextToken()); // <-- read single word on line and parse to int
        j = Integer.parseInt(tk.nextToken()); // <-- read single word on line and parse to int
        trash = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        data[i][j] = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        line = in.readLine();
      }
    } catch (FileNotFoundException fe){
      throw fe;
    }
      catch (Exception e) {
      // if any I/O error occurs
      System.err.println("Point: " + i + " " + j);
      e.printStackTrace();
    }
    return data;
  }
}
