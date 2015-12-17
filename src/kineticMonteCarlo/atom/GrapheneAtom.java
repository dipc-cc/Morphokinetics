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
  /**
   * Total number of 1st neighbours.
   * @return 0 <= value <= 3
   */
  private byte n1;
  /**
   * Total number of 2nd neighbours.
   * @return 0 <= value <= 6
   */
  private byte n2;  
  /**
   * Total number of 3rd neighbours.
   * @return 0 <= value <= 3
   */
  private byte n3;
  private HopsPerStep distancePerStep;

  public GrapheneAtom(short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(iHexa, jHexa, 12);
  
    this.distancePerStep = distancePerStep;
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
  }

  @Override
  public void initialise(AbstractGrowthLattice lattice, double[][] probabilities, ModifiedBuffer modified) {
    super.initialise(probabilities, modified);
    this.lattice = (GrapheneLattice) lattice;
  }

  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() < KINK);
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

    setType(TERRACE);
    n1 = n2 = n3 = TERRACE;
    setOccupied(false);
    setOutside(false);

    resetTotalProbability();
    setList(null);

    if (getBondsProbability() != null) {
      PStack.returnProbArray(getBondsProbability());
      setBondsProbability(null);
    }
  }

  @Override
  public int getOrientation() {

    if (n1 == 1) {
      for (int i = 0; i < 3; i++) {
        if (lattice.getNeighbour(getiHexa(), getjHexa(), i).isOccupied()) {
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
      return lattice.getNeighbour(getiHexa(), getjHexa(), (int) (raw * 12));
    }

    double linearSearch = raw * getTotalProbability();

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
    //System.out.println(bondsProbability.length);
    return lattice.getNeighbour(getiHexa(), getjHexa(), cont - 1);
  }

  private void add1stNeighbour(boolean forceNucleation) {
    byte newType = typesTable.getType(++n1, n2, n3);
    if (forceNucleation && isOccupied()) {
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
    setOccupied(true);
    if (forceNucleation) {
      setType(TERRACE);
    }
    int i = 0;

    for (; i < 3; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).add1stNeighbour(forceNucleation);
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).add2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).add3rdNeighbour();
    }

    addOwnAtom();
    if (getN1N2N3() > 0) {
      addBondAtom();
    }
    resetTotalProbability();
  }

  /**
   * Extrae el átomo de este lugar (pásalo a no occupied y reduce la vecindad de los átomos vecinos,
   * si cambia algún tipo, recalcula probabilidades)
   */
  @Override
  public void extract() {
    setOccupied(false);

    int i = 0;
    for (; i < 3; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).remove1stNeighbour();
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).remove2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(getiHexa(), getjHexa(), i).remove3rdNeighbour();
    }

    if (getN1N2N3() > 0) {
      addBondAtom();
    }

    addTotalProbability(-getTotalProbability());
    this.setList(null);
    if (getBondsProbability() != null) {
      PStack.returnProbArray(getBondsProbability());
      setBondsProbability(null);
    }
  }

  @Override
  public void updateAllRates() {
    double temp = -getTotalProbability();
    resetTotalProbability();

    if (this.isEligible()) {
      obtainRatesFromNeighbours(areAllRatesTheSame());
      temp += getTotalProbability();
    }
    if (this.isOnList()) {
      addTotalProbability(temp);
    }
  }

  private void obtainRatesFromNeighbours(boolean equalRates) {
    if (equalRates) {
      addToTotalProbability(probJumpToNeighbour(getType(), 0) * 12.0);
    } else {
      if (getBondsProbability() == null) {
        setBondsProbability(PStack.getProbArray());
      }
      for (int i = 0; i < 12; i++) {

        getBondsProbability()[i] = probJumpToNeighbour(getType(), i);
        addToTotalProbability(getBondsProbability()[i]);
      }
    }
  }

  private boolean areAllRatesTheSame() {
    if ((n1 + n2 + n3) != TERRACE) {
      return false;
    }
    for (int i = 11; i >= 3; i--) {
      if (lattice.getNeighbour(getiHexa(), getjHexa(), i).getType() != 0) {
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

    if (getBondsProbability() == null) {
      double newRate = probJumpToNeighbour(getType(), i);
      if (newRate * 12 != getTotalProbability()) {
        double independentProbability = getTotalProbability() / 12.0;
        setBondsProbability(PStack.getProbArray());
        for (int a = 0; a < 12; a++) {
          getBondsProbability()[a] = independentProbability;
        }
        getBondsProbability()[i] = newRate;
        addToTotalProbability(newRate - independentProbability);
        temp += (newRate - independentProbability);
      }
    } else {
      addToTotalProbability(-getBondsProbability()[i]);
      temp -= getBondsProbability()[i];
      getBondsProbability()[i] = probJumpToNeighbour(getType(), i);
      addToTotalProbability(getBondsProbability()[i]);
      temp += getBondsProbability()[i];
    }
    addTotalProbability(temp);

  }

  /**
   * Probabilidad de saltar a cierta posicion vecina
   * @param originType
   * @param pos
   * @return 
   */
  private double probJumpToNeighbour(int originType, int pos) {

    AbstractGrowthAtom atom = lattice.getNeighbour(getiHexa(), getjHexa(), pos);

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

  private void evaluateModifiedWhenRemNeigh(byte newType) {
    if (getType() != newType) {
      setType(newType);
      if (isOccupied()) {
        addOwnAtom();
      }
      if (getN1N2N3() > 0 && !isOccupied()) {
        addBondAtom();
      }
    }
  }

  private void evaluateModifiedWhenAddNeigh(byte newType) {
    if (getType() != newType) {
      setType(newType);
      if (isOccupied()) {
        addOwnAtom();
      }
      if (getN1N2N3() > 1 && !isOccupied()) {
        addBondAtom();
      }
    }
  }
}
