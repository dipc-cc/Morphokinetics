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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaRestart extends AbstractCatalysisRestart {
  
  private PrintWriter outCatalysis;
  private String outDataFormat;
  private double currentTime;
  private double previousTime;
  private PrintWriter outTof;
  private PrintWriter outData;
  private PrintWriter outDataAe[];
  /**
   * Previous moment of production amount: NO and N2, H2O is also stored.
   */
  private long[] noN2P;
  /**
   * Current moment of production amount: NO and N2, H2O is also stored.
   */
  private long[] noN2C;
  private long counterNoN2;
  
  public CatalysisAmmoniaRestart(boolean catalysisOutput, String restartFolder) {
    super(catalysisOutput, restartFolder);
  }
  @Override
  public void writeExtraCatalysisOutput(double time, float[] coverages, long[] steps, long[] production, int[] sizes) {
    outCatalysis.format(outDataFormat, time, coverages[0], coverages[1], coverages[2], coverages[3], coverages[4], coverages[5], coverages[6],
            steps[0], steps[1], steps[2], steps[3], 
            production[0], production[1], production[2], 
            sizes[0], sizes[1], sizes[2], sizes[3]);
    noN2C = production;
    currentTime = time;
  }

  @Override
  void initCatalysis(int simulationNumber) {
    String folder = getFolder();
    noN2P = new long[3];
    counterNoN2 = 0;
    if (super.isOutput()) {
      // new file
      try {
        String fileName = format("%sdataCatalysis%03d.txt", folder, simulationNumber);
        outCatalysis = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outCatalysis.println("# File " + fileName);
        outCatalysis.println("# Information about the system every fixed number of events\n# [1. time 2. coverage[NH3], 3. coverage[NH2], 4. coverage[NH], 5. coverage[NO], 6. coverage[N], 7. coverage[O], 8. coverage[OH], 9. nAdsorption, 10. nDesorption, 11. nReaction, 12. nDiffusion, 13. H2O, 14. N2, 15. NO, 15. sizeAdsorption, 16. sizeDesorption, 17. sizeReaction, 18. sizeDiffusion, ");
        outDataFormat = "%g\t%g\t%g\t%g\t%g\t%g\t%g\t%g\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n";
        fileName = format("%sdataTof%03d.txt", folder, simulationNumber);
        outTof = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outTof.println("# File " + fileName);
        outTof.println("# Information about TOF\n# [1.CO[BR]+O[BR], 2. CO[BR]+O[CUS], 3. CO[CUS]+O[BR], 4. CO[CUS]+O[CUS]]");
        fileName = format("%sdataAe%03d.txt", folder, simulationNumber);
        outData = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outData.println("# File " + fileName);
        outDataAe = new PrintWriter[7];

        String names[] = new String[]{"InstantaneousDiscrete", "Success", "PossibleFromList",
          "PossibleDiscrete", "RatioTimesPossible", "Multiplicity", "All"};
        for (int i = 0; i < names.length; i++) {
          fileName = format("%sdataAe%s%03d.txt", folder, names[i], simulationNumber);
          outData = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
          outData.println("# File " + fileName);
          outDataAe[i] = outData;
        }
      } catch (IOException e) {
        Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
      }
    }
  }
  
  @Override
  public void reset() {
    super.reset();
    noN2P = new long[4];
    initCatalysis(getIteration());
    setIteration(getIteration()+1);
  }
  
  @Override
  public void resetCatalysis() {
    noN2P = new long[3];
    noN2C = new long[3];
  }

  @Override
  public void flushCatalysis() {
    double deltaTime = currentTime - previousTime;
    outTof.print(currentTime + "\t");
    // compute TOF
    for (int i = 0; i < noN2P.length; i++) {
      double tof = (noN2C[i] - noN2P[i]) / deltaTime;
      outTof.print(tof + "\t");
    }
    long sumNoN2 = 0;
    for (int i = 0; i < 2; i++) { // Only NO and N2, not H2O
      long molecules =  noN2C[i] - noN2P[i];
      outTof.print(molecules + "\t");
      sumNoN2 += molecules;
      noN2P[i] = noN2C[i];
    }
    counterNoN2 += sumNoN2;
    outTof.print(counterNoN2);
    
    outTof.println();
    previousTime = currentTime;
    outCatalysis.flush();
    outTof.flush();
  }
}
