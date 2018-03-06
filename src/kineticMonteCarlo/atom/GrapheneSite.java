/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.atom;

import java.util.Arrays;
import java.util.List;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import utils.StaticRandom;

public class GrapheneSite extends AbstractGrowthSite {

  private static GrapheneTypesTable typesTable;
  private boolean allRatesTheSame;
  private GrapheneSite[] neighbours = new GrapheneSite[12];
  
  /**
   * Total number of 1st neighbours.
   * 0 <= n1 <= 3
   */
  private int n1;
  /**
   * Total number of 2nd neighbours.
   * 0 <= n2 <= 6
   */
  private int n2;  
  /**
   * Total number of 3rd neighbours.
   * 0 <= n3 <= 3
   */
  private int n3;
  private HopsPerStep distancePerStep;

  public GrapheneSite(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, 12, 2);
  
    this.distancePerStep = distancePerStep;
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
    allRatesTheSame = false;
  }

  public void setNeighbours(GrapheneSite[] neighbours) {
    this.neighbours = neighbours;
  }

  /**
   * KINK and BULK atom types are considered immobile atoms.
   *
   * @return true if current atom can be moved, false otherwise.
   */
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() < KINK);
  }
   
  @Override
  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == BULK;
  }

  /**
   * Total number of 1st neighbours.
   * @return 0 <= value <= 3
   */
  public int getN1() {
    return n1;
  }

  public void setN1(int addOrRemove) {
    this.n1 += addOrRemove;
  }

  /**
   * Total number of 2nd neighbours.
   * @return 0 <= value <= 6
   */
  public int getN2() {
    return n2;
  }

  /**
   * Total number of 3rd neighbours.
   * @return 0 <= value <= 3
   */
  public int getN3() {
    return n3;
  }

  public HopsPerStep getDistancePerStep() {
    return distancePerStep;
  }
  
  /**
   * Total number of neighbours.
   * @return 0 <= value <= 12
   */
  public int getNeighbourCount() {
    return n1 + n2 + n3;
  }
  
  @Override
  public void setNeighbour(AbstractGrowthSite a, int pos) {
    neighbours[pos] = (GrapheneSite) a;
  }

  @Override
  public GrapheneSite getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  @Override
  public List getAllNeighbours() {
    return Arrays.asList(neighbours);
  }
  

  @Override
  public int getOrientation() {
    if (n1 == 1) {
      for (int i = 0; i < 3; i++) {
        if (neighbours[i].isOccupied()) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
    n1 = n2 = n3 = 0; // current atom has no neighbour

  }

  /**
   * Decides to which position is going to jump.
   * 
   * @return destination atom
   */
  @Override
  public AbstractGrowthSite chooseRandomHop() {
    double raw = StaticRandom.raw();

    if (allRatesTheSame) {
      return neighbours[(int) (raw * getNumberOfNeighbours())];
    }

    double linearSearch = raw * getProbability();

    double sum = 0;
    int cont = 0;
    while (true) {
      sum += getBondsProbability(cont++);
      if (sum >= linearSearch) {
        break;
      }
      if (cont == getNumberOfNeighbours()) {
        break;
      }
    }
    return neighbours[cont - 1];
  }

  @Override
  public boolean areTwoTerracesTogether() {
    return n1 == ZIGZAG_EDGE && n2 == TERRACE && n3 == TERRACE;
  }

  /**
   * Calculates the new atom type when adding or removing a neighbour.
   * 
   * @param neighbourPosition position of the neighbour. Must be 1, 2 or 3
   * @param addOrRemove add or remove one neighbour. Must be -1 or 1
   * @return new atom type
   */
  public byte getNewType(int neighbourPosition, int addOrRemove) {
    switch (neighbourPosition) {
      case (1):
        n1 += addOrRemove;
        break;
      case (2):
        n2 += addOrRemove;
        break;
      case (3):
        n3 += addOrRemove;
        break;
      default:
        throw new IllegalArgumentException("neighbourPosition must be 1, 2 or 3");
    }
    return typesTable.getType(n1, n2, n3);
  }
  
  @Override
  public void obtainRateFromNeighbours() {
    if (areAllRatesTheSame()) {
      addProbability(probJumpToNeighbour(getType(), 0) * getNumberOfNeighbours()); // it was 12.0
    } else {
      for (int i = 0; i < getNumberOfNeighbours(); i++) {
        setBondsProbability(probJumpToNeighbour(getType(), i), i);
        addProbability(getBondsProbability(i));
      }
    }
  }
  
  @Override
  public double updateOneBound(int posNeighbour) {
    double probabilityChange = 0;

    int i = posNeighbour; //eres el vecino 1 de tu 1º vecino. Eres el vecino 2 de tu 2º vecino. eres el 3º Vecino de tu vecino 3.
    switch (posNeighbour) {
      case 3:
      case 4:
      case 5:
        i += 3;
        break;
      case 6:
      case 7:
      case 8:
        i -= 3;
        break;
      default:
        break;
    }

    addProbability(-getBondsProbability(i));
    probabilityChange -= getBondsProbability(i);
    setBondsProbability(probJumpToNeighbour(getType(), i), i);
    addProbability(getBondsProbability(i));
    probabilityChange += getBondsProbability(i);

    return probabilityChange;
  }
  
  /**
   * Probability to jump to given neighbour position. 
   * 
   * @param originType
   * @param pos
   * @return probability
   */
  @Override
  public double probJumpToNeighbour(int originType, int pos) {
    AbstractGrowthSite atom = neighbours[pos];
    if (atom.isOccupied()) {
      return 0;
    }

    byte destinationType = atom.getTypeWithoutNeighbour(pos);

    double rate;
    int multiplier = super.getMultiplier();
    if (multiplier != 1) {
      rate = getProbability(originType, destinationType) / multiplier;
      super.setMultiplier(1);
    } else {
      int hops = distancePerStep.getDistancePerStep(originType, originType);

      switch (originType) {
        case TERRACE:
        case ZIGZAG_EDGE:
        case ARMCHAIR_EDGE:
          rate = getProbability(originType, destinationType) / (hops * hops);
          break;
        default:
          rate = getProbability(originType, destinationType);
          break;
      }
    }
    return rate;
  }


  /**
   * saber que tipo de átomo sería si el vecino originPos estuviera vacío
   * útil para saber el tipo de átomos que serás al saltar.
   * sabiendo el tipo de átomo origen y destino puedes sacar la probabilidad de éxito   origen ====> destino
   * @param originPos
   * @return atom type without neighbour
   */
  @Override
  public byte getTypeWithoutNeighbour(int originPos) {
    if (originPos < 3) {
      return typesTable.getType(n1 - 1, n2, n3);
    }
    if (originPos < 9) {
      return typesTable.getType(n1, n2 - 1, n3);
    }
    return typesTable.getType(n1, n2, n3 - 1);
  }
  
  private boolean areAllRatesTheSame() {
    if ((n1 + n2 + n3) != TERRACE) {
      allRatesTheSame = false;
      return false;
    }
    for (int i = getNumberOfNeighbours() - 1; i >= 3; i--) {
      if (neighbours[i].getType() != TERRACE) {
      allRatesTheSame = false;
        return false;
      }
    }
    allRatesTheSame = true;
    return true;
  }
}
