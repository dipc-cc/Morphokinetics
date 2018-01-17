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
package main;

import basic.Parser;
import basic.io.Restart;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import static java.lang.String.format;
import static java.lang.System.exit;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 * It does a PSD from a input binary mko file with format X Y value.
 *
 * @author J. Alberdi-Rodriguez
 */
public class PsdFromSurfaces {

  public PsdFromSurfaces(Parser parser) {
    System.out.println("This is a utility of morphokinetics program");
    String surfaceFileName;
    boolean showGui;
    boolean doTent;
    boolean print = parser.outputData();
    ExecuteShellCommand com = new ExecuteShellCommand();

    surfaceFileName = "dummy";
    showGui = parser.withGui() && parser.visualise();
    doTent = !parser.getSurfaceType().equals("plane");

    Restart restart = new Restart("./");
    int[] sizes = null;
    float[][] surface = null;
    PsdSignature2D psd = null;
    double scale = parser.getPsdScale();
    for (int i = 0; i < parser.getNumberOfSimulations(); i++) {
      try {
        surfaceFileName = format("surface%03d", i);
        surface = restart.readSurfaceBinary2D(surfaceFileName);
        surface = MathUtils.scale(surface, scale);
        if (psd == null) {
          sizes = new int[2];
          sizes[0] = (int) (surface.length);
          sizes[1] = (int) (surface[0].length);
          psd = new PsdSignature2D(sizes[0], sizes[1], 1);
        }
      } catch (Exception e){
        System.err.println("Provided filename [" + surfaceFileName + i + ".mko] does not exist. Continuing");
        continue;
      }
      if (doTent) {
        MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
      } else {
        MathUtils.planeSurface(surface);
      }
      
      psd.addSurfaceSample(surface);
      if (print) {
        restart.writeSurfaceText2D(2, sizes, surface, format("readSurface%03d.txt", i));
        psd.writePsdBinary(i); 
      }
    }

    psd.printAvgToFile();
    if (showGui) {
      psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
      psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
      Frame2D psdFrame = new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(psd.getPsd(), 1));
      psdFrame.setLogScale(true);
      psdFrame.setShift(true);
      psdFrame.setVisible(true);
      psdFrame.printToImage(1);

      Frame2D surfaceFrame = new Frame2D("Sampled surface");
      surfaceFrame.setMesh(surface);
      surfaceFrame.setVisible(true);
      surfaceFrame.printToImage(2);
    }
    if (parser.printToImage()) {
      System.out.println("Doing the graphs with gnuplot");
      System.out.println("Min is " + psd.getMin() + " Max is " + psd.getMax());
      restart.writeTextString(restart.getPsdScript("psdAvgFil", "psdAvg", psd.getMin(), psd.getMax(), sizes[0], sizes[1]), "tmpGnuplotScript");
      System.out.println(com.executeCommand("gnuplot results/tmpGnuplotScript.txt"));
      System.out.println(com.executeCommand("inkscape --export-area-drawing --export-latex results/psdAvg.eps --export-pdf=results/psdAvg.pdf"));
    }
    System.out.println("Finishing the program");
    if (!showGui) {
      exit(0);
    }
  }
}
