/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType.formatFlag;
import basic.io.Restart;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.AbstractKmc;
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
  private float coverage;
  private int islands;
  private float fractalD;
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
  }

  public static void printHeader() {
    System.out.println("This is morphokinetics software");
    System.out.println(" _  _   __  ____  ____  _  _   __  __ _  __  __ _  ____  ____  __  ___  ____");
    System.out.println("( \\/ ) /  \\(  _ \\(  _ \\/ )( \\ /  \\(  / )(  )(  ( \\(  __)(_  _)(  )/ __)/ ___)");
    System.out.println("/ \\/ \\(  O ))   / ) __/) __ ((  O ))  (  )( /    / ) _)   )(   )(( (__ \\___ \\");
    System.out.println("\\_)(_/ \\__/(__\\_)(__)  \\_)(_/ \\__/(__\\_)(__)\\_)__)(____) (__) (__)\\___)(____/");
    System.out.println("");
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
  
  String getRestartFolderName() {
    return restartFolderName;
  }
  
  public abstract void printRates(Parser parser);

  public abstract void finishSimulation();

  /**
   * Creates the simulation frame.
   */
  public abstract void createFrame();

  abstract void initialiseRates(IRates ratesFactory, Parser myParser);
  /**
   * Initialises Kmc, the basic simulation class
   */
  public void initialiseKmc() {
    this.kmc = null;
    this.rates = null;
  }

  public void doSimulation() {
    startTime = System.currentTimeMillis();
    totalTime = 0.0;
    coverage = 0.0f;
    islands = 0;
    fractalD = 0.0f;
    boolean printPsd = (parser.doPsd() && parser.outputData());

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

    System.out.println("_____________________________________________________________________________");
    System.out.println("These are simulation rates: ");
    printRates(parser);
    
    System.out.println("_____________________________________________________________________________");
    System.out.println("Surface output: " + parser.printToImage());
    System.out.println("PSD     output: " + printPsd);
    System.out.println("Output format : " + parser.getOutputFormats());
    System.out.println("Output folder : " + restartFolderName);
    System.out.println("_____________________________________________________________________________");

    System.out.println("    I\tSimul t\tCover.\tCPU\tIslands\tFractal d.");
    System.out.println("    \t(units)\t(%)\t(ms)");
    System.out.println("    _________________________________________________________________________");
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
      coverage += kmc.getCoverage();
      islands += kmc.getLattice().getIslandCount();
      fractalD += kmc.getLattice().getFractalDimension();
    }

    printFooter();

    if (parser.doPsd()) {
      if (parser.isPsdSymmetric()) {
        psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
        psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
      }
      if (parser.visualise()) {
      }
      psd.printAvgToFile();
    }
  }


  public void updateCurrentProgress() {
    if (parser.justCentralFlake()) {
      currentProgress = kmc.getCurrentRadius();
    } else {
      currentProgress = (int) Math.floor(kmc.getCoverage() * 100);
    }
  }
  
  public double getSimulatedTime() {
    return totalTime / parser.getNumberOfSimulations();
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

  private void printOutput() {
    System.out.format("    %03d", simulations);
    System.out.format("\t%.3f", (double) kmc.getTime());
    System.out.format("\t%.4f", kmc.getCoverage());

    if (parser.getSurfaceType().equals("cartesian")) {
      sampledSurface = kmc.getSampledSurface(surfaceSizes[0], surfaceSizes[1]); // get the just simulated surface
    } else { // "periodic"
      sampledSurface = kmc.getHexagonalPeriodicSurface(surfaceSizes[0], surfaceSizes[1]);
    }
    float[][] extentSurface = MathUtils.increaseEmptyArea(sampledSurface, parser.getPsdExtend());
    if (parser.outputData()) {
      if (parser.getOutputFormats().contains(formatFlag.MKO)) {
        restart.writeSurfaceBinary(2, extentSizes, extentSurface, simulations);
      }
      if (parser.getOutputFormats().contains(formatFlag.TXT)) {
        restart.writeSurfaceText2D(2, extentSizes, extentSurface, simulations);
      }
      if (parser.getOutputFormats().contains(formatFlag.XYZ)) {
        restart.writeXyz(simulations, getKmc().getLattice());
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

    System.out.print("\t" + (System.currentTimeMillis() - iterationStartTime));
    System.out.print("\t" + kmc.getLattice().getIslandCount());
    System.out.format("\t%.4f", kmc.getLattice().getFractalDimension());
    System.out.println("");
  }

  private void printFooter() {
    String kmcResult = "";
    kmcResult += "\n\t__________________________________________________\n";
    kmcResult += "\tAverage\n";
    kmcResult += "\tSimulation time\t\tCoverage\tCPU time\tIsland avg.\tFractal d.\n";
    kmcResult += "\t(units)\t\t\t (%)\t\t (ms/s/min)\n";
    kmcResult += "\t__________________________________________________\n";
    kmcResult += "\t" + totalTime / parser.getNumberOfSimulations();
		kmcResult += "\t" + coverage / parser.getNumberOfSimulations();
    long msSimulationTime = (System.currentTimeMillis() - startTime) / parser.getNumberOfSimulations();
    kmcResult += "\t" + msSimulationTime + "/" + msSimulationTime / 1000 + "/" + msSimulationTime / 1000 / 60;
    kmcResult += "\t\t" + (float) (islands) / (float) (parser.getNumberOfSimulations());
    kmcResult += "\t" + fractalD / (float) parser.getNumberOfSimulations()+"\n";
    System.out.println(kmcResult);
    return kmcResult;

  /**
   * Does nothing. Used to have a common interface
   *
   * @param i
   */
  protected void printToImage(int i) {
    //Do nothing
  }
}
