/*
 * 
 * Clase que modela un átomo de la retícula
 *  La clase alberga todas los parámetros necesarios
 * 
 */
package kineticMonteCarlo.atom.diffusion.grapheneCvdGrowth;

import kineticMonteCarlo.atom.diffusion.Abstract2DDiffusionAtom;
import kineticMonteCarlo.atom.diffusion.ArrayStack;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.diffusion.ModifiedBuffer;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import kineticMonteCarlo.lattice.diffusion.grapheneCvdGrowth.GrapheneCvdGrowthLattice;
import utils.StaticRandom;

public class GrapheneAtom extends Abstract2DDiffusionAtom {

  private GrapheneCvdGrowthLattice lattice;
  private static final ArrayStack PStack = new ArrayStack(12);
  private static GrapheneTypesTable typesTable;
  private byte n1, n2, n3;

  public GrapheneAtom(short X, short Y, HopsPerStep distancePerStep) {

    super(X, Y, distancePerStep);
    if (typesTable == null) {
      typesTable = new GrapheneTypesTable();
    }
  }

  @Override
  public void initialize(Abstract2DDiffusionLattice lattice, double[][] probabilities, ModifiedBuffer modified) {
    super.initialize(probabilities, modified);
    this.lattice = (GrapheneCvdGrowthLattice) lattice;
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

//escogemos a que posición vamos a saltar
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

    byte tipo_new = typesTable.getType(++n1, n2, n3);
    if (forceNucleation && occupied) {
      tipo_new = 7;
    }
    evaluateModifiedWhenAddNeigh(tipo_new);
  }

  private void add2ndNeighbour() {

    byte tipo_new = typesTable.getType(n1, ++n2, n3);
    evaluateModifiedWhenAddNeigh(tipo_new);
  }

  private void add3rdNeighbour() {

    byte tipo_new = typesTable.getType(n1, n2, ++n3);
    evaluateModifiedWhenAddNeigh(tipo_new);
  }

  private void remove1stNeighbour() {

    byte tipo_new = typesTable.getType(--n1, n2, n3);
    evaluateModifiedWhenRemNeigh(tipo_new);
  }

  private void remove2ndNeighbour() {

    byte tipo_new = typesTable.getType(n1, --n2, n3);
    evaluateModifiedWhenRemNeigh(tipo_new);
  }

  private void remove3rdNeighbour() {

    byte tipo_new = typesTable.getType(n1, n2, --n3);
    evaluateModifiedWhenRemNeigh(tipo_new);
  }

  @Override
  public boolean areTwoTerracesTogether() {
    return n1 == 2 && n2 == 0 && n3 == 0;
  }

  @Override
  public void deposit(boolean force_nucleation) {

    occupied = true;
    if (force_nucleation) {
      type = 7;
    }
    int i = 0;

    for (; i < 3; i++) {
      lattice.getNeighbour(X, Y, i).add1stNeighbour(force_nucleation);
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

//extrae el átomo de este lugar (pásalo a no occupied y reduce la vecindad de los átomos vecinos, si cambia algún tipo, recalcula probabilidades)
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
      obtainRatesFromNeighBors(areAllRatesTheSame());
      temp += totalProbability;
    }
    if (this.isOnList()) {
      list.addTotalProbability(temp);
    }
  }

  private void obtainRatesFromNeighBors(boolean equalRates) {
    if (equalRates) {
      totalProbability += prob_jump_to_neighbor(type, 0) * 12.0;
    } else {
      if (bondsProbability == null) {
        bondsProbability = PStack.getProbArray();
      }
      for (int i = 0; i < 12; i++) {

        bondsProbability[i] = prob_jump_to_neighbor(type, i);
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
  public void updateOneBound(int neighborpos) {

    double temp = 0;

    int i = neighborpos; //eres el vecino 1 de tu 1º vecino. Eres el vecino 2 de tu 2º vecino. eres el 3º Vecino de tu vecino 3.
    switch (neighborpos) {
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
      double rateNuevo = prob_jump_to_neighbor(type, i);
      if (rateNuevo * 12 != totalProbability) {
        double prob_independiente = totalProbability / 12.0;
        bondsProbability = PStack.getProbArray();
        for (int a = 0; a < 12; a++) {
          bondsProbability[a] = prob_independiente;
        }
        bondsProbability[i] = rateNuevo;
        totalProbability += (rateNuevo - prob_independiente);
        temp += (rateNuevo - prob_independiente);
      }
    } else {
      totalProbability -= bondsProbability[i];
      temp -= bondsProbability[i];
      bondsProbability[i] = prob_jump_to_neighbor(type, i);
      totalProbability += bondsProbability[i];
      temp += bondsProbability[i];
    }
    list.addTotalProbability(temp);

  }

//probabilidad de saltar a cierta posicion vecina
  private double prob_jump_to_neighbor(int origin_type, int pos) {

    Abstract2DDiffusionAtom atom = lattice.getNeighbour(X, Y, pos);

    if (atom.isOccupied()) {

      return 0;
    }

    byte lastTemp = atom.getTypeWithoutNeighbour(pos);

    double rate;
    if (multiplier != 1) {
      rate = probabilities[origin_type][lastTemp] / multiplier;
      multiplier = 1;
    } else {
      int hops = distancePerStep.getDistancePerStep(origin_type, origin_type);

           //if (origen_tipo==2) System.out.println(origen_tipo+" "+lastTemp+" "+hops);
      switch (origin_type) {
        case 0:
          rate = probabilities[origin_type][lastTemp] / (hops * hops);
          break;
        case 2:
          rate = probabilities[origin_type][lastTemp] / (hops * hops);
          break;
        case 3:
          rate = probabilities[origin_type][lastTemp] / (hops * hops);
          break;
        default:
          rate = probabilities[origin_type][lastTemp];
          break;
      }
    }
    return rate;
  }

//saber que tipo de átomo sería si el vecino pos_origen estuviera vacío
//útil para saber el tipo de átomos que serás al saltar.
//sabiendo el tipo de átomo origen y destino puedes sacar la probabilidad de éxito   origen ====> destino
  @Override
  public byte getTypeWithoutNeighbour(int pos_origen) {

    if (pos_origen < 3) {
      return typesTable.getType(n1 - 1, n2, n3);
    }
    if (pos_origen < 9) {
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
