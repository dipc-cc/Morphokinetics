/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
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

  protected AbstractKmc kmc;
  protected IRatesFactory rates;
  protected DiffusionKmcFrame frame;
  protected PsdSignature2D psd;

  protected ListConfiguration config;
  protected Parser parser;

  private StaticRandom staticRandom;
  
  public AbstractSimulation(Parser parser) {
    kmc = null;
    rates = null;
    frame = null;
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
    float[][] sampledSurface = null;
    long startTime = System.currentTimeMillis();
    double totalTime = 0.0;
    float coverage = 0.0f;
    boolean printPsd = (parser.doPsd() && parser.outputData());
    String folderName = "results/run"+System.currentTimeMillis();
    Restart restart = new Restart(folderName);
    
    int sizes[] = new int[2];
    //it is a good idea to divide the sample surface dimensions by two (e.g. 256->128)
    sizes[0] = parser.getCartSizeX() / 2;
    sizes[1] = parser.getCartSizeY() / 2;
    
    if (parser.doPsd()) {
      psd = new PsdSignature2D(sizes[0], sizes[1]);
      psd.setRestart(restart); // All the output should go the same folder
    }
    System.out.println("    I\tSimul t\tCover.\tPNG output\tSurface output\t\t CPU ");
    System.out.println("    \t(units)\t(%)\t(results/)\t"+folderName+" (ms)");
    System.out.println("    _________________________________________________________________________");
    // Main loop
    for (int simulations = 0; simulations < parser.getNumberOfSimulations(); simulations++) {
      long iterStartTime = System.currentTimeMillis();
      kmc.reset();
      kmc.depositSeed();
      kmc.simulate();
      System.out.format("    %03d", simulations);
      System.out.format("\t%.3f",(double)kmc.getTime());
      System.out.format("\t%.3f",kmc.getCoverage());
      if (parser.printToImage()) {
        frame.printToImage(simulations);
        System.out.format("\tsurface%03d.png", simulations);
      } else {
        System.out.print("\t none       ");
      }
      if (parser.doPsd()) {
        sampledSurface = kmc.getSampledSurface(sizes[0], sizes[1]);
        psd.addSurfaceSample(sampledSurface);
        if (parser.outputData()) {
          psd.printToFile(simulations);
          restart.writeSurfaceBinary(2, sizes, sampledSurface, simulations);
          System.out.format("\tpsd%03d.txt|surface%03d.txt", simulations, simulations);
        } 
      }
      if (!printPsd) 
        System.out.print("\tnone\t\t\t");
      System.out.print(" "+(System.currentTimeMillis() - iterStartTime));
      System.out.println("");
      totalTime += kmc.getTime();
      coverage += kmc.getCoverage();
    }
    System.out.println("\n\t__________________________________________________");
    System.out.println("\tAverage");
    System.out.println("\tSimulation time\t\tCoverage\tCPU time");
    System.out.println("\t(units)\t\t\t (%)\t\t (ms)");
    System.out.println("\t__________________________________________________");
    System.out.print("\t"+ totalTime / parser.getNumberOfSimulations());
    System.out.print("\t"+coverage/ parser.getNumberOfSimulations());
    System.out.println("\t"+((System.currentTimeMillis() - startTime) / parser.getNumberOfSimulations())+"\n");
    
    if (parser.doPsd()) {
      psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
      psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
      if (parser.visualize()){
        new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(psd.getPsd(), 1))
              .setLogScale(true)
              .setShift(true)
              .performDrawToImage(1);

        new Frame2D("Sampled surface").setMesh(sampledSurface).performDrawToImage(2);
      }
      psd.printAvgToFile();
    }
  }

  protected abstract void initializeRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser);

  public abstract void finishSimulation();

  public AbstractKmc getKmc() {
    return kmc;
  }

  public DiffusionKmcFrame getFrame() {
    return frame;
  }

  public PsdSignature2D getPsd() {
    return psd;
  }
  
  public void setRates(double[] rates) {
    kmc.initialiseRates(rates);
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
  }
  
  public static void printHeader(String message){
    printHeader();
    System.out.println("Execution: " + message);
  }
}
