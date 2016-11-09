/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import android.content.Context;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import eus.ehu.dipc.morphokinetics.R;
import kineticMonteCarlo.atom.IAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AbstractLattice;
import kineticMonteCarlo.lattice.AgLattice;
import kineticMonteCarlo.lattice.GrapheneLattice;
import kineticMonteCarlo.lattice.SiLattice;
import kineticMonteCarlo.unitCell.IUc;

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

  static float[][] readLowText2D(String fileName, Context androidContext) throws FileNotFoundException {
    float[][] data = null;
    System.out.println("Trying to read " + fileName + " file of unknown size ");
    InputStream inputStream = androidContext.getResources().openRawResource(R.raw.reentrancesperanglehexagonal10million);
    System.out.println("resources " + androidContext.getResources() + " is " + inputStream);
    InputStreamReader inputreader = new InputStreamReader(inputStream);

    BufferedReader in = new BufferedReader(inputreader);
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
          IAtom atom = uc.getAtom(j);
          if (atom.isOccupied()) {
            posX = (uc.getPos().getX() + atom.getPos().getX()) * scale;
            posY = (uc.getPos().getY() + atom.getPos().getY()) * scale;
            posZ = (uc.getPos().getZ() + atom.getPos().getZ()) * scale;
            s = format("%s %.3f %.3f %.3f", element, posX, posY, posZ);
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
