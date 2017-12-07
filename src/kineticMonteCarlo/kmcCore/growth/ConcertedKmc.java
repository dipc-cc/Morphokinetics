/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import basic.io.Restart;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.ConcertedAtom;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import kineticMonteCarlo.lattice.Concerted6LatticeSimple;
import kineticMonteCarlo.lattice.Island;
import static kineticMonteCarlo.process.ConcertedProcess.ADSORB;
import static kineticMonteCarlo.process.ConcertedProcess.CONCERTED;
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
  AtomsCollection col;
  private final boolean automaticCollections;
  private final long maxSteps;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private double adsorptionRatePerSite;
  private double[][] diffusionRatePerAtom;
  private double[] diffusionRatePerIslandSize;
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
 
  public ConcertedKmc(Parser parser, String restartFolder) {
    super(parser);
    Concerted6LatticeSimple concertedLattice;
    concertedLattice = new Concerted6LatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), null);
    setLattice(concertedLattice);
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }   
    sites = new IAtomsCollection[3];
    automaticCollections = parser.areCollectionsAutomatic();
    col = new AtomsCollection(concertedLattice, "concerted");
    // Either a tree or array 
    sites[ADSORB] = col.getCollection(false, ADSORB);
    sites[SINGLE] = col.getCollection(false, SINGLE);
    sites[CONCERTED] = col.getCollection(false, CONCERTED);

    totalRate = new double[3]; // adsorption, diffusion, island diffusion

    maxSteps = parser.getNumberOfSteps();
    maxCoverage = (float) parser.getCoverage() / 100;
    extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    activationEnergy = new ActivationEnergy(parser);
    restart = new Restart(false, restartFolder);
    doIslandDiffusion = parser.doIslandDiffusion();
  }

  public void setRates(AbstractConcertedRates rates) {
    adsorptionRatePerSite = rates.getDepositionRatePerSite();
    
    diffusionRatePerAtom = rates.getDiffusionRates();
    if (doIslandDiffusion) {
      diffusionRatePerIslandSize = rates.getIslandDiffusionRates();
    } else {
      diffusionRatePerIslandSize = new double[9]; // 0 rate for all islands
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
        if (extraOutput && getCoverage() * limit >= coverageThreshold) { // print extra data every 1% of coverage, previously every 1/1000 and 1/10000
            if (coverageThreshold == 10 && limit > 100) { // change the interval of printing
              limit = limit / 10;
              coverageThreshold = 1;
            }
            printData();
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
    totalRate = new double[3];
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
    sites[ADSORB].clear();
    sites[SINGLE].clear();
    sites[CONCERTED].clear();
  }
  
  private boolean depositAtom(ConcertedAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }
    getLattice().deposit(atom, false);
    
    return true;
  }
  
  private ConcertedAtom depositNewAtom() {
    ConcertedAtom destinationAtom = null;
    int ucIndex = 0;
    byte atomType;
    
     if (sites[ADSORB].isEmpty()) {
      // can not deposit anymore
      return null;
    }

    destinationAtom = (ConcertedAtom) sites[ADSORB].randomAtom();

    if (destinationAtom == null || destinationAtom.getRate(ADSORB) == 0 || destinationAtom.isOccupied()) {
      boolean isThereAnAtom = destinationAtom == null;
      System.out.println("Something is wrong " + isThereAnAtom);
    }
    //check neighbourhood
    atomType = (byte) destinationAtom.getOccupiedNeighbours();
    destinationAtom.setType(atomType);
    depositAtom(destinationAtom);
    
    updateRates(destinationAtom);
    updateRatesIslands(null, destinationAtom, false);
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    return destinationAtom;
  }
  
  /**
   * Moves an atom.
   */
  private void diffuseAtom() {
    ConcertedAtom originAtom = (ConcertedAtom) sites[SINGLE].randomAtom();
    ConcertedAtom destinationAtom = originAtom.getRandomNeighbour(SINGLE);
    int oldType = originAtom.getType();
    boolean wasDimer = originAtom.isDimer();
    getLattice().extract(originAtom);    
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    if (aeOutput) {
      activationEnergy.updateSuccess(oldType, destinationAtom.getType());
    }
    updateRates(originAtom);
    updateRates(destinationAtom);
    updateRatesIslands(originAtom, destinationAtom, wasDimer);
  }
  
  private int getRandomIsland() {
    double randomNumber = StaticRandom.raw() * totalRate[CONCERTED];
    double sum = 0.0;
    for (int i = 0; i < getLattice().getIslandCount(); i++) {
      sum += getLattice().getIsland(i).getTotalRate();
      if (sum > randomNumber) {
        return i;
      }
    }
    throw new ArrayIndexOutOfBoundsException("\nTotal rate " + totalRate[CONCERTED]
        + " random number " + randomNumber + " number of islands " + getLattice().getIslandCount()
        + " sum " + sum);
  }

  /**
   * Moves an island.
   */
  private void diffuseIsland() {
    AbstractGrowthAtom iOrigAtom;
    AbstractGrowthAtom iDestAtom;
    int randomIsland = getRandomIsland();
    Island originIsland = getLattice().getIsland(randomIsland);
    Island destinationIsland = new Island(originIsland.getIslandNumber());
    int direction = originIsland.getRandomDirection();
    ArrayList<AbstractGrowthAtom> modifiedAtoms = new ArrayList<>();
    while (originIsland.getNumberOfAtoms() > 0) {     
      iOrigAtom = originIsland.getAtomAt(0); // hau aldatu in behar da
      iDestAtom = iOrigAtom.getNeighbour(direction);
      originIsland.removeAtom(iOrigAtom);
      iOrigAtom.setIslandNumber(0);
      
      if (iDestAtom.isOccupied()) {
        originIsland.addAtom(iOrigAtom);// move to the back and try again
        continue;
      }
      getLattice().extract(iOrigAtom);
      getLattice().deposit(iDestAtom, false);
      iDestAtom.swapAttributes(iOrigAtom);
      modifiedAtoms.add(iOrigAtom);
      modifiedAtoms.add(iDestAtom);
      // add both neighbourhoods
      for (int j = 0; j < iOrigAtom.getNumberOfNeighbours(); j++) {
        modifiedAtoms.add(iOrigAtom.getNeighbour(j));
        modifiedAtoms.add(iDestAtom.getNeighbour(j));
      }
      
      destinationIsland.addAtom(iDestAtom);
      iDestAtom.setIslandNumber(randomIsland + 1);
    }
    
    for (int i = 0; i < modifiedAtoms.size(); i++) { // Update all touched area
      ConcertedAtom atom = (ConcertedAtom) modifiedAtoms.get(i);
      updateRates(atom);
    }
    //updateRatesIslands((ConcertedAtom) modifiedAtoms.get(1));
    getLattice().swapIsland(originIsland, destinationIsland);
    checkMergeIslands(destinationIsland);
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
            && neighbour.getIslandNumber() > 0
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
      getLattice().getIsland(i).setTotalRate(rate);
    }
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initRates() {
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        ConcertedAtom a = (ConcertedAtom) uc.getAtom(j);
        a.setRate(ADSORB, adsorptionRatePerSite); // there is no neighbour
        a.setOnList(ADSORB, true);
        totalRate[ADSORB] += a.getRate(ADSORB);
        sites[ADSORB].insert(a);
        sites[SINGLE].insert(a);
        sites[CONCERTED].insert(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORB].populate();
    sites[SINGLE].populate();
    sites[CONCERTED].populate();
    
    if (aeOutput) {
      activationEnergy.setRates(diffusionRatePerAtom);
    }
  }
  
  /**
   * Updates total adsorption and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(ConcertedAtom atom) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    // recompute the probability of the current atom
    recomputeAdsorptionProbability(atom);
    recomputeDiffusionProbability(atom);
    recomputeConcertedDiffusionProbability(atom);
    atom.setVisited(false);
    // recompute the probability of the first and second neighbour atoms
    int possibleDistance = 0;
    int thresholdDistance = 2;
    while (true) {
      atom = (ConcertedAtom) atom.getNeighbour(4); // get the first neighbour
      for (int direction = 0; direction < 6; direction++) {
        for (int j = 0; j <= possibleDistance; j++) {
          recomputeAdsorptionProbability(atom);
          recomputeDiffusionProbability(atom);
          recomputeConcertedDiffusionProbability(atom);
          atom = (ConcertedAtom) atom.getNeighbour(direction);
          atom.setVisited(false);
        }
      }
      possibleDistance++;
      if (possibleDistance == thresholdDistance) {
        break;
      }
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
            totalRate[CONCERTED] -= getLattice().getIsland(neighbour.getIslandNumber()-1).getTotalRate();
            getLattice().identifyIsland(destination, true, true);
            totalRate[CONCERTED] += getConcertedDiffusionRate(destination.getIslandNumber());
            getLattice().getIsland(destination.getIslandNumber() - 1).setTotalRate(
                getConcertedDiffusionRate(destination.getIslandNumber()));
            checked = true;
            break;
          }
        }
        if (!checked) {
          getLattice().identifyIsland(destination, false, fromNeighbour);
          totalRate[CONCERTED] += getConcertedDiffusionRate(destination.getIslandNumber());
          getLattice().getIsland(destination.getIslandNumber() - 1).setTotalRate(
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
      
      
      // TODO: an atom can go to one island to another.
      checkMergeIslands(destination);
    }
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
   * @param process ADSORPTION, DESORPTION, REACTION, DIFFUSION.
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
   * Method to print rates. Equivalent to table 1 of Temel et al. J. Chem. Phys. 126 (2007).
   */
  public void printRates() {
    /*System.out.println(" Process ");
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "CO adsorption\t", adsorptionRateCOPerSite);
    System.out.format("%s\t%1.1e\t%1.1e\n", " O adsorption\t",adsorptionRateOPerSite, adsorptionRateOPerSite * 2);
    System.out.println(" ------- ");
    System.out.println("Desorption");
    System.out.format("%s\t\t%1.1e\n","CO^BR", desorptionRateCOPerSite[0]);
    System.out.format("%s\t\t%1.1e\n", "CO^CUS", desorptionRateCOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^BR", desorptionRateOPerSite[0]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^CUS", desorptionRateOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^BR", desorptionRateOPerSite[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^CUS", desorptionRateOPerSite[3]);
    System.out.println(" ------- ");
    System.out.println("Reaction");
    System.out.format("%s\t%1.1e\n", "CO^BR + O^BR", reactionRateCoO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR + O^CUS", reactionRateCoO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^BR", reactionRateCoO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^CUS", reactionRateCoO[3]);
    System.out.println(" ------- ");
    System.out.println("Diffusion");
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^BR  ", diffusionRateCO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^CUS ", diffusionRateCO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^BR ", diffusionRateCO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^CUS", diffusionRateCO[3]);
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "O^BR -> O^BR    ", diffusionRateO[0]);
    System.out.format("%s\t%1.1e\n", "O^BR -> O^CUS   ", diffusionRateO[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^BR   ", diffusionRateO[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^CUS  ", diffusionRateO[3]);//*/
  }
  
  /**
   * Print current information to extra file.
   *
   * @param coverage used to have exactly the coverage and to be easily greppable.
   */
  private void printData() {
    restart.writeExtraOutput(getLattice(), getCoverage(), 0, getTime(), totalRate[ADSORB],
			     getList().getDiffusionProbability(), simulatedSteps, totalRate[SINGLE]);
    
    if (aeOutput) {
      activationEnergy.printAe(restart.getExtraWriters(), getTime());
    }
    restart.flushExtra();
  }
}
