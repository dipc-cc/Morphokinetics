/*
 * 
 * Clase que modela un átomo de la retícula
 *  La clase alberga todas los parámetros necesarios
 * 
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.GrapheneLattice;
import utils.StaticRandom;

public class GrapheneAtom extends AbstractGrowthAtom {

  private GrapheneLattice lattice;
  private static final ArrayStack PStack = new ArrayStack(12);
  private static GrapheneTypesTable typesTable;
  private byte n1, n2, n3;

  public GrapheneAtom(short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(iHexa, jHexa, distancePerStep);
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
    
    setNumberOfNeighbours(12);
    bondsProbability = new double[getNumberOfNeighbours()];
  }

  @Override
  public void initialize(AbstractGrowthLattice lattice, double[][] probabilities, ModifiedBuffer modified) {
    super.initialize(probabilities, modified);
    this.lattice = (GrapheneLattice) lattice;
  }

  @Override
  public boolean isEligible() {
    return occupied && (type < KINK);
  } // KINK and BULK atoms types are considered immobil atoms

  /**
   * Total number of 1st neighbours.
   * @return 0 <= value <= 3
   */
  public byte getN1() {
    return n1;
  }

  /**
   * Total number of 2nd neighbours.
   * @return 0 <= value <= 6
   */
  public byte getN2() {
    return n2;
  }

  /**
   * Total number of 3rd neighbours.
   * @return 0 <= value <= 3
   */
  public byte getN3() {
    return n3;
  }

  /**
   * Total number of neighbours.
   * @return 0 <= value <= 12
   */
  public int getN1N2N3() {
    return (n1 + n2 + n3);
  }

  @Override
  public void clear() {

    n1 = n2 = n3 = type = TERRACE;
    occupied = false;
    outside = false;

    totalProbability = 0;
    setOnList(null);

    if (bondsProbability != null) {
      PStack.returnProbArray(bondsProbability);
      bondsProbability = null;
    }
  }

  @Override
  public int getOrientation() {

    if (n1 == 1) {
      for (int i = 0; i < 3; i++) {
        if (lattice.getNeighbour(iHexa, jHexa, i).isOccupied()) {
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

    if (bondsProbability == null) {
      return lattice.getNeighbour(iHexa, jHexa, (int) (raw * 12));
    }

    double linearSearch = raw * totalProbability;

    double sum = 0;
    int cont = 0;
    while (true) {
      sum += bondsProbability[cont++];
      if (sum >= linearSearch) {
        break;
      }
      if (cont == bondsProbability.length) {
        break;
      }
    }
    //System.out.println(bondsProbability.length);
    return lattice.getNeighbour(iHexa, jHexa, cont - 1);
  }

  private void add1stNeighbour(boolean forceNucleation) {
    byte newType = typesTable.getType(++n1, n2, n3);
    if (forceNucleation && occupied) {
      newType = BULK;
    }
    evaluateModifiedWhenAddNeigh(newType);
  }

  private void add2ndNeighbour() {
    byte newType = typesTable.getType(n1, ++n2, n3);
    evaluateModifiedWhenAddNeigh(newType);
  }

  private void add3rdNeighbour() {
    byte newType = typesTable.getType(n1, n2, ++n3);
    evaluateModifiedWhenAddNeigh(newType);
  }

  private void remove1stNeighbour() {
    byte newType = typesTable.getType(--n1, n2, n3);
    evaluateModifiedWhenRemNeigh(newType);
  }

  private void remove2ndNeighbour() {
    byte newType = typesTable.getType(n1, --n2, n3);
    evaluateModifiedWhenRemNeigh(newType);
  }

  private void remove3rdNeighbour() {
    byte newType = typesTable.getType(n1, n2, --n3);
    evaluateModifiedWhenRemNeigh(newType);
  }

  @Override
  public boolean areTwoTerracesTogether() {
    return n1 == ZIGZAG_EDGE && n2 == TERRACE && n3 == TERRACE;
  }

  @Override
  public void deposit(boolean forceNucleation) {
    occupied = true;
    if (forceNucleation) {
      type = TERRACE;
    }
    int i = 0;

    for (; i < 3; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).add1stNeighbour(forceNucleation);
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).add2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).add3rdNeighbour();
    }

    modified.addOwnAtom(this);
    if (getN1N2N3() > 0) {
      modified.addBondAtom(this);
    }
    totalProbability = 0;
  }

  /**
   * Extrae el átomo de este lugar (pásalo a no occupied y reduce la vecindad de los átomos vecinos,
   * si cambia algún tipo, recalcula probabilidades)
   */
  @Override
  public void extract() {
    occupied = false;

    int i = 0;
    for (; i < 3; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).remove1stNeighbour();
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).remove2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(iHexa, jHexa, i).remove3rdNeighbour();
    }

    if (getN1N2N3() > 0) {
      modified.addBondAtom(this);
    }

    list.addTotalProbability(-totalProbability);
    this.setOnList(null);
    if (bondsProbability != null) {
      PStack.returnProbArray(bondsProbability);
      bondsProbability = null;
    }
  }

  @Override
  public void updateAllRates() {
    double temp = -totalProbability;
    totalProbability = 0;

    if (this.isEligible()) {
      obtainRatesFromNeighbours(areAllRatesTheSame());
      temp += totalProbability;
    }
    if (this.isOnList()) {
      list.addTotalProbability(temp);
    }
  }

  private void obtainRatesFromNeighbours(boolean equalRates) {
    if (equalRates) {
      totalProbability += probJumpToNeighbour(type, 0) * 12.0;
    } else {
      if (bondsProbability == null) {
        bondsProbability = PStack.getProbArray();
      }
      for (int i = 0; i < 12; i++) {

        bondsProbability[i] = probJumpToNeighbour(type, i);
        totalProbability += bondsProbability[i];
      }
    }
  }

  private boolean areAllRatesTheSame() {
    if ((n1 + n2 + n3) != TERRACE) {
      return false;
    }
    for (int i = 11; i >= 3; i--) {
      if (lattice.getNeighbour(iHexa, jHexa, i).getType() != 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void updateOneBound(int posNeighbour) {

    double temp = 0;

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

    if (bondsProbability == null) {
      double newRate = probJumpToNeighbour(type, i);
      if (newRate * 12 != totalProbability) {
        double independentProbability = totalProbability / 12.0;
        bondsProbability = PStack.getProbArray();
        for (int a = 0; a < 12; a++) {
          bondsProbability[a] = independentProbability;
        }
        bondsProbability[i] = newRate;
        totalProbability += (newRate - independentProbability);
        temp += (newRate - independentProbability);
      }
    } else {
      totalProbability -= bondsProbability[i];
      temp -= bondsProbability[i];
      bondsProbability[i] = probJumpToNeighbour(type, i);
      totalProbability += bondsProbability[i];
      temp += bondsProbability[i];
    }
    list.addTotalProbability(temp);

  }

  /**
   * Probabilidad de saltar a cierta posicion vecina
   * @param originType
   * @param pos
   * @return 
   */
  private double probJumpToNeighbour(int originType, int pos) {

    AbstractGrowthAtom atom = lattice.getNeighbour(iHexa, jHexa, pos);

    if (atom.isOccupied()) {

      return 0;
    }

    byte lastTemp = atom.getTypeWithoutNeighbour(pos);

    double rate;
    int multiplier = super.getMultiplier();
    if (multiplier != 1) {
      rate = probabilities[originType][lastTemp] / multiplier;
      super.setMultiplier(1);
    } else {
      int hops = distancePerStep.getDistancePerStep(originType, originType);

      switch (originType) {
        case TERRACE:
        case ZIGZAG_EDGE:
        case ARMCHAIR_EDGE:
          rate = probabilities[originType][lastTemp] / (hops * hops);
          break;
        default:
          rate = probabilities[originType][lastTemp];
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

  private void evaluateModifiedWhenRemNeigh(byte newType) {
    if (type != newType) {
      type = newType;
      if (occupied) {
        modified.addOwnAtom(this);
      }
      if (getN1N2N3() > 0 && !occupied) {
        modified.addBondAtom(this);
      }
    }
  }

  private void evaluateModifiedWhenAddNeigh(byte newType) {
    if (type != newType) {
      type = newType;
      if (occupied) {
        modified.addOwnAtom(this);
      }
      if (getN1N2N3() > 1 && !occupied) {
        modified.addBondAtom(this);
      }
    }
  }
}
