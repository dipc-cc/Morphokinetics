/*
 * 
 * Clase que modela un átomo de la retícula
 *  La clase alberga todas los parámetros necesarios
 * 
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import utils.StaticRandom;

public class GrapheneAtom extends AbstractGrowthAtom {

  private static final ArrayStack PStack = new ArrayStack(12);
  private static GrapheneTypesTable typesTable;
  private GrapheneAtom[] neighbours = new GrapheneAtom[12];
  
  /**
   * Total number of 1st neighbours.
   * @return 0 <= value <= 3
   */
  private int n1;
  /**
   * Total number of 2nd neighbours.
   * @return 0 <= value <= 6
   */
  private int n2;  
  /**
   * Total number of 3rd neighbours.
   * @return 0 <= value <= 3
   */
  private int n3;
  private HopsPerStep distancePerStep;

  public GrapheneAtom(short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(iHexa, jHexa, 12);
  
    this.distancePerStep = distancePerStep;
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
  }

  public void setNeighbours(GrapheneAtom[] neighbours) {
    this.neighbours = neighbours;
  }
  
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() < KINK);
  } // KINK and BULK atoms types are considered immobil atoms

  /**
   * Total number of 1st neighbours.
   * @return 0 <= value <= 3
   */
  public int getN1() {
    return n1;
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
  public void setNeighbour(AbstractGrowthAtom a, int pos) {
    neighbours[pos] = (GrapheneAtom) a;
  }

  @Override
  public GrapheneAtom getNeighbour(int pos) {
    return neighbours[pos];
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability
   */
  @Override
  public void clear() {

    super.clear();
    setType(TERRACE);
    n1 = n2 = n3 = 0; // current atom has no neighbour

    if (getBondsProbability() != null) {
      PStack.returnProbArray(getBondsProbability());
      setBondsProbability(null);
    }
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
   * escogemos a que posición vamos a saltar
   * @return 
   */
  @Override
  public AbstractGrowthAtom chooseRandomHop() {
    double raw = StaticRandom.raw();

    if (getBondsProbability() == null) {
      return neighbours[(int) (raw * 12)];
    }

    double linearSearch = raw * getProbability();

    double sum = 0;
    int cont = 0;
    while (true) {
      sum += getBondsProbability()[cont++];
      if (sum >= linearSearch) {
        break;
      }
      if (cont == getBondsProbability().length) {
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
   * @param neighbourPosition position of the neighbour. Must be 1, 2 or 3
   * @param addOrRemove add or remove one neighbour. Must be -1 or 1
   * @return 
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
      addProbability(probJumpToNeighbour(getType(), 0) * 12.0);
    } else {
      if (getBondsProbability() == null) {
        setBondsProbability(PStack.getProbArray());
      }
      for (int i = 0; i < getNumberOfNeighbours(); i++) {
        getBondsProbability()[i] = probJumpToNeighbour(getType(), i);
        addProbability(getBondsProbability()[i]);
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

    if (getBondsProbability() == null) {
      double newRate = probJumpToNeighbour(getType(), i);
      if (newRate * 12 != getProbability()) {
        double independentProbability = getProbability() / 12.0;
        setBondsProbability(PStack.getProbArray());
        for (int a = 0; a < 12; a++) {
          getBondsProbability()[a] = independentProbability;
        }
        getBondsProbability()[i] = newRate;
        addProbability(newRate - independentProbability);
        probabilityChange += (newRate - independentProbability);
      }
    } else {
      addProbability(-getBondsProbability()[i]);
      probabilityChange -= getBondsProbability()[i];
      getBondsProbability()[i] = probJumpToNeighbour(getType(), i);
      addProbability(getBondsProbability()[i]);
      probabilityChange += getBondsProbability()[i];
    }

    return probabilityChange;
  }
  
  boolean areAllRatesTheSame() {
    if ((n1 + n2 + n3) != TERRACE) {
      return false;
    }
    for (int i = 11; i >= 3; i--) {
      if (neighbours[i].getType() != TERRACE) {
        return false;
      }
    }
    return true;
  }

  /**
   * Probabilidad de saltar a cierta posicion vecina
   * @param originType
   * @param pos
   * @return 
   */
  private double probJumpToNeighbour(int originType, int pos) {

    AbstractGrowthAtom atom = neighbours[pos];
    if (atom.isOccupied()) {
      return 0;
    }

    byte lastTemp = atom.getTypeWithoutNeighbour(pos);

    double rate;
    int multiplier = super.getMultiplier();
    if (multiplier != 1) {
      rate = getProbability(originType, lastTemp) / multiplier;
      super.setMultiplier(1);
    } else {
      int hops = distancePerStep.getDistancePerStep(originType, originType);

      switch (originType) {
        case TERRACE:
        case ZIGZAG_EDGE:
        case ARMCHAIR_EDGE:
          rate = getProbability(originType, lastTemp) / (hops * hops);
          break;
        default:
          rate = getProbability(originType, lastTemp);
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
   * @return 
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
}
