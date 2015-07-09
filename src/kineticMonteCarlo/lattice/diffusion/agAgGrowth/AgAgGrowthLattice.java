/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.agAgGrowth;

import kineticMonteCarlo.atom.diffusion.Abstract2DDiffusionAtom;
import kineticMonteCarlo.atom.diffusion.agAgGrowth.AgAgAtom;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.diffusion.ModifiedBuffer;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import java.awt.geom.Point2D;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgGrowthLattice extends Abstract2DDiffusionLattice {

  public static final float Y_ratio = (float) Math.sqrt(3) / 2.0f;

  public AgAgGrowthLattice(int sizeX, int sizeY, ModifiedBuffer modified, HopsPerStep distance_per_step) {

    super(sizeX, sizeY, modified, distance_per_step);

    atoms = new AgAgAtom[sizeX][sizeY];

    create_atoms(distance_per_step);
    setAngles();
  }

  private void create_atoms(HopsPerStep distance_per_step) {
    instantiate_atoms(distance_per_step);
    interconnect_atoms();
  }

  private void instantiate_atoms(HopsPerStep distance_per_step) {
    for (int i = 0; i < sizeX; i++) {
      for (int j = 0; j < sizeY; j++) {
        atoms[i][j] = new AgAgAtom((short) i, (short) j, distance_per_step);
      }
    }
  }

  private void interconnect_atoms() {

    for (int j = 0; j < sizeY; j++) {
      for (int i = 0; i < sizeX; i++) {
        AgAgAtom atom = (AgAgAtom) atoms[i][j];
        int X = i;
        int Y = j - 1;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 0);
        X = i + 1;
        Y = j - 1;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 1);
        X = i + 1;
        Y = j;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 2);
        X = i;
        Y = j + 1;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 3);
        X = i - 1;
        Y = j + 1;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 4);
        X = i - 1;
        Y = j;
        if (X < 0) {
          X = sizeX - 1;
        }
        if (X == sizeX) {
          X = 0;
        }
        if (Y < 0) {
          Y = sizeY - 1;
        }
        if (Y == sizeY) {
          Y = 0;
        }
        atom.setNeighbor((AgAgAtom) atoms[X][Y], 5);
      }
    }
  }

  @Override
  public Abstract2DDiffusionAtom getNeighbor(int Xpos, int Ypos, int neighbor) {
    return ((AgAgAtom) atoms[Xpos][Ypos]).getNeighbor(neighbor);
  }

  @Override
  public int getAvailableDistance(int atomType, short Xpos, short Ypos, int thresholdDistance) {

    switch (atomType) {
      case 0:
        return getClearArea_terrace(Xpos, Ypos, thresholdDistance);
      case 2:
        return getClearArea_step(Xpos, Ypos, thresholdDistance);
      default:
        return 0;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getFarSite(int originType, short Xpos, short Ypos, int distance) {

    switch (originType) {
      case 0:
        return chooseClearArea_terrace(Xpos, Ypos, distance, StaticRandom.raw());
      case 2:
        return chooseClearArea_step(Xpos, Ypos, distance, StaticRandom.raw());
      default:
        return null;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getAtom(int X, int Y) {
    return atoms[X][Y];
  }

  @Override
  public Point2D getCentralLatticeLocation() {
    return new Point2D.Float(sizeX / 2.0f, (float) (sizeY * Y_ratio / 2.0));
  }

  @Override
  public float getSpatialSizeX() {
    return sizeX;
  }

  @Override
  public float getSpatialSizeY() {
    return sizeY * Y_ratio;
  }

  @Override
  public Point2D getSpatialLocation(int Xpos, int Ypos) {

    float XLocation = Xpos + Ypos * 0.5f;
    if (XLocation >= sizeX) {
      XLocation -= sizeX;
    }
    float YLocation = Ypos * Y_ratio;
    return new Point2D.Double(XLocation, YLocation);
  }

  public int getClearArea_terrace(short X, short Y, int m) {

    int s = 1;

    int X_v, Y_v;
    X_v = X;
    Y_v = Y - 1;
    byte error_code = 0;
    if (Y_v < 0) {
      Y_v = sizeY - 1;
    }

    out:
    while (true) {
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        X_v++;
        if (X_v == sizeX) {
          X_v = 0;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        Y_v++;
        if (Y_v == sizeY) {
          Y_v = 0;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        Y_v++;
        X_v--;
        if (Y_v == sizeY) {
          Y_v = 0;
        }
        if (X_v < 0) {
          X_v = sizeX - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        X_v--;
        if (X_v < 0) {
          X_v = sizeX - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        Y_v--;
        if (Y_v < 0) {
          Y_v = sizeY - 1;
        }
      }
      for (int i = 0; i < s; i++) {
        if (atoms[X_v][Y_v].is_outside()) {
          error_code |= 1;
        }
        if (atoms[X_v][Y_v].isOccupied()) {
          error_code |= 2;
          break out;
        }
        Y_v--;
        X_v++;
        if (Y_v < 0) {
          Y_v = sizeY - 1;
        }
        if (X_v == sizeX) {
          X_v = 0;
        }
      }

      if (error_code != 0) {
        break;
      }
      if (s >= m) {
        return s;
      }
      s++;
      Y_v--;
      if (Y_v < 0) {
        Y_v = sizeY - 1;
      }
    }

    if ((error_code & 2) != 0) {
      return s - 1;
    }
    if ((error_code & 1) != 0) {
      return s;
    }
    return -1;
  }

  public Abstract2DDiffusionAtom chooseClearArea_terrace(short X, short Y, int s, double raw) {

    int temp = (int) (raw * (s * 6));

    int X_v, Y_v;
    X_v = X;
    Y_v = Y - s;
    if (Y_v < 0) {
      Y_v = sizeY - 1;
    }

    int contador = 0;

    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      X_v++;
      if (X_v == sizeX) {
        X_v = 0;
      }
    }
    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      Y_v++;
      if (Y_v == sizeY) {
        Y_v = 0;
      }
    }
    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      Y_v++;
      X_v--;
      if (Y_v == sizeY) {
        Y_v = 0;
      }
      if (X_v < 0) {
        X_v = sizeX - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      X_v--;
      if (X_v < 0) {
        X_v = sizeX - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      Y_v--;
      if (Y_v < 0) {
        Y_v = sizeY - 1;
      }
    }
    for (int i = 0; i < s; i++) {
      contador++;
      if (contador > temp) {
        return atoms[X_v][Y_v];
      }
      Y_v--;
      X_v++;
      if (Y_v < 0) {
        Y_v = sizeY - 1;
      }
      if (X_v == sizeX) {
        X_v = 0;
      }
    }

    return null;

  }

  public int getClearArea_step(short X, short Y, int m) {

    int s = 1;
    int X_v, Y_v;

    switch (atoms[X][Y].getOrientation()) {
      case 0:
      case 3:
        while (true) {
          X_v = X + s;
          if (X_v >= sizeX) {
            X_v = 0;
          }
          if (atoms[X_v][Y].isOccupied() || atoms[X_v][Y].getType() < 2) {
            return s - 1;
          }
          X_v = X - s;
          if (X_v < 0) {
            X_v = sizeX - 1;
          }
          if (atoms[X_v][Y].isOccupied() || atoms[X_v][Y].getType() < 2) {
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
          Y_v = Y + s;
          if (Y_v >= sizeY) {
            Y_v = 0;
          }
          if (atoms[X][Y_v].isOccupied() || atoms[X][Y_v].getType() < 2) {
            return s - 1;
          }
          Y_v = Y - s;
          if (Y_v < 0) {
            Y_v = sizeY - 1;
          }
          if (atoms[X][Y_v].isOccupied() || atoms[X][Y_v].getType() < 2) {
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
          X_v = X - s;
          if (X_v < 0) {
            X_v = sizeX - 1;
          }
          Y_v = Y + s;
          if (Y_v >= sizeY) {
            Y_v = 0;
          }
          if (atoms[X_v][Y_v].isOccupied() || atoms[X_v][Y_v].getType() < 2) {
            return s - 1;
          }
          X_v = X + s;
          if (X_v >= sizeX) {
            X_v = 0;
          }
          Y_v = Y - s;
          if (Y_v < 0) {
            Y_v = sizeY - 1;
          }
          if (atoms[X_v][Y_v].isOccupied() || atoms[X_v][Y_v].getType() < 2) {
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

  public Abstract2DDiffusionAtom chooseClearArea_step(short X, short Y, int s, double raw) {

    int X_v, Y_v;

    switch (atoms[X][Y].getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          X_v = X + s;
          if (X_v >= sizeX) {
            X_v = 0;
          }
          return atoms[X_v][Y];
        } else {
          X_v = X - s;
          if (X_v < 0) {
            X_v = sizeX - 1;
          }
          return atoms[X_v][Y];
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          Y_v = Y + s;
          if (Y_v >= sizeY) {
            Y_v = 0;
          }
          return atoms[X][Y_v];
        } else {
          Y_v = Y - s;
          if (Y_v < 0) {
            Y_v = sizeY - 1;
          }
          return atoms[X][Y_v];
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          X_v = X - s;
          if (X_v < 0) {
            X_v = sizeX - 1;
          }
          Y_v = Y + s;
          if (Y_v >= sizeY) {
            Y_v = 0;
          }
          return atoms[X_v][Y_v];
        } else {
          X_v = X + s;
          if (X_v >= sizeX) {
            X_v = 0;
          }
          Y_v = Y - s;
          if (Y_v < 0) {
            Y_v = sizeY - 1;
          }
          return atoms[X_v][Y_v];
        }
    }
    return null;
  }

}
