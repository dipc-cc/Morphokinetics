package utils.list;

import basic.Parser;
import basic.io.OutputType;
import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import static kineticMonteCarlo.atom.AgAtom.CORNER;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.KINK;
import static kineticMonteCarlo.atom.AgAtom.EDGE_A;
import static kineticMonteCarlo.atom.AgAtom.EDGE_B;
import kineticMonteCarlo.atom.AgAtom;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import utils.StaticRandom;

/**
 * Linked list basic implementation.
 */
public class LinearList extends AbstractList implements IProbabilityHolder{

  private final ArrayList<AbstractAtom> surface;
  /**
   * Stores if the current totalProbability and the probability calculated from the list are the same.
   */
  private boolean clean;
  private double Ri_DeltaI;
  
  private final boolean aeOutput;
  private boolean doActivationEnergyStudy;
  private double[][] histogramPossible;
  private int[][] histogramPossibleCounter;
  private int[][] histogramPossibleSimple;

  public LinearList(Parser parser) {
    super();
    surface = new ArrayList();
    this.setLevel(-1);
    clean = false;
    Ri_DeltaI = 0.0;

    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    doActivationEnergyStudy = false;
    if (aeOutput) {
      if (parser.getCalculationMode().equals("basic")) {
        doActivationEnergyStudy = true;
        histogramPossible = new double[4][4];
        histogramPossibleCounter = new int[4][4];
      }
      if (parser.getCalculationMode().equals("Ag") || parser.getCalculationMode().equals("AgUc")) {
        doActivationEnergyStudy = true;
        histogramPossible = new double[7][7];
        histogramPossibleCounter = new int[7][7];
      }
    }
  }
  
  @Override
  public double getTotalProbabilityFromList() {
    if (clean) {
      return getTotalProbability();
    } else {
      double totalProbability = 0;

      ListIterator<AbstractAtom> li = surface.listIterator();
      while (li.hasNext()) {
        AbstractAtom atom = li.next();
        if (atom.isEligible()) {
          totalProbability += atom.getProbability();
        }
      }
      clean = true;
      setTotalProbability(totalProbability);
      return totalProbability;
    }
  }  
  
  /**
   * Updates the total probability
   * @param probabilityChange probability change
   */
  @Override
  public void addTotalProbability(double probabilityChange) {
    clean = false;
    setTotalProbability(getTotalProbability() + probabilityChange);
  }

  @Override
  public void reset() {
    super.reset();
    surface.clear();
    clean = false;
    Ri_DeltaI = 0.0;
  }

  @Override
  public int cleanup() {
    int tmp = getTotalAtoms();
    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom atom = li.next();
      if (!atom.isEligible()) {
        li.remove();
        setTotalAtoms(getTotalAtoms() - 1);
      }
    }
    return (tmp - getTotalAtoms());
  }

  @Override
  public void addAtom(AbstractAtom atom) {
    clean = false;
    surface.add(0, atom);
    atom.setList(true);
    setTotalAtoms(getTotalAtoms() + 1);
  }
  
  @Override
  public void deleteAtom(AbstractAtom atom) {
    surface.remove(atom);
  }

  @Override
  public AbstractAtom nextEvent() {
    addRemovalsSinceLastCleanup();
    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * (getTotalProbability() + getDepositionProbability());

    addTime(-Math.log(StaticRandom.raw()) / (getTotalProbability() + getDepositionProbability()));
    if (position < getDepositionProbability()) {
      return null; //toca añadir un átomo nuevo
    }

    int[][] histograma = new int[7][7];
    double[][] histograma2 = new double[7][7];
    // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
    for (int i = 0; i < surface.size(); i++) {
      AgAtom atom = (AgAtom) surface.get(i);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        AgAtom neighbourAtom = atom.getNeighbour(pos);
        if (!neighbourAtom.isPartOfImmobilSubstrate()) {
          int myPositionForNeighbour = (pos + 3) % atom.getNumberOfNeighbours();
          byte destination = neighbourAtom.getTypeWithoutNeighbour(myPositionForNeighbour);
          destination = neighbourAtom.getRealType();
          byte origin = atom.getRealType();
          double prob = atom.probJumpToNeighbour(1, pos);
          if (prob > 0) {
            if ((origin == EDGE_A || origin == EDGE_B) && destination == CORNER) {
              //byte originalDestination  = destination;
              //AgAtom aheadAtom = a.aheadCornerAtom(pos);
              //if (aheadAtom != null) {
                //destination = aheadAtom.getRealType();
                if (origin == EDGE_A) destination =EDGE_B;
                if (origin == EDGE_B) destination =EDGE_A;
              //}
            } else {

              int orientation = neighbourAtom.getOrientation(atom);
              if ((destination == EDGE_A || destination == KINK) && (orientation & 1) != 0) {
                destination += 3; //convert EDGE_A to EDGE_B or KINK_A to KINK_B
              }
            }
            /*if (origin == EDGE) {
              System.out.print("From A");
              byte newDest = neighbourAtom.getTypeWithoutNeighbour(myPositionForNeighbour);
              System.out.println(" to "+newDest+"("+destination+") "+orientation);
            }*/
            histogramPossible[origin][destination] += 1/getTotalProbability();
            histogramPossibleCounter[origin][destination]++;
            histograma[origin][destination]++;
            histograma2[origin][destination] += ((AgAtom) surface.get(0)).getProbability(origin, destination);
            if (atom.getBondsProbability(pos) != ((AgAtom) surface.get(0)).getProbability(origin, destination)) {
              System.out.println("error! origin "+origin +" dest. "+destination+" stored probability "+atom.getBondsProbability(pos) +" calculated probability "+ ((AgAtom) surface.get(0)).getProbability(origin, destination));// " id "+atom.getId());
              int tmpType = atom.getRealType();
            } else {
              System.out.println("OK");
            }
          }
        }       
      }
    }
    
    double tmpProbability = 0.0;
    double histograma2suma = 0.0;
    for (int i = 0; i < histograma.length; i++) {
      for (int j = 0; j < histograma[0].length; j++) {
        tmpProbability += histograma[i][j]*((AgAtom) surface.get(0)).getProbability(i, j);
        histograma2suma += histograma2[i][j];
      }
    }
    System.out.println("probability "+getTotalProbability()+" from list " + getTotalProbabilityFromList()+ " hist1 "+tmpProbability+" hist2 "+histograma2suma);

    double time = 1 / (getTotalProbability());
    Ri_DeltaI += getTotalProbability() * time;
    position -= getDepositionProbability();
    double currentProbability = 0;

    AbstractAtom atom = null;
    for (int i = 0; i < surface.size(); i++) {
      clean = false;
      atom = surface.get(i);
      currentProbability += atom.getProbability();
      if (currentProbability >= position) {
        surface.remove(i);
        setTotalAtoms(getTotalAtoms() - 1);
        return atom;
      }
    }

    if (atom != null) {
      clean = false;
      surface.remove(surface.size() - 1); // Remove from the list the last element
      setTotalAtoms(getTotalAtoms() - 1); // Update accordingly the number of atoms
    }
    
    return atom;
  }

  @Override
  public int getSize() {
    return surface.size();
  }

  @Override
  public AbstractAtom getAtomAt(int position) {
    return surface.get(position);

  }

  @Override
  public ListIterator<AbstractAtom> getIterator() {
    return surface.listIterator();
  }

  public double getRi_DeltaI() {
    return Ri_DeltaI;
  }
  
  public double[][] getHistogramPossible() {
    return histogramPossible;
}
  
  public int[][] getHistogramPossibleCounter() {
    return histogramPossibleCounter;
  }
  
}
