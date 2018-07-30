package basic.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.lattice.AbstractLattice;

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

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaRestart extends Restart {
  
  private PrintWriter outData;
  
  public BdaRestart(String restartFolder) {
    super(restartFolder);
  }
  
  @Override
  public void writeSvg(int simulationNumber, AbstractLattice lattice) {
    String fileName = format("%s/surface%03d.svg", getFolder(), simulationNumber);
    RestartLow.writeSvgBda(fileName, lattice);
  }
  
  public void writeExtraOutput(double temperature, double time, int[] coverages, long[] steps, long[] production, int[] sizes) {
    String frmt = "%g";
    outData.format(frmt, temperature);
    frmt = "\t%g";
    outData.format(frmt, time);
    frmt = "\t%d";
    for (int i = 0; i < coverages.length; i++) {
      outData.format(frmt, coverages[i]);
    }
    frmt = "\n";
    outData.format(frmt, "");
  }
  
  public void initBdaRestart(int simulationNumber) {
    //String folder = getFolder();try {
    try {
      String fileName = format("%sdataBda%03d.txt", getFolder(), simulationNumber);
      outData = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
      outData.println("# File " + fileName);
    } catch (IOException e) {
      Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
    }
  }
  
  @Override
  public void reset() {
    super.reset();
    initBdaRestart(getIteration());
  }
  
  @Override
  public void flushExtra() {
    super.flushExtra();
    outData.flush();
  }
}
