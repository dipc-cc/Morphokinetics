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
public class CatalysisCoRestart extends AbstractCatalysisRestart {

  private PrintWriter outCatalysis;
  private String outDataFormat;
  /**
   * Previous moment CO2 amount.
   */
  private long[] co2P;
  /**
   * Current moment CO2 amount.
   */
  private long[] co2C;
  private int counterCo2;
  private double currentTime;
  private double previousTime;
  private PrintWriter outTof;
  private PrintWriter outData;
  private PrintWriter outDataAe[];

  public CatalysisCoRestart(boolean catalysisOutput, String restartFolder) {
    super(catalysisOutput, restartFolder);
  }
  
  @Override
  public PrintWriter[] getExtraWriters() {
    return outDataAe;
  }
  
  @Override
  void initCatalysis(int simulationNumber) {
    String folder = getFolder();
    co2P = new long[4];
    counterCo2 = 0;
    if (super.isOutput()) {
      // new file
      try {
        String fileName = format("%sdataCatalysis%03d.txt", folder, simulationNumber);
        outCatalysis = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outCatalysis.println("# File " + fileName);
        outCatalysis.println("# Information about the system every fixed number of events\n# [1. time 2. coverage[CO][BR], 3. coverage[CO][CUS], 4. coverage[O][BR], 5. coverage[O][CUS], 6. nAdsorption, 7. nDesorption, 8. nReaction, 9. nDiffusion, 10. CO[BR]+O[BR], 11. CO[BR]+O[CUS], 12. CO[CUS]+O[BR], 13. CO[CUS]+O[CUS], 14. sizeAdsorption, 15. sizeDesorption, 16. sizeReaction, 17. sizeDiffusion, ");
        //outDataFormat = "%g\t%g\t%g\t%g\t%g\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n";
        outDataFormat = "%g\t%g\t%g\t%g\t%g\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n";
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
  public void writeExtraCatalysisOutput(double time, float[] coverages, long[] steps, long[] co2, int[] sizes) {
    outCatalysis.format(outDataFormat, time, coverages[0], coverages[1], coverages[2], coverages[3], steps[0], steps[1], steps[2], steps[3], co2[0], co2[1], co2[2], co2[3], sizes[0], sizes[1], sizes[2], sizes[3]);
    co2C = co2;
    currentTime = time;
  }
  
  @Override
  public void reset() {
    super.reset();
    co2P = new long[4];
    initCatalysis(getIteration());
    setIteration(getIteration()+1);
  }
  
  /**
   * Once stationary state is reached, reset the counters.
   */
  @Override
  public void resetCatalysis() {
    co2P = new long[4];
    co2C = new long[4];
    counterCo2 = 0;
  }
    
  @Override
  public void flushCatalysis() {
    double deltaTime = currentTime - previousTime;
    outTof.print(currentTime + "\t");
    // compute TOF
    for (int i = 0; i < co2P.length; i++) {
      double tof = (co2C[i] - co2P[i]) / deltaTime;
      outTof.print(tof + "\t");
    }
    long sumCo2 = 0;
    for (int i = 0; i < co2P.length; i++) {
      long molecules =  co2C[i] - co2P[i];
      outTof.print(molecules + "\t");
      sumCo2 += molecules;
      co2P[i] = co2C[i];
    }
    counterCo2 += sumCo2;
    outTof.print(counterCo2);
    
    outTof.println();
    previousTime = currentTime;
    outCatalysis.flush();
    outTof.flush();
  }
}
