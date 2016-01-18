/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.GrapheneAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import java.awt.geom.Point2D;
import static kineticMonteCarlo.atom.AbstractAtom.BULK;
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import kineticMonteCarlo.atom.ArrayStack;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class GrapheneLattice extends AbstractGrowthLattice {

  private static int[] latticeNeighborhoodData;
  private static final double cos60 = Math.cos(60 * Math.PI / 180);
  private static final double cos30 = Math.cos(30 * Math.PI / 180);
  private static final ArrayStack PStack = new ArrayStack(12);;
  private Point2D centralCartesianLocation;

  public GrapheneLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(hexaSizeI, hexaSizeJ, modified);

    // j axis has to be multiple of two
    if ((hexaSizeJ % 2) != 0) {
      hexaSizeJ++;
    }
    if (latticeNeighborhoodData == null) {
      initializeNeighborHoodCache();
    }
    
    centralCartesianLocation = getCartesianLocation(getHexaSizeI() / 2, getHexaSizeJ() / 2);
    setAtoms(createAtoms(hexaSizeI, hexaSizeJ, distancePerStep));
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

  private GrapheneAtom[][] createAtoms(int hexaSizeI, int hexaSizeJ, HopsPerStep distancePerStep) {

    //Instantiate atoms
    GrapheneAtom[][] atoms = new GrapheneAtom[hexaSizeI][hexaSizeJ];
    for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa += 2) {
      for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa += 2) {
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
    
    setAtoms(atoms);
    
    //Interconect atoms
    for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
      for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
        // Get and all 12 neighbours of current graphene atom
        GrapheneAtom[] neighbours = new GrapheneAtom[12];
        for (int i = 0; i < 12; i++) {
          neighbours[i] = getNeighbour(iHexa, jHexa, i);
        }
        atoms[iHexa][jHexa].setNeighbours(neighbours);
      }
    }
    return atoms;
  }

  @Override
  public GrapheneAtom getNeighbour(int xCart, int yCart, int neighbour) {

    int vec = latticeNeighborhoodData[neighbour];                      //esto define el tipo de atomo
    int vec_X = (short) (vec & 0xFFFF); // bitwise and for all the bits
    int vec_Y = ((vec >> 16)); // shift 16 positions to right
    if (((xCart + yCart) & 1) != 0) { // if it is odd
      vec_X = -vec_X;
      vec_Y = -vec_Y;
    }
    int iHexa = xCart + vec_X;
    if (iHexa < 0) {
      iHexa += getHexaSizeI();
    } else if (iHexa >= getHexaSizeI()) {
      iHexa -= getHexaSizeI();
    }
    int jHexa = yCart + vec_Y;
    if (jHexa < 0) {
      jHexa += getHexaSizeJ();
    } else if (jHexa >= getHexaSizeJ()) {
      jHexa -= getHexaSizeJ();
    }
    return (GrapheneAtom) getAtom(iHexa, jHexa);
  }

  public int getHexaPosI(int iHexa, int jHexa, int pos, boolean type0) {
    int vecI = (short) (latticeNeighborhoodData[pos] & 0xFFFF);
    if (!type0) {
      vecI = -vecI;
    }
    int posI = iHexa + vecI;
    if (posI < 0) {
      posI += getHexaSizeI();
    } else if (posI >= getHexaSizeI()) {
      posI -= getHexaSizeI();
    }
    return posI;
  }

  public int getHexaPosJ(int iHexa, int jHexa, int pos, boolean type0) {
    int vecJ = ((latticeNeighborhoodData[pos] >> 16));
    if (!type0) {
      vecJ = -vecJ;
    }
    int posJ = jHexa + vecJ;
    if (posJ < 0) {
      posJ += getHexaSizeJ();
    } else if (posJ >= getHexaSizeJ()) {
      posJ -= getHexaSizeJ();
    }
    return posJ;
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
  public AbstractGrowthAtom getFarSite(int originType, short iHexa, short jHexa, int distance) {

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

  private AbstractGrowthAtom chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int thresholdDistance, double raw) {

    int temp = (int) (raw * (thresholdDistance * 2 * 6));

    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);
    int iHexa = iHexaOrigin;
    int jHexa = (jHexaOrigin - thresholdDistance * 2);
    if (jHexa < 0) {
      jHexa += getHexaSizeJ();
    }

    int counter = 0;

    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 0, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 0, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 1, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 2, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 2, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 1, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 1, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 2, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 2, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 0, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 1, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 1, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 0, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 0, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 1, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 1, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 2, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 2, false);
      }
      type0 = !type0;
    }
    for (int i = 0; i < thresholdDistance * 2; i++) {
      counter++;
      if (counter > temp) {
        return getAtom(iHexa, jHexa);
      }
      if (type0) {
        iHexa = getHexaPosI(iHexa, jHexa, 0, true);
        jHexa = getHexaPosJ(iHexa, jHexa, 0, true);
      } else {
        iHexa = getHexaPosI(iHexa, jHexa, 2, false);
        jHexa = getHexaPosJ(iHexa, jHexa, 2, false);
      }
      type0 = !type0;
    }
    return null;
  }

  private int getClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {

    int distance = 1;

    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);

    short iHexa;
    iHexa = iHexaOrigin;

    short jHexa = (short) (jHexaOrigin - 2);
    if (jHexa < 0) {
      jHexa += getHexaSizeJ();
    }
    byte errorCode = 0;

    out:
    while (true) {

      for (int i = 0; i < distance * 2; i++) {
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

      for (int i = 0; i < distance * 2; i++) {
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

      for (int i = 0; i < distance * 2; i++) {
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

      for (int i = 0; i < distance * 2; i++) {
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

      for (int i = 0; i < distance * 2; i++) {
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

      for (int i = 0; i < distance * 2; i++) {
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
      if (distance >= thresholdDistance) {
        return distance;
      }
      distance++;
      jHexa -= 2;
      if (jHexa < 0) {
        jHexa = (short) (getHexaSizeJ() - 1);
      }
    }

    if ((errorCode & 2) != 0) {
      return distance - 1;
    }
    if ((errorCode & 1) != 0) {
      return distance;
    }
    return -1;
  }

  private int getClearAreaZigzag(short iHexaOrigin, short jHexaOrigin, int thresholdDistance, int[] destinationIJ, double raw) {

    int distance = 1;
    int orientation = getAtom(iHexaOrigin, jHexaOrigin).getOrientation();

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

      iHexa1 = getHexaPosI(iHexa1, jHexa1, neighbour1, true);
      iHexa2 = getHexaPosI(iHexa2, jHexa2, neighbour2, true);
      jHexa1 = getHexaPosJ(iHexa1, jHexa1, neighbour1, true);
      jHexa2 = getHexaPosJ(iHexa2, jHexa2, neighbour2, true);
      if (distance == 1 && getAtom(iHexaOrigin, jHexaOrigin).isOccupied()) {
        type1 = getAtom(iHexa1, jHexa1).getTypeWithoutNeighbour(neighbour1);
        type2 = getAtom(iHexa2, jHexa2).getTypeWithoutNeighbour(neighbour2);
      } else {
        type1 = getAtom(iHexa1, jHexa1).getType();
        type2 = getAtom(iHexa2, jHexa2).getType();
      }

      if (getAtom(iHexa1, jHexa1).isOccupied() || getAtom(iHexa2, jHexa2).isOccupied() || type2 != 2 || type1 != 2) {
        return distance - 1;
      }

      if (raw < 0.5) {
        destinationIJ[0] = iHexa1;
        destinationIJ[1] = jHexa1;
      } else {
        destinationIJ[0] = iHexa2;
        destinationIJ[1] = jHexa2;
      }

      if (distance == thresholdDistance) {
        return distance;
      }
      distance++;
    }
  }

  private int getClearAreaArmchair(short iHexaOrigin, short jHexaOrigin, int thresholdDistance, int[] destinationIJ, double raw) {

    int distance = 1;
    boolean type0 = (((iHexaOrigin + jHexaOrigin) & 1) == 0);
    int orientacion = getAtom(iHexaOrigin, jHexaOrigin).getOrientation();

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
      int type1, type2;

      if (type0) {
        iHexa1 = getHexaPosI(iHexa1, jHexa1, neighbour1, true);
        iHexa2 = getHexaPosI(iHexa2, jHexa2, neighbour2, true);
        jHexa1 = getHexaPosJ(iHexa1, jHexa1, neighbour1, true);
        jHexa2 = getHexaPosJ(iHexa2, jHexa2, neighbour2, true);
        if (distance == 1 && getAtom(iHexaOrigin, jHexaOrigin).isOccupied()) {
          type1 = getAtom(iHexa1, jHexa1).getTypeWithoutNeighbour(neighbour1);
          type2 = getAtom(iHexa2, jHexa2).getTypeWithoutNeighbour(neighbour2);
        } else {
          type1 = getAtom(iHexa1, jHexa1).getType();
          type2 = getAtom(iHexa2, jHexa2).getType();
        }
      } else {
        iHexa1 = getHexaPosI(iHexa1, jHexa1, neighbour2, false);
        iHexa2 = getHexaPosI(iHexa2, jHexa2, neighbour1, false);
        jHexa1 = getHexaPosJ(iHexa1, jHexa1, neighbour2, false);
        jHexa2 = getHexaPosJ(iHexa2, jHexa2, neighbour1, false);
        if (distance == 1 && getAtom(iHexaOrigin, jHexaOrigin).isOccupied()) {
          type1 = getAtom(iHexa1, jHexa1).getTypeWithoutNeighbour(neighbour2);
          type2 = getAtom(iHexa2, jHexa2).getTypeWithoutNeighbour(neighbour1);
        } else {
          type1 = getAtom(iHexa1, jHexa1).getType();
          type2 = getAtom(iHexa2, jHexa2).getType();
        }
      }

      if (getAtom(iHexa1, jHexa1).isOccupied() || getAtom(iHexa2, jHexa2).isOccupied() || type2 != 3 || type1 != 3) {
        return distance - 1;
      }

      if (raw < 0.5) {
        destinationIJ[0] = iHexa1;
        destinationIJ[1] = jHexa1;
      } else {
        destinationIJ[0] = iHexa2;
        destinationIJ[1] = jHexa2;
      }

      if (distance == thresholdDistance) {
        return distance;
      }
      type0 = !type0;
      distance++;
    }
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI() * 1.5f;
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ() * Y_RATIO;
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return centralCartesianLocation;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {

    double xCart;
    if ((iHexa & 1) == 0) { //even
      xCart = (iHexa >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iHexa) + cos60;
    } else { //odd
      xCart = (iHexa >> 1) * (2 + 2 * cos60) + 0.5 + (1 & iHexa) * (1 + 2 * cos60);
    }
    double yCart = (jHexa >> 1) * (2 * cos30) + (1 & jHexa) * cos30;
    return new Point2D.Double(xCart, yCart);

  }
  
  @Override
  public double getCartX(int iHexa, int jHexa) {
    double xCart;
    if ((jHexa & 1) == 0) { //j even
      xCart = (iHexa >> 1) + iHexa + 0.5;
    } else { // odd
      xCart = (iHexa + 1 >> 1) + iHexa;
    }
    return xCart;
  }
  
  @Override
  public double getCartY(int jHexa) {
    return jHexa * Y_RATIO;
  }
  
  @Override
  public int getiHexa(double xCart, double yCart) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getjHexa(double yCart) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    GrapheneAtom atom = (GrapheneAtom) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(TERRACE);
    }
    int i = 0;

    for (; i < 3; i++) {
      add1stNeighbour(atom.getNeighbour(i), forceNucleation);
    }
    for (; i < 9; i++) {
      add2ndNeighbour(atom.getNeighbour(i));
    }
    for (; i < 12; i++) {
      add3rdNeighbour(atom.getNeighbour(i));
    }

    addAtom(atom);
    if (atom.getNeighbourCount() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
    
  /**
   * Extrae el átomo de este lugar (pásalo a no occupied y reduce la vecindad de los átomos vecinos,
   * si cambia algún tipo, recalcula probabilidades)
   */
  @Override
  public void extract(AbstractGrowthAtom a) {
    GrapheneAtom atom = (GrapheneAtom) a;
    atom.setOccupied(false);

    int i = 0;
    for (; i < 3; i++) {
      remove1stNeighbour(atom.getNeighbour(i));
    }
    for (; i < 9; i++) {
      remove2ndNeighbour(atom.getNeighbour(i));
    }
    for (; i < 12; i++) {
      remove3rdNeighbour(atom.getNeighbour(i));
    }

    if (atom.getNeighbourCount() > 0) {
      addBondAtom(atom);
    }

    if (atom.getBondsProbability() != null) {
      PStack.returnProbArray(atom.getBondsProbability());
      atom.setBondsProbability(null);
    }
    atom.setList(false);
  }
      
  private void add1stNeighbour(GrapheneAtom atom, boolean forceNucleation) {
    byte newType = atom.getNewType(1, 1);
    //set type is missing!
    if (forceNucleation && atom.isOccupied()) {
      newType = BULK;
    }
    evaluateModifiedWhenAddNeigh(atom, newType);
  }

  private void add2ndNeighbour(GrapheneAtom atom) {
    byte newType = atom.getNewType(2, 1);
    evaluateModifiedWhenAddNeigh(atom, newType);
  }

  private void add3rdNeighbour(GrapheneAtom atom) {
    byte newType = atom.getNewType(3, 1);
    evaluateModifiedWhenAddNeigh(atom, newType);
  }

  private void remove1stNeighbour(GrapheneAtom atom) {
    byte newType = atom.getNewType(1, -1);
    evaluateModifiedWhenRemNeigh(atom, newType);
  }

  private void remove2ndNeighbour(GrapheneAtom atom) {
    byte newType = atom.getNewType(2, -1);
    evaluateModifiedWhenRemNeigh(atom, newType);
  }

  private void remove3rdNeighbour(GrapheneAtom atom) {
    byte newType = atom.getNewType(3, -1);
    evaluateModifiedWhenRemNeigh(atom, newType);
  }
  
  private void evaluateModifiedWhenRemNeigh(GrapheneAtom atom, byte newType) {
    if (atom.getType() != newType) {
      atom.setType(newType);
      if (atom.isOccupied()) {
        addAtom(atom);
      }
      if (atom.getNeighbourCount() > 0 && !atom.isOccupied()) {
        addBondAtom(atom);
      }
    }
  }

  private void evaluateModifiedWhenAddNeigh(GrapheneAtom atom, byte newType) {
    if (atom.getType() != newType) {
      atom.setType(newType);
      if (atom.isOccupied()) {
        addAtom(atom);
      }
      if (atom.getNeighbourCount() > 1 && !atom.isOccupied()) {
        addBondAtom(atom);
      }
    }
  }
}
