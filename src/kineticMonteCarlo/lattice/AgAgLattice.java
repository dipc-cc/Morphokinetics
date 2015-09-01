/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;
import kineticMonteCarlo.atom.AgAgAtom;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.ModifiedBuffer;
import java.awt.geom.Point2D;
import static kineticMonteCarlo.atom.AbstractAtom.EDGE;
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgLattice extends Abstract2DDiffusionLattice {

  public static final float YRatio = (float) Math.sqrt(3) / 2.0f; // it is the same as: sin 60º

  public AgAgLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(hexaSizeI, hexaSizeJ, modified);

    atoms = new AgAgAtom[hexaSizeI][hexaSizeJ];

    createAtoms(distancePerStep);
    setAngles();
  }

  private void createAtoms(HopsPerStep distancePerStep) {
    instantiateAtoms(distancePerStep);
    interconnectAtoms();
  }

  private void instantiateAtoms(HopsPerStep distancePerStep) {
    for (int i = 0; i < hexaSizeI; i++) {
      for (int j = 0; j < hexaSizeJ; j++) {
        atoms[i][j] = new AgAgAtom((short) i, (short) j, distancePerStep);
      }
    }
  }

  private void interconnectAtoms() {

    for (int jHexa = 0; jHexa < hexaSizeJ; jHexa++) {
      for (int iHexa = 0; iHexa < hexaSizeI; iHexa++) {
        AgAgAtom atom = (AgAgAtom) atoms[iHexa][jHexa];
        int X = iHexa;
        int Y = jHexa - 1;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 0);
        X = iHexa + 1;
        Y = jHexa - 1;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 1);
        X = iHexa + 1;
        Y = jHexa;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 2);
        X = iHexa;
        Y = jHexa + 1;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 3);
        X = iHexa - 1;
        Y = jHexa + 1;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 4);
        X = iHexa - 1;
        Y = jHexa;
        if (X < 0) {
          X = hexaSizeI - 1;
        }
        if (X == hexaSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = hexaSizeJ - 1;
        }
        if (Y == hexaSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 5);
      }
    }
  }

  @Override
  public Abstract2DDiffusionAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    return ((AgAgAtom) atoms[iHexa][jHexa]).getNeighbour(neighbour);
  }

  @Override
  public int getAvailableDistance(int atomType, short iHexa, short jHexa, int thresholdDistance) {

    switch (atomType) {
      case TERRACE:
        return getClearAreaTerrace(iHexa, jHexa, thresholdDistance);
      case EDGE:
        return getClearAreaStep(iHexa, jHexa, thresholdDistance);
      default:
        return 0;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getFarSite(int originType, short iHexa, short jHexa, int distance) {

    switch (originType) {
      case TERRACE:
        return chooseClearAreaTerrace(iHexa, jHexa, distance, StaticRandom.raw());
      case EDGE:
        return chooseClearAreaStep(iHexa, jHexa, distance, StaticRandom.raw());
      default:
        return null;
    }
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(hexaSizeI / 2.0f, (float) (hexaSizeJ * YRatio / 2.0f));
  }

  @Override
  public float getCartSizeX() {
    return hexaSizeI;
  }

  @Override
  public float getCartSizeY() {
    return hexaSizeJ * YRatio;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {

    float xCart = iHexa + jHexa * 0.5f;
    if (xCart >= hexaSizeI) {
      xCart -= hexaSizeI;
    }
    float yCart = jHexa * YRatio;
    return new Point2D.Double(xCart, yCart);
  }

  /**
   * The Cartesian X is the location I, plus the half of J.
   * We have to do the module to ensure that fits in a rectangular Cartesian mesh
   * @param iHexa
   * @param jHexa
   * @return
   */
  @Override
  public double getCartX(int iHexa, int jHexa) {
    float xCart = (iHexa + jHexa * 0.5f) % hexaSizeI;
    return xCart;
  }

  /**
   * Simple relation between Y (Cartesian) and J (hexagonal),
   * with YRatio (=sin 60º)
   * @param jHexa
   * @return
   */
  @Override
  public double getCartY(int jHexa) {
    return jHexa * YRatio;
  }

  public int getClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {

    int possibleDistance = 1;

    int i = iHexaOrigin;
    int j = jHexaOrigin - 1;
    byte errorCode = 0;
    if (j < 0) {
      j = hexaSizeJ - 1;
    }
    
    // This while follows this iteration pattern:
    // go right, up, left up, left, down, right down (-1), jump down and increment
    //
    // This implementation is clearly not efficient (simple profiling is enough to demonstrate)
    out:
    while (true) {
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        i++;
        if (i == hexaSizeI) i = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        j++;
        if (j == hexaSizeJ) j = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        j++;
        i--;
        if (j == hexaSizeJ) j = 0;
        if (i < 0) i = hexaSizeI - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        i--;
        if (i < 0) i = hexaSizeI - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        j--;
        if (j < 0) j = hexaSizeJ - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (atoms[i][j].isOutside()) errorCode |= 1;
        if (atoms[i][j].isOccupied()) {errorCode |= 2; break out;}
        j--;
        i++;
        if (j < 0) j = hexaSizeJ - 1;
        if (i == hexaSizeI) i = 0;
      }

      if (errorCode != 0) break;
      if (possibleDistance >= thresholdDistance) return possibleDistance;
      possibleDistance++;
      j--;
      if (j < 0) j = hexaSizeJ - 1;
      
    }
    if ((errorCode & 2) != 0) return possibleDistance - 1;
    if ((errorCode & 1) != 0) return possibleDistance;
    return -1;
  }

  public Abstract2DDiffusionAtom chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {

    int tmp = (int) (raw * (distance * 6));

    int iHexa = iHexaOrigin;
    int jHexa = jHexaOrigin - distance;
    if (jHexa < 0) {
      jHexa = hexaSizeJ - 1;
    }

    int counter = 0;

    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      iHexa++;
      if (iHexa == hexaSizeI) {
        iHexa = 0;
      }
    }
    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      jHexa++;
      if (jHexa == hexaSizeJ) {
        jHexa = 0;
      }
    }
    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      jHexa++;
      iHexa--;
      if (jHexa == hexaSizeJ) {
        jHexa = 0;
      }
      if (iHexa < 0) {
        iHexa = hexaSizeI - 1;
      }
    }
    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      iHexa--;
      if (iHexa < 0) {
        iHexa = hexaSizeI - 1;
      }
    }
    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      jHexa--;
      if (jHexa < 0) {
        jHexa = hexaSizeJ - 1;
      }
    }
    for (int i = 0; i < distance; i++) {
      counter++;
      if (counter > tmp) {
        return atoms[iHexa][jHexa];
      }
      jHexa--;
      iHexa++;
      if (jHexa < 0) {
        jHexa = hexaSizeJ - 1;
      }
      if (iHexa == hexaSizeI) {
        iHexa = 0;
      }
    }

    return null;

  }

  public int getClearAreaStep(short iHexaOrigin, short jHexaOrigin, int m) {

    int s = 1;
    int iHexa;
    int jHexa;

    switch (atoms[iHexaOrigin][jHexaOrigin].getOrientation()) {
      case 0:
      case 3:
        while (true) {
          iHexa = iHexaOrigin + s;
          if (iHexa >= hexaSizeI) {
            iHexa = 0;
          }
          if (atoms[iHexa][jHexaOrigin].isOccupied() || atoms[iHexa][jHexaOrigin].getType() < 2) {
            return s - 1;
          }
          iHexa = iHexaOrigin - s;
          if (iHexa < 0) {
            iHexa = hexaSizeI - 1;
          }
          if (atoms[iHexa][jHexaOrigin].isOccupied() || atoms[iHexa][jHexaOrigin].getType() < 2) {
            return s - 1;
          }
          if (s == m) {
            return s;
          }
          s++;
        }

      case 1:
      case 4:
        while (true) {
          jHexa = jHexaOrigin + s;
          if (jHexa >= hexaSizeJ) {
            jHexa = 0;
          }
          if (atoms[iHexaOrigin][jHexa].isOccupied() || atoms[iHexaOrigin][jHexa].getType() < 2) {
            return s - 1;
          }
          jHexa = jHexaOrigin - s;
          if (jHexa < 0) {
            jHexa = hexaSizeJ - 1;
          }
          if (atoms[iHexaOrigin][jHexa].isOccupied() || atoms[iHexaOrigin][jHexa].getType() < 2) {
            return s - 1;
          }
          if (s == m) {
            return s;
          }
          s++;
        }

      case 2:
      case 5:
        while (true) {
          iHexa = iHexaOrigin - s;
          if (iHexa < 0) {
            iHexa = hexaSizeI - 1;
          }
          jHexa = jHexaOrigin + s;
          if (jHexa >= hexaSizeJ) {
            jHexa = 0;
          }
          if (atoms[iHexa][jHexa].isOccupied() || atoms[iHexa][jHexa].getType() < 2) {
            return s - 1;
          }
          iHexa = iHexaOrigin + s;
          if (iHexa >= hexaSizeI) {
            iHexa = 0;
          }
          jHexa = jHexaOrigin - s;
          if (jHexa < 0) {
            jHexa = hexaSizeJ - 1;
          }
          if (atoms[iHexa][jHexa].isOccupied() || atoms[iHexa][jHexa].getType() < 2) {
            return s - 1;
          }
          if (s == m) {
            return s;
          }
          s++;
        }

      default:
        return -1;
    }
  }

  public Abstract2DDiffusionAtom chooseClearAreaStep(short iHexaOrigin, short jHexaOrigin, int s, double raw) {

    int iHexa;
    int jHexa;

    switch (atoms[iHexaOrigin][jHexaOrigin].getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          iHexa = iHexaOrigin + s;
          if (iHexa >= hexaSizeI) {
            iHexa = 0;
          }
          return atoms[iHexa][jHexaOrigin];
        } else {
          iHexa = iHexaOrigin - s;
          if (iHexa < 0) {
            iHexa = hexaSizeI - 1;
          }
          return atoms[iHexa][jHexaOrigin];
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          jHexa = jHexaOrigin + s;
          if (jHexa >= hexaSizeJ) {
            jHexa = 0;
          }
          return atoms[iHexaOrigin][jHexa];
        } else {
          jHexa = jHexaOrigin - s;
          if (jHexa < 0) {
            jHexa = hexaSizeJ - 1;
          }
          return atoms[iHexaOrigin][jHexa];
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          iHexa = iHexaOrigin - s;
          if (iHexa < 0) {
            iHexa = hexaSizeI - 1;
          }
          jHexa = jHexaOrigin + s;
          if (jHexa >= hexaSizeJ) {
            jHexa = 0;
          }
          return atoms[iHexa][jHexa];
        } else {
          iHexa = iHexaOrigin + s;
          if (iHexa >= hexaSizeI) {
            iHexa = 0;
          }
          jHexa = jHexaOrigin - s;
          if (jHexa < 0) {
            jHexa = hexaSizeJ - 1;
          }
          return atoms[iHexa][jHexa];
        }
    }
    return null;
  }

}
