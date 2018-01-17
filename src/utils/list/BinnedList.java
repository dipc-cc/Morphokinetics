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
package utils.list;

import basic.Parser;
import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.StaticRandom;

/**
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BinnedList extends AbstractList implements IProbabilityHolder {

  private static final float BIN_DIFFERENCE_FACTOR = 1.25f;
  private final AbstractList[] bins;
  private int currentBin;
  /**
   * Stores if the current totalProbability and the probability calculated from the list are the same.
   */
  private boolean clean;

  public BinnedList(Parser parser, int binAmount, int extraBinLevels) {
    super(parser);
    this.setLevel(extraBinLevels);

    if (extraBinLevels > 0) {
      bins = new BinnedList[binAmount];
      for (int i = 0; i < binAmount; i++) {
        bins[i] = new BinnedList(parser, binAmount, extraBinLevels - 1);
        bins[i].setParent(this);
      }
    } else {
      bins = new LinearList[binAmount];
      for (int i = 0; i < binAmount; i++) {
        bins[i] = new LinearList(parser);
        bins[i].setParent(this);
      }
    }
    clean = false;
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
    clean = false;
    updateCurrentList();
    setTotalAtoms(getTotalAtoms() + 1);
    bins[currentBin].addAtom(atom);
  }
  
  /**
   * Not tested. Might fail.There might be a problem between selected bin and current atom.
   *
   * @param atom atom to be deleted.
   */
  @Override
  public void deleteAtom(AbstractAtom atom) {
    updateCurrentList();
    setTotalAtoms(getTotalAtoms() - 1);
    bins[currentBin].deleteAtom(atom);
  }
  
  /**
   * Updates the total probability 
   * @param probabilityChange probability change
   */
  @Override
  public void addDiffusionProbability(double probabilityChange) {
    clean = false;
    setDiffusionProbability(getDiffusionProbability() + probabilityChange);
    // How I know which is the correct bin?? 
    // next line creates an ERROR!! (when using getTotalProbability()
    // Previously the current atom was updating its linear list and propagating the change to the parents.
    bins[currentBin].addDiffusionProbability(probabilityChange);
  }
  
  @Override
  public AbstractAtom nextEvent() {
    clean = false;
    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * (getDiffusionProbabilityFromList() + getDepositionProbability());
    if (this.getParent() == null) {
      addTime();
    }

    if (position < getDepositionProbability()) {
      return null; //we have to add a new atom
    }
    position -= getDepositionProbability();
    int selectedBin = 0;
    double accumulation = bins[selectedBin].getDiffusionProbabilityFromList();

    while (position >= accumulation) {
      selectedBin++;
      if (selectedBin == bins.length - 1) {
        break;
      }
      accumulation += bins[selectedBin].getDiffusionProbabilityFromList();
    }
    
    AbstractAtom atom = bins[selectedBin].nextEvent();
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
  public double getDiffusionProbabilityFromList() {
    if (clean) {
      return getDiffusionProbability();
    } else {
      double diffusionProbability = 0;
      for (AbstractList bin : bins) {
        diffusionProbability += bin.getDiffusionProbabilityFromList();
      }

      clean = true;
      setDiffusionProbability(diffusionProbability);
      return diffusionProbability;
    }
  }

  @Override
  public void reset() {
    super.reset();
    currentBin = 0;
    for (AbstractList bin : bins) {
      bin.reset();
    }
    clean = false;
  }

  @Override
  public AbstractAtom getAtomAt(int position) {
    int cont = 0;
    int i = 0;
    while (position >= cont + bins[i].getSize()) {
      cont += bins[i].getSize();
      i++;
    }
    return bins[i].getAtomAt(position - cont);
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
