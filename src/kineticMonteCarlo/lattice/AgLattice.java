/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.AgSite;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.site.ModifiedBuffer;
import java.awt.geom.Point2D;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import static kineticMonteCarlo.site.AgSite.EDGE;
import static kineticMonteCarlo.site.AgSite.ISLAND;
import static kineticMonteCarlo.site.AgSite.KINK_A;
import static kineticMonteCarlo.site.AgSite.TERRACE;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class AgLattice extends AbstractGrowthLattice {
  
  private final Point2D centralCartesianLocation;

  public AgLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified);
    centralCartesianLocation = new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() * Y_RATIO / 2.0f));
  }
  
  @Override
  public AgSite getCentralSite() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2) - (getHexaSizeJ() / 4);
    
    int index = jCentre * getHexaSizeI() + iCentre;
    return (AgSite) getUc(index).getSite(0);
  }
      
  @Override
  public Point2D getCentralCartesianLocation() {
    return centralCartesianLocation;
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ() * Y_RATIO;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    float xCart = iHexa + jHexa * 0.5f;
    if (xCart >= getHexaSizeI()) {
      xCart -= getHexaSizeI();
    }
    float yCart = jHexa * Y_RATIO;
    return new Point2D.Double(xCart, yCart);
  }

  public int[] getHexagonalCoordinates(double xCart, double yCart) {
    int[] result; 
    result = new int[2];
    result[0] = getiHexa(xCart, yCart);
    result[1] = getjHexa(yCart);
    
    return result;
  }
    
  /**
   * Knowing the X and Y Cartesian location, returns closest site hexagonal coordinate.
   * 
   * @param xCart Cartesian X coordinate
   * @param yCart Cartesian Y coordinate
   * @return i hexagonal position
   */
  @Override
  public int getiHexa(double xCart, double yCart) {
    int iHexa;
    int jHexa = getjHexa(yCart); // First, we need to know which is the j coordinate
    double x = xCart;
    if (yCart > Y_RATIO * 2 * x) { // If it is in the wrapped Cartesian area (a triangle)
      x += getHexaSizeI(); // Move to the mirrored location
    }
    iHexa = (int) Math.round(x - (jHexa / 2)); // Calculate its hexagonal position
    // Wrap if needed
    while (iHexa < 0) {
      iHexa = iHexa + getHexaSizeI();
    }
    while (iHexa >= getHexaSizeI()) {
      iHexa = iHexa - getHexaSizeI();
    }
    return iHexa;
  }
  
  /**
   * Knowing the X and Y Cartesian location, returns closest site hexagonal coordinate.
   * 
   * @param yCart Cartesian Y coordinate.
   * @return j hexagonal position.
   */
  @Override
  public int getjHexa(double yCart) {
    int jHexa = (int) Math.round(yCart / Y_RATIO); // Calculate its hexagonal position
    // Wrap if needed
    while (jHexa < 0) {
      jHexa = jHexa + getHexaSizeJ();
    }
    while (jHexa >= getHexaSizeJ()) {
      jHexa = jHexa - getHexaSizeJ();
    }
    return jHexa;
  }
  
  /**
   * The Cartesian X is the location I, plus the half of J. We have to do the module to ensure that
   * fits in a rectangular Cartesian mesh.
   *
   * @param iHexa
   * @param jHexa
   * @return x Cartesian position.
   */
  @Override
  public double getCartX(int iHexa, int jHexa) {
    float xCart = (iHexa + jHexa * 0.5f) % getHexaSizeI();
    return xCart;
  }

  /**
   * Simple relation between Y (Cartesian) and J (hexagonal), with Y_RATIO (=sin 60º).
   *
   * @param jHexa
   * @return y Cartesian position.
   */
  @Override
  public double getCartY(int jHexa) {
    return jHexa * Y_RATIO;
  }

  @Override
  public AbstractGrowthSite getNeighbour(int iHexa, int jHexa, int neighbour) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return ((AgSite) getUc(index).getSite(0)).getNeighbour(neighbour);
  }

  @Override
  public int getAvailableDistance(AbstractGrowthSite atom, int thresholdDistance) {
    short iHexa = atom.getiHexa();
    short jHexa = atom.getjHexa();
    switch (atom.getType()) {
      case TERRACE:
        return getClearAreaTerrace(iHexa, jHexa, thresholdDistance);
      case EDGE:
        return getClearAreaStep(iHexa, jHexa, thresholdDistance);
      default:
        return 0;
    }
  }

  @Override
  public AbstractGrowthSite getFarSite(AbstractGrowthSite atom, int distance) {
    short iHexa = atom.getiHexa();
    short jHexa = atom.getjHexa();
    switch (atom.getType()) {
      case TERRACE:
        return chooseClearAreaTerrace(iHexa, jHexa, distance, StaticRandom.raw());
      case EDGE:
        return chooseClearAreaStep(iHexa, jHexa, distance, StaticRandom.raw());
      default:
        return null;
    }
  }

  public void init() {
    setSites(generateSites());
    setAngles();
  }    

  @Override
  public void deposit(AbstractSurfaceSite a, boolean forceNucleation) {
    AgSite atom = (AgSite) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(ISLAND);
    }

    byte originalType = atom.getType();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        addOccupiedNeighbour(atom.getNeighbour(i), originalType, forceNucleation);
      }
    }

    addAtom(atom);
    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }

  @Override
  public double extract(AbstractSurfaceSite a) {
    AgSite atom = (AgSite) a;
    atom.setOccupied(false);
    double probabilityChange = atom.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        removeMobileOccupied(atom.getNeighbour(i));
      }
    }

    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
  }

  /**
   * Changes the occupation of the clicked atom from unoccupied to occupied, or vice versa. It is
   * experimental and only works with AgUc simulation mode. If fails, the execution continues
   * normally.
   *
   * @param xMouse absolute X location of the pressed point.
   * @param yMouse absolute Y location of the pressed point.
   * @param scale zoom level.
   */
  @Override
  public void changeOccupationByHand(double xMouse, double yMouse, int scale) {
    int iLattice;
    int jLattice;
    // scale the position with respect to the current scale.
    double xCanvas = xMouse / scale;
    double yCanvas = yMouse / scale;
    // choose the correct lattice
    jLattice = (int) Math.floor(yCanvas / Y_RATIO);
    iLattice = (int) Math.floor(xCanvas - 0.5*jLattice);
    double j = yCanvas / Y_RATIO;
    int pos = 0;

    // for debugging
    System.out.println("scale " + scale + " " + (jLattice - j));
    System.out.println("x y " + xMouse + " " + yMouse + " | " + xCanvas + " " + yCanvas + " | " + iLattice + " " + jLattice + " | ");
    AbstractSurfaceSite atom = getUc(iLattice, jLattice).getSite(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
      
  private AgSite[][] generateSites() {
    //Instantiate atoms
    AgSite[][] sites = new AgSite[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        sites[i][j] = new AgSite(createId(i, j), (short) i, (short) j);
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        AgSite site = (AgSite) sites[iHexa][jHexa];
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;

        site.setNeighbour((AgSite) sites[i][j], 0);
        i = iHexa + 1;
        j = jHexa - 1;
        if (i == getHexaSizeI()) i = 0;
        if (j < 0) j = getHexaSizeJ() - 1;

        site.setNeighbour((AgSite) sites[i][j], 1);
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;

        site.setNeighbour((AgSite) sites[i][j], 2);
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;

        site.setNeighbour((AgSite) sites[i][j], 3);
        i = iHexa - 1;
        j = jHexa + 1;
        if (i < 0) i = getHexaSizeI() - 1;
        if (j == getHexaSizeJ()) j = 0;

        site.setNeighbour((AgSite) sites[i][j], 4);
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        site.setNeighbour((AgSite) sites[i][j], 5);
      }
    }
    return sites;
  }
  
  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
    
  private int getClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {
    int possibleDistance = 1;
    int i = iHexaOrigin;
    int j = jHexaOrigin - 1;
    int index;
    byte errorCode = 0;
    if (j < 0) {
      j = getHexaSizeJ() - 1;
    }
    
    // This 'while' follows this iteration pattern:
    // go right, up, left up, left, down, right down (-1), jump down and increment
    //
    // This implementation is clearly not efficient (simple profiling is enough to demonstrate)
    out:
    while (true) {
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
        i++;
        if (i == getHexaSizeI()) i = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
        j++;
        if (j == getHexaSizeJ()) j = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
        j++;
        i--;
        if (j == getHexaSizeJ()) j = 0;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
        i--;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
        j--;
        if (j < 0) j = getHexaSizeJ() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        index = j * getHexaSizeI() + i;
        AbstractGrowthSite site = (AbstractGrowthSite) getUc(index).getSite(0);
        if (site.isOutside()) errorCode |= 1;
        if (site.isOccupied()) {errorCode |= 2; break out;}
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

  private AbstractGrowthSite chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {
    int randomNumber = (int) (raw * (distance * 6));
    int i = iHexaOrigin;
    int j = jHexaOrigin - distance;
    int index;
    if (j < 0) j = getHexaSizeJ() - 1;

    int counter = 0;

    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      i++;
      if (i == getHexaSizeI()) i = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      j++;
      if (j == getHexaSizeJ()) j = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      j++;
      i--;
      if (j == getHexaSizeJ()) j = 0;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      i--;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      j--;
      if (j < 0) j = getHexaSizeJ() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      index = j * getHexaSizeI() + i;
      counter++;
      if (counter > randomNumber) return (AbstractGrowthSite) getUc(index).getSite(0);
      j--;
      i++;
      if (j < 0) j = getHexaSizeJ() - 1;
      if (i == getHexaSizeI()) i = 0;
    }

    return null;
  }

  private int getClearAreaStep(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {
    int distance = 1;
    int i;
    int j;
    int index = jHexaOrigin * getHexaSizeI() + iHexaOrigin;
    
    switch (((AbstractGrowthSite) getUc(index).getSite(0)).getOrientation()) {
      case 0:
      case 3:
        while (true) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          index = jHexaOrigin * getHexaSizeI() + i;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          index = jHexaOrigin * getHexaSizeI() + i;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
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
          index = j * getHexaSizeI() + iHexaOrigin;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
            return distance - 1;
          }
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          index = j * getHexaSizeI() + iHexaOrigin;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
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
          index = j * getHexaSizeI() + i;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          index = j * getHexaSizeI() + i;
          if (getUc(index).getSite(0).isOccupied() || getUc(index).getSite(0).getType() < 2) {
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

  private AbstractGrowthSite chooseClearAreaStep(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {
    int i;
    int j;
    int index = jHexaOrigin * getHexaSizeI() + iHexaOrigin;
    
    switch (((AbstractGrowthSite) getUc(index).getSite(0)).getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          index = jHexaOrigin * getHexaSizeI() + i;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        } else {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          index = jHexaOrigin * getHexaSizeI() + i;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          index = j * getHexaSizeI() + iHexaOrigin;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        } else {
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          index = j * getHexaSizeI() + iHexaOrigin;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          index = j * getHexaSizeI() + i;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        } else {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          index = j * getHexaSizeI() + i;
          return (AbstractGrowthSite) getUc(index).getSite(0);
        }
    }
    return null;
  }
  
  private void removeImmobilAddMobile(AgSite site) {
    if (site.getNImmobile() == 0) {  //estado de transición
      site.addNMobile(1); // nMobile++;
      site.addNImmobile(-1); // nImmobile--;
      return;
    }

    byte newType = site.getNewType(-1, +1); // --nImmobile, ++nMobile
    site.addNImmobile(-1);
    site.addNMobile(1);

    if (site.getType() != newType) { // ha cambiado el tipo, hay que actualizar ligaduras
      boolean immobileToMobile = (site.getType() >= KINK_A && newType < KINK_A);
      site.setType(newType);
      addAtom(site);
      if (site.getNMobile() > 0 && !site.isOccupied()) {
        addBondAtom(site);
      }

      if (immobileToMobile && site.isOccupied()) {
        for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
          if (!site.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeImmobilAddMobile(site.getNeighbour(i));
          }
        }
      }
    }
  }
  
  private void removeMobileAddImmobile(AgSite site, boolean forceNucleation) {
    if (site.getNMobile() == 0) {
      site.addNMobile(-1); // nMobile--
      site.addNImmobile(1); // nImmobile++
      return;
    }

    byte newType = site.getNewType(1, -1); //++nImmobile, --nMobile
    site.addNMobile(-1);
    site.addNImmobile(1);

    if (forceNucleation && site.isOccupied()) {
      newType = ISLAND;
    }

    if (site.getType() != newType) { // ha cambiado el tipo, hay que actualizar ligaduras
      boolean mobileToImmobile = (site.getType() < KINK_A && newType >= KINK_A);
      site.setType(newType);
      addAtom(site);
      if (site.getNMobile() > 0 && !site.isOccupied()) {
        addBondAtom(site);
      }
      if (mobileToImmobile && site.isOccupied()) {
        for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
          if (!site.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeMobileAddImmobile(site.getNeighbour(i), forceNucleation);
          }
        }
      }
    }
  }
   
  /**
   * Éste lo ejecutan los primeros vecinos.
   * 
   * @param neighbourSite neighbour site of the original site.
   * @param originType type of the original site.
   * @param forceNucleation
   */
  private void addOccupiedNeighbour(AgSite neighbourSite, byte originType, boolean forceNucleation) {
    byte newType;

    if (originType < KINK_A) { // was a TERRACE, CORNER or EDGE
      newType = neighbourSite.getNewType(0, 1); //nImmobile, ++nMobile
      neighbourSite.addNMobile(1);
    } else { // was a KINK or ISLAND
      newType = neighbourSite.getNewType(1, 0); //++nImmobile, nMobile
      neighbourSite.addNImmobile(1);
    }

    if (forceNucleation) {
      newType = ISLAND;
    }

    if (neighbourSite.getType() != newType) { // the type of neighbour has changed
      boolean mobileToImmobile = (neighbourSite.getType() < KINK_A && newType >= KINK_A);
      neighbourSite.setType(newType);
      //addAtom(neighbourAtom); // always has to be checked, its neighbours has changed.
      if (neighbourSite.getNMobile() > 0 && !neighbourSite.isOccupied()) {
        addBondAtom(neighbourSite);
      }
      if (mobileToImmobile && neighbourSite.isOccupied()) {
        for (int i = 0; i < neighbourSite.getNumberOfNeighbours(); i++) {
          if (!neighbourSite.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeMobileAddImmobile(neighbourSite.getNeighbour(i), forceNucleation);
          }
        }
      }
    }
    if (neighbourSite.isOccupied()) { // the type of neighbour does not change, but it has a mew neighbour (caller one)
      addAtom(neighbourSite);
    }
      
  }
   
  /**
   * Computes the removal of one mobile site.
   * 
   * @param neighbourSite neighbour site of the original site.
   */
  private void removeMobileOccupied(AgSite neighbourSite) {

    byte newType = neighbourSite.getNewType(0, -1); //nImmobile, --nMobile
    neighbourSite.addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    if (neighbourSite.getType() != newType) {
      boolean immobileToMobile = (neighbourSite.getType() >= KINK_A && newType < KINK_A);
      neighbourSite.setType(newType);
      addAtom(neighbourSite);
      if (neighbourSite.getNMobile() > 0 && !neighbourSite.isOccupied()) {
        addBondAtom(neighbourSite);
      }
      if (immobileToMobile && neighbourSite.isOccupied()) {
        for (int i = 0; i < neighbourSite.getNumberOfNeighbours(); i++) {
          if (!neighbourSite.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeImmobilAddMobile(neighbourSite.getNeighbour(i));
          }
        }
      }
    }
  }
}
