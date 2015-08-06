/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.GrapheneAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import java.awt.geom.Point2D;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class GrapheneLattice extends Abstract2DDiffusionLattice {

  private static int[] latticeNeighborhoodData;
  private static final double cos60 = Math.cos(60 * Math.PI / 180);
  private static final double cos30 = Math.cos(30 * Math.PI / 180);

  public GrapheneLattice(int axonSizeI, int axonSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(axonSizeI, axonSizeJ, modified);

    atoms = new GrapheneAtom[axonSizeI][axonSizeJ];

    if (latticeNeighborhoodData == null) {
      initializeNeighborHoodCache();
    }
    createAtoms(distancePerStep);
    setAngles();
  }

  private static void initializeNeighborHoodCache() {

    latticeNeighborhoodData = new int[12];
    latticeNeighborhoodData[0] = (1 & 0xFFFF) + (0 << 16);
    latticeNeighborhoodData[1] = (-1 << 16);
    latticeNeighborhoodData[2] = (1 << 16);

    latticeNeighborhoodData[3] = (1 & 0xFFFF) + (1 << 16);
    latticeNeighborhoodData[4] = (1 & 0xFFFF) + (-1 << 16);
    latticeNeighborhoodData[5] = (-2 << 16);
    latticeNeighborhoodData[6] = (-1 & 0xFFFF) + (-1 << 16);
    latticeNeighborhoodData[7] = (-1 & 0xFFFF) + (1 << 16);
    latticeNeighborhoodData[8] = (2 << 16);

    latticeNeighborhoodData[9] = (1 & 0xFFFF) + (2 << 16);
    latticeNeighborhoodData[10] = (1 & 0xFFFF) + (-2 << 16);
    latticeNeighborhoodData[11] = (-1 & 0xFFFF) + (0 << 16);
  }

  private void createAtoms(HopsPerStep distancePerStep) {

    for (int iAxon = 0; iAxon < axonSizeI; iAxon += 2) {
      for (int jAxon = 0; jAxon < axonSizeJ; jAxon += 2) {
        //para cada unit cell

        //atomo 0 de la unit cell, tipo 0
        atoms[iAxon][jAxon] = new GrapheneAtom((short) iAxon, (short) jAxon, distancePerStep);

        iAxon++;
        //atomo 1 de la unit cell, tipo 1
        atoms[iAxon][jAxon] = new GrapheneAtom((short) iAxon, (short) jAxon, distancePerStep);

        iAxon--;
        jAxon++;
        //atomo 2 de la unit cell, tipo 1   
        atoms[iAxon][jAxon] = new GrapheneAtom((short) iAxon, (short) jAxon, distancePerStep);

        iAxon++;
        //atomo 3 de la unit cell, tipo 0
        atoms[iAxon][jAxon] = new GrapheneAtom((short) iAxon, (short) jAxon, distancePerStep);

        iAxon--;
        jAxon--;
      }
    }
  }

  @Override
  public GrapheneAtom getNeighbour(int xCart, int yCart, int neighbour) {

    int vec = latticeNeighborhoodData[neighbour];                      //esto define el tipo de atomo
    int vec_X = (short) (vec & 0xFFFF);
    int vec_Y = ((vec >> 16));
    if (((xCart + yCart) & 1) != 0) {
      vec_X = -vec_X;
      vec_Y = -vec_Y;
    }
    int iAxon = xCart + vec_X;
    if (iAxon < 0) {
      iAxon += axonSizeI;
    } else if (iAxon >= axonSizeI) {
      iAxon -= axonSizeI;
    }
    int jAxon = yCart + vec_Y;
    if (jAxon < 0) {
      jAxon += axonSizeJ;
    } else if (jAxon >= axonSizeJ) {
      jAxon -= axonSizeJ;
    }
    return (GrapheneAtom) atoms[iAxon][jAxon];
  }

  public int getCartPosX(int iAxon, int jAxon, int pos, boolean type0) {
    int vec_X = (short) (latticeNeighborhoodData[pos] & 0xFFFF);
    if (!type0) {
      vec_X = -vec_X;
    }
    int posXV = iAxon + vec_X;
    if (posXV < 0) {
      posXV += axonSizeI;
    } else if (posXV >= axonSizeI) {
      posXV -= axonSizeI;
    }
    return posXV;
  }

  public int getCartPosY(int iAxon, int jAxon, int pos, boolean type0) {
    int vec_Y = ((latticeNeighborhoodData[pos] >> 16));
    if (!type0) {
      vec_Y = -vec_Y;
    }
    int posYV = jAxon + vec_Y;
    if (posYV < 0) {
      posYV += axonSizeJ;
    } else if (posYV >= axonSizeJ) {
      posYV -= axonSizeJ;
    }
    return posYV;
  }

  @Override
  public int getAvailableDistance(int atomType, short iAxon, short jAxon, int thresholdDistance) {

    int[] point = new int[2];
    switch (atomType) {
      case 0:
        return getClearAreaTerrace(iAxon, jAxon, thresholdDistance);
      case 2:
        return getClearAreaZigzag(iAxon, jAxon, thresholdDistance, point, StaticRandom.raw());
      case 3:
        return getClearAreaArmchair(iAxon, jAxon, thresholdDistance, point, StaticRandom.raw());
      default:
        return 0;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getFarSite(int originType, short iAxon, short jAxon, int distance) {

    int[] point = new int[2];
    switch (originType) {
      case 0:
        return chooseClearAreaTerrace(iAxon, jAxon, distance, StaticRandom.raw());
      case 2:
        getClearAreaZigzag(iAxon, jAxon, distance, point, StaticRandom.raw());
        return this.getAtom(point[0], point[1]);
      case 3:
        getClearAreaArmchair(iAxon, jAxon, distance, point, StaticRandom.raw());
        return this.getAtom(point[0], point[1]);
      default:
        return null;
    }
  }

  private Abstract2DDiffusionAtom chooseClearAreaTerrace(short iAxonOrigin, short jAxonOrigin, int s, double raw) {

    int temp = (int) (raw * (s * 2 * 6));

    boolean type0 = (((iAxonOrigin + jAxonOrigin) & 1) == 0);
    int iAxon = iAxonOrigin;
    int jAxon = (jAxonOrigin - s * 2);
    if (jAxon < 0) {
      jAxon += axonSizeJ;
    }

    int counter = 0;

    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 0, true);
        jAxon = getCartPosY(iAxon, jAxon, 0, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 1, false);
        jAxon = getCartPosY(iAxon, jAxon, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 2, true);
        jAxon = getCartPosY(iAxon, jAxon, 2, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 1, false);
        jAxon = getCartPosY(iAxon, jAxon, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 2, true);
        jAxon = getCartPosY(iAxon, jAxon, 2, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 0, false);
        jAxon = getCartPosY(iAxon, jAxon, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 1, true);
        jAxon = getCartPosY(iAxon, jAxon, 1, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 0, false);
        jAxon = getCartPosY(iAxon, jAxon, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 1, true);
        jAxon = getCartPosY(iAxon, jAxon, 1, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 2, false);
        jAxon = getCartPosY(iAxon, jAxon, 2, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iAxon][jAxon];
      }
      if (type0) {
        iAxon = getCartPosX(iAxon, jAxon, 0, true);
        jAxon = getCartPosY(iAxon, jAxon, 0, true);
      } else {
        iAxon = getCartPosX(iAxon, jAxon, 2, false);
        jAxon = getCartPosY(iAxon, jAxon, 2, false);
      }
      type0 = !type0;
    }
    return null;
  }

  private int getClearAreaTerrace(short iAxonOrigin, short jAxonOrigin, int m) {

    int s = 1;

    boolean type0 = (((iAxonOrigin + jAxonOrigin) & 1) == 0);

    short iAxon;
    iAxon = iAxonOrigin;

    short jAxon = (short) (jAxonOrigin - 2);
    if (jAxon < 0) {
      jAxon += axonSizeJ;
    }
    byte errorCode = 0;

    out:
    while (true) {

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 0);
        } else {
          a = getNeighbour(iAxon, jAxon, 1);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 2);
        } else {
          a = getNeighbour(iAxon, jAxon, 1);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 2);
        } else {
          a = getNeighbour(iAxon, jAxon, 0);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 1);
        } else {
          a = getNeighbour(iAxon, jAxon, 0);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 1);
        } else {
          a = getNeighbour(iAxon, jAxon, 2);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iAxon, jAxon, 0);
        } else {
          a = getNeighbour(iAxon, jAxon, 2);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iAxon = a.getX();
        jAxon = a.getY();
        type0 = !type0;
      }

      if (errorCode != 0) {
        break;
      }
      if (s >= m) {
        return s;
      }
      s++;
      jAxon -= 2;
      if (jAxon < 0) {
        jAxon = (short) (axonSizeJ - 1);
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

  private int getClearAreaZigzag(short iAxonOrigin, short jAxonOrigin, int m, int[] XY_destino, double raw) {

    int s = 1;
    int orientation = atoms[iAxonOrigin][jAxonOrigin].getOrientation();

    int iAxon1 = iAxonOrigin;
    int jAxon1 = jAxonOrigin;
    int iAxon2 = iAxonOrigin;
    int jAxon2 = jAxonOrigin;

    int neighbour1, neighbour2;

    switch (orientation) {

      case 0:
        neighbour1 = 5;
        neighbour2 = 8;
        break;
      case 1:
        neighbour1 = 4;
        neighbour2 = 7;
        break;
      case 2:
        neighbour1 = 3;
        neighbour2 = 6;
        break;
      default: {
        return -1;
      }
    }

    while (true) {
      int type1, type2;

      iAxon1 = getCartPosX(iAxon1, jAxon1, neighbour1, true);
      iAxon2 = getCartPosX(iAxon2, jAxon2, neighbour2, true);
      jAxon1 = getCartPosY(iAxon1, jAxon1, neighbour1, true);
      jAxon2 = getCartPosY(iAxon2, jAxon2, neighbour2, true);
      if (s == 1 && atoms[iAxonOrigin][jAxonOrigin].isOccupied()) {
        type1 = atoms[iAxon1][jAxon1].getTypeWithoutNeighbour(neighbour1);
        type2 = atoms[iAxon2][jAxon2].getTypeWithoutNeighbour(neighbour2);
      } else {
        type1 = atoms[iAxon1][jAxon1].getType();
        type2 = atoms[iAxon2][jAxon2].getType();
      }

      if (atoms[iAxon1][jAxon1].isOccupied() || atoms[iAxon2][jAxon2].isOccupied() || type2 != 2 || type1 != 2) {
        return s - 1;
      }

      if (raw < 0.5) {
        XY_destino[0] = iAxon1;
        XY_destino[1] = jAxon1;
      } else {
        XY_destino[0] = iAxon2;
        XY_destino[1] = jAxon2;
      }

      if (s == m) {
        return s;
      }
      s++;
    }
  }

  private int getClearAreaArmchair(short iAxonOrigin, short jAxonOrigin, int m, int[] XY_destination, double raw) {

    int s = 1;
    boolean type0 = (((iAxonOrigin + jAxonOrigin) & 1) == 0);
    int orientacion = atoms[iAxonOrigin][jAxonOrigin].getOrientation();

    int iAxon1 = iAxonOrigin;
    int jAxon1 = jAxonOrigin;
    int iAxon2 = iAxonOrigin;
    int jAxon2 = jAxonOrigin;

    int neighbour1;
    int neighbour2;

    switch (orientacion) {
      case 0:
        neighbour1 = 10;
        neighbour2 = 2;
        if (getNeighbour(iAxonOrigin, jAxonOrigin, neighbour1).isOccupied()) {
          neighbour1 = 9;
          neighbour2 = 1;
        }
        break;
      case 1:
        neighbour1 = 11;
        neighbour2 = 0;
        if (getNeighbour(iAxonOrigin, jAxonOrigin, neighbour1).isOccupied()) {
          neighbour1 = 10;
          neighbour2 = 2;
        }
        break;

      case 2:
        neighbour1 = 11;
        neighbour2 = 0;
        if (getNeighbour(iAxonOrigin, jAxonOrigin, neighbour1).isOccupied()) {
          neighbour1 = 9;
          neighbour2 = 1;
        }
        break;

      default:
        return -1;
    }

    while (true) {
      int tipo1, tipo2;

      if (type0) {
        iAxon1 = getCartPosX(iAxon1, jAxon1, neighbour1, true);
        iAxon2 = getCartPosX(iAxon2, jAxon2, neighbour2, true);
        jAxon1 = getCartPosY(iAxon1, jAxon1, neighbour1, true);
        jAxon2 = getCartPosY(iAxon2, jAxon2, neighbour2, true);
        if (s == 1 && atoms[iAxonOrigin][jAxonOrigin].isOccupied()) {
          tipo1 = atoms[iAxon1][jAxon1].getTypeWithoutNeighbour(neighbour1);
          tipo2 = atoms[iAxon2][jAxon2].getTypeWithoutNeighbour(neighbour2);
        } else {
          tipo1 = atoms[iAxon1][jAxon1].getType();
          tipo2 = atoms[iAxon2][jAxon2].getType();
        }
      } else {
        iAxon1 = getCartPosX(iAxon1, jAxon1, neighbour2, false);
        iAxon2 = getCartPosX(iAxon2, jAxon2, neighbour1, false);
        jAxon1 = getCartPosY(iAxon1, jAxon1, neighbour2, false);
        jAxon2 = getCartPosY(iAxon2, jAxon2, neighbour1, false);
        if (s == 1 && atoms[iAxonOrigin][jAxonOrigin].isOccupied()) {
          tipo1 = atoms[iAxon1][jAxon1].getTypeWithoutNeighbour(neighbour2);
          tipo2 = atoms[iAxon2][jAxon2].getTypeWithoutNeighbour(neighbour1);
        } else {
          tipo1 = atoms[iAxon1][jAxon1].getType();
          tipo2 = atoms[iAxon2][jAxon2].getType();
        }
      }

      if (atoms[iAxon1][jAxon1].isOccupied() || atoms[iAxon2][jAxon2].isOccupied() || tipo2 != 3 || tipo1 != 3) {
        return s - 1;
      }

      if (raw < 0.5) {
        XY_destination[0] = iAxon1;
        XY_destination[1] = jAxon1;
      } else {
        XY_destination[0] = iAxon2;
        XY_destination[1] = jAxon2;
      }

      if (s == m) {
        return s;
      }
      type0 = !type0;
      s++;
    }
  }

  @Override
  public float getCartSizeX() {
    return axonSizeI;
  }

  @Override
  public float getCartSizeY() {
    return axonSizeJ;
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return getCartesianLocation(axonSizeI / 2, axonSizeJ / 2);
  }

  @Override
  public Point2D getCartesianLocation(int iAxon, int jAxon) {

    double xCart;
    if ((iAxon & 1) == 0) {
      xCart = (iAxon >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iAxon) + cos60;
    } else {
      xCart = (iAxon >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iAxon) * (1 + 2 * cos60);
    }
    double yCart = (jAxon >> 1) * (2 * cos30) + (1 & jAxon) * cos30;
    return new Point2D.Double(xCart, yCart);

  }
}
