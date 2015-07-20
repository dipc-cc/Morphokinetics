/*
 * 
 * Clase que modela un átomo de la retícula
 *  La clase alberga todas los parámetros necesarios
 * 
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import kineticMonteCarlo.lattice.GrapheneLattice;
import utils.StaticRandom;

public class GrapheneAtom extends Abstract2DDiffusionAtom {

  private GrapheneLattice lattice;
  private static final ArrayStack PStack = new ArrayStack(12);
  private static GrapheneTypesTable typesTable;
  private byte n1, n2, n3;

  public GrapheneAtom(short X, short Y, HopsPerStep distancePerStep) {
    super(X, Y, distancePerStep);
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
    
    bondsProbability = new double[6];
  }

  @Override
  public void initialize(Abstract2DDiffusionLattice lattice, double[][] probabilities, ModifiedBuffer modified) {
    super.initialize(probabilities, modified);
    this.lattice = (GrapheneLattice) lattice;
  }

  @Override
  public boolean isEligible() {
    return occupied && (type < 6);
  } // tipos 6 y 7 son considerados atomos inmoviles

  public byte getn1() {
    return n1;
  }

  public byte getn2() {
    return n2;
  }

  public byte getn3() {
    return n3;
  }

  public int getn1n2n3() {
    return (n1 + n2 + n3);
  }

  @Override
  public void clear() {

    n1 = n2 = n3 = type = 0;
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
        if (lattice.getNeighbour(X, Y, i).isOccupied()) {
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
  public Abstract2DDiffusionAtom chooseRandomHop() {

    double raw = StaticRandom.raw();

    if (bondsProbability == null) {
      return lattice.getNeighbour(X, Y, (int) (raw * 12));
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
    return lattice.getNeighbour(X, Y, cont - 1);
  }

  private void add1stNeighbour(boolean forceNucleation) {

    byte newType = typesTable.getType(++n1, n2, n3);
    if (forceNucleation && occupied) {
      newType = 7;
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
    return n1 == 2 && n2 == 0 && n3 == 0;
  }

  @Override
  public void deposit(boolean forceNucleation) {

    occupied = true;
    if (forceNucleation) {
      type = 7;
    }
    int i = 0;

    for (; i < 3; i++) {
      lattice.getNeighbour(X, Y, i).add1stNeighbour(forceNucleation);
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(X, Y, i).add2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(X, Y, i).add3rdNeighbour();
    }

    modified.addOwnAtom(this);
    if (getn1n2n3() > 0) {
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
      lattice.getNeighbour(X, Y, i).remove1stNeighbour();
    }
    for (; i < 9; i++) {
      lattice.getNeighbour(X, Y, i).remove2ndNeighbour();
    }
    for (; i < 12; i++) {
      lattice.getNeighbour(X, Y, i).remove3rdNeighbour();
    }

    if (getn1n2n3() > 0) {
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
    if ((n1 + n2 + n3) != 0) {
      return false;
    }
    for (int i = 11; i >= 3; i--) {
      if (lattice.getNeighbour(X, Y, i).getType() != 0) {
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

    Abstract2DDiffusionAtom atom = lattice.getNeighbour(X, Y, pos);

    if (atom.isOccupied()) {

      return 0;
    }

    byte lastTemp = atom.getTypeWithoutNeighbour(pos);

    double rate;
    if (multiplier != 1) {
      rate = probabilities[originType][lastTemp] / multiplier;
      multiplier = 1;
    } else {
      int hops = distancePerStep.getDistancePerStep(originType, originType);

           //if (originType==2) System.out.println(originType+" "+lastTemp+" "+hops);
      switch (originType) {
        case 0:
          rate = probabilities[originType][lastTemp] / (hops * hops);
          break;
        case 2:
          rate = probabilities[originType][lastTemp] / (hops * hops);
          break;
        case 3:
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

  @Override
  public int getNeighbourCount() {
    return 12;
  }

  private void evaluateModifiedWhenRemNeigh(byte newType) {
    if (type != newType) {
      type = newType;
      if (occupied) {
        modified.addOwnAtom(this);
      }
      if (getn1n2n3() > 0 && !occupied) {
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
      if (getn1n2n3() > 1 && !occupied) {
        modified.addBondAtom(this);
      }
    }
  }
}
