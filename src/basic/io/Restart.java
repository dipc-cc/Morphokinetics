/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;
import main.Morphokinetics;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Restart {

  public static final int MAX_DIMS = 3;
  private String folder; 

  private int sizeX;
  private int sizeY;

  public Restart() {
    folder = "results/";
    createFolder(folder);
  }
  
  public Restart(String restartFolder){ 
    folder = restartFolder;
    if (!folder.endsWith("/")){
      folder += "/";
    }
    createFolder(restartFolder);
  }
  
  private void createFolder(String restartFolder){
    try {
      File file = new File(restartFolder);
      file.mkdirs();
    } catch (Exception e) {
      System.err.println("Error creating folder: " + restartFolder);
    }
  }

  public int getSizeX() {
    return sizeX;
  }

  public int getSizeY() {
    return sizeY;
  }

  /**
   * Returns the base location of the JAR file (or the main executable instead).
   * @return 
   */
  public static String getJarBaseDir() {
    final Class<?> referenceClass = Morphokinetics.class;
    final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
    File jarPath;
    try {
      jarPath = new File(url.toURI()).getParentFile(); // this is the path you want 
    } catch (final URISyntaxException e) {
      System.err.println("Could not find the base JAR directory. Probably something will go wrong");
      jarPath = new File("./");
    }
    return jarPath.toString();
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
    String fileName = format("%spsd%03d.mko",folder,simulationNumber);
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
   * Instructions to plot: > set dgrid3d 30,30 > splot "psd000.txt" u 1:2:3 w l
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param simulationNumber
   */
  public void writePsdText2D(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = format("%spsd%03d.txt",folder,simulationNumber);
    writeLowText2D(data, fileName, true);
  }

  public void writePsdText2D(int dimensions, int[] sizes, float[][] data, String fileName) {
    String subfix = ".txt";
    if (!fileName.endsWith(subfix)) {
      fileName = fileName + ".txt";
    }
    writeLowText2D(data, fileName, true);
  }
  
  public void writeTextString(String data, String fileName) {
    fileName = folder+fileName;
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);
      printWriter.write(data + "\n");
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
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
    String fileName = format("%ssurface%03d.mko",folder,simulationNumber);
    writeLowBinary(dimensions, sizes, data, fileName);

    writeSurfaceText2D(dimensions, sizes, data, simulationNumber);
  }

  public void writeSurfaceText2D(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = format("%ssurface%03d.txt",folder,simulationNumber);
    writeLowText2D(data, fileName, false);
  }

  public void writeSurfaceText2D(int dimensions, int[] sizes, float[][] data, String fileName){
    writeLowText2D(data, fileName, false);
  }
  
  public float[][] readSurfaceText2D(int dimensions, int[] sizes, String fileName) throws FileNotFoundException {
    return readLowText2D(fileName, sizes);
  }
  
  public float[][] readSurfaceBinary2D(String fileName) throws FileNotFoundException {
    return readLowBinary(fileName); 
  }
  
  /**
   * Reads a surface from a (binary) file and it reduces it surface by the given factor.
   * @param fileName
   * @param factor how much we want to reduce the surface. A number greater than one
   * @return reduced surface
   * @throws FileNotFoundException 
   */
  public float[][] readSurfaceBinary2D(String fileName, int factor) throws FileNotFoundException {
    float[][] originalSurface = readLowBinary(fileName); 
    return scale(originalSurface, factor);
  }
  
  /**
   * Given an original surface it reduces by the given factor.
   * @param originalSurface
   * @param factor
   * @return reduced surface
   */
  private float[][] scale(float[][] originalSurface, int factor) {
    int originalSizeX = originalSurface.length;
    int originalSizeY = originalSurface[0].length;
    double scale = (double)1/ (double) factor;
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
        int reducedX = (int)(x*scale);
        int reducedY = (int)(y*scale);
        reducedSurface[reducedX][reducedY] = originalSurface[x][y];
      }
    }
    return reducedSurface;
  }
  
  public float[][] readSurfaceText2D(String fileName) throws FileNotFoundException {
    return readLowText2D(fileName);
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
   * This method reads the binary file
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param fileName
   */
  private float[][] readLowBinary(String fileName) throws FileNotFoundException {
    System.out.println("Reading matrix of unknown sizes. "
            + "File: " + fileName);

    float[][] data = null;
    FileInputStream fis;
    DataInputStream dis;
    int dimensions = -1; 
    int i = -1;
    int j = -1;
    int[] sizes = new int[3];
    int tmp = -99;
    try {
      //readHeaderBinary(dimensions, sizes, fileName);
      // create file output stream
      fis = new FileInputStream(fileName);
      // create data output stream
      dis = new DataInputStream(fis);
     
      dis.skipBytes(14);
      // Read the dimensions of the file
      dimensions = dis.readInt();
      // Read the sizes of the actual dimensions
      for (i = 0; i < dimensions; i++) {
        sizes[i] = dis.readInt();
      }
      // Skip the rest of the dimensions
      for (j = i; j < MAX_DIMS; j++) {
        tmp = dis.readInt();
      }
      // Skip the rest of the header
      for (j = 0; j < 8; j++) {
        tmp = dis.readInt();
      }
      
      this.sizeX = sizes[0];
      this.sizeY = sizes[1];
      data = new float [sizes[0]][sizes[1]];
      
      // for each byte in the buffer
      for (i = 0; i < sizes[0]; i++) {
        for (j = 0; j < sizes[1]; j++) {
          // write float to the dos, reading an int
          data[i][j] = (float) dis.readByte();
        }
      }
      // releases all system resources from the streams
      fis.close();
      dis.close();
    } catch (FileNotFoundException fe) {
      throw fe;
    } catch (Exception e) {
      // if any I/O error occurs
      System.err.println("Point: " + i + " " + j);
      e.printStackTrace();
    }
    return data;
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
   
  private float[][] readLowText2D(String fileName) throws FileNotFoundException {
    float[][] data = null;
    System.out.println("Trying to read "+folder+ fileName + " file of unknown size ");
    int x = -1;
    int y = -1;
    try {
      BufferedReader in = new BufferedReader(new FileReader(folder+fileName));
      String line;
      // <-- read whole line
      line = in.readLine();
      if (line != null) {
        StringTokenizer tk = new StringTokenizer(line);
        if (!tk.nextToken().equals("#")) {
          System.err.println("File format not valid. Should start with a line with # character");
          throw new FileNotFoundException("Fix the file format");
        }
        this.sizeY = Integer.parseInt(tk.nextToken());
        this.sizeX = Integer.parseInt(tk.nextToken());
        data = new float[sizeX][sizeY];
      }
      line = in.readLine();
      for (x=0; x<this.sizeX; x++) {
        StringTokenizer tk = new StringTokenizer(line);
        for (y=0; y<this.sizeY; y++) {
          data[x][y] = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        }
        line = in.readLine();
      }
    } catch (FileNotFoundException fe){
      throw fe;
    }
      catch (Exception e) {
      // if any I/O error occurs
      System.err.println("Point: " + x + " " + y);
      e.printStackTrace();
    }
    
    return data;
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
