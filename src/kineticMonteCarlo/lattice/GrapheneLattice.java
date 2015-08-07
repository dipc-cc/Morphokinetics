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

  public GrapheneLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(hexaSizeI, hexaSizeJ, modified);

    atoms = new GrapheneAtom[hexaSizeI][hexaSizeJ];

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

    for (int iHexa = 0; iHexa < hexaSizeI; iHexa += 2) {
      for (int jHexa = 0; jHexa < hexaSizeJ; jHexa += 2) {
        //para cada unit cell

        //atomo 0 de la unit cell, tipo 0
        atoms[iHexa][jHexa] = new GrapheneAtom((short) iHexa, (short) jHexa, distancePerStep);

        iHexa++;
        //atomo 1 de la unit cell, tipo 1
        atoms[iHexa][jHexa] = new GrapheneAtom((short) iHexa, (short) jHexa, distancePerStep);

        iHexa--;
        jHexa++;
        //atomo 2 de la unit cell, tipo 1   
        atoms[iHexa][jHexa] = new GrapheneAtom((short) iHexa, (short) jHexa, distancePerStep);

        iHexa++;
        //atomo 3 de la unit cell, tipo 0
        atoms[iHexa][jHexa] = new GrapheneAtom((short) iHexa, (short) jHexa, distancePerStep);

        iHexa--;
        jHexa--;
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
    int iHexa = xCart + vec_X;
    if (iHexa < 0) {
      iHexa += hexaSizeI;
    } else if (iHexa >= hexaSizeI) {
      iHexa -= hexaSizeI;
    }
    int jHexa = yCart + vec_Y;
    if (jHexa < 0) {
      jHexa += hexaSizeJ;
    } else if (jHexa >= hexaSizeJ) {
      jHexa -= hexaSizeJ;
    }
    return (GrapheneAtom) atoms[iHexa][jHexa];
  }

  public int getCartPosX(int iHexa, int jHexa, int pos, boolean type0) {
    int vec_X = (short) (latticeNeighborhoodData[pos] & 0xFFFF);
    if (!type0) {
      vec_X = -vec_X;
    }
    int posXV = iHexa + vec_X;
    if (posXV < 0) {
      posXV += hexaSizeI;
    } else if (posXV >= hexaSizeI) {
      posXV -= hexaSizeI;
    }
    return posXV;
  }

  public int getCartPosY(int iHexa, int jHexa, int pos, boolean type0) {
    int vec_Y = ((latticeNeighborhoodData[pos] >> 16));
    if (!type0) {
      vec_Y = -vec_Y;
    }
    int posYV = jHexa + vec_Y;
    if (posYV < 0) {
      posYV += hexaSizeJ;
    } else if (posYV >= hexaSizeJ) {
      posYV -= hexaSizeJ;
    }
    return posYV;
  }

  @Override
  public int getAvailableDistance(int atomType, short iHexa, short jHexa, int thresholdDistance) {

    int[] point = new int[2];
    switch (atomType) {
      case 0:
        return getClearAreaTerrace(iHexa, jHexa, thresholdDistance);
      case 2:
        return getClearAreaZigzag(iHexa, jHexa, thresholdDistance, point, StaticRandom.raw());
      case 3:
        return getClearAreaArmchair(iHexa, jHexa, thresholdDistance, point, StaticRandom.raw());
      default:
        return 0;
    }
  }

  @Override
  public Abstract2DDiffusionAtom getFarSite(int originType, short iHexa, short jHexa, int distance) {

    int[] point = new int[2];
    switch (originType) {
      case 0:
        return chooseClearAreaTerrace(iHexa, jHexa, distance, StaticRandom.raw());
      case 2:
        getClearAreaZigzag(iHexa, jHexa, distance, point, StaticRandom.raw());
        return this.getAtom(point[0], point[1]);
      case 3:
        getClearAreaArmchair(iHexa, jHexa, distance, point, StaticRandom.raw());
        return this.getAtom(point[0], point[1]);
      default:
        return null;
    }
  }

  private Abstract2DDiffusionAtom chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int s, double raw) {

    int temp = (int) (raw * (s * 2 * 6));

    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);
    int iHexa = iHexaOrigin;
    int jHexa = (jHexaOrigin - s * 2);
    if (jHexa < 0) {
      jHexa += hexaSizeJ;
    }

    int counter = 0;

    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 0, true);
        jHexa = getCartPosY(iHexa, jHexa, 0, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 1, false);
        jHexa = getCartPosY(iHexa, jHexa, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 2, true);
        jHexa = getCartPosY(iHexa, jHexa, 2, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 1, false);
        jHexa = getCartPosY(iHexa, jHexa, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 2, true);
        jHexa = getCartPosY(iHexa, jHexa, 2, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 0, false);
        jHexa = getCartPosY(iHexa, jHexa, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 1, true);
        jHexa = getCartPosY(iHexa, jHexa, 1, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 0, false);
        jHexa = getCartPosY(iHexa, jHexa, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 1, true);
        jHexa = getCartPosY(iHexa, jHexa, 1, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 2, false);
        jHexa = getCartPosY(iHexa, jHexa, 2, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < s * 2; i++) {
      counter++;
      if (counter > temp) {
        return atoms[iHexa][jHexa];
      }
      if (type0) {
        iHexa = getCartPosX(iHexa, jHexa, 0, true);
        jHexa = getCartPosY(iHexa, jHexa, 0, true);
      } else {
        iHexa = getCartPosX(iHexa, jHexa, 2, false);
        jHexa = getCartPosY(iHexa, jHexa, 2, false);
      }
      type0 = !type0;
    }
    return null;
  }

  private int getClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int m) {

    int s = 1;

    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);

    short iHexa;
    iHexa = iHexaOrigin;

    short jHexa = (short) (jHexaOrigin - 2);
    if (jHexa < 0) {
      jHexa += hexaSizeJ;
    }
    byte errorCode = 0;

    out:
    while (true) {

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 0);
        } else {
          a = getNeighbour(iHexa, jHexa, 1);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 2);
        } else {
          a = getNeighbour(iHexa, jHexa, 1);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 2);
        } else {
          a = getNeighbour(iHexa, jHexa, 0);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 1);
        } else {
          a = getNeighbour(iHexa, jHexa, 0);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 1);
        } else {
          a = getNeighbour(iHexa, jHexa, 2);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      for (int i = 0; i < s * 2; i++) {
        GrapheneAtom a;
        if (type0) {
          a = getNeighbour(iHexa, jHexa, 0);
        } else {
          a = getNeighbour(iHexa, jHexa, 2);
        }
        if (a.isOutside()) {
          errorCode |= 1;
        }
        if (a.isOccupied()) {
          errorCode |= 2;
          break out;
        }
        iHexa = a.getX();
        jHexa = a.getY();
        type0 = !type0;
      }

      if (errorCode != 0) {
        break;
      }
      if (s >= m) {
        return s;
      }
      s++;
      jHexa -= 2;
      if (jHexa < 0) {
        jHexa = (short) (hexaSizeJ - 1);
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

  private int getClearAreaZigzag(short iHexaOrigin, short jHexaOrigin, int m, int[] XY_destino, double raw) {

    int s = 1;
    int orientation = atoms[iHexaOrigin][jHexaOrigin].getOrientation();

    int iHexa1 = iHexaOrigin;
    int jHexa1 = jHexaOrigin;
    int iHexa2 = iHexaOrigin;
    int jHexa2 = jHexaOrigin;

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

      iHexa1 = getCartPosX(iHexa1, jHexa1, neighbour1, true);
      iHexa2 = getCartPosX(iHexa2, jHexa2, neighbour2, true);
      jHexa1 = getCartPosY(iHexa1, jHexa1, neighbour1, true);
      jHexa2 = getCartPosY(iHexa2, jHexa2, neighbour2, true);
      if (s == 1 && atoms[iHexaOrigin][jHexaOrigin].isOccupied()) {
        type1 = atoms[iHexa1][jHexa1].getTypeWithoutNeighbour(neighbour1);
        type2 = atoms[iHexa2][jHexa2].getTypeWithoutNeighbour(neighbour2);
      } else {
        type1 = atoms[iHexa1][jHexa1].getType();
        type2 = atoms[iHexa2][jHexa2].getType();
      }

      if (atoms[iHexa1][jHexa1].isOccupied() || atoms[iHexa2][jHexa2].isOccupied() || type2 != 2 || type1 != 2) {
        return s - 1;
      }

      if (raw < 0.5) {
        XY_destino[0] = iHexa1;
        XY_destino[1] = jHexa1;
      } else {
        XY_destino[0] = iHexa2;
        XY_destino[1] = jHexa2;
      }

      if (s == m) {
        return s;
      }
      s++;
    }
  }

  private int getClearAreaArmchair(short iHexaOrigin, short jHexaOrigin, int m, int[] XY_destination, double raw) {

    int s = 1;
    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);
    int orientacion = atoms[iHexaOrigin][jHexaOrigin].getOrientation();

    int iHexa1 = iHexaOrigin;
    int jHexa1 = jHexaOrigin;
    int iHexa2 = iHexaOrigin;
    int jHexa2 = jHexaOrigin;

    int neighbour1;
    int neighbour2;

    switch (orientacion) {
      case 0:
        neighbour1 = 10;
        neighbour2 = 2;
        if (getNeighbour(iHexaOrigin, jHexaOrigin, neighbour1).isOccupied()) {
          neighbour1 = 9;
          neighbour2 = 1;
        }
        break;
      case 1:
        neighbour1 = 11;
        neighbour2 = 0;
        if (getNeighbour(iHexaOrigin, jHexaOrigin, neighbour1).isOccupied()) {
          neighbour1 = 10;
          neighbour2 = 2;
        }
        break;

      case 2:
        neighbour1 = 11;
        neighbour2 = 0;
        if (getNeighbour(iHexaOrigin, jHexaOrigin, neighbour1).isOccupied()) {
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
        iHexa1 = getCartPosX(iHexa1, jHexa1, neighbour1, true);
        iHexa2 = getCartPosX(iHexa2, jHexa2, neighbour2, true);
        jHexa1 = getCartPosY(iHexa1, jHexa1, neighbour1, true);
        jHexa2 = getCartPosY(iHexa2, jHexa2, neighbour2, true);
        if (s == 1 && atoms[iHexaOrigin][jHexaOrigin].isOccupied()) {
          tipo1 = atoms[iHexa1][jHexa1].getTypeWithoutNeighbour(neighbour1);
          tipo2 = atoms[iHexa2][jHexa2].getTypeWithoutNeighbour(neighbour2);
        } else {
          tipo1 = atoms[iHexa1][jHexa1].getType();
          tipo2 = atoms[iHexa2][jHexa2].getType();
        }
      } else {
        iHexa1 = getCartPosX(iHexa1, jHexa1, neighbour2, false);
        iHexa2 = getCartPosX(iHexa2, jHexa2, neighbour1, false);
        jHexa1 = getCartPosY(iHexa1, jHexa1, neighbour2, false);
        jHexa2 = getCartPosY(iHexa2, jHexa2, neighbour1, false);
        if (s == 1 && atoms[iHexaOrigin][jHexaOrigin].isOccupied()) {
          tipo1 = atoms[iHexa1][jHexa1].getTypeWithoutNeighbour(neighbour2);
          tipo2 = atoms[iHexa2][jHexa2].getTypeWithoutNeighbour(neighbour1);
        } else {
          tipo1 = atoms[iHexa1][jHexa1].getType();
          tipo2 = atoms[iHexa2][jHexa2].getType();
        }
      }

      if (atoms[iHexa1][jHexa1].isOccupied() || atoms[iHexa2][jHexa2].isOccupied() || tipo2 != 3 || tipo1 != 3) {
        return s - 1;
      }

      if (raw < 0.5) {
        XY_destination[0] = iHexa1;
        XY_destination[1] = jHexa1;
      } else {
        XY_destination[0] = iHexa2;
        XY_destination[1] = jHexa2;
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
    return hexaSizeI;
  }

  @Override
  public float getCartSizeY() {
    return hexaSizeJ;
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return getCartesianLocation(hexaSizeI / 2, hexaSizeJ / 2);
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {

    double xCart;
    if ((iHexa & 1) == 0) {
      xCart = (iHexa >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iHexa) + cos60;
    } else {
      xCart = (iHexa >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iHexa) * (1 + 2 * cos60);
    }
    double yCart = (jHexa >> 1) * (2 * cos30) + (1 & jHexa) * cos30;
    return new Point2D.Double(xCart, yCart);

  }
}
