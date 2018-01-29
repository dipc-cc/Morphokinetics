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
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import basic.io.Restart;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.ConcertedAtom;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import kineticMonteCarlo.lattice.Concerted6LatticeSimple;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.lattice.MultiAtom;
import static kineticMonteCarlo.process.ConcertedProcess.ADSORB;
import static kineticMonteCarlo.process.ConcertedProcess.CONCERTED;
import static kineticMonteCarlo.process.ConcertedProcess.MULTI;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import ratesLibrary.concerted.AbstractConcertedRates;
import utils.StaticRandom;
import utils.list.LinearList;
import utils.list.atoms.AtomsArrayList;
import utils.list.atoms.AtomsAvlTree;
import utils.list.atoms.AtomsCollection;
import utils.list.atoms.IAtomsCollection;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedKmc extends AbstractGrowthKmc {

  private long simulatedSteps;
  private final IAtomsCollection[] sites;
  /** Stores all collections of atoms; either in a tree or an array. */
  private AtomsCollection col;
  private final boolean automaticCollections;
  private final long maxSteps;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private double adsorptionRatePerSite;
  private double[][] diffusionRatePerAtom;
  private double[] diffusionRatePerIslandSize;
  private double[] diffusionRateMultiAtom;
  // Total rates
  private double[] totalRate;
  /**
   * Attribute to control the output of data every 1% and nucleation.
   */
  private final boolean extraOutput;
  /**
   * Activation energy output at the end of execution
   */
  private final boolean aeOutput;
  private final ActivationEnergy activationEnergy;
  private final Restart restart;
  private final boolean doIslandDiffusion;
  private final boolean doMultiAtomDiffusion;
 
  public ConcertedKmc(Parser parser, String restartFolder) {
    super(parser);
    Concerted6LatticeSimple concertedLattice;
    concertedLattice = new Concerted6LatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), null);
    setLattice(concertedLattice);
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }   
    sites = new IAtomsCollection[4];
    automaticCollections = parser.areCollectionsAutomatic();
    col = new AtomsCollection(concertedLattice, "concerted");
    // Either a tree or array 
    sites[ADSORB] = col.getCollection(false, ADSORB);
    sites[SINGLE] = col.getCollection(false, SINGLE);
    sites[CONCERTED] = col.getCollection(false, CONCERTED);
    sites[MULTI] = col.getCollection(false, MULTI);

    totalRate = new double[4]; // adsorption, diffusion, island diffusion, multi-atom

    maxSteps = parser.getNumberOfSteps();
    maxCoverage = (float) parser.getCoverage() / 100;
    extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    activationEnergy = new ActivationEnergy(parser);
    restart = new Restart(false, restartFolder);
    doIslandDiffusion = parser.doIslandDiffusion();
    doMultiAtomDiffusion = parser.doMultiAtomDiffusion();
  }

  public void setRates(AbstractConcertedRates rates) {
    adsorptionRatePerSite = rates.getDepositionRatePerSite();
    
    diffusionRatePerAtom = rates.getDiffusionRates();
    if (doIslandDiffusion) {
      diffusionRatePerIslandSize = rates.getIslandDiffusionRates();
    } else {
      diffusionRatePerIslandSize = new double[9]; // 0 rate for all islands
    }
    if (doMultiAtomDiffusion) {
      diffusionRateMultiAtom = rates.getMultiAtomRates();
    } else {
      diffusionRateMultiAtom = new double[4];
    }    
    getLattice().setAtomsTypesCounter(12); // There are 7 types and some have subtypes. See {@link ConcertedAtom#getRealType()} for more information
  }

  /**
   * Performs a simulation step.
   *
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    if (getList().getGlobalProbability() == 0) {
      return true; // there is nothing more we can do
    }
    byte reaction = getList().nextReaction();
    switch (reaction) {
      case ADSORB:
        depositNewAtom();
        break;
      case SINGLE:
        diffuseAtom(); 
        break;
      case CONCERTED:
        diffuseIsland();
        break;
      case MULTI:
        diffuseMultiAtom();
        break;
    }
            
    simulatedSteps++;
    return simulatedSteps == maxSteps;
  }

  @Override
  public int simulate() {
    int coverageThreshold = 1;
    int limit = 100000;
    int returnValue = 0;

    while (getLattice().getCoverage() < maxCoverage) {
      getList().getDeltaTime(true);
      if (getLattice().isPaused()) {
        try {
          Thread.sleep(250);
        } catch (InterruptedException ex) {
          Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else {
        activationEnergy.updatePossibles(sites[SINGLE].iterator(), getList().getGlobalProbability(), getList().getDeltaTime(false));
        activationEnergy.updatePossiblesIslands(getLattice().getIslandIterator(), getList().getGlobalProbability(), getList().getDeltaTime(false));
        activationEnergy.updatePossiblesMultiAtoms(getLattice().getMultiAtomIterator(), getList().getGlobalProbability(), getList().getDeltaTime(false));
        if (extraOutput && getCoverage() * limit >= coverageThreshold) { // print extra data every 1% of coverage, previously every 1/1000 and 1/10000
            if (coverageThreshold == 10 && limit > 100) { // change the interval of printing
              limit = limit / 10;
              coverageThreshold = 1;
            }
            printData();
            mergeIslands(); // recompute island's rate, after island counting have been deleted in previous islands counting.
            coverageThreshold++;
          }
        if (performSimulationStep()) {
          break;
        }
        checkSizes();
      }
    }
    if (aeOutput) {
      double ri = ((LinearList) getList()).getRi_DeltaI();
      double time = getList().getTime();
      System.out.println("Needed steps " + simulatedSteps + " time " + time + " Ri_DeltaI " + ri + " R " + ri / time + " R " + simulatedSteps / time);
      printData();
    } 
    return returnValue;
  }

  @Override
  public void depositSeed() {
    totalRate = new double[4];
    getLattice().resetOccupied();
    initRates();
    simulatedSteps = 0;
  }

  @Override
  public void reset() {
    activationEnergy.reset();
    Iterator iter = getList().getIterator();
    while (iter.hasNext()) {
      ConcertedAtom atom = (ConcertedAtom) iter.next();
      atom.clear();
    }
    getLattice().reset();
    getList().reset();
    restart.reset();
    sites[ADSORB].reset();
    sites[SINGLE].reset();
    sites[CONCERTED].reset();
    sites[MULTI].reset();
  }
  
  private boolean depositAtom(ConcertedAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }
    getLattice().deposit(atom, false);
    
    return true;
  }
  
  private ConcertedAtom depositNewAtom() {
    return depositNewAtom(-1);
  }
  
  /**
   * Deposit new atom in with the given label.
   * 
   * @param id if negative, random number is chosen.
   * @return 
   */
  private ConcertedAtom depositNewAtom(int id) {
    ConcertedAtom destinationAtom = null;
    int ucIndex = 0;
    byte atomType;

    if (sites[ADSORB].isEmpty()) {
      // can not deposit anymore
      return null;
    }
    
    if (id < 0) {
      destinationAtom = (ConcertedAtom) sites[ADSORB].randomElement();
    } else {
      destinationAtom = (ConcertedAtom) sites[ADSORB].search(new ConcertedAtom(id, -1));
    }

    if (destinationAtom == null || destinationAtom.getRate(ADSORB) == 0 || destinationAtom.isOccupied()) {
      boolean isThereAnAtom = destinationAtom == null;
      System.out.println("Something is wrong " + isThereAnAtom);
    }
    //check neighbourhood
    atomType = (byte) destinationAtom.getOccupiedNeighbours();
    destinationAtom.setType(atomType);
    depositAtom(destinationAtom);
    
    Set<ConcertedAtom> modifiedAtoms = addModifiedAtoms(null, destinationAtom);
    updateRates(modifiedAtoms);
    updateRatesIslands(null, destinationAtom, false);
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    return destinationAtom;
  }
  
  /**
   * Moves an atom.
   */
  private void diffuseAtom() {
    ConcertedAtom originAtom = (ConcertedAtom) sites[SINGLE].randomElement();
    ConcertedAtom destinationAtom = originAtom.getRandomNeighbour(SINGLE);
    int oldType = originAtom.getType();
    boolean wasDimer = originAtom.isDimer();
    getLattice().extract(originAtom);
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    getLattice().swapAtomsInMultiAtom(originAtom, destinationAtom);
    if (aeOutput) {
      activationEnergy.updateSuccess(oldType, destinationAtom.getType());
    }
    Set<ConcertedAtom> modifiedAtoms = addModifiedAtoms(null, originAtom);
    modifiedAtoms = addModifiedAtoms(modifiedAtoms, destinationAtom);
    updateRates(modifiedAtoms);
    updateRatesIslands(originAtom, destinationAtom, wasDimer);
  }
  
  private int getRandomIsland() {
    double randomNumber = StaticRandom.raw() * totalRate[CONCERTED];
    double sum = 0.0;
    for (int i = 0; i < getLattice().getIslandCount(); i++) {
      sum += getLattice().getIsland(i).getRate(CONCERTED);
      if (sum > randomNumber) {
        return i;
      }
    }
    throw new ArrayIndexOutOfBoundsException("\nTotal rate " + totalRate[CONCERTED]
        + " random number " + randomNumber + " number of islands " + getLattice().getIslandCount()
        + " sum " + sum);
  }

  /**
   * Moves an island and recomputes all the neighbourhood.
   *
   * @param originIsland Island to be moved.
   * @param direction Moving direction.
   * @return New island with the number of the original one.
   */
  private Island moveIsland(Island originIsland, int direction) {
    ConcertedAtom iOrigAtom;
    ConcertedAtom iDestAtom;
    Island destinationIsland = new Island(originIsland.getIslandNumber());
    Set<ConcertedAtom> modifiedAtoms = new HashSet<>();
    //clone
    Island originIslandCopy = new Island(originIsland);
    
    while (originIslandCopy.getNumberOfAtoms() > 0) {     
      iOrigAtom = (ConcertedAtom) originIslandCopy.getAtomAt(0); // hau aldatu in behar da
      iDestAtom = (ConcertedAtom) iOrigAtom.getNeighbour(direction);
      originIslandCopy.removeAtom(iOrigAtom);
      
      if (iDestAtom.isOccupied()) {
        originIslandCopy.addAtom(iOrigAtom);// move to the back and try again
        continue;
      }
      getLattice().extract(iOrigAtom);
      getLattice().deposit(iDestAtom, false);
      iDestAtom.swapAttributes(iOrigAtom);              
      getLattice().swapAtomsInMultiAtom(iOrigAtom, iDestAtom);
      modifiedAtoms.add(iOrigAtom);
      modifiedAtoms.add(iDestAtom);
      
      destinationIsland.addAtom(iDestAtom);
    }
    
    ArrayList<ConcertedAtom> tmpAtoms = new ArrayList<>();
    tmpAtoms.addAll(modifiedAtoms); // we need to copy the set to be able to iterate it, while modifying it.
    for (int i = 0; i < tmpAtoms.size(); i++) { // Update all touched area
      addModifiedAtoms(modifiedAtoms, tmpAtoms.get(i));
    }
    updateRates(modifiedAtoms);
    
    return destinationIsland;
  }
  
  /**
   * A copy of moveIsland(): Moves an island and recomputes all the neighbourhood.
   * Should be merged in the future.
   *
   * @param originMultiAtom Island to be moved.
   * @param direction Moving direction.
   * @return New island with the number of the original one.
   */
  private Island moveMultiAtom(MultiAtom originMultiAtom, int direction) {
    ConcertedAtom iOrigAtom;
    ConcertedAtom iDestAtom;
    MultiAtom destinationMultiAtom = new MultiAtom(originMultiAtom.getIslandNumber());
    Set<ConcertedAtom> modifiedAtoms = new HashSet<>();
    //clone
    Island originMultiAtomCopy = new MultiAtom(originMultiAtom);
    
    //move occupied
    while (originMultiAtomCopy.getNumberOfAtoms() > 0) {
      iOrigAtom = (ConcertedAtom) originMultiAtomCopy.getAtomAt(0); // hau aldatu in behar da
      iDestAtom = (ConcertedAtom) iOrigAtom.getNeighbour(direction);
      originMultiAtomCopy.removeAtom(iOrigAtom);
      
      if (iDestAtom.isOccupied()) {
        originMultiAtomCopy.addAtom(iOrigAtom);// move to the back and try again
        continue;
      }
      getLattice().extract(iOrigAtom);
      getLattice().deposit(iDestAtom, false);
      iDestAtom.swapAttributes(iOrigAtom);              
      getLattice().swapAtomsInMultiAtom(iOrigAtom, iDestAtom);
      // Different lines. start
      //swapIslands!!!!
      int islandNumber = iDestAtom.getIslandNumber()-1;
      Island island = getLattice().getIsland(islandNumber);
      island.removeAtom(iOrigAtom);
      island.addAtom(iDestAtom);
      // Different lines. end
      modifiedAtoms.add(iOrigAtom);
      modifiedAtoms.add(iDestAtom);
      
      destinationMultiAtom.addAtom(iDestAtom);
    }
    
    ArrayList<ConcertedAtom> tmpAtoms = new ArrayList<>();
    tmpAtoms.addAll(modifiedAtoms); // we need to copy the set to be able to iterate it, while modifying it.
    for (int i = 0; i < tmpAtoms.size(); i++) { // Update all touched area
      addModifiedAtoms(modifiedAtoms, tmpAtoms.get(i));
    }
    updateRates(modifiedAtoms);
    
    return destinationMultiAtom;
  }
  
  /**
   * Moves an island.
   */
  private void diffuseIsland() {
    int randomIsland = getRandomIsland();
    Island originIsland = getLattice().getIsland(randomIsland);
    int direction = originIsland.getRandomDirection();
    Island destinationIsland = moveIsland(originIsland, direction);
    getLattice().swapIsland(originIsland, destinationIsland);
    checkMergeIslands(destinationIsland);
  }
  
  private MultiAtom getRandomMultiAtom() {
    // take into account that the indexes are not consecutive
    double randomNumber = StaticRandom.raw() * totalRate[MULTI];
    double sum = 0.0;
    Iterator iter = getLattice().getMultiAtomsIterator();
    while (iter.hasNext()) {
      MultiAtom multiAtom = ((MultiAtom) iter.next());
      sum += multiAtom.getRate(MULTI);
      if (sum > randomNumber) {
        return multiAtom;
      }
    }
    throw new ArrayIndexOutOfBoundsException("\nTotal rate " + totalRate[MULTI]
        + " random number " + randomNumber + " number of islands " + getLattice().getMultiAtomCount()
        + " sum " + sum);
  }
  
  private void diffuseMultiAtom() {
    MultiAtom multiAtom = getRandomMultiAtom();
    int direction = multiAtom.getRandomMultiAtomDirection();
    moveMultiAtom(multiAtom, direction);
  }
  
  private void checkMergeIslands(Island island) {
    int islandNumber = island.getIslandNumber() + 1;
    for (int i = 0; i < island.getNumberOfAtoms(); i++) {
      AbstractGrowthAtom atom = island.getAtomAt(i);
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(j);
        if (neighbour.isOccupied() && neighbour.getIslandNumber() != islandNumber) {
          // two islands have collided
          mergeIslands();
          return;
        }
      }
    }
  }
  
  private void checkMergeIslands(AbstractGrowthAtom atom) {
    int islandNumber = atom.getIslandNumber();
    if (islandNumber > 0) {
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(j);
        if (neighbour.isOccupied()
            && neighbour.getIslandNumber() != islandNumber) {
          // two islands have collided
          mergeIslands();
          return;
        }
      }
    }
  }
  
  private void mergeIslands() {
    totalRate[CONCERTED] = 0;
    getLattice().countIslands(null);
    for (int i = 0; i < getLattice().getIslandCount(); i++) {
      double rate = getConcertedDiffusionRate(i + 1);
      totalRate[CONCERTED] += rate;
      getLattice().getIsland(i).setRate(CONCERTED, rate);
    }
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initRates() {
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // UC has two atoms
        ConcertedAtom a = (ConcertedAtom) uc.getAtom(j);
        a.setRate(ADSORB, adsorptionRatePerSite); // all empty sites have the same adsorption rate
        a.setOnList(ADSORB, true);
        totalRate[ADSORB] += a.getRate(ADSORB);
        sites[ADSORB].insert(a);
        sites[SINGLE].insert(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORB].populate();
    sites[SINGLE].populate();
    
    if (aeOutput) {
      activationEnergy.setRates(diffusionRatePerAtom);
    }
  }
  
  /**
   * Includes all the first and second neighbourhood of the current atom in a
   * list without repeated elements.
   *
   * @param modifiedAtoms previously added atoms, can be null.
   * @param atom current central atom.
   * @return A list with of atoms that should be recomputed their rate.
   */
  private Set<ConcertedAtom> addModifiedAtoms(Set<ConcertedAtom> modifiedAtoms, ConcertedAtom atom) {
    if (modifiedAtoms == null) {
      modifiedAtoms = new HashSet<>();
    }
    modifiedAtoms.add(atom);
    // collect first and second neighbour atoms
    int possibleDistance = 0;
    int thresholdDistance = 2;
    while (true) {
      atom = (ConcertedAtom) atom.getNeighbour(4); // get the first neighbour
      for (int direction = 0; direction < 6; direction++) {
        for (int j = 0; j <= possibleDistance; j++) {
          modifiedAtoms.add(atom);
          atom = (ConcertedAtom) atom.getNeighbour(direction);
        }
      }
      possibleDistance++;
      if (possibleDistance == thresholdDistance) {
        break;
      }
    }
    return modifiedAtoms;
  }
  
  /**
   * Updates total adsorption and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(Set<ConcertedAtom> modifiedAtoms) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    // recompute the probability of the first and second neighbour atoms
    Iterator i = modifiedAtoms.iterator();
    ConcertedAtom atom;
    while (i.hasNext()) {
      atom = (ConcertedAtom) i.next();
      recomputeAdsorptionProbability(atom);
      recomputeDiffusionProbability(atom);
      recomputeConcertedDiffusionProbability(atom);
      recomputeMultiAtomProbability(atom);
      atom.setVisited(false);
    }
    
    // recalculate total probability, if needed
    if (totalRate[ADSORB] / previousRate[ADSORB] < 1e-1) {
      updateRateFromList(ADSORB);
    }
    if (totalRate[SINGLE] / previousRate[SINGLE] < 1e-1) {
      //System.out.println(simulatedSteps+" "+previousDesorptionRate + " " + totalDesorptionRate + " " + sites[DESORPTION].getDesorptionRate());
      updateRateFromList(SINGLE);
    }
    if (totalRate[CONCERTED] / previousRate[CONCERTED] < 1e-1) {
      updateRateFromList(CONCERTED);
    }
    
    // tell to the list new probabilities
    getList().setRates(totalRate);
  }
  
  private void updateRatesIslands(ConcertedAtom origin, ConcertedAtom destination, boolean wasDimer) {
    if (!doIslandDiffusion) {
      return;
    }
    boolean checked = false;
    boolean fromNeighbour = false;
    if (origin != null && origin.getIslandNumber() > 0) { // origin atom was on a island; remove it.
      getLattice().getIsland(origin.getIslandNumber() - 1).removeAtom(origin);
      origin.setIslandNumber(0);
    }
    if (destination.getIslandNumber() <= 0) { // destination atom was not on an island
      if (destination.getOccupiedNeighbours() > 0) { // previously one neighbour was in an island
        for (int i = 0; i < destination.getNumberOfNeighbours(); i++) {
          AbstractGrowthAtom neighbour = destination.getNeighbour(i);
          if (neighbour.isOccupied() && neighbour.getIslandNumber() > 0) {
            totalRate[CONCERTED] -= getLattice().getIsland(neighbour.getIslandNumber()-1).getRate(CONCERTED);
            getLattice().identifyIsland(destination, true, true);
            totalRate[CONCERTED] += getConcertedDiffusionRate(destination.getIslandNumber());
            getLattice().getIsland(destination.getIslandNumber() - 1).setRate(CONCERTED, 
                getConcertedDiffusionRate(destination.getIslandNumber()));
            checked = true;
            break;
          }
        }
        if (!checked) {
          getLattice().identifyIsland(destination, false, fromNeighbour);
          totalRate[CONCERTED] += getConcertedDiffusionRate(destination.getIslandNumber());
          getLattice().getIsland(destination.getIslandNumber() - 1).setRate(CONCERTED,
              getConcertedDiffusionRate(destination.getIslandNumber()));
        }
      }
    } else {
      Island island = getLattice().getIsland(destination.getIslandNumber()-1);
      island.removeAtom(origin);
      // Check detached
      if (destination.getOccupiedNeighbours() == 0) {
        destination.setIslandNumber(0);
        if (wasDimer) {// A dimer can detach also!
          mergeIslands(); //recompute again all islands (easiest to implement)
        }
      } else {
        island.addAtom(destination);
      }
      //check attached (by diffusion)
      int currentIsland = destination.getIslandNumber();
      for (int i = 0; i < destination.getNumberOfNeighbours(); i++) {
        AbstractGrowthAtom neighbour = destination.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getIslandNumber() != currentIsland) {
          mergeIslands(); //recompute again all islands (easiest to implement)
        }
      }
    }
    checkMergeIslands(destination);
  }
  
  private void recomputeAdsorptionProbability(ConcertedAtom atom) {
    double oldAdsorptionRate = atom.getRate(ADSORB);
    totalRate[ADSORB] -= oldAdsorptionRate;
    if (atom.isOccupied()) {
      atom.setRate(ADSORB, 0);
    } else {
      atom.setRate(ADSORB, adsorptionRatePerSite);
    }
    recomputeCollection(ADSORB, atom, oldAdsorptionRate);
  }

  private void recomputeDiffusionProbability(ConcertedAtom atom) {
    totalRate[SINGLE] -= atom.getRate(SINGLE);
    double oldDiffusionRate = atom.getRate(SINGLE);
    if (!atom.isOccupied()) {
      if (atom.isOnList(SINGLE)) {
        sites[SINGLE].removeAtomRate(atom);
      }
      atom.setOnList(SINGLE, false);
      return;
    }
    atom.setRate(SINGLE, 0);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      ConcertedAtom neighbour = (ConcertedAtom) atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionRate(atom, neighbour, i);
        atom.addRate(SINGLE, probability, i);
      }
    }
    recomputeCollection(SINGLE, atom, oldDiffusionRate);
  }
  
  /**
   * Doesn't work yet!!
   * @param atom 
   */
  private void recomputeConcertedDiffusionProbability(ConcertedAtom atom) {
    /*totalRate[CONCERTED] -= atom.getRate(CONCERTED);
    double oldDiffusionRate = atom.getRate(CONCERTED);
    if (!atom.isOccupied()) {
      if (atom.isOnList(CONCERTED)) {
        sites[CONCERTED].removeAtomRate(atom);
      }
      atom.setOnList(CONCERTED, false);
      return;
    }
    atom.setRate(CONCERTED, 0);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      ConcertedAtom neighbour = (ConcertedAtom) atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionRate(atom, neighbour,i);
        atom.addRate(CONCERTED, probability, i);
      }
    }
    recomputeCollection(CONCERTED, atom, oldDiffusionRate);//*/
  }
  
  private void recomputeMultiAtomProbability(ConcertedAtom atom) {
    if (!doMultiAtomDiffusion) {
      return;
    }
    ArrayList<MultiAtom> multiAtomIsland = getLattice().identifyAddMultiAtom(atom);
    for (int i = 0; i < multiAtomIsland.size(); i++) {
      MultiAtom multiAtom = multiAtomIsland.get(i);
      double rate = getMultiAtomDiffusionRate(multiAtom);
      totalRate[MULTI] += rate;
      multiAtom.setRate(MULTI, rate);
    }
    double removedRate = getLattice().identifyRemoveMultiAtomIsland(atom);
    totalRate[MULTI] -= removedRate;
  }
  
  private void recomputeCollection(byte process, ConcertedAtom atom, double oldRate) {
    totalRate[process] += atom.getRate(process);
    if (atom.getRate(process) > 0) {
      if (atom.isOnList(process)) {
        if (oldRate != atom.getRate(process)) {
          sites[process].updateRate(atom, -(oldRate - atom.getRate(process)));
        } else { // rate is the same as it was.
          //do nothing.
        }
      } else { // atom it was not in the list
        sites[process].addRate(atom);
      }
      atom.setOnList(process, true);
    } else { // reaction == 0
      if (atom.isOnList(process)) {
        if (oldRate > 0) {
          sites[process].updateRate(atom, -oldRate);
          sites[process].removeAtomRate(atom);
        }
      } else { // not on list
        // do nothing
      }
      atom.setOnList(process, false);
    }
  }
  
  private double getDiffusionRate(ConcertedAtom atom, ConcertedAtom neighbour, int position) {
    double probability;
    int origType = atom. getRealType();
    int destType = neighbour.getTypeWithoutNeighbour(position);
    probability = diffusionRatePerAtom[origType][destType];
    return probability;
  }
  
  private double getConcertedDiffusionRate(int islandNumber) {
    Island island = getLattice().getIsland(islandNumber - 1);
    int numberOfAtoms = island.getNumberOfAtoms();
    if (numberOfAtoms < 0) {
      throw new ArrayIndexOutOfBoundsException("The number of atoms in the island is <0, which is in practice impossible");
    }
    if (numberOfAtoms < 9)
      return diffusionRatePerIslandSize[numberOfAtoms];
    else
      return 0; // bigger than 8 does not diffuse.
  }
  
  private double getMultiAtomDiffusionRate(MultiAtom multiAtom) {
    double edgeRate0 = diffusionRateMultiAtom[multiAtom.getEdgeType(0)];
    double edgeRate1 = diffusionRateMultiAtom[multiAtom.getEdgeType(1)];
    multiAtom.addRate(MULTI, edgeRate0, 0);
    multiAtom.addRate(MULTI, edgeRate1, 1);
    return edgeRate0 + edgeRate1;
  }

  private void updateRateFromList(byte process) {
    sites[process].recomputeTotalRate(process);
    totalRate[process] = sites[process].getTotalRate(process);
  }

  /**
   * If a process is stored in a array and it is too big, change to be a tree. If a tree is too
   * small, change it to be an array.
   */
  private void checkSizes() {
    if (automaticCollections) {
      // ADSORB, SINGLE (diffusion), CONCERTED (diffusion)
      for (byte i = 0; i < sites.length; i++) {
        long startTime = System.currentTimeMillis();
        if ((sites[i].size() > 1000) && sites[i] instanceof AtomsArrayList) {
          changeCollection(i, true);
          System.out.println("Changed to Tree " + i + " in " + (System.currentTimeMillis() - startTime) + " ms");
        }
        if ((sites[i].size() < 500) && sites[i] instanceof AtomsAvlTree) {
          changeCollection(i, false);
          System.out.println("Changed to Array " + i + " in " + (System.currentTimeMillis() - startTime) + " ms");
        }
      }
    }
  }
  
  /**
   * Changes current collection from array/tree to tree/array.
   * 
   * @param process ADSORPTION, SINGLE diffusion, CONCERTED island diffusion or MULTI atom diffusion.
   * @param toTree if true from array to tree, otherwise from tree to array.
   */
  private void changeCollection(byte process, boolean toTree) {
    sites[process] = col.getCollection(toTree, process);
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        AbstractGrowthAtom a = uc.getAtom(j);
        sites[process].insert(a);
      }
    }
    sites[process].populate();
  }
  
  /**
   * Print current information to extra file.
   *
   * @param coverage used to have exactly the coverage and to be easily greppable.
   */
  private void printData() {
    restart.writeSvg(1000+(int)(getCoverage()*100), getLattice());
    restart.writeExtraOutput(getLattice(), getCoverage(), 0, getTime(), totalRate[ADSORB],
			     getList().getDiffusionProbability(), simulatedSteps, totalRate[SINGLE]);
    
    if (aeOutput) {
      activationEnergy.printAe(restart.getExtraWriters(), getTime());
    }
    restart.flushExtra();
  }
}
