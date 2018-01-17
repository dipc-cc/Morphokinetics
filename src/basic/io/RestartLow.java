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

import static basic.io.Restart.MAX_DIMS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import kineticMonteCarlo.atom.IAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AbstractLattice;
import kineticMonteCarlo.lattice.AgLattice;
import kineticMonteCarlo.lattice.GrapheneLattice;
import kineticMonteCarlo.lattice.SiLattice;
import kineticMonteCarlo.unitCell.IUc;
import main.Configurator;

/**
 * Class responsible to do the actual writings and readings. Only has to be used from Restart class.
 *
 * @author J. Alberdi-Rodriguez
 */
class RestartLow {
  
  /**
   * This method does the actual writing to the file
   *
   * @param dimensions
   * @param sizes
   * @param data
   * @param fileName
   */
  static void writeLowBinary(int dimensions, int[] sizes, float[][] data, String fileName) {
    writeHeaderBinary(dimensions, sizes, fileName);
    // create data output stream with its file output stream
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName, true))) {
      // for each byte in the buffer
      for (int i = 0; i < sizes[0]; i++) {
        for (int j = 0; j < sizes[1]; j++) {
          // write float to the dos
          out.writeFloat(data[i][j]);
        }
      }

      // force bytes to the underlying stream
      out.flush();
      // releases all system resources from the streams
      out.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  /**
   * This method reads the binary file
   *
   * @param fileName file name to be read
   * @return read surface
   */
  static float[][] readLowBinary(String fileName) throws FileNotFoundException {
    System.out.println("Reading matrix of unknown sizes. "
            + "File: " + fileName);

    float[][] data = null;
    int dimensions;
    int i = -1;
    int j = -1;
    int[] sizes = new int[3];
    // create data input stream with its file input stream
    try (DataInputStream in = new DataInputStream(new FileInputStream(fileName))) {
      in.skipBytes(16);
      // Read the dimensions of the file
      dimensions = in.readInt();
      // Read the sizes of the actual dimensions
      for (i = 0; i < dimensions; i++) {
        sizes[i] = in.readInt();
      }
      // Skip the rest of the dimensions
      for (j = i; j < MAX_DIMS; j++) {
        in.readInt();
      }
      // Skip the rest of the header
      for (j = 0; j < 8; j++) {
        in.readInt();
      }

      data = new float[sizes[0]][sizes[1]];

      // for each byte in the buffer
      for (i = 0; i < sizes[0]; i++) {
        for (j = 0; j < sizes[1]; j++) {
          // write float to the dos, reading an int
          data[i][j] = (float) in.readFloat();
        }
      }
      // releases all system resources from the streams
      in.close();
    } catch (FileNotFoundException fe) {
      throw fe;
    } catch (Exception e) {
      // if any I/O error occurs
      System.err.println("Point: " + i + " " + j);
      e.printStackTrace();
    }
    return data;
  }
  
  static void writeLowText1D(float[] data, String fileName) {
    // create file descriptor. It will be automatically closed.
    try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        String s = format("%.3f", data[i]);
        out.write(i + " " + s + "\n");
      }
      out.flush();
      out.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  static void writeLowText2D(float[][] data, String fileName, boolean shift) {
    // create file descriptor. It will be automatically closed.
    try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
      String s;
      String sLog;
      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[0].length; j++) {
          int posX = i;
          int posY = j;
          if (shift) {
            posX = (posX + data.length / 2) % data.length;
            posY = (posY + data[0].length / 2) % data[0].length;
          }
          s = format("%.3f", data[i][j]);
          sLog = format("%.3f", Math.log(data[i][j]));
          out.write(posX + " " + posY + " " + sLog + " " + s + "\n");
        }
      }
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  static float[][] readLowText2D(String fileName) throws FileNotFoundException {
    float[][] data = null;

    if (Configurator.getConfigurator() != null) { //

      BufferedReader in = Configurator.getConfigurator().getBufferedReader(fileName);
      int x = -1;
      int y = -1;
      int sizeY = 0;
      int sizeX = 0;
      // create file descriptor. It will be automatically closed.
      try {
        String line;
        // <-- read whole line
        line = in.readLine();
        if (line != null) {
          StringTokenizer tk = new StringTokenizer(line);
          if (!tk.nextToken().equals("#")) {
            System.err.println("File format not valid. Should start with a line with # character");
            throw new FileNotFoundException("Fix the file format");
          }
          sizeY = Integer.parseInt(tk.nextToken());
          sizeX = Integer.parseInt(tk.nextToken());
          data = new float[sizeX][sizeY];
        }
        line = in.readLine();
        for (x = 0; x < sizeX; x++) {
          StringTokenizer tk = new StringTokenizer(line);
          for (y = 0; y < sizeY; y++) {
            data[x][y] = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
          }
          line = in.readLine();
        }
        in.close();
      } catch (FileNotFoundException fe) {
        throw fe;
      } catch (Exception e) {
        // if any I/O error occurs
        System.err.println("Point: " + x + " " + y);
        e.printStackTrace();
      }
    }
    return data;
  }

  static float[][] readLowText2D(String fileName, int[] sizes, boolean shift) throws FileNotFoundException {
    float[][] data = new float[sizes[0]][sizes[1]];
    System.out.println("Trying to read " + fileName + " file of size " + sizes[0] + "x" + sizes[1]);
    int i = -1;
    int j = -1;
    // create file descriptor. It will be automatically closed.
    try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
      String line;
      // <-- read whole line
      line = in.readLine();
      while (line != null) {
        StringTokenizer tk = new StringTokenizer(line);
        i = Integer.parseInt(tk.nextToken()); // <-- read single word on line and parse to int
        j = Integer.parseInt(tk.nextToken()); // <-- read single word on line and parse to int
        int posX = i;
        int posY = j;
        if (shift) {
          posX = (posX + data.length / 2) % data.length;
          posY = (posY + data[0].length / 2) % data[0].length;
        }
        Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        data[posX][posY] = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        line = in.readLine();
      }
      in.close();
    } catch (FileNotFoundException fe) {
      throw fe;
    } catch (Exception e) {
      // if any I/O error occurs
      System.err.println("Point: " + i + " " + j);
      e.printStackTrace();
    }
    return data;
  }
  
  /**
   * Reads a file to a ArrayList of unknown size aimed to be like array[][]
   *
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  static ArrayList<ArrayList> readLowTextData(String fileName) throws FileNotFoundException {
    ArrayList<ArrayList> data = new ArrayList<>();
    // create file descriptor. It will be automatically closed.
    try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
      String line;
      // <-- read whole line
      line = in.readLine();
      while (line != null) {
        data.add(new ArrayList());
        StringTokenizer tk = new StringTokenizer(line);
        while (tk.hasMoreTokens())  {
          String token = tk.nextToken();
          if (token.equals("#")) {
            break;
          }
          try {
            data.get(data.size()-1).add(new Double(token));
          } catch (NumberFormatException e) {
            break;
          }
        }
        line = in.readLine();
      }
      in.close();
    } catch (FileNotFoundException fe) {
      throw fe;
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
    return data;
  }
  
  static String readGitRevision(String folder) {
    String rev;
    String fileName = folder + "/.gitRevision";
    // create file descriptor. It will be automatically closed.
    try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
      String line;
      // <-- read whole line
      rev = in.readLine();
    } catch (FileNotFoundException fe) {
      rev = "not found";
    } catch (Exception e) {
      // if any I/O error occurs
      rev = "not known";
      e.printStackTrace();
    }
    return rev;
  }
    
  static void writeLowTextHexagonal(float[][] data, String fileName) {
    // create file descriptor. It will be automatically closed.
    try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[0].length; j++) {
          String s = format("%.3f", data[i][j]);
          out.write(i + " " + j + " " + s + "\n");
        }
      }
      out.flush();
      out.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  /**
   * Writes a XYZ format file.
   * 
   * @param fileName
   * @param lattice 
   */
  static void writeXyz(String fileName, AbstractLattice lattice) {
    // Check that is growth simulation, in etching are missing getUc in AbstractLattice and getPos and isOccupied in AbstractAtom
       
    int numberOfAtoms = lattice.size();
    double scale = 1; // default distance in Anstroms
    String element = "H";
    // Setup
    if (lattice instanceof AbstractGrowthLattice) { // get the exact number of atoms
      numberOfAtoms = ((AbstractGrowthLattice) lattice).getOccupied();
    }
    if (lattice instanceof GrapheneLattice){
      scale = 1.42; 
      element = "C";
    } else if (lattice instanceof AgLattice) {
      scale = 2.892; 
      element = "Ag";
    } else if (lattice instanceof SiLattice) {
      scale = .177083; // I don't know
      element = "Si";
    }
    // create file descriptor. It will be automatically closed.
    try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName))){
      PrintWriter surface = new PrintWriter(new FileWriter(fileName+".xyz"));
      String s = format("%d", numberOfAtoms);
      printWriter.write(s +"\n simple XYZ file made with Morphokinetics\n");
      s = format("%d", lattice.getHexaSizeI() * lattice.getHexaSizeJ() * lattice.getUnitCellSize());
      surface.write(s +"\n surface XYZ file made with Morphokinetics\n");
      IUc uc;
      double posX;
      double posY;
      double posZ;
      // for each atom in the uc
      for (int i = 0; i < lattice.size(); i++) {
        uc = lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          IAtom atom = uc.getAtom(j);
          posX = (uc.getPos().getX() + atom.getPos().getX()) * scale+scale/2;
          posY = (uc.getPos().getY() + atom.getPos().getY()) * scale+scale/3;
          posZ = ((uc.getPos().getZ() + atom.getPos().getZ()) * scale)-scale;
          s = format("%s %.3f %.3f %.3f", element, posX, posY, posZ);
          surface.write(s + "\n");
          if (atom.isOccupied()) {
            switch ((int) atom.getType()) {
              case 0:
                element = "Au";
                break;
              case 1:
                element = "Cu";
                break;
              case 2:
                element = "Rg";
                break;
              case 3:
                element = "Mg";
                break;
              case 4:
              case 5:
              case 6:
                element = "Al";
                break;
              default:
                element = "Ag";
                break;
            }
            posX = (uc.getPos().getX() + atom.getPos().getX()) * scale;
            posY = (uc.getPos().getY() + atom.getPos().getY()) * scale;
            posZ = (uc.getPos().getZ() + atom.getPos().getZ()) * scale;
            s = format("%s %.3f %.3f %.3f", element, posX, posY, posZ);
            printWriter.write(s + "\n");
            element = "Ag";
          }
        }
      }
      printWriter.flush();
      printWriter.close();
      surface.flush();
      surface.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  /**
   * Writes a SVG format file.
   * 
   * @param fileName
   * @param lattice 
   */
  static void writeSvg(String fileName, AbstractLattice lattice) {
    // Check that is growth simulation, in etching are missing getUc in AbstractLattice and getPos and isOccupied in AbstractAtom
 
    double scale = 5; // default distance to big enough picture
    // create file descriptor. It will be automatically closed.
    try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName))){
      String s = format("<svg height=\"%f\" width=\"%f\">",
          ((AbstractGrowthLattice) lattice).getCartSizeX() * scale,
          ((AbstractGrowthLattice) lattice).getCartSizeY() * scale);
      printWriter.write(s +"\n");
      IUc uc;
      double posX;
      double posY;
      // for each atom in the uc
      for (int i = 0; i < lattice.size(); i++) {
        uc = lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          IAtom atom = uc.getAtom(j);
          posX = (uc.getPos().getX() + atom.getPos().getX()) * scale;
          posY = (uc.getPos().getY() + atom.getPos().getY()) * scale;
          String colour = "white";
          if (atom.isOccupied()) {
            colour = "black";
            switch ((int) atom.getType()) {
              case 0:
                colour = "blue";
                break;
              case 1:
                colour = "red";
                break;
            }
          }
          s = format("<circle cx=\"%.3f\" cy=\"%.3f\" r=\"2.5\" stroke=\"black\" stroke-width=\"0.2\" fill=\"%s\" />", posX, posY, colour);
          printWriter.write(s + "\n");
        }
      }
      
      printWriter.write("</svg>\n");
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  static void writeAdsorptionLowSimulationDataText(double[][] data, String fileName) {
    String separator;
    Locale locale = Locale.UK;
    if (System.getProperty("os.name").contains("Linux")) {
      separator = "\t";
    } else {
      separator = ";";
    }   
    // create file descriptor. It will be automatically closed.
    try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
      // for each byte in the buffer
      String s = "# [1. step 2. time 3. coverage 4. coverageCO 5. coverageO 6. coverageLake 7. coverageGaps]\n";
      if (System.getProperty("os.name").contains("Linux")) {
        out.write(s);
      }
      for (int i = 0; i < data.length; i++) {
        double coverage = data[i][0];
        double t = data[i][1];
        double coverageCO = data[i][2];
        double coverageO = data[i][3];
        double coverageLake = data[i][4];
        double coverageGaps = data[i][5];
        
        if (t > 0 || coverage > 0) {
          s = format(locale, "%d%s%g%s%g%s%g%s%g%s%g%s%g\n",i, separator, t, separator, coverage, separator, coverageCO, separator, coverageO, separator, coverageLake, separator, coverageGaps);
          out.write(s);
        }
      }
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  /**
   * Only used to read input "parameters" file.
   * 
   * @param fileName
   * @return
   * @throws IOException 
   */
  static List<String> readSmallTextFile(String fileName) throws IOException {
    List<String> readText = new ArrayList<>();
    // create file descriptor. It will be automatically closed.
    try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
      String line;
      // <-- read whole line
      line = in.readLine();
      while (line != null) {
                readText.add(line);
        line = in.readLine();
      }
      in.close();
    } catch (FileNotFoundException fe) {
      throw fe;
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
    return readText;
  }
  
  /**
   * The header of the file has to have the following structure: - The "Morphokinetics" keyword -
   * The number of dimensions (int) - The size of each dimension (3 x int) - Trash to fill 64 bytes
   * (in case we need some more space for the future) Right now is 32. Has to be adapted in the
   * future
   *
   * @param dimensions
   * @param sizes
   */
  private static void writeHeaderBinary(int dimensions, int[] sizes, String fileName) {
    // create data output stream with its file output stream
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName))) {
      // write header to the dos
      out.writeUTF("Morphokinetics");
      out.writeInt(dimensions);
      int i;
      for (i = 0; i < dimensions; i++) {
        out.writeInt(sizes[i]);
      }
        for (int j = i; j < MAX_DIMS; j++) {
        out.writeInt(0);
      }

      for (int j = 0; j < 8; j++) {
        out.writeInt(-1);
      }
      out.size();
      // force bytes to the underlying stream
      out.flush();
      // releases all system resources from the streams
      out.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
}
