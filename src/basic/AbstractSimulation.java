/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType.formatFlag;
import basic.io.Restart;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import ratesLibrary.IRatesFactory;
import utils.MathUtils;
import utils.StaticRandom;
import utils.list.ListConfiguration;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSimulation {

  private AbstractKmc kmc;
  private IRatesFactory rates;
  private PsdSignature2D psd;
  private ListConfiguration config;
  private final Parser parser;
  private StaticRandom staticRandom;
  private String restartFolderName;
  private long startTime;
  private long iterationStartTime;
  private double totalTime;
  private float coverage;
  private int islands;
  private int simulations;
  private float[][] sampledSurface;
  private int[] surfaceSizes;
  private int[] extentSizes;
  private Restart restart;

  public AbstractSimulation(Parser parser) {
    kmc = null;
    rates = null;
    psd = null;
    this.parser = parser;
    staticRandom = new StaticRandom(parser.randomSeed());
  }

  /**
   * Initialises Kmc, the basic simulation class
   */
  public void initialiseKmc() {
    switch (parser.getListType()) {
      case "linear":
        this.config = new ListConfiguration().setListType(ListConfiguration.LINEAR_LIST);
        break;
      case "binned":
        this.config = new ListConfiguration().setListType(ListConfiguration.BINNED_LIST)
                .setBinsPerLevel(parser.getBinsLevels())
                .setExtraLevels(parser.getExtraLevels());
        break;
      default:
        System.err.println("listType is not properly set");
        System.err.println("listType currently is " + parser.getListType());
        System.err.println("Available options are \"linear\" and \"binned\" ");
        this.config = null;
    }
    this.kmc = null;
    this.rates = null;
  }

  /**
   * Creates the simulation frame.
   */
  public abstract void createFrame();

  public void doSimulation() {
    startTime = System.currentTimeMillis();
    totalTime = 0.0;
    coverage = 0.0f;
    islands = 0;
    boolean printPsd = (parser.doPsd() && parser.outputData());
    restartFolderName = "results/run" + System.currentTimeMillis();
    restart = new Restart(restartFolderName);

    surfaceSizes = new int[2];
    //it is a good idea to divide the sample surface dimensions by two (e.g. 256->128)
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
    System.out.println("Output format : " + "mko");
    System.out.println("Output folder : " + restartFolderName);
    System.out.println("_____________________________________________________________________________");

    System.out.println("    I\tSimul t\tCover.\tCPU\tIslands");
    System.out.println("    \t(units)\t(%)\t(ms)");
    System.out.println("    _________________________________________________________________________");
    // Main loop
    for (simulations = 0; simulations < parser.getNumberOfSimulations(); simulations++) {
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
      islands += kmc.getIslandCount();
    }

    printFooter();

    if (parser.doPsd()) {
      if (parser.isPsdSymmetric()) {
        psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
        psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
      }
      if (parser.visualise()) {
        Frame2D psdFrame = new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(psd.getPsd(), 1));
        psdFrame.setLogScale(true)
                .setShift(true);                
        psdFrame.setVisible(true);
        psdFrame.toBack();
        psdFrame.printToImage(restartFolderName, 1);

        Frame2D surfaceFrame = new Frame2D("Sampled surface");
        surfaceFrame.setMesh(sampledSurface);
        surfaceFrame.setVisible(true);
        surfaceFrame.toBack();
        surfaceFrame.printToImage(restartFolderName, 2);
      }
      psd.printAvgToFile();
    }
  }

  protected abstract void initialiseRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser);

  public abstract void finishSimulation();

  public void setKmc(AbstractKmc kmc) {
    this.kmc = kmc;
  }

  public AbstractKmc getKmc() {
    return kmc;
  }

  public PsdSignature2D getPsd() {
    return psd;
  }

  public IRatesFactory getRates() {
    return rates;
  }

  public void setRates(IRatesFactory rates) {
    this.rates = rates;
  }

  public IRatesFactory getRatesFactory() {
    return rates;
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

  private void printOutput() {
    System.out.format("    %03d", simulations);
    System.out.format("\t%.3f", (double) kmc.getTime());
    System.out.format("\t%.4f", kmc.getCoverage());

    if (parser.getSurfaceType().equals("cartesian")) {
      sampledSurface = kmc.getSampledSurface(surfaceSizes[0], surfaceSizes[1]); // get the just simulated surface
    } else { // "periodic"
      sampledSurface = kmc.getHexagonalPeriodicSurface(surfaceSizes[0], surfaceSizes[1]);
    }
    float[][] extentSurface = kmc.increaseEmptyArea(sampledSurface, parser.getPsdExtend());
    if (parser.outputData()) {
      if (parser.getOutputFormats().contains(formatFlag.MKO)) {
        restart.writeSurfaceBinary(2, extentSizes, extentSurface, simulations);
      }
      if (parser.getOutputFormats().contains(formatFlag.TXT)) {
        restart.writeSurfaceText2D(2, extentSizes, extentSurface, simulations);
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
    System.out.print("\t" + kmc.getIslandCount());
    System.out.println("");
  }

  private void printFooter() {
    System.out.println("\n\t__________________________________________________");
    System.out.println("\tAverage");
    System.out.println("\tSimulation time\t\tCoverage\tCPU time\tIsland avg.");
    System.out.println("\t(units)\t\t\t (%)\t\t (ms/s/min)");
    System.out.println("\t__________________________________________________");
    System.out.print("\t" + totalTime / parser.getNumberOfSimulations());
    System.out.print("\t" + coverage / parser.getNumberOfSimulations());
    long msSimulationTime = (System.currentTimeMillis() - startTime) / parser.getNumberOfSimulations();
    System.out.print("\t" + msSimulationTime + "/" + msSimulationTime / 1000 + "/" + msSimulationTime / 1000 / 60);
    System.out.println("\t\t" + (float) (islands) / (float) (parser.getNumberOfSimulations()));
  }

  /**
   * Does nothing. Used to have a common interface
   *
   * @param i
   */
  protected void printToImage(int i) {
    //Do nothing
  }

  /**
   * Does nothing. Used to have a common interface
   *
   * @param folderName
   * @param i
   */
  protected void printToImage(String folderName, int i) {
    //Do nothing
  }

  public Parser getParser() {
    return parser;
  }

  public ListConfiguration getConfig() {
    return config;
  }

  String getRestartFolderName() {
    return restartFolderName;
  }
  
  public abstract void printRates(Parser parser);
}
