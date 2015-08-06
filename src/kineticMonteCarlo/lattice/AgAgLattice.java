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
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgLattice extends Abstract2DDiffusionLattice {

  public static final float YRatio = (float) Math.sqrt(3) / 2.0f; // it is the same as: sin 60º

  public AgAgLattice(int axonSizeI, int axonSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(axonSizeI, axonSizeJ, modified);

    atoms = new AgAgAtom[axonSizeI][axonSizeJ];

    createAtoms(distancePerStep);
    setAngles();
  }

  private void createAtoms(HopsPerStep distancePerStep) {
    instantiateAtoms(distancePerStep);
    interconnectAtoms();
  }

  private void instantiateAtoms(HopsPerStep distancePerStep) {
    for (int i = 0; i < axonSizeI; i++) {
      for (int j = 0; j < axonSizeJ; j++) {
        atoms[i][j] = new AgAgAtom((short) i, (short) j, distancePerStep);
      }
    }
  }

  private void interconnectAtoms() {

    for (int jAxon = 0; jAxon < axonSizeJ; jAxon++) {
      for (int iAxon = 0; iAxon < axonSizeI; iAxon++) {
        AgAgAtom atom = (AgAgAtom) atoms[iAxon][jAxon];
        int X = iAxon;
        int Y = jAxon - 1;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 0);
        X = iAxon + 1;
        Y = jAxon - 1;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 1);
        X = iAxon + 1;
        Y = jAxon;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 2);
        X = iAxon;
        Y = jAxon + 1;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 3);
        X = iAxon - 1;
        Y = jAxon + 1;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 4);
        X = iAxon - 1;
        Y = jAxon;
        if (X < 0) {
          X = axonSizeI - 1;
        }
        if (X == axonSizeI) {
          X = 0;
        }
        if (Y < 0) {
          Y = axonSizeJ - 1;
        }
        if (Y == axonSizeJ) {
          Y = 0;
        }
        atom.setNeighbour((AgAgAtom) atoms[X][Y], 5);
      }
    }
  }

  @Override
  public Abstract2DDiffusionAtom getNeighbour(int iAxon, int jAxon, int neighbour) {
    return ((AgAgAtom) atoms[iAxon][jAxon]).getNeighbour(neighbour);
  }

  @Override
  public int getAvailableDistance(int atomType, short iAxon, short jAxon, int thresholdDistance) {

    switch (atomType) {
      case 0:
        return getClearAreaTerrace(iAxon, jAxon, thresholdDistance);
      case 2:
        return getClearAreaStep(iAxon, jAxon, thresholdDistance);
      default:
        return 0;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getFarSite(int originType, short iAxon, short jAxon, int distance) {

    switch (originType) {
      case 0:
        return chooseClearAreaTerrace(iAxon, jAxon, distance, StaticRandom.raw());
      case 2:
        return chooseClearAreaStep(iAxon, jAxon, distance, StaticRandom.raw());
      default:
        return null;
    }
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(axonSizeI / 2.0f, (float) (axonSizeJ * YRatio / 2.0f));
  }

  @Override
  public float getCartSizeX() {
    return axonSizeI;
  }

  @Override
  public float getCartSizeY() {
    return axonSizeJ * YRatio;
  }

  @Override
  public Point2D getCartesianLocation(int iAxon, int jAxon) {

    float xCart = iAxon + jAxon * 0.5f;
    if (xCart >= axonSizeI) {
      xCart -= axonSizeI;
    }
    float yCart = jAxon * YRatio;
    return new Point2D.Double(xCart, yCart);
  }

  public int getClearAreaTerrace(short iAxonOrigin, short jAxonOrigin, int m) {

    int s = 1;

    int iAxon = iAxonOrigin;
    int jAxon = jAxonOrigin - 1;
    byte errorCode = 0;
    if (jAxon < 0) {
      jAxon = axonSizeJ - 1;
    }

    out:
    while (true) {
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon++;
        if (iAxon == axonSizeI) {
          iAxon = 0;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        jAxon++;
        if (jAxon == axonSizeJ) {
          jAxon = 0;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        jAxon++;
        iAxon--;
        if (jAxon == axonSizeJ) {
          jAxon = 0;
        }
        if (iAxon < 0) {
          iAxon = axonSizeI - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon--;
        if (iAxon < 0) {
          iAxon = axonSizeI - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        jAxon--;
        if (jAxon < 0) {
          jAxon = axonSizeJ - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[iAxon][jAxon].isOutside()) {
          errorCode |= 1;
        }
        if (atoms[iAxon][jAxon].isOccupied()) {
          errorCode |= 2;
          break out;
        }
        jAxon--;
        iAxon++;
        if (jAxon < 0) {
          jAxon = axonSizeJ - 1;
        }
        if (iAxon == axonSizeI) {
          iAxon = 0;
        }
      }

      if (errorCode != 0) {
        break;
      }
      if (s >= m) {
        return s;
      }
      s++;
      jAxon--;
      if (jAxon < 0) {
        jAxon = axonSizeJ - 1;
      }
    }

    if ((errorCode & 2) != 0) {
      return s - 1;
    }
    if ((errorCode & 1) != 0) {
      return s;
    }
    return -1;
  }

  public Abstract2DDiffusionAtom chooseClearAreaTerrace(short iAxonOrigin, short jAxonOrigin, int s, double raw) {

    int temp = (int) (raw * (s * 6));

    int iAxon = iAxonOrigin;
    int jAxon = jAxonOrigin - s;
    if (jAxon < 0) {
      jAxon = axonSizeJ - 1;
    }

    int counter = 0;

    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      iAxon++;
      if (iAxon == axonSizeI) {
        iAxon = 0;
      }
    }
    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      jAxon++;
      if (jAxon == axonSizeJ) {
        jAxon = 0;
      }
    }
    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      jAxon++;
      iAxon--;
      if (jAxon == axonSizeJ) {
        jAxon = 0;
      }
      if (iAxon < 0) {
        iAxon = axonSizeI - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      iAxon--;
      if (iAxon < 0) {
        iAxon = axonSizeI - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      jAxon--;
      if (jAxon < 0) {
        jAxon = axonSizeJ - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      jAxon--;
      iAxon++;
      if (jAxon < 0) {
        jAxon = axonSizeJ - 1;
      }
      if (iAxon == axonSizeI) {
        iAxon = 0;
      }
    }

    return null;

  }

  public int getClearAreaStep(short iAxonOrigin, short jAxonOrigin, int m) {

    int s = 1;
    int iAxon;
    int jAxon;

    switch (atoms[iAxonOrigin][jAxonOrigin].getOrientation()) {
      case 0:
      case 3:
        while (true) {
          iAxon = iAxonOrigin + s;
          if (iAxon >= axonSizeI) {
            iAxon = 0;
          }
          if (atoms[iAxon][jAxonOrigin].isOccupied() || atoms[iAxon][jAxonOrigin].getType() < 2) {
            return s - 1;
          }
          iAxon = iAxonOrigin - s;
          if (iAxon < 0) {
            iAxon = axonSizeI - 1;
          }
          if (atoms[iAxon][jAxonOrigin].isOccupied() || atoms[iAxon][jAxonOrigin].getType() < 2) {
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
          jAxon = jAxonOrigin + s;
          if (jAxon >= axonSizeJ) {
            jAxon = 0;
          }
          if (atoms[iAxonOrigin][jAxon].isOccupied() || atoms[iAxonOrigin][jAxon].getType() < 2) {
            return s - 1;
          }
          jAxon = jAxonOrigin - s;
          if (jAxon < 0) {
            jAxon = axonSizeJ - 1;
          }
          if (atoms[iAxonOrigin][jAxon].isOccupied() || atoms[iAxonOrigin][jAxon].getType() < 2) {
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
          iAxon = iAxonOrigin - s;
          if (iAxon < 0) {
            iAxon = axonSizeI - 1;
          }
          jAxon = jAxonOrigin + s;
          if (jAxon >= axonSizeJ) {
            jAxon = 0;
          }
          if (atoms[iAxon][jAxon].isOccupied() || atoms[iAxon][jAxon].getType() < 2) {
            return s - 1;
          }
          iAxon = iAxonOrigin + s;
          if (iAxon >= axonSizeI) {
            iAxon = 0;
          }
          jAxon = jAxonOrigin - s;
          if (jAxon < 0) {
            jAxon = axonSizeJ - 1;
          }
          if (atoms[iAxon][jAxon].isOccupied() || atoms[iAxon][jAxon].getType() < 2) {
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

  public Abstract2DDiffusionAtom chooseClearAreaStep(short iAxonOrigin, short jAxonOrigin, int s, double raw) {

    int iAxon;
    int jAxon;

    switch (atoms[iAxonOrigin][jAxonOrigin].getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          iAxon = iAxonOrigin + s;
          if (iAxon >= axonSizeI) {
            iAxon = 0;
          }
          return atoms[iAxon][jAxonOrigin];
        } else {
          iAxon = iAxonOrigin - s;
          if (iAxon < 0) {
            iAxon = axonSizeI - 1;
          }
          return atoms[iAxon][jAxonOrigin];
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          jAxon = jAxonOrigin + s;
          if (jAxon >= axonSizeJ) {
            jAxon = 0;
          }
          return atoms[iAxonOrigin][jAxon];
        } else {
          jAxon = jAxonOrigin - s;
          if (jAxon < 0) {
            jAxon = axonSizeJ - 1;
          }
          return atoms[iAxonOrigin][jAxon];
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          iAxon = iAxonOrigin - s;
          if (iAxon < 0) {
            iAxon = axonSizeI - 1;
          }
          jAxon = jAxonOrigin + s;
          if (jAxon >= axonSizeJ) {
            jAxon = 0;
          }
          return atoms[iAxon][jAxon];
        } else {
          iAxon = iAxonOrigin + s;
          if (iAxon >= axonSizeI) {
            iAxon = 0;
          }
          jAxon = jAxonOrigin - s;
          if (jAxon < 0) {
            jAxon = axonSizeJ - 1;
          }
          return atoms[iAxon][jAxon];
        }
    }
    return null;
  }

}
