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
import basic.io.BdaRestart;
import basic.io.OutputType;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.lattice.BdaLattice;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.DESORPTION;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import static kineticMonteCarlo.process.BdaProcess.TRANSFORMATION;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.site.BdaMoleculeSite;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;
import ratesLibrary.bda.AbstractBdaRates;
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
public class BdaKmc extends AbstractGrowthKmc {
  
  private final BdaLattice lattice;
  private long simulatedSteps;
  private final BdaRestart restart;
  private int simulationNumber;
  private final IAtomsCollection[] sites;
  /** Stores all collections of atoms; either in a tree or an array. */
  private AtomsCollection col;
  private final boolean automaticCollections;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  /**
   * Attribute to control the output of data every 1% and nucleation.
   */
  // Total rates
  private double[] totalRate;
  private final boolean extraOutput;
  /**
   * Activation energy output at the end of execution
   */
  private final boolean aeOutput;
  private final long maxSteps;
  private double adsorptionRatePerSite;
  private double[][] diffusionRatePerMolecule;
  private double[] diffusionRatePerIslandSize;
  private double[] diffusionRateMultiAtom;
  
  public BdaKmc(Parser parser, String restartFolder) {
    super(parser);
    BdaLattice lat = new BdaLattice(parser.getHexaSizeI(), parser.getHexaSizeJ());
    setLattice(lat);
    lattice = lat;
    restart = new BdaRestart(restartFolder);
  
    sites = new IAtomsCollection[6];
    automaticCollections = parser.areCollectionsAutomatic();
    col = new AtomsCollection(lattice, "bda");
    // Either a tree or array 
    sites[ADSORPTION] = col.getCollection(false, ADSORPTION);
    sites[DESORPTION] = col.getCollection(false, DESORPTION);
    sites[2] = col.getCollection(false, (byte) 2); // just to keep all the values
    sites[DIFFUSION] = col.getCollection(false, DIFFUSION);
    sites[ROTATION] = col.getCollection(false, ROTATION);
    sites[TRANSFORMATION] = col.getCollection(false,TRANSFORMATION);
    
    totalRate = new double[6]; // adsorption, diffusion, island diffusion, multi-atom

    maxSteps = parser.getNumberOfSteps();
    maxCoverage = (float) parser.getCoverage() / 100;
    extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    
    
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
        //updatePossibles();
        if (extraOutput && getCoverage() * limit >= coverageThreshold) { // print extra data every 1% of coverage, previously every 1/1000 and 1/10000
            if (coverageThreshold == 10 && limit > 100) { // change the interval of printing
              limit = limit / 10;
              coverageThreshold = 1;
            }
            //printData();
            //mergeIslands(); // recompute island's rate, after island counting have been deleted in previous islands counting.
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
      //printData();
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
      case ADSORPTION:
        depositNewMolecule();
        break;
      case DESORPTION:
        desorbMolecule(); 
        break;
      case DIFFUSION:
        diffuseMolecule(); 
        break;
      case ROTATION:
        rotateMolecule();
        break;
      case TRANSFORMATION:
        transformMolecule();
        break;
    }
            
    simulatedSteps++;
    return simulatedSteps == maxSteps;
  }
  
  private void depositNewMolecule() {
    if (sites[ADSORPTION].isEmpty()) {
      // can not deposit anymore
      return;
    }
    
    BdaAgSurfaceSite destinationSite = (BdaAgSurfaceSite) sites[ADSORPTION].randomElement();
    lattice.deposit(destinationSite, false);
    
    updateRates(lattice.getModifiedSites(null, destinationSite));
  }
  
  private void desorbMolecule() {
    int random = StaticRandom.rawInteger(lattice.getMoleculeUcSize());
    //BdaMoleculeUc m = lattice.getMoleculeUc(random);
    lattice.extract(random);
  }
  
  private void diffuseMolecule() {
    BdaSurfaceUc agUc = lattice.getRandomOccupiedUc();
    lattice.extract(agUc);
    int randomDirection = StaticRandom.rawInteger(4);
    BdaSurfaceUc neighbour = agUc.getNeighbour(randomDirection);
    lattice.deposit(neighbour);
  }
  
  private void rotateMolecule() {
    BdaSurfaceUc agUc = lattice.getRandomOccupiedUc();
    lattice.extract(agUc);
    agUc.getBdaUc().setRotated(!agUc.getBdaUc().isRotated());
    lattice.deposit(agUc, agUc.getBdaUc());
  }
  
  private void transformMolecule() {}
  
  private void updateRates(Set<AbstractGrowthSite> modifiedSites) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    Iterator i = modifiedSites.iterator();
    BdaAgSurfaceSite site;
    while (i.hasNext()) {
      site = (BdaAgSurfaceSite) i.next();
      recomputeAdsorptionProbability(site);
      //recomputeDiffusionProbability(site);
    }
  }

  private void recomputeAdsorptionProbability(BdaAgSurfaceSite site) {
    double oldAdsorptionRate = site.getRate(ADSORPTION);
    totalRate[ADSORPTION] -= oldAdsorptionRate;
    BdaSurfaceUc sUc = lattice.getAgUc(site);
    if (sUc.isAvailable()) {
      site.setRate(ADSORPTION, adsorptionRatePerSite);
    } else {
      site.setRate(ADSORPTION, 0);
    }
    recomputeCollection(ADSORPTION, site, oldAdsorptionRate);
  }

  private void recomputeDiffusionProbability(BdaAgSurfaceSite agSite) {
    BdaSurfaceUc agUc = lattice.getAgUc(agSite);
    BdaMoleculeSite bdaSite = (BdaMoleculeSite) agUc.getBdaUc().getSite(0);
    if (bdaSite == null) 
      return;
    /*agUc.isOccupied();
    totalRate[DIFFUSION] -= site.getRate(DIFFUSION);
    double oldDiffusionRate = site.getRate(DIFFUSION);
    if (!site.isOccupied()) {
      if (site.isOnList(DIFFUSION)) {
        sites[DIFFUSION].removeAtomRate(site);
      }
      site.setOnList(DIFFUSION, false);
      return;
    }
    site.setRate(DIFFUSION, 0);
    for (int i = 0; i < site.getNumberOfNeighbours(); i++) {
      BdaAgSurfaceSite neighbour = (BdaAgSurfaceSite) site.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionRate(site, neighbour, i);
        site.addRate(DIFFUSION, probability, i);
        site.setEdgeType(DIFFUSION, neighbour.getTypeWithoutNeighbour(i), i);
      }
    }
    recomputeCollection(DIFFUSION, site, oldDiffusionRate);*/
  }
  
  private double getDiffusionRate(BdaAgSurfaceSite origin, BdaAgSurfaceSite destination, int position) {
    double rate;
    /*int origType = atom. getRealType();
    int destType = neighbour.getTypeWithoutNeighbour(position);
    rate = diffusionRatePerMolecule[origType][destType];*/
    rate = diffusionRatePerMolecule[0][0];
    return rate;
  }
  
  /**
   * Exact copy from ConcertedKmc. It can be generalised. Remove this if changed!!!
   * @param process
   * @param site
   * @param oldRate 
   */
  private void recomputeCollection(byte process, BdaAgSurfaceSite site, double oldRate) {
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
  
  /**
   * If a process is stored in a array and it is too big, change to be a tree. If a tree is too
   * small, change it to be an array.
   */
  private void checkSizes() {
    if (automaticCollections) {
      // ADSORB, DIFFUSION (diffusion), CONCERTED (diffusion)
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
   * @param process ADSORPTION, DIFFUSION diffusion, CONCERTED island diffusion or MULTI atom diffusion.
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
  
  @Override
  public void depositSeed() {
    totalRate = new double[6];
    initRates();
    simulatedSteps = 0;
  }
  
  @Override
  public void reset() {
    super.reset();
    getLattice().reset();
    getList().reset();
    restart.reset();
    for (int i = 0; i < sites.length; i++) {
      sites[i].reset();
    }
  }
  
  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initRates() {
    for (int i = 0; i < lattice.size(); i++) {
      BdaSurfaceUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) { // UC has two atoms
        BdaAgSurfaceSite a = (BdaAgSurfaceSite) uc.getSite(j);
        a.setRate(ADSORPTION, adsorptionRatePerSite); // all empty sites have the same adsorption rate
        a.setOnList(ADSORPTION, true);
        totalRate[ADSORPTION] += a.getRate(ADSORPTION);
        sites[ADSORPTION].insert(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORPTION].populate();
  }
    
  public void setRates(AbstractBdaRates rates) {
    adsorptionRatePerSite = rates.getDepositionRatePerSite();
    diffusionRatePerMolecule = rates.getDiffusionRates();
  }
}
