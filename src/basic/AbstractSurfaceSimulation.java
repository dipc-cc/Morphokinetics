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
import kineticMonteCarlo.kmcCore.growth.AbstractSurfaceKmc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSurfaceSimulation extends AbstractSimulation {

  private IGrowthKmcFrame frame;
  private int totalSavedImages;
  private final boolean printIntermediatePngFiles;
  
  public AbstractSurfaceSimulation(Parser parser) {
    super(parser);
    totalSavedImages = 0;
    printIntermediatePngFiles = parser.outputData() && parser.getOutputFormats().contains(formatFlag.PNG);
  }
   
  @Override
  public AbstractSurfaceKmc getKmc() {
    return (AbstractSurfaceKmc) super.getKmc();
  }
  
  @Override
  float[] getCoverage() {
    float[] coverage = new float[1];
    coverage[0] = getKmc().getCoverage();
    return coverage;
  }  
  
  IGrowthKmcFrame constructFrame(Class<?> genericClass, int max) throws Exception {
    return (IGrowthKmcFrame) genericClass.getConstructors()[1].newInstance(getKmc().getLattice(), max);
  }
  
  @Override
  public void createFrame() {
    boolean error = false;
    if (getParser().withGui()) {
      try {
        int max = (int) getParser().getCoverage();
        // check we whether are in android or not
        String className;
        if (System.getProperty("java.vm.name").equals("Dalvik")) {
          className = "android.fakeGraphicInterfaces.growth.GrowthKmcFrame";
        } else {
          className = "graphicInterfaces.growth.GrowthKmcFrame";
        }
        Class<?> genericClass = Class.forName(className);
        frame = constructFrame(genericClass, max);
       // frame = new GrowthKmcFrame((AbstractSurfaceLattice) getKmc().getLattice(), max);
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
      p.start();
    } else if (isGrowth()) {
      p = new TerminalLoop();
      p.setDaemon(true); // make the progress bar finish, when main program fails
      p.start();
    }
  }
  
  boolean isGrowth() {
    return false;
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
   * Auxiliary method to choose to print intermediate files
   * 
   * @return true if should print.
   */
  boolean shouldPrint() {
    return getKmc().getCoverage() * 100 >= getCurrentProgress();
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
          if (// If this is true, print a png image to a file. This is true when coverage is multiple of 0.1
                  shouldPrint()) {
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
            if (shouldPrint()) {

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
}
