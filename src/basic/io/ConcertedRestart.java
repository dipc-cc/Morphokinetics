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
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AbstractLattice;
import kineticMonteCarlo.site.ISite;
import kineticMonteCarlo.unitCell.IUc;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedRestart extends Restart {
  
  private String growthElement;
  private String surfaceElement;
  private double latticeConstantElement;
  private double latticeConstantSurface;
  
  public ConcertedRestart(boolean catalysisOutput, String restartFolder) {
    super(catalysisOutput, restartFolder);
  }
  
  /**
   * 
   * @param gE growth element. Material that is growing
   * @param sE surface element. 
   * @param restartFolder 
   */
  public ConcertedRestart(String gE, String sE, String restartFolder) {
    super(restartFolder);
    growthElement = gE;
    surfaceElement = sE;
    if (growthElement.equals("Cu")) {
      latticeConstantElement = 3.61;
    }
    if (surfaceElement.equals("Cu")) {
      latticeConstantSurface = 3.61;
    }
    if (growthElement.equals("Ni")) {
      latticeConstantElement = 3.52;
    }
    if (surfaceElement.equals("Ni")) {
      latticeConstantSurface = 3.52;
    }
  }

  @Override
  public void writeXyz(int simulationNumber, AbstractLattice lattice) {
    String fileName = format("%ssurface%03d.xyz", getFolder(), simulationNumber);
    writeXyz(fileName, lattice);
  }
  
  /**
   * Writes a XYZ format file.
   * 
   * @param fileName
   * @param lattice 
   */
  private void writeXyz(String fileName, AbstractLattice lattice) {
    // get the lattice size
    int numberOfAtoms = lattice.getHexaSizeI() * lattice.getHexaSizeJ() * lattice.getUnitCellSize();
    // add occupied atoms
    numberOfAtoms += ((AbstractGrowthLattice) lattice).getOccupied();

    // create file descriptor. It will be automatically closed.
    try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName))){
      String s = format("%d", numberOfAtoms);
      printWriter.write(s +"\n simple XYZ file made with Morphokinetics\n");
      IUc uc;
      double posX;
      double posY;
      double posZ;
      // for each atom in the uc
      for (int i = 0; i < lattice.size(); i++) {
        uc = lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          ISite site = uc.getSite(j);
          posX = (uc.getPos().getX() + site.getPos().getX()) * latticeConstantSurface+latticeConstantSurface/2;
          posY = (uc.getPos().getY() + site.getPos().getY()) * latticeConstantSurface+latticeConstantSurface/3;
          posZ = ((uc.getPos().getZ() + site.getPos().getZ()) * latticeConstantSurface)-latticeConstantSurface;
          s = format("%s %.3f %.3f %.3f", surfaceElement, posX, posY, posZ);
          printWriter.write(s + "\n");
          if (site.isOccupied()) {
            posX = (uc.getPos().getX() + site.getPos().getX()) * latticeConstantElement;
            posY = (uc.getPos().getY() + site.getPos().getY()) * latticeConstantElement;
            posZ = (uc.getPos().getZ() + site.getPos().getZ()) * latticeConstantElement;
            s = format("%s %.3f %.3f %.3f", growthElement, posX, posY, posZ);
            printWriter.write(s + "\n");
          }
        }
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  @Override
  public void reset() {
    // new files
    try {
      String fileName = format("%sdataAe%03d.txt", getFolder(), getIteration());
      setExtraWriter(new PrintWriter(new BufferedWriter(new FileWriter(fileName))));
      getExtraWriter().println("# File " + fileName);
      getExtraWriter().println("# Information about the system every 1% of coverage and every deposition\n[1. coverage, 2. time, 3. nucleations, 4. islands, 5. depositionProbability, 6. totalProbability, 7. numberOfMonomers, 8. numberOfEvents, 9. sumOfProbabilities, 10. avgRadiusOfGyration, 11. innerPerimeter, 12. outerPerimeter, 13. tracer diffusivity distance 14. centre of mass diffusivity distance 15. numberOfAtomsFirstIsland 16. TotalHops 17. and so on, different atom types] ");
      setFormat("\t%g\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d%s%s\n");
      PrintWriter outDataAe[] = new PrintWriter[6];
      
      String names[] = new String[]{"InstantaneousDiscrete", "Success", "PossibleFromList", 
        "PossibleDiscrete", "RatioTimesPossible", "Multiplicity"};
      for (int i = 0; i < names.length; i++) {
        fileName = format("%sdataAe%s%03d.txt", getFolder(), names[i], getIteration());
        PrintWriter outDataTmp = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        outDataTmp.println("# File " + fileName);
        outDataAe[i] = outDataTmp;
      }
      setExtraWriters(outDataAe);
    } catch (IOException e) {
      Logger.getLogger(Restart.class.getName()).log(Level.SEVERE, null, e);
    }
    setIteration(getIteration() + 1);
  }
}
