/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

import static basic.io.Restart.MAX_DIMS;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.StringTokenizer;
import kineticMonteCarlo.atom.IAtom;
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
    FileOutputStream fos;
    DataOutputStream dos;

    try {
      writeHeaderBinary(dimensions, sizes, fileName);
      // create file output stream
      fos = new FileOutputStream(fileName, true);
      // create data output stream
      dos = new DataOutputStream(fos);

      // for each byte in the buffer
      for (int i = 0; i < sizes[0]; i++) {
        for (int j = 0; j < sizes[1]; j++) {
          // write float to the dos
          dos.writeFloat(data[i][j]);
        }
      }

      // force bytes to the underlying stream
      dos.flush();
      // releases all system resources from the streams
      fos.close();
      dos.close();
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
    FileInputStream fis;
    DataInputStream dis;
    int dimensions = -1;
    int i = -1;
    int j = -1;
    int[] sizes = new int[3];
    int tmp = -99;
    try {
      //readHeaderBinary(dimensions, sizes, fileName);
      // create file output stream
      fis = new FileInputStream(fileName);
      // create data output stream
      dis = new DataInputStream(fis);

      dis.skipBytes(16);
      // Read the dimensions of the file
      dimensions = dis.readInt();
      // Read the sizes of the actual dimensions
      for (i = 0; i < dimensions; i++) {
        sizes[i] = dis.readInt();
      }
      // Skip the rest of the dimensions
      for (j = i; j < MAX_DIMS; j++) {
        tmp = dis.readInt();
      }
      // Skip the rest of the header
      for (j = 0; j < 8; j++) {
        tmp = dis.readInt();
      }

      data = new float[sizes[0]][sizes[1]];

      // for each byte in the buffer
      for (i = 0; i < sizes[0]; i++) {
        for (j = 0; j < sizes[1]; j++) {
          // write float to the dos, reading an int
          data[i][j] = (float) dis.readFloat();
        }
      }
      // releases all system resources from the streams
      fis.close();
      dis.close();
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
   * The header of the file has to have the following structure: - The "Morphokinetics" keyword -
   * The number of dimensions (int) - The size of each dimension (3 x int) - Trash to fill 64 bytes
   * (in case we need some more space for the future) Right now is 32. Has to be adapted in the
   * future
   *
   * @param dimensions
   * @param sizes
   */
  private static void writeHeaderBinary(int dimensions, int[] sizes, String fileName) {
    FileOutputStream fos;
    DataOutputStream dos;
    try {
      // create file output stream
      fos = new FileOutputStream(fileName);
      // create data output stream
      dos = new DataOutputStream(fos);

      // write header to the dos
      dos.writeUTF("Morphokinetics");
      dos.writeInt(dimensions);
      int i;
      for (i = 0; i < dimensions; i++) {
        dos.writeInt(sizes[i]);
      }
      for (int j = i; j < MAX_DIMS; j++) {
        dos.writeInt(0);
      }

      for (int j = 0; j < 8; j++) {
        dos.writeInt(-1);
      }
      dos.size();
      // force bytes to the underlying stream
      dos.flush();
      // releases all system resources from the streams
      fos.close();
      dos.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  static void writeLowText1D(float[] data, String fileName) {
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);
      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        String s = format("%.3f", data[i]);
        printWriter.write(i + " " + s + "\n");
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  static void writeLowText2D(float[][] data, String fileName, boolean shift) {
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);

      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[0].length; j++) {
          int posX = i;
          int posY = j;
          if (shift) {
            posX = (posX + data.length / 2) % data.length;
            posY = (posY + data[0].length / 2) % data[0].length;
          }
          String s = format("%.3f", data[i][j]);
          String sLog = format("%.3f", Math.log(data[i][j]));
          printWriter.write(posX + " " + posY + " " + sLog + " " + s + "\n");
        }

        //printWriter.write("\n");
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }

  static float[][] readLowText2D(String fileName) throws FileNotFoundException {
    float[][] data = null;
    System.out.println("Trying to read " + fileName + " file of unknown size ");
    int x = -1;
    int y = -1;
    int sizeY = 0;
    int sizeX = 0;
    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));
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
    float trash;
    try {

      BufferedReader in = new BufferedReader(new FileReader(fileName));
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
        trash = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        data[posX][posY] = Float.parseFloat(tk.nextToken()); // <-- read single word on line and parse to float
        line = in.readLine();
      }
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
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);

      // for each byte in the buffer
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[0].length; j++) {
          String s = format("%.3f", data[i][j]);
          printWriter.write(i + " " + j + " " + s + "\n");
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
   * Given an original surface it reduces by the given factor.
   *
   * @param originalSurface
   * @param factor
   * @return reduced surface
   */
  static float[][] scale(float[][] originalSurface, int factor) {
    int originalSizeX = originalSurface.length;
    int originalSizeY = originalSurface[0].length;
    double scale = (double) 1 / (double) factor;
    int reducedSizeX = (int) Math.ceil(originalSizeX * scale); // This is required to ensure that data properly fits afterwards
    int reducedSizeY = (int) Math.ceil(originalSizeY * scale);

    float[][] reducedSurface = new float[reducedSizeX][reducedSizeY];
    if (scale > 1) {
      System.err.println("Error:scale must be less or equal to 1.");
      return originalSurface;
    }
    // This is really simple interpolation; last position is visited, this value is assigned
    for (int x = 0; x < originalSizeX; x++) {
      for (int y = 0; y < originalSizeY; y++) {
        int reducedX = (int) (x * scale);
        int reducedY = (int) (y * scale);
        reducedSurface[reducedX][reducedY] = originalSurface[x][y];
      }
    }
    return reducedSurface;
  }

  /**
   * Writes a XYZ format file 
   * @param fileName
   * @param lattice 
   */
  static void writeXyz(String fileName, AbstractLattice lattice) {
    // Check that is growth simulation, in etching are missing getUc in AbstractLattice and getPos and isOccupied in AbstractAtom
       
    int numberOfAtoms = lattice.size();
    double scale = 1; // default distance in Anstroms
    String element = "H";
    // Setup
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
    try {
      // create file descriptor
      File file = new File(fileName);
      PrintWriter printWriter = new PrintWriter(file);
      String s;
      s = format("%d", numberOfAtoms);
      printWriter.write(s +"\n simple XYZ file made with Morphokinetics\n");
      
      // for each atom in the uc
      for (int i = 0; i < lattice.size(); i++) {
        IUc uc = lattice.getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          IAtom atom = uc.getAtom(j);
          double posX = (uc.getPos().getX() + atom.getPos().getX()) * scale;
          double posY = (uc.getPos().getY() + atom.getPos().getY()) * scale;
          double posZ = (uc.getPos().getZ() + atom.getPos().getZ()) * scale;
          s = format("%s %.3f %.3f %.3f", element, posX, posY, posZ);
          if (atom.isOccupied()) {
            printWriter.write(s + "\n");
          }
        }

        //printWriter.write("\n");
      }
      printWriter.flush();
      printWriter.close();
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
}
