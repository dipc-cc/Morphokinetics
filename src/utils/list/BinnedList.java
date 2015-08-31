/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.edu.cornell.lassp.houle.rngPack.RandomSeedable;

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

    this.level = extraBinLevels;

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

    while (bins[currentBin].getSize() > (totalAtoms * BIN_DIFFERENCE_FACTOR / bins.length)) {
      currentBin++;
      if (currentBin == bins.length) {
        currentBin = 0;
      }
    }
  }

  @Override
  public void addAtom(AbstractAtom atom) {
    updateCurrentList();
    totalAtoms++;
    totalProbability += atom.getProbability();
    bins[currentBin].addAtom(atom);
  }

  @Override
  public AbstractAtom nextEvent(RandomSeedable rng) {

    if (autoCleanup && removalsSinceLastCleanup > EVENTS_PER_CLEANUP) {
      this.cleanup();
      removalsSinceLastCleanup = 0;
    }

    double position = rng.raw() * (totalProbability + depositionProbability);
    if (this.parent == null) {
      time -= Math.log(rng.raw()) / (totalProbability + depositionProbability);
    }

    if (position < depositionProbability) {
      return null; //we have to add a new atom
    }
    position -= depositionProbability;
    int selected = 0;
    double accumulation = bins[selected].getTotalProbability();

    while (position >= accumulation) {
      selected++;
      if (selected == bins.length - 1) {
        break;
      }
      accumulation += bins[selected].getTotalProbability();
    }

    AbstractAtom atom = bins[selected].nextEvent(rng);
    if (atom != null) {
      totalAtoms--;
    }

    return atom;
  }

  @Override
  public int cleanup() {
    int totalAtomsOld = totalAtoms;
    for (AbstractList bin : bins) {
      totalAtoms -= bin.cleanup();
    }
    return (totalAtomsOld - totalAtoms);
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
  public double getTotalProbability() {
    return totalProbability;
  }

  @Override
  public void reset() {
    time = 0;
    totalProbability = 0;
    totalAtoms = 0;
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
    return totalAtoms;
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

  public int getLevel() {
    return level;
  }

  @Override
  public ListIterator getIterator() {
    return new BinnedListIterator(this);
  }

}
