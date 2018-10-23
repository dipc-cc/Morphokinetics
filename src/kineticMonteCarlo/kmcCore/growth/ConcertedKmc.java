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

import kineticMonteCarlo.activationEnergy.ConcertedActivationEnergy;
import basic.Parser;
import basic.io.OutputType;
import static basic.io.OutputType.formatFlag.MKO;
import static basic.io.OutputType.formatFlag.SVG;
import basic.io.Restart;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.ConcertedSite;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import kineticMonteCarlo.lattice.Concerted6LatticeSimple;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.lattice.MultiAtom;
import static kineticMonteCarlo.process.ConcertedProcess.ADSORB;
import static kineticMonteCarlo.process.ConcertedProcess.CONCERTED;
import static kineticMonteCarlo.process.ConcertedProcess.MULTI;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import ratesLibrary.concerted.AbstractConcertedRates;
import utils.StaticRandom;
import utils.list.LinearList;
import utils.list.sites.SitesArrayList;
import utils.list.sites.SitesAvlTree;
import utils.list.sites.SitesCollection;
import utils.list.sites.ISitesCollection;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedKmc extends AbstractGrowthKmc {

  private long simulatedSteps;
  private int simulationNumber;
  private final ISitesCollection[] sites;
  /** Stores all collections of atoms; either in a tree or an array. */
  private SitesCollection col;
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
  private final ConcertedActivationEnergy activationEnergy;
  private final Restart restart;
  private final boolean doIslandDiffusion;
  private final boolean doMultiAtomDiffusion;
  private final boolean[] write;
 
  public ConcertedKmc(Parser parser, String restartFolder) {
    super(parser);
    simulationNumber = 0;
    Concerted6LatticeSimple concertedLattice = new Concerted6LatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), null);
    setLattice(concertedLattice);
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }   
    sites = new ISitesCollection[4];
    automaticCollections = parser.areCollectionsAutomatic();
    col = new SitesCollection(concertedLattice, "concerted");
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
    activationEnergy = new ConcertedActivationEnergy(parser);
    restart = new Restart(false, restartFolder);
    doIslandDiffusion = parser.doIslandDiffusion();
    doMultiAtomDiffusion = parser.doMultiAtomDiffusion();
    write = new boolean[2];
    write[0] = parser.getOutputFormats().contains(MKO);
    write[1] = parser.getOutputFormats().contains(SVG);
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

  @Override
  public void depositSeed() {
    totalRate = new double[4];
    getLattice().resetOccupied();
    initRates();
    simulatedSteps = 0;
  }
  
  @Override
  public long getSimulatedSteps() {
    return simulatedSteps;
  }

  @Override
  public int simulate() {
    simulationNumber++;
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
        updatePossibles();
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
  public void reset() {
    activationEnergy.reset();
    Iterator iter = getList().getIterator();
    while (iter.hasNext()) {
      ConcertedSite site = (ConcertedSite) iter.next();
      site.clear();
    }
    getLattice().reset();
    getList().reset();
    restart.reset();
    sites[ADSORB].reset();
    sites[SINGLE].reset();
    sites[CONCERTED].reset();
    sites[MULTI].reset();
  }
  
  private ConcertedSite depositNewAtom() {
    return depositNewAtom(-1);
  }
  
  /**
   * Deposit new atom in with the given label.
   * 
   * @param id if negative, random number is chosen.
   * @return 
   */
  private ConcertedSite depositNewAtom(int id) {
    ConcertedSite destinationAtom = null;
    int ucIndex = 0;
    byte atomType;

    if (sites[ADSORB].isEmpty()) {
      // can not deposit anymore
      return null;
    }
    
    if (id < 0) {
      destinationAtom = (ConcertedSite) sites[ADSORB].randomElement();
    } else {
      destinationAtom = (ConcertedSite) sites[ADSORB].search(new ConcertedSite(id, -1));
    }

    if (destinationAtom == null || destinationAtom.getRate(ADSORB) == 0 || destinationAtom.isOccupied()) {
      boolean isThereAnAtom = destinationAtom == null;
      System.out.println("Something is wrong " + isThereAnAtom);
    }
    //check neighbourhood
    atomType = (byte) destinationAtom.getOccupiedNeighbours();
    destinationAtom.setType(atomType);
    getLattice().deposit(destinationAtom, false);
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    Set<AbstractGrowthSite> modifiedSites = getLattice().getModifiedSites(null, destinationAtom);
    updateRates(modifiedSites);
    updateRatesIslands(null, destinationAtom, false);
    
    return destinationAtom;
  }
  
  /**
   * Moves an atom.
   */
  private void diffuseAtom() {
    ConcertedSite originAtom = (ConcertedSite) sites[SINGLE].randomElement();
    ConcertedSite destinationSite = (ConcertedSite) originAtom.getRandomNeighbour(SINGLE);
    int oldType = originAtom.getType();
    boolean wasDimer = originAtom.isDimer();
    getLattice().extract(originAtom);
    getLattice().deposit(destinationSite, false);
    
    destinationSite.swapAttributes(originAtom);
    getLattice().swapAtomsInMultiAtom(originAtom, destinationSite);
    if (aeOutput) {
      activationEnergy.updateSuccess(oldType, destinationSite.getType());
    }
    Set<AbstractGrowthSite> modifiedSites = getLattice().getModifiedSites(null, originAtom);
    modifiedSites = getLattice().getModifiedSites(modifiedSites, destinationSite);
    updateRates(modifiedSites);
    updateRatesIslands(originAtom, destinationSite, wasDimer);
  }
  
  /**
   * Moves an island.
   */
  private void diffuseIsland() {
    Island originIsland = getRandomIsland();
    int direction = originIsland.getRandomDirection();
    Island destinationIsland = moveIsland(originIsland, direction);
    getLattice().swapIsland(originIsland, destinationIsland);
    checkMergeIslands(destinationIsland);
  }
  
  private void diffuseMultiAtom() {
    MultiAtom multiAtom = getRandomMultiAtom();
    int direction = multiAtom.getRandomMultiAtomDirection();
    moveMultiAtom(multiAtom, direction);
  }
	
  private Island getRandomIsland() {
    double randomNumber = StaticRandom.raw() * totalRate[CONCERTED];
    double sum = 0.0;
    for (int i = 0; i < getLattice().getIslandCount(); i++) {
      sum += getLattice().getIsland(i).getRate(CONCERTED);
      if (sum > randomNumber) {
        return getLattice().getIsland(i);
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
    ConcertedSite iOrigSite;
    ConcertedSite iDestSite;
    Island destinationIsland = new Island(originIsland.getIslandNumber());
    Set<AbstractGrowthSite> modifiedSites = new HashSet<>();
    //clone
    Island originIslandCopy = new Island(originIsland);
    
    while (originIslandCopy.getNumberOfAtoms() > 0) {     
      iOrigSite = (ConcertedSite) originIslandCopy.getAtomAt(0); // hau aldatu in behar da
      iDestSite = (ConcertedSite) iOrigSite.getNeighbour(direction);
      originIslandCopy.removeAtom(iOrigSite);
      
      if (iDestSite.isOccupied()) {
        originIslandCopy.addAtom(iOrigSite);// move to the back and try again
        continue;
      }
      getLattice().extract(iOrigSite);
      getLattice().deposit(iDestSite, false);
      iDestSite.swapAttributes(iOrigSite);              
      getLattice().swapAtomsInMultiAtom(iOrigSite, iDestSite);
      modifiedSites.add(iOrigSite);
      modifiedSites.add(iDestSite);
      
      destinationIsland.addAtom(iDestSite);
    }
    
    ArrayList<AbstractGrowthSite> tmpAtoms = new ArrayList<>();
    tmpAtoms.addAll(modifiedSites); // we need to copy the set to be able to iterate it, while modifying it.
    for (int i = 0; i < tmpAtoms.size(); i++) { // Update all touched area
      getLattice().getModifiedSites(modifiedSites, tmpAtoms.get(i));
    }
    updateRates(modifiedSites);
    
    return destinationIsland;
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
	
  /**
   * A copy of moveIsland(): Moves an island and recomputes all the neighbourhood.
   * Should be merged in the future.
   *
   * @param originMultiAtom Island to be moved.
   * @param direction Moving direction.
   * @return New island with the number of the original one.
   */
  private Island moveMultiAtom(MultiAtom originMultiAtom, int direction) {
    ConcertedSite iOrigSite;
    ConcertedSite iDestSite;
    MultiAtom destinationMultiAtom = new MultiAtom(originMultiAtom.getIslandNumber());
    Set<AbstractGrowthSite> modifiedSites = new HashSet<>();
    //clone
    Island originMultiAtomCopy = new MultiAtom(originMultiAtom);
    
    //move occupied
    while (originMultiAtomCopy.getNumberOfAtoms() > 0) {
      iOrigSite = (ConcertedSite) originMultiAtomCopy.getAtomAt(0); // hau aldatu in behar da
      iDestSite = (ConcertedSite) iOrigSite.getNeighbour(direction);
      originMultiAtomCopy.removeAtom(iOrigSite);
      
      if (iDestSite.isOccupied()) {
        originMultiAtomCopy.addAtom(iOrigSite);// move to the back and try again
        continue;
      }
      getLattice().extract(iOrigSite);
      getLattice().deposit(iDestSite, false);
      iDestSite.swapAttributes(iOrigSite);              
      getLattice().swapAtomsInMultiAtom(iOrigSite, iDestSite);
      // Different lines. start
      //swapIslands!!!!
      int islandNumber = iDestSite.getIslandNumber()-1;
      Island island = getLattice().getIsland(islandNumber);
      island.removeAtom(iOrigSite);
      island.addAtom(iDestSite);
      // Different lines. end
      modifiedSites.add(iOrigSite);
      modifiedSites.add(iDestSite);
      
      destinationMultiAtom.addAtom(iDestSite);
    }
    
    ArrayList<AbstractGrowthSite> tmpSites = new ArrayList<>();
    tmpSites.addAll(modifiedSites); // we need to copy the set to be able to iterate it, while modifying it.
    for (int i = 0; i < tmpSites.size(); i++) { // Update all touched area
      getLattice().getModifiedSites(modifiedSites, tmpSites.get(i));
    }
    updateRates(modifiedSites);
    
    return destinationMultiAtom;
  }
    
  private void checkMergeIslands(Island island) {
    int islandNumber = island.getIslandNumber() + 1;
    for (int i = 0; i < island.getNumberOfAtoms(); i++) {
      AbstractGrowthSite atom = island.getAtomAt(i);
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        AbstractGrowthSite neighbour = atom.getNeighbour(j);
        if (neighbour.isOccupied() && neighbour.getIslandNumber() != islandNumber) {
          // two islands have collided
          mergeIslands();
          return;
        }
      }
    }
  }
  
  private void checkMergeIslands(AbstractGrowthSite atom) {
    int islandNumber = atom.getIslandNumber();
    if (islandNumber > 0) {
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        AbstractGrowthSite neighbour = atom.getNeighbour(j);
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
      for (int j = 0; j < uc.size(); j++) { // UC has two sites
        ConcertedSite a = (ConcertedSite) uc.getSite(j);
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
   * Updates total adsorption and diffusion probabilities.
   *
   * @param modifiedSites 
   */
  private void updateRates(Set<AbstractGrowthSite> modifiedSites) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    // recompute the probability of the first and second neighbour atoms
    Iterator i = modifiedSites.iterator();
    ConcertedSite site;
    while (i.hasNext()) {
      site = (ConcertedSite) i.next();
      if (aeOutput) {
        activationEnergy.removeTransitions(site);
      }
      recomputeAdsorptionProbability(site);
      recomputeDiffusionProbability(site);
      recomputeConcertedDiffusionProbability(site);
      recomputeMultiAtomProbability(site);
      site.setVisited(false);
      if (aeOutput) {
        activationEnergy.addTransitions(site);
        site.setOldType(site.getRealType());
      }
    }
    
    // recalculate total probability, if needed
    if (totalRate[ADSORB] / previousRate[ADSORB] < 1e-10) {
      updateRateFromList(ADSORB);
    }
    if (totalRate[SINGLE] / previousRate[SINGLE] < 1e-10) {
      updateRateFromList(SINGLE);
    }
    if (totalRate[CONCERTED] / previousRate[CONCERTED] < 1e-10) {
      updateRateFromList(CONCERTED);
    }
    if (totalRate[MULTI] / previousRate[MULTI] < 1e-10) {
      updateRateFromList(MULTI);
    }
    
    // tell to the list new probabilities
    getList().setRates(totalRate);
  }
  
  private void updateRatesIslands(ConcertedSite origin, ConcertedSite destination, boolean wasDimer) {
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
          AbstractGrowthSite neighbour = destination.getNeighbour(i);
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
        AbstractGrowthSite neighbour = destination.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getIslandNumber() != currentIsland) {
          mergeIslands(); //recompute again all islands (easiest to implement)
        }
      }
    }
    checkMergeIslands(destination);
  }
  
  private void recomputeAdsorptionProbability(ConcertedSite site) {
    double oldAdsorptionRate = site.getRate(ADSORB);
    totalRate[ADSORB] -= oldAdsorptionRate;
    if (site.isOccupied()) {
      site.setRate(ADSORB, 0);
    } else {
      site.setRate(ADSORB, adsorptionRatePerSite);
    }
    recomputeCollection(ADSORB, site, oldAdsorptionRate);
  }

  private void recomputeDiffusionProbability(ConcertedSite site) {
    totalRate[SINGLE] -= site.getRate(SINGLE);
    double oldDiffusionRate = site.getRate(SINGLE);
    if (!site.isOccupied()) {
      if (site.isOnList(SINGLE)) {
        sites[SINGLE].removeAtomRate(site);
      }
      site.setOnList(SINGLE, false);
      return;
    }
    site.setRate(SINGLE, 0);
    for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
      ConcertedSite neighbour = (ConcertedSite) site.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionRate(site, neighbour, i);
        site.addRate(SINGLE, probability, i);
        site.setEdgeType(SINGLE, neighbour.getTypeWithoutNeighbour(i), i);
      }
    }
    recomputeCollection(SINGLE, site, oldDiffusionRate);
  }
  
  /**
   * Doesn't work yet!!
   * @param atom 
   */
  private void recomputeConcertedDiffusionProbability(ConcertedSite atom) {
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
  
  private void recomputeMultiAtomProbability(ConcertedSite site) {
    if (!doMultiAtomDiffusion) {
      return;
    }
    ArrayList<MultiAtom> multiAtomIsland = getLattice().identifyAddMultiAtom(site);
    for (int i = 0; i < multiAtomIsland.size(); i++) {
      MultiAtom multiAtom = multiAtomIsland.get(i);
      double rate = getMultiAtomDiffusionRate(multiAtom);
      totalRate[MULTI] += rate;
      multiAtom.setRate(MULTI, rate);
    }
    double removedRate = getLattice().identifyRemoveMultiAtomIsland(site);
    totalRate[MULTI] -= removedRate;
  }
  
  private void recomputeCollection(byte process, ConcertedSite site, double oldRate) {
    totalRate[process] += site.getRate(process);
    if (site.getRate(process) > 0) {
      if (site.isOnList(process)) {
        if (oldRate != site.getRate(process)) {
          sites[process].updateRate(site, -(oldRate - site.getRate(process)));
        } else { // rate is the same as it was.
          //do nothing.
        }
      } else { // atom it was not in the list
        sites[process].addRate(site);
      }
      site.setOnList(process, true);
    } else { // reaction == 0
      if (site.isOnList(process)) {
        if (oldRate > 0) {
          sites[process].updateRate(site, -oldRate);
          sites[process].removeAtomRate(site);
        }
      } else { // not on list
        // do nothing
      }
      site.setOnList(process, false);
    }
  }
  
  private double getDiffusionRate(ConcertedSite atom, ConcertedSite neighbour, int position) {
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
        if ((sites[i].size() > 1000) && sites[i] instanceof SitesArrayList) {
          changeCollection(i, true);
          System.out.println("Changed to Tree " + i + " in " + (System.currentTimeMillis() - startTime) + " ms");
        }
        if ((sites[i].size() < 500) && sites[i] instanceof SitesAvlTree) {
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
        AbstractSurfaceSite a = uc.getSite(j);
        sites[process].insert(a);
      }
    }
    sites[process].populate();
  }
	
  private void updatePossibles() {
    activationEnergy.updatePossibles(getList().getGlobalProbability(), getList().getDeltaTime(false));
    if (doIslandDiffusion) {
      activationEnergy.updatePossiblesIslands(getLattice().getIslandIterator(), getList().getGlobalProbability(), getList().getDeltaTime(false));
    }
    if (doMultiAtomDiffusion) {
      activationEnergy.updatePossiblesMultiAtoms(getLattice().getMultiAtomIterator(), getList().getGlobalProbability(), getList().getDeltaTime(false));
    }
  }
    
  /**
   * Print current information to extra file.
   *
   * @param coverage used to have exactly the coverage and to be easily greppable.
   */
  private void printData() {
    if (getCoverage() > 0.01 && (int) (getCoverage() * 100) % 5 == 0) { //only write when is bigger than 1% and multiple of %5
      int surfaceNumber = 1000 * simulationNumber + (int) (getCoverage() * 100);
      if (write[0]) {
        restart.writeSurfaceBinary2D(
                getSampledSurface((int) getLattice().getCartSizeX(),(int) getLattice().getCartSizeY()),
                surfaceNumber);
      }
      if (write[1]) {
        restart.writeSvg(surfaceNumber, getLattice(), true);
      }
    }
    restart.writeExtraOutput(getLattice(), getCoverage(), 0, getTime(), totalRate[ADSORB],
			     getList().getDiffusionProbability(), simulatedSteps, totalRate[SINGLE]);
    
    if (aeOutput) {
      activationEnergy.printAe(restart.getExtraWriters(), getTime());
    }
    restart.flushExtra();
  }
}
