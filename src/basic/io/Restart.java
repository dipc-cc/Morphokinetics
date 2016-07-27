/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.net.URISyntaxException;
import java.net.URL;
import kineticMonteCarlo.lattice.AbstractLattice;
import main.Morphokinetics;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Restart {

  public static final int MAX_DIMS = 3;
  private String folder;

  public Restart() {
    folder = "results/";
    createFolder(folder);
  }

  public Restart(String restartFolder) {
    folder = restartFolder;
    if (!folder.endsWith("/")) {
      folder += "/";
    }
    createFolder(restartFolder);
  }

  /**
   * Returns the base location of the JAR file (or the main executable instead).
   *
   * @return base location of the executable
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

  public String getPsdScript(String inputFileName, String outputFileName, float min, float max, int sizeX, int sizeY) {
    String base = "reset\n"
            + "set term postscript enhanced color \"Arial\" 20\n"
            + "set output \"" + folder + outputFileName + ".eps\"\n"
            + "\n"
            + "set zrange[" + min + ":" + max + "]; set xrange[0:" + (sizeX - 1) + "]; set yrange[0:" + (sizeY - 1) + "]\n"
            + "set isosample 500,500\n"
            + "set table \"" + folder + "imagePsdAvgFil.dat\"\n"
            + "splot \"" + folder + inputFileName + ".txt\" u 1:2:3\n"
            + "unset table\n"
            + "\n"
            + "set contour\n"
            + "set cntrparam level incremental 4, 0.5, 16\n"
            + "set dgrid3d 128,128\n"
            + "unset surface\n"
            + "set view 0,0\n"
            + "set table \"" + folder + "contourPsdAvgFil.dat\"\n"
            + "splot \"" + folder + inputFileName + ".txt\" u 1:2:3 w l lt -1 notitle\n"
            + "unset table\n"
            + "\n"
            + "reset\n"
            + "set xrange[0:" + (sizeX - 1) + "]; set yrange[0:" + (sizeY - 1) + "]\n"
            + "set palette rgbformulae 33,13,10\n"
            + "unset key\n"
            + "set size square\n"
            + "set cbrange[" + min + ":" + max + "]\n"
            + "plot \"<sort -n -k1,1 -k2 " + folder + "imagePsdAvgFil.dat\" with image, \"" + folder + "contourPsdAvgFil.dat\" w l lt -1 lw 1.5";
    return base;
  }
  
  /**
   * Writes float data to a file called "psd[number].mko". First of all calls to the header writing:
   * look documentation there.
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param simulationNumber
   */
  public void writePsdBinary(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = format("%spsd%03d.mko", folder, simulationNumber);
    RestartLow.writeLowBinary(dimensions, sizes, data, fileName);
  }

  public void writePsdBinary(int dimensions, int[] sizes, float[][] data, String fileName) {
    fileName = addFolderAndSuffix(fileName, ".mko");
    RestartLow.writeLowBinary(dimensions, sizes, data, fileName);
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
    String fileName = format("%ssurface%03d.mko", folder, simulationNumber);
    RestartLow.writeLowBinary(dimensions, sizes, data, fileName);
  }

  public void writeSurfaceBinary2D(float[][] data, int simulationNumber) {
    int sizes[] = new int[2];
    sizes[0] = data.length;
    sizes[1] = data[0].length;
    String fileName = format("%ssurface%03d.mko", folder, simulationNumber);
    RestartLow.writeLowBinary(2, sizes, data, fileName);
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
    String fileName = format("%spsd%03d.txt", folder, simulationNumber);
    RestartLow.writeLowText2D(data, fileName, true);
  }

  public void writePsdText1D(float[] data, String fileName) {
    fileName = addFolderAndSuffix(fileName, ".txt");
    RestartLow.writeLowText1D(data, fileName);
  }
    
  public void writePsdText2D(int dimensions, int[] sizes, float[][] data, String fileName) {
    fileName = addFolderAndSuffix(fileName, ".txt");
    RestartLow.writeLowText2D(data, fileName, true);
  }

  public void writeSurfaceText2D(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = format("%ssurface%03d.txt", folder, simulationNumber);
    RestartLow.writeLowText2D(data, fileName, false);
  }

  public void writeSurfaceText2D(int dimensions, int[] sizes, float[][] data, String fileName) {
    fileName = addFolderAndSuffix(fileName, ".txt");
    RestartLow.writeLowText2D(data, fileName, false);
  }

  public void writeSurfaceHexagonal(int dimensions, int[] sizes, float[][] data, int simulationNumber) {
    String fileName = format("%ssurfaceHexagonal%03d.txt", folder, simulationNumber);
    RestartLow.writeLowTextHexagonal(data, fileName);
  }

  public void writeTextString(String data, String fileName) {
    fileName = addFolderAndSuffix(fileName, ".txt");
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
  
  public void writeXyz(int simulationNumber, AbstractLattice lattice) {
    String fileName = format("%ssurface%03d.xyz", folder, simulationNumber);
    RestartLow.writeXyz(fileName, lattice);
  }
  
  /**
   * Reads a surface from a (binary) file and it reduces it surface by the given factor.
   *
   * @param fileName
   * @param factor how much we want to reduce the surface. A number greater than one
   * @return reduced surface
   * @throws FileNotFoundException
   */
  public float[][] readSurfaceBinary2D(String fileName, int factor) throws FileNotFoundException {
    float[][] originalSurface = RestartLow.readLowBinary(fileName);
    return RestartLow.scale(originalSurface, factor);
  }
  
  public float[][] readSurfaceBinary2D(String fileName) throws FileNotFoundException {
    return RestartLow.readLowBinary(fileName);
  }
  
  public float[][] readPsdText2D(int dimensions, int[] sizes, String fileName) throws FileNotFoundException {
    fileName = addFolderAndSuffix(fileName, ".txt");
    return RestartLow.readLowText2D(fileName, sizes, true);
  }
  
  public float[][] readSurfaceText2D(int dimensions, int[] sizes, String fileName) throws FileNotFoundException {
    fileName = addFolderAndSuffix(fileName, ".txt");
    return RestartLow.readLowText2D(fileName, sizes, false);
  }

  public float[][] readSurfaceText2D(String fileName) throws FileNotFoundException {
    fileName = addFolderAndSuffix(fileName, ".txt");
    return RestartLow.readLowText2D(fileName);
  }
  
  private void createFolder(String restartFolder) {
    try {
      File file = new File(restartFolder);
      file.mkdirs();
    } catch (Exception e) {
      System.err.println("Error creating folder: " + restartFolder);
    }
  }
  
  /**
   * Normalises fileName with the folder and proper suffix. 
   * @param fileName input file name.
   * @param suffix suffix to be added.
   * @return complete file name.
   */
  private String addFolderAndSuffix(String fileName, String suffix) {
    if (!fileName.startsWith(folder)) {
      fileName = folder + fileName;
    }
   
    if (!fileName.endsWith(suffix)) {
      fileName = fileName + suffix;
    }
    return fileName;
  }
}
