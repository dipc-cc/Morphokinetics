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
package basic;

import java.util.logging.Level;
import java.util.logging.Logger;

import basic.io.OutputType.formatFlag;
import graphicInterfacesCommon.growth.IGrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSimulation {

  private IGrowthKmcFrame frame;
  private int totalSavedImages;
  private final boolean printIntermediatePngFiles;
  
  public AbstractGrowthSimulation(Parser parser) {
    super(parser);
    totalSavedImages = 0;
    printIntermediatePngFiles = parser.outputData() && parser.getOutputFormats().contains(formatFlag.PNG);
  }

  @Override
  void initialiseRates(IRates rates, Parser parser) {
    double depositionRatePerSite;
    rates.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    getKmc().initialiseRates(rates.getRates(parser.getTemperature()));
  }
  
  @Override
  public void createFrame() {
    boolean error = false;
    if (getParser().withGui()) {
      try {
        int max;
        if (getParser().justCentralFlake()) {
          max = (int) (getParser().getCartSizeX() / 2);
        } else {
          max = (int) getParser().getCoverage();
        }
        // check we whether are in android or not
        String className;
        if (System.getProperty("java.vm.name").equals("Dalvik")) {
          className = "android.fakeGraphicInterfaces.growth.GrowthKmcFrame";
        } else {
          className = "graphicInterfaces.growth.GrowthKmcFrame";
        }
        Class<?> genericClass = Class.forName(className); 
        frame = (IGrowthKmcFrame) genericClass.getConstructors()[0].newInstance(getKmc().getLattice(), ((AbstractGrowthKmc) getKmc()).getPerimeter(), max);
      } catch (Exception e) {
        Logger.getLogger(AbstractGrowthSimulation.class.getName()).log(Level.SEVERE, null, e);
        System.err.println("Error: Execution is not able to create the X11 frame.");
        System.err.println("Continuing without any graphic...");
        error = true;
      }
    }
    Thread p;
    if (getParser().visualise() && !error) {
      frame.setVisible(true);
      p = new PaintLoop();
    } else {
      p = new TerminalLoop();
      p.setDaemon(true); // make the progress bar finish, when main program fails
    }
    p.start();
  }
  
  /**
   * Do nothing.
   */
  @Override
  public void finishSimulation() {

  }
  
  /**
   * Prints the current frame to a file.
   *
   * @param i simulation number.
   */
  @Override
  void printToImage(int i) {
    frame.printToImage(i);
  }
  
  /**
   * Prints the current frame to a file.
   *
   * @param folderName
   * @param i simulation number.
   */
  @Override
  void printToImage(String folderName, int i) {
    frame.printToImage(folderName, i);
  }
  
  /**
   * Private class responsible to repaint every 100 ms the KMC frame.
   */
  final class PaintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        frame.repaintKmc();
        try {
          PaintLoop.sleep(100);
          if ((getParser().justCentralFlake() && getKmc().getCurrentRadius() >= getCurrentProgress())
                  || // If this is true, print a png image to a file. This is true when coverage is multiple of 0.1
                  (getKmc().getCoverage() * 100 >= getCurrentProgress())) {
            if (printIntermediatePngFiles) {
              frame.printToImage(getRestartFolderName(), 1000 + totalSavedImages);
            }
            frame.updateProgressBar(getCurrentProgress());
            updateCurrentProgress();
            totalSavedImages++;
          }
        } catch (Exception e) {
        }
      }
    }
  }
  
  /**
   * Private class responsible to repaint every 1000 ms the progress bar to the terminal.
   */
  final class TerminalLoop extends Thread {

    @Override
    public void run() {
      final int width; // progress bar width in chars
      if (getParser().justCentralFlake()) {
        width = (int) Math.max(getKmc().getLattice().getHexaSizeI() / 2, getKmc().getLattice().getHexaSizeJ() / 2);
      } else {
        width = (int) getParser().getCoverage();
      }
      if (false) {
        while (true) {
          try {
            TerminalLoop.sleep(1000);
            if ((getParser().justCentralFlake() && getKmc().getCurrentRadius() >= getCurrentProgress())
                    || (getKmc().getCoverage() * 100 > getCurrentProgress())) {

              System.out.print("\r[");
              int i = 0;
              for (; i < getCurrentProgress(); i++) {
                System.out.print(".");
              }
              for (; i < width; i++) {
                System.out.print(" ");
              }
              System.out.print("] ");
              updateCurrentProgress();

            }
          } catch (Exception e) {
          }
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
