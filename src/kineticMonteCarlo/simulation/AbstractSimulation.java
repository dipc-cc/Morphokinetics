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
package kineticMonteCarlo.simulation;

import basic.Parser;
import basic.io.OutputType.formatFlag;
import basic.io.Restart;
import graphicInterfacesCommon.surfaceViewer2D.IFrame2D;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.kmcCore.catalysis.CatalysisKmc;
import ratesLibrary.IRates;
import utils.MathUtils;
import utils.StaticRandom;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSimulation {

  private AbstractKmc kmc;
  private IRates rates;
  private PsdSignature2D psd;
  private final Parser parser;
  private final StaticRandom staticRandom;
  private final String restartFolderName;
  private long startTime;
  private long iterationStartTime;
  private double totalTime;
  private float[] coverage;
  private int islands;
  private float gyradius;
  private int simulations;
  private int currentProgress;
  private float[][] sampledSurface;
  private int[] surfaceSizes;
  private int[] extentSizes;
  private final Restart restart;

  public AbstractSimulation(Parser parser) {
    kmc = null;
    rates = null;
    psd = null;
    this.parser = parser;
    staticRandom = new StaticRandom(parser.randomSeed());
    restartFolderName = "results/run" + System.currentTimeMillis();
    restart = new Restart(restartFolderName);
    coverage = new float[1]; // Coverage
  }

  public static void printHeader() {
    System.out.println("This is morphokinetics software");
    System.out.println(" _  _   __  ____  ____  _  _   __  __ _  __  __ _  ____  ____  __  ___  ____");
    System.out.println("( \\/ ) /  \\(  _ \\(  _ \\/ )( \\ /  \\(  / )(  )(  ( \\(  __)(_  _)(  )/ __)/ ___)");
    System.out.println("/ \\/ \\(  O ))   / ) __/) __ ((  O ))  (  )( /    / ) _)   )(   )(( (__ \\___ \\");
    System.out.println("\\_)(_/ \\__/(__\\_)(__)  \\_)(_/ \\__/(__\\_)(__)\\_)__)(____) (__) (__)\\___)(____/");
    System.out.println("");
    System.out.println("Git revision: " + Restart.getGitRevision());
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    System.out.print("Execution started on " + dateFormat.format(date)); //2014/08/06 15:59:48
    java.net.InetAddress localMachine;
    try {
      localMachine = java.net.InetAddress.getLocalHost();
      System.out.println(" in " + localMachine.getHostName());
    } catch (UnknownHostException ex) {
      Logger.getLogger(AbstractSimulation.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void printHeader(String message) {
    printHeader();
    System.out.println("Execution: " + message);
  }
  
  public void setKmc(AbstractKmc kmc) {
    this.kmc = kmc;
  }

  public AbstractKmc getKmc() {
    return kmc;
  }

  public PsdSignature2D getPsd() {
    return psd;
  }

  public IRates getRates() {
    return rates;
  }

  public void setRates(IRates rates) {
    this.rates = rates;
  }

  public IRates getRatesFactory() {
    return rates;
  }

  public Parser getParser() {
    return parser;
  }
  
  public int getCurrentProgress() {
    return currentProgress + 1;
  }
  
  public abstract void printRates(Parser parser);

  public abstract void finishSimulation();

  /**
   * Creates the simulation frame.
   */
  public abstract void createFrame();

  /**
   * Initialises Kmc, the basic simulation class
   */
  public void initialiseKmc() {
    this.kmc = null;
    this.rates = null;
  }

  public void updateCurrentProgress() {
    currentProgress = calculateCurrentProgress();
  }
  
  public double getSimulatedTime() {
    return totalTime / parser.getNumberOfSimulations();
  }

  public void doSimulation() {
    startTime = System.currentTimeMillis();
    totalTime = 0.0;
    coverage[0] = 0.0f;
    islands = 0;
    gyradius = 0.0f;

    initPsd();
    printTop();
    // Main loop
    for (simulations = 0; simulations < parser.getNumberOfSimulations(); simulations++) {
      currentProgress = 0;
      iterationStartTime = System.currentTimeMillis();
      kmc.reset();
      kmc.depositSeed();
      if (parser.getEndTime() > 0) { // simulate until fixed simulation end time
        kmc.simulate(parser.getEndTime());
      } else { // simulate until a given coverage or size (single flake)
        kmc.simulate();
      }
      
      printOutput();
      totalTime += kmc.getTime();
      islands += countIslands();
      gyradius += getGyradius();
    }

    printFooter();
    doPsd();
  }
  
  abstract void initialiseRates(IRates ratesFactory, Parser myParser);
  
  String getRestartFolderName() {
    return restartFolderName;
  }

  float[] getCoverage() {
    coverage[0] = -1;
    return coverage;
  }
  
  int countIslands() {
    return -1;
  }
  
  float getGyradius() {
    return -1.0f;
  }
  
  int calculateCurrentProgress() {
    return -1;
  }
  
  void initPsd() {
    surfaceSizes = new int[2];
    // More precise (more points) the PSD better precision we get
    surfaceSizes[0] = (int) (parser.getCartSizeX() * parser.getPsdScale());
    surfaceSizes[1] = (int) (parser.getCartSizeY() * parser.getPsdScale());
    extentSizes = new int[2];
    extentSizes[0] = (int) (surfaceSizes[0] * parser.getPsdExtend());
    extentSizes[1] = (int) (surfaceSizes[1] * parser.getPsdExtend());
    
    if (parser.doPsd()) {
      psd = new PsdSignature2D(surfaceSizes[0], surfaceSizes[1], parser.getPsdExtend());
      psd.setRestart(restart); // All the output should go the same folder
    }
  }
  
  void doPsd() {
    if (parser.doPsd()) {
      if (parser.isPsdSymmetric()) {
        psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
        psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
      }
      if (parser.visualise()) {
        try {
          // check we whether are in android or not
          String className;
          if (System.getProperty("java.vm.name").equals("Dalvik")) {
            className = "android.fakeGraphicInterfaces.surfaceViewer2D.Frame2D";
          } else {
            className = "graphicInterfaces.surfaceViewer2D.Frame2D";
          }
          Class<?> genericClass = Class.forName(className);
          IFrame2D psdFrame = (IFrame2D) genericClass.getConstructors()[0].newInstance("PSD analysis");
          psdFrame.setMesh(MathUtils.avgFilter(psd.getPsd(), 1));
          psdFrame.setLogScale(true)
              .setShift(true);
          psdFrame.setVisible(true);
          psdFrame.toBack();
          psdFrame.printToImage(restartFolderName, 1);

          IFrame2D surfaceFrame = (IFrame2D) genericClass.getConstructors()[0].newInstance("Sampled surface");
          surfaceFrame.setMesh(sampledSurface);
          surfaceFrame.setVisible(true);
          surfaceFrame.toBack();
          surfaceFrame.printToImage(restartFolderName, 2);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          Logger.getLogger(AbstractSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      psd.printAvgToFile();
    }
  }
  
  /**
   * Does nothing. Used to have a common interface
   *
   * @param i
   */
  void printToImage(int i) {
    //Do nothing
  }

  /**
   * Does nothing. Used to have a common interface
   *
   * @param folderName
   * @param i
   */
  void printToImage(String folderName, int i) {
    //Do nothing
  }

  void printOutput() {
    coverage = getCoverage();
    System.out.format("    %03d", simulations);
    System.out.format("\t%.3g", (double) kmc.getTime());
    System.out.format("\t%.4f", coverage[0]);
    
    if (parser.outputData() || parser.doPsd()) {
      sampledSurface = kmc.getSampledSurface(surfaceSizes[0], surfaceSizes[1]); // get the just simulated surface
      
      float[][] extentSurface = MathUtils.increaseEmptyArea(sampledSurface, parser.getPsdExtend());
      if (parser.outputData()) {
        if (parser.getOutputFormats().contains(formatFlag.CAT)) {
          double[][] data = ((CatalysisKmc) kmc).getOutputAdsorptionData();
          restart.writeCatalysisAdsorptionDataText(simulations, data);
        }
        if (parser.getOutputFormats().contains(formatFlag.MKO)) {
          restart.writeSurfaceBinary(2, extentSizes, extentSurface, simulations);
        }
        if (parser.getOutputFormats().contains(formatFlag.TXT)) {
          restart.writeSurfaceText2D(2, extentSizes, extentSurface, simulations);
        }
        if (parser.getOutputFormats().contains(formatFlag.XYZ)) {
          restart.writeXyz(simulations, getKmc().getLattice());
        }
        if (parser.getOutputFormats().contains(formatFlag.SVG)) {
          restart.writeSvg(simulations, getKmc().getLattice());
        }
        if (parser.getOutputFormats().contains(formatFlag.PNG) && parser.withGui()) {
          printToImage(restartFolderName, simulations);
        }
      }
      
      if (parser.doPsd()) {
        psd.addSurfaceSample(extentSurface);
        if (parser.outputData()) {
          if (parser.getOutputFormats().contains(formatFlag.MKO)) {
            psd.writePsdBinary(simulations);
          }
          if (parser.getOutputFormats().contains(formatFlag.TXT)) {
            psd.writePsdText(simulations);
          }
        }
      }
    }

    System.out.print("\t" + (System.currentTimeMillis() - iterationStartTime));
    printBottom();
    System.out.println("");
  }

  void printTop() {
    boolean printPsd = (parser.doPsd() && parser.outputData());
    
    System.out.println("_____________________________________________________________________________");
    System.out.println("These are simulation rates: ");
    printRates(parser);
    
    System.out.println("_____________________________________________________________________________");
    System.out.println("Surface output: " + parser.printToImage());
    System.out.println("PSD     output: " + printPsd);
    System.out.println("Output format : " + parser.getOutputFormats());
    System.out.println("Output folder : " + restartFolderName);
    System.out.println("_____________________________________________________________________________");

    String secondLine = "    \t(units) \t(%)\t(ms)";
    secondLine += secondLine();
    System.out.println(secondLine);
    System.out.println("    _________________________________________________________________________");
  }
  
  String secondLine() {
    System.out.println("    I\tSimul time\tCover.\tCPU\tIslands\tFractal d.");
    return "";
  }
  
  void printBottom() {
    System.out.print("\t" + countIslands());
    System.out.format("\t%.4f", getGyradius());
  }
    
  
  private String printFooter() {
    int i = parser.getNumberOfSimulations();
    String kmcResult = "";
    kmcResult += "\n\t__________________________________________________\n";
    kmcResult += "\tAverage\n";
    if (kmc instanceof CatalysisKmc) {
      kmcResult += "\tSimulation time\t\tCoverage\tCPU time\tCove CO\tCove O\n";
    } else {
      kmcResult += "\tSimulation time\t\tCoverage\tCPU time\tIsland avg.\tGyradius\n";
    }
    kmcResult += "\t(units)\t\t\t (%)\t\t (ms/s/min)\n";
    kmcResult += "\t__________________________________________________\n";
    kmcResult += "\t" + totalTime / i;
    kmcResult += "\t" + coverage[0] / i;
    long msSimulationTime = (System.currentTimeMillis() - startTime) / i;
    kmcResult += "\t" + msSimulationTime + "/" + msSimulationTime / 1000 + "/" + msSimulationTime / 1000 / 60;
    kmcResult += printCoverages();
    System.out.println(kmcResult);
    return kmcResult;
  }
  
  String printCoverages() {
    int i = parser.getNumberOfSimulations();
    String kmcResult = "";
    kmcResult += "\t\t" + (float) (islands) / (float) (i);
    kmcResult += "\t" + gyradius / (float) i + "\n";
    return kmcResult;
  }
}
