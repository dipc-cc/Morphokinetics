/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import basic.io.Restart;
import java.io.PrintWriter;
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
    sites[CONCERTED] = col.getCollection(false, SINGLE);

    totalRate = new double[3]; // adsorption, diffusion, island diffusion

    maxSteps = parser.getNumberOfSteps();
    maxCoverage = (float) parser.getCoverage() / 100;
    extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    activationEnergy = new ActivationEnergy(parser);
    restart = new Restart(parser.outputData(), restartFolder);
  }

  public void setRates(AbstractConcertedRates rates) {
    diffusionRatePerAtom = new double[7][7]; // empty
    adsorptionRatePerSite = rates.getDepositionRatePerSite();
    
    diffusionRatePerAtom = rates.getDiffusionRates();
    getLattice().setAtomsTypesCounter(7);
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
	  printData(null, activationEnergy, restart);
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
      PrintWriter standardOutputWriter = new PrintWriter(System.out);
      activationEnergy.printAe(restart.getExtraWriters(), getCoverage());
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
    Iterator iter = getList().getIterator();
    while (iter.hasNext()) {
      ConcertedAtom atom = (ConcertedAtom) iter.next();
      atom.clear();
    }
    getLattice().reset();
    getList().reset();
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
    getLattice().extract(originAtom);    
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    if (aeOutput) {
      activationEnergy.updateSuccess(oldType, destinationAtom.getType());
    }
    updateRates(originAtom);
    updateRates(destinationAtom);
    updateRatesIslands(destinationAtom);
  }

  /**
   * Moves an island.
   */
  private void diffuseIsland() {
    Island originIsland = getLattice().getIsland(0);
    Island destinationIsland = new Island(originIsland.getIslandNumber());
    int direction = originIsland.getRandomDirection();
    ArrayList<AbstractGrowthAtom> modifiedAtoms = new ArrayList<>();
    ArrayList<AbstractGrowthAtom> postponedAtoms = new ArrayList<>();
    int islandSize = originIsland.getNumberOfAtoms();
    for (int i = 0; i < islandSize; i++) { // Move atoms one by one
      AbstractGrowthAtom iOrigAtom = originIsland.getAtomAt(0); // hau aldatu in behar da
      AbstractGrowthAtom iDestAtom = iOrigAtom.getNeighbour(direction);
      originIsland.removeAtom(iOrigAtom);
      
      if (iDestAtom.isOccupied()) {
        postponedAtoms.add(iOrigAtom);
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
    }
    for (int i = 0; i < postponedAtoms.size(); i++) { // Move rest of the atoms
      AbstractGrowthAtom iOrigAtom = postponedAtoms.get(i);
      AbstractGrowthAtom iDestAtom = iOrigAtom.getNeighbour(direction);
      
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
    }
    
    //for (int i = 0; i < originIsland.getNumberOfAtoms(); i++) {
    for (int i = 0; i < modifiedAtoms.size(); i++) { // Update all touched area
      ConcertedAtom atom = (ConcertedAtom) modifiedAtoms.get(i);
      updateRates(atom);
    }
    getLattice().swapIsland(originIsland, destinationIsland, 0);
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
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
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
    // recompute the probability of the neighbour atoms
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      ConcertedAtom neighbour = (ConcertedAtom) atom.getNeighbour(i);
      recomputeAdsorptionProbability(neighbour);
      recomputeDiffusionProbability(neighbour);
      recomputeConcertedDiffusionProbability(neighbour);
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
  
  public void updateRatesIslands(ConcertedAtom atom) {
    if (atom.isDimer()) {
      atom.isDimer();
      //getLattice().countIslands(null);
      getLattice().identifyIsland(atom, false, 0, 0);
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
        double probability = getDiffusionRate(atom, neighbour);
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
        double probability = getDiffusionRate(atom, neighbour);
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
  
  double getDiffusionRate(ConcertedAtom atom, ConcertedAtom neighbour) {
    double probability;
    probability = diffusionRatePerAtom[atom.getType()][neighbour.getType() - 1];
    return probability;
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
  void printData(Integer coverage, ActivationEnergy ae, Restart rt) {
    float printCoverage;
    if (coverage != null) {
      printCoverage = (float) (coverage) / 100;
    } else {
      printCoverage = getCoverage();
    }
    restart.writeExtraOutput(getLattice(), printCoverage, 0, getTime(), 
			     //(double) (depositionRatePerSite * freeArea),
			     0,
			     getList().getDiffusionProbability(), simulatedSteps, totalRate[SINGLE]);
    
    if (aeOutput) {
      ae.printAe(rt.getExtraWriters(), printCoverage);
    }
    restart.flushExtra();
  }
}
