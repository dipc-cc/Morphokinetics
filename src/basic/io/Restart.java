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
package basic.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AbstractLattice;
import utils.MathUtils;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Restart {

  public static final int MAX_DIMS = 3;

  private String folder;
  final static Charset ENCODING = StandardCharsets.UTF_8;
  private int iteration;
  
  /**
   * Attribute to control the output of data every 1% and nucleation.
   */
  private boolean extraOutput;
  /**
   * Attribute to control the output of extra data of delta time between two attachments and between
   * an atom is deposited and attached to an island.
   */
  private boolean extraOutput2;
  private final boolean isCatalysis;
  private final boolean isConcerted;
  private String outDataFormat;
  private PrintWriter outData;
  private PrintWriter outDataAe[];
  private PrintWriter outDeltaAttachments;
  private PrintWriter outPerAtom;
  private List<Double> deltaTimeBetweenTwoAttachments;
  private List<Double> deltaTimePerAtom;
  private double previousTime;

  public Restart() {
    folder = "results/";
    createFolder(folder);
    isCatalysis = false;
    isConcerted = false;
  }

  public Restart(String restartFolder) {
    folder = restartFolder;
    if (!folder.endsWith("/")) {
      folder += "/";
    }
    createFolder(restartFolder);
    isCatalysis = false;
    isConcerted = false;
  }
  
  /**
   * Creates a restart object to be used to write extra data files.
   * 
   * @param extraOutput
   * @param extraOutput2 
   */
  public Restart(boolean extraOutput, boolean extraOutput2) {
    this.extraOutput = extraOutput;
    this.extraOutput2 = extraOutput2;
    deltaTimeBetweenTwoAttachments = new ArrayList<>();
    deltaTimePerAtom = new ArrayList<>();  
    if (extraOutput) {
      try {
        outData = new PrintWriter(new BufferedWriter(new FileWriter("results/dataEvery1percentAndNucleation.txt")));
        outData.println("# Information about the system every 1% of coverage and every deposition\n[1. coverage, 2. time, 3. nucleations, 4. islands, 5. depositionProbability, 6. totalProbability, 7. numberOfMonomers, 8. numberOfEvents, 9. sumOfProbabilities, 10. avgRadiusOfGyration, 11. innerPerimeter, 12. outerPerimeter, 13. tracer diffusivity distance 14. centre of mass diffusivity distance 15. numberOfAtomsFirstIsland 16. TotalHops 17. and so on, different atom types] ");
        outDataFormat = "\t%g\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d%s%s\n";
      } catch (IOException e) {
        Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    if (extraOutput2) {
      try {
        File file = new File("results/deltaTimeBetweenTwoAttachments.txt");
        outDeltaAttachments = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        outDeltaAttachments.println("# Time difference between two attachments to the islands [1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability, 8. No. islands] ");
      } catch (IOException e) {
        Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
      }
      try {
        outPerAtom = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimePerAtom.txt")));
        outPerAtom.println("# Time difference between deposition and attachment to the islands for a single atom[1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability] ");
      } catch (IOException e) {
        Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    isCatalysis = false;
    isConcerted = false;
  }
  
  public Restart(boolean catalysisOutput, String restartFolder) {
    folder = restartFolder;
    if (!folder.endsWith("/")) {
      folder += "/";
    }
    createFolder(restartFolder);
    iteration = 0;
    if (catalysisOutput) {
      isCatalysis = true;
      isConcerted = false;
    } else {
      isCatalysis = false;
      isConcerted = true;
      extraOutput = true;
    }
  }
  
  public String getFolder() {
    return folder;
  }
  
  public int getIteration() {
    return iteration;
  }

  public void setIteration(int iteration) {
    this.iteration = iteration;
  }
  
  /**
   * Returns the base location of the JAR file (or the main executable instead).
   *
   * @return base location of the executable
   */
  public static String getJarBaseDir() {
    if (System.getProperty("java.vm.name").equals("Dalvik")) {
      // Return empty String. In Android fails this method and in any case, it is useless.
      return "";
    } else {
      final Class<?> referenceClass = Restart.class;
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
  }

  public static String getGitRevision() {
    String folder = getJarBaseDir();
    String revision = RestartLow.readGitRevision(folder);
    if (revision.equals("not found")) {
      folder = folder + "/../";
      revision = RestartLow.readGitRevision(folder);
    }
    return revision;
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
  
  public void writeSurfaceStationary(float[][] data) {
    String fileName = format("surfaceStationary%03d.txt",iteration-1);
    fileName = addFolderAndSuffix(fileName, ".txt");
    RestartLow.writeLowText2D(data, fileName, false);
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
  
  public void writeCatalysisAdsorptionDataText(int simulationNumber, double[][] data) {
    String fileName = format("%scatalisysAdsorption%03d.txt", folder, simulationNumber);
    fileName = addFolderAndSuffix(fileName, ".txt");
    RestartLow.writeAdsorptionLowSimulationDataText(data, fileName);
  }
  
  public void writeXyz(int simulationNumber, AbstractLattice lattice) {
    String fileName = format("%ssurface%03d.xyz", folder, simulationNumber);
    RestartLow.writeXyz(fileName, lattice);
  }
  
  public void writeSvg(int simulationNumber, AbstractLattice lattice, boolean complete) {
    String fileName = format("%ssurface%03d.svg", folder, simulationNumber);
    RestartLow.writeSvg(fileName, lattice);
  }
  
  public void writeExtraOutput(AbstractGrowthLattice lattice, float coverage, int nucleations,
          double time, double adsorptionRate, double diffusionRate, long simulatedSteps, double sumProbabilities) {
    if (extraOutput) {
      int islandCount = lattice.countIslands(outData);
      String coverageFormat = "%f";

      lattice.getCentreOfMass();
      lattice.getDistancesToCentre();
      lattice.countPerimeter(null);
      //compute the average distances to centre.
      float avgGyradius = lattice.getAverageGyradius();
      int numberOfAtomFirstIsland = 0;
      try {
        numberOfAtomFirstIsland = lattice.getIsland(0).getNumberOfAtoms();
      } catch (NullPointerException | IndexOutOfBoundsException e) { // It may occur that there is no any island
      }
      outData.format(Locale.US, coverageFormat + outDataFormat, coverage, time,
              nucleations, islandCount, adsorptionRate,
              diffusionRate, lattice.getMonomerCount(), simulatedSteps, sumProbabilities, avgGyradius,
              lattice.getInnerPerimeterLenght(), lattice.getOuterPerimeterLenght(), lattice.getTracerDistance(), lattice.getCmDistance(), numberOfAtomFirstIsland, lattice.getTotalHops(),
              lattice.getAtomTypesCounter(), lattice.getEmptyTypesCounter());
    }
  }
  
  public void writeExtra2Output(AbstractGrowthLattice lattice, AbstractGrowthSite atom, 
          float coverage, double time, double diffusionRate) {
    if (extraOutput2) {
      int islandCount = lattice.countIslands(null);
      deltaTimeBetweenTwoAttachments.add(time - previousTime);
      outDeltaAttachments.println(coverage + " " + time + " " + deltaTimeBetweenTwoAttachments.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().mapToDouble(e -> e).average().getAsDouble() + " "
              + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
              + diffusionRate + " "
              + islandCount);
      previousTime = time;
      deltaTimePerAtom.add(time - atom.getDepositionTime());
      outPerAtom.println(coverage + " " + time + " " + deltaTimePerAtom.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().mapToDouble(e -> e).average().getAsDouble() + " "
              + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
              + diffusionRate);
    }
  }

  public void writeExtraCatalysisOutput(double time, float[] coverages, long[] steps, long[] co2, int[] sizes) {
  }

  public PrintWriter getExtraWriter() {
    return outData;
  }
  
  public PrintWriter[] getExtraWriters() {
    return outDataAe;
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
    return MathUtils.scale(originalSurface, factor);
  }
  
  public float[][] readSurfaceBinary2D(String fileName) throws FileNotFoundException {
    fileName = addFolderAndSuffix(fileName, ".mko");
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
  
  /**
   * Reads a file to a ArrayList of unknown size aimed to be like array[][].
   *
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  public ArrayList<ArrayList> readDataTextFile(String fileName) throws FileNotFoundException {
    fileName = addFolderAndSuffix(fileName, ".txt");
    return RestartLow.readLowTextData(fileName);
  }
    
    
  public String readFile(String fileName) {
    List<String> readList = null;
    try {
      //read the parameters file
      readList = RestartLow.readSmallTextFile(fileName);
    } catch (IOException exception) {
      System.err.println("Could not read file " + fileName);
      Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, exception);
      return "{}";
    }

    int lines = readList.size();
    String str = new String();
    for (int i = 0; i < lines; i++) {
      str += String.valueOf(readList.get(i));
    }
    
    System.out.println("Parser read " + lines + " lines from " + fileName);

    return str;
  }

  public void flushExtra() {
    if (extraOutput) {
      outData.flush();
    }
    if (extraOutput2) {
      outDeltaAttachments.flush();
      outPerAtom.flush();
    }
  }
  
  public void reset() {
    if (extraOutput2) {
      deltaTimeBetweenTwoAttachments.clear();
      deltaTimePerAtom.clear();
    }
    previousTime = 0;
    /*if (isCatalysis) {
      initCatalysis(iteration++);
    }*/
    if (isConcerted) {
      initConcerted(iteration++);
    }
  }

  private void initConcerted(int simulationNumber) {
    // new files
    try {
      String fileName = format("%sdataAe%03d.txt", folder, simulationNumber);
      outData = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
      outData.println("# File " + fileName);
      outData.println("# Information about the system every 1% of coverage and every deposition\n[1. coverage, 2. time, 3. nucleations, 4. islands, 5. depositionProbability, 6. totalProbability, 7. numberOfMonomers, 8. numberOfEvents, 9. sumOfProbabilities, 10. avgRadiusOfGyration, 11. innerPerimeter, 12. outerPerimeter, 13. tracer diffusivity distance 14. centre of mass diffusivity distance 15. numberOfAtomsFirstIsland 16. TotalHops 17. and so on, different atom types] ");
      outDataFormat = "\t%g\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d%s%s\n";
      outDataAe = new PrintWriter[6];
      
      String names[] = new String[]{"InstantaneousDiscrete", "Success", "PossibleFromList", 
        "PossibleDiscrete", "RatioTimesPossible", "Multiplicity"};
      for (int i = 0; i < names.length; i++) {
        fileName = format("%sdataAe%s%03d.txt", folder, names[i], simulationNumber);
        PrintWriter outDataTmp = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outDataTmp.println("# File " + fileName);
        outDataAe[i] = outDataTmp;
      }
    } catch (IOException e) {
      Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
    }
  }
  
  final void createFolder(String restartFolder) {
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
