/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 *
 *
 */
public class BinnedList extends AbstractList implements IProbabilityHolder {

  private static final float BIN_DIFFERENCE_FACTOR = 1.25f;
  private final AbstractList[] bins;
  private int currentBin;

  public BinnedList(int binAmount, int extraBinLevels) {
    super();
    this.setLevel(extraBinLevels);

    if (extraBinLevels > 0) {
      bins = new BinnedList[binAmount];
      for (int i = 0; i < binAmount; i++) {
        bins[i] = new BinnedList(binAmount, extraBinLevels - 1);
        bins[i].setParent(this);
      }
    } else {
      bins = new LinearList[binAmount];
      for (int i = 0; i < binAmount; i++) {
        bins[i] = new LinearList();
        bins[i].setParent(this);
      }
    }
  }

  private void updateCurrentList() {
    
    while (bins[currentBin].getSize() > (getTotalAtoms() * BIN_DIFFERENCE_FACTOR / bins.length)) {
      currentBin++;
      if (currentBin == bins.length) {
        currentBin = 0;
      }
    }
  }

  @Override
  public void addAtom(AbstractAtom atom) {
    updateCurrentList();
    setTotalAtoms(getTotalAtoms() + 1);
    bins[currentBin].addAtom(atom);
  }
  
  /**
   * Updates the total probability
   * @param prob probability change
   */
  @Override
  public void addTotalProbability(double prob) {
    setTotalProbability(getTotalProbability() + prob);
    bins[currentBin].addTotalProbability(prob);
  }
  
  @Override
  public AbstractAtom nextEvent() {

    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * (getTotalProbabilityFromList() + getDepositionProbability()); // has to be getTotalProbability() 
    if (this.getParent() == null) {
      addTime(-Math.log(StaticRandom.raw()) / (getTotalProbabilityFromList() + getDepositionProbability())); // has to be getTotalProbability()
    }

    if (position < getDepositionProbability()) {
      return null; //we have to add a new atom
    }
    // Possible bug here! !Removing next line results of Ag run are much more similar (remove & solve?) 
    position -= getDepositionProbability();
    int selected = 0;
    double accumulation = bins[selected].getTotalProbabilityFromList(); //has to be bins[selectedBin].getTotalProbability() instead

    while (position >= accumulation) {
      selected++;
      if (selected == bins.length - 1) {
        break;
      }
      accumulation += bins[selected].getTotalProbabilityFromList(); // has to be bins[selectedBin].getTotalProbability() instead
    }
    
    AbstractAtom atom = bins[selected].nextEvent();
    if (atom != null) { //this never happens (with no extra levels at least)
      setTotalAtoms(getTotalAtoms() - 1);
    }
    
    return atom;
  }

  @Override
  public int cleanup() {
    int totalAtomsOld = getTotalAtoms();
    for (AbstractList bin : bins) {
      setTotalAtoms(getTotalAtoms() - bin.cleanup());
    }
    return (totalAtomsOld - getTotalAtoms());
  }

  @Override
  public double getTotalProbabilityFromList() {
    double totalProb = 0;
    for (AbstractList bin : bins) {
      totalProb += bin.getTotalProbabilityFromList();
    }

    return totalProb;
  }

  @Override
  public void reset() {
    super.reset();
    currentBin = 0;
    for (AbstractList bin : bins) {
      bin.reset();
    }
  }

  @Override
  public AbstractAtom getAtomAt(int pos) {
    int cont = 0;
    int i = 0;
    while (pos >= cont + bins[i].getSize()) {
      cont += bins[i].getSize();
      i++;
    }
    return bins[i].getAtomAt(pos - cont);
  }

  @Override
  public int getSize() {
    return getTotalAtoms();
  }

  public void traceSizes(String separator) {
    System.out.println(separator + bins.length);
    String lowerLevelSeparator = separator + "\t";
    for (AbstractList bin : bins) {
      if (bin instanceof BinnedList) {
        ((BinnedList) bin).traceSizes(lowerLevelSeparator);
      } else {
        System.out.println(lowerLevelSeparator + bin.getSize());
      }
    }
  }

  public AbstractList[] getBins() {
    return bins;
  }

  @Override
  public ListIterator getIterator() {
    return new BinnedListIterator(this);
  }

}
