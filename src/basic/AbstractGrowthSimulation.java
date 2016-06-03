/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType.formatFlag;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSimulation {

  private double previousCoverage;
  private final double printEvery;
  private int savedImages;
  private int totalSavedImages;
  private final boolean printIntermediatePngFiles;
  
  public AbstractGrowthSimulation(Parser parser) {
    super(parser);
    savedImages = 1;
    printEvery = 0.1;
    totalSavedImages = 0;
    printIntermediatePngFiles = parser.outputData() && parser.getOutputFormats().contains(formatFlag.PNG);
  }

  @Override
  protected void initialiseRates(IRatesFactory ratesFactory, Parser parser) {
    double depositionRatePerSite;
    ratesFactory.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = ratesFactory.getDepositionRatePerSite();
    double islandDensity = ratesFactory.getIslandDensity(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    getKmc().initialiseRates(ratesFactory.getRates(parser.getTemperature()));
  }
  
  @Override
  public void createFrame() {
    if (getParser().withGui()) {
      try {
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
      if (getParser().visualise()) {
        paintLoop p = new paintLoop();
        p.start();
      }
    }
  }
  
  /**
   * Do nothing.
   */
  @Override
  public void finishSimulation() {

  }
  
  /**
   * Prints the current frame to a file
   * @param i simulation number
   */
  @Override
  protected void printToImage(int i) {

  }
  
  /**
   * Prints the current frame to a file
   * @param folderName
   * @param i simulation number
   */
  @Override
  protected void printToImage(String folderName, int i) {
    // reset saved images for current simulation
    savedImages = 1;
  }
  
  /**
   * Private class responsible to repaint every 100 ms the KMC frame.
   */
  final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          paintLoop.sleep(100);
          // If this is true, print a png image to a file. This is true when coverage is multiple of 0.1
          if (printIntermediatePngFiles &&
                  previousCoverage < (printEvery * savedImages) && getKmc().getCoverage() > (printEvery * savedImages)) {
            savedImages++;
            totalSavedImages++;
          }
          previousCoverage = getKmc().getCoverage();
        } catch (Exception e) {
        }
      }
    }
  }
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());
    //we modify the 1D array into a 2D array;
    int length = (int) Math.sqrt(rates.length);

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        System.out.printf("%1.3E  ", rates[i * length + j]);
      }
      System.out.println(" ");
    }
    System.out.println("Deposition rate (per site): " + getRates().getDepositionRatePerSite());
    System.out.println("Island density:             " + getRates().getIslandDensity(parser.getTemperature()));
  }
}
