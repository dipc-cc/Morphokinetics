/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.AgAtom;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.ModifiedBuffer;
import java.awt.geom.Point2D;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgLattice extends AbstractGrowthLattice {

  public static final float YRatio = (float) Math.sqrt(3) / 2.0f; // it is the same as: sin 60º

  public AgLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(hexaSizeI, hexaSizeJ, modified);

    setAtoms(createAtoms());
    setAngles();
  }

  private AgAtom[][] createAtoms() {
    //Instantiate atoms
    AgAtom[][] atoms = new AgAtom[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = new AgAtom((short) i, (short) j);
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        AgAtom atom = (AgAtom) atoms[iHexa][jHexa];
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;

        atom.setNeighbour((AgAtom) atoms[i][j], 0);
        i = iHexa + 1;
        j = jHexa - 1;
        if (i == getHexaSizeI()) i = 0;
        if (j < 0) j = getHexaSizeJ() - 1;

        atom.setNeighbour((AgAtom) atoms[i][j], 1);
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 2);
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 3);
        i = iHexa - 1;
        j = jHexa + 1;
        if (i < 0) i = getHexaSizeI() - 1;
        if (j == getHexaSizeJ()) j = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 4);
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((AgAtom) atoms[i][j], 5);
      }
    }
    return atoms;
  }

  @Override
  public AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    return ((AgAtom) getAtom(iHexa, jHexa)).getNeighbour(neighbour);
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
  public AbstractGrowthAtom getFarSite(int originType, short iHexa, short jHexa, int distance) {
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
    return new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() * YRatio / 2.0f));
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ() * YRatio;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {

    float xCart = iHexa + jHexa * 0.5f;
    if (xCart >= getHexaSizeI()) {
      xCart -= getHexaSizeI();
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
    float xCart = (iHexa + jHexa * 0.5f) % getHexaSizeI();
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
      j = getHexaSizeJ() - 1;
    }
    
    // This while follows this iteration pattern:
    // go right, up, left up, left, down, right down (-1), jump down and increment
    //
    // This implementation is clearly not efficient (simple profiling is enough to demonstrate)
    out:
    while (true) {
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        i++;
        if (i == getHexaSizeI()) i = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j++;
        if (j == getHexaSizeJ()) j = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j++;
        i--;
        if (j == getHexaSizeJ()) j = 0;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        i--;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j--;
        if (j < 0) j = getHexaSizeJ() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j--;
        i++;
        if (j < 0) j = getHexaSizeJ() - 1;
        if (i == getHexaSizeI()) i = 0;
      }

      if (errorCode != 0) break;
      if (possibleDistance >= thresholdDistance) return possibleDistance;
      possibleDistance++;
      j--;
      if (j < 0) j = getHexaSizeJ() - 1;
      
    }
    if ((errorCode & 2) != 0) return possibleDistance - 1;
    if ((errorCode & 1) != 0) return possibleDistance;
    return -1;
  }

  public AbstractGrowthAtom chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {

    int tmp = (int) (raw * (distance * 6));

    int i = iHexaOrigin;
    int j = jHexaOrigin - distance;
    if (j < 0) j = getHexaSizeJ() - 1;

    int counter = 0;

    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      i++;
      if (i == getHexaSizeI()) i = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j++;
      if (j == getHexaSizeJ()) j = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j++;
      i--;
      if (j == getHexaSizeJ()) j = 0;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      i--;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j--;
      if (j < 0) j = getHexaSizeJ() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j--;
      i++;
      if (j < 0) j = getHexaSizeJ() - 1;
      if (i == getHexaSizeI()) i = 0;
    }

    return null;
  }

  public int getClearAreaStep(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {

    int distance = 1;
    int i;
    int j;

    switch (getAtom(iHexaOrigin, jHexaOrigin).getOrientation()) {
      case 0:
      case 3:
        while (true) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          if (getAtom(i, jHexaOrigin).isOccupied() || getAtom(i, jHexaOrigin).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          if (getAtom(i, jHexaOrigin).isOccupied() || getAtom(i, jHexaOrigin).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      case 1:
      case 4:
        while (true) {
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          if (getAtom(iHexaOrigin, j).isOccupied() || getAtom(iHexaOrigin, j).getType() < 2) {
            return distance - 1;
          }
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          if (getAtom(iHexaOrigin, j).isOccupied() || getAtom(iHexaOrigin, j).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      case 2:
      case 5:
        while (true) {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          if (getAtom(i, j).isOccupied() || getAtom(i, j).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          if (getAtom(i, j).isOccupied() || getAtom(i, j).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      default:
        return -1;
    }
  }

  public AbstractGrowthAtom chooseClearAreaStep(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {

    int i;
    int j;

    switch (getAtom(iHexaOrigin, jHexaOrigin).getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          return getAtom(i, jHexaOrigin);
        } else {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          return getAtom(i, jHexaOrigin);
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          return getAtom(iHexaOrigin, j);
        } else {
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          return getAtom(iHexaOrigin, j);
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          return getAtom(i, j);
        } else {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          return getAtom(i, j);
        }
    }
    return null;
  }

}
