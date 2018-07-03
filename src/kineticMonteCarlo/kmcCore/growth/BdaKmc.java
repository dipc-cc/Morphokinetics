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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.lattice.BdaLattice;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.DESORPTION;
import static kineticMonteCarlo.process.BdaProcess.DIFFUSION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.site.ISite;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import kineticMonteCarlo.unitCell.BdaSurfaceUc;
import ratesLibrary.bda.AbstractBdaRates;
import utils.StaticRandom;
import utils.list.LinearList;
import utils.list.sites.SitesArrayList;
import utils.list.sites.SitesAvlTree;
import utils.list.sites.AtomsCollection;
import static kineticMonteCarlo.process.BdaProcess.TRANSFORM;
import static kineticMonteCarlo.site.BdaMoleculeSite.BETA;
import utils.list.sites.ISitesCollection;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaKmc extends AbstractGrowthKmc {
  
  private final BdaLattice lattice;
  private long simulatedSteps;
  private long[] steps;
  private final BdaRestart restart;
  private int simulationNumber;
  private final ISitesCollection[] sites;
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
  private final boolean outputData;
  private int outputEvery;
  private int intermediateWrites;
  private double adsorptionRatePerSite;
  private double[] desorptionRatePerMolecule;
  private double[] diffusionRateMultiAtom;
  private AbstractBdaRates rates;
  private boolean[] doP;
  private boolean adsorptionStopped;

  public BdaKmc(Parser parser, String restartFolder) {
    super(parser);
    BdaLattice lat = new BdaLattice(parser.getHexaSizeI(), parser.getHexaSizeJ());
    setLattice(lat);
    lattice = lat;
    restart = new BdaRestart(restartFolder);
  
    sites = new ISitesCollection[6];
    automaticCollections = parser.areCollectionsAutomatic();
    col = new AtomsCollection(lattice, "bda");
    // Either a tree or array 
    sites[ADSORPTION] = col.getCollection(false, ADSORPTION);
    sites[DESORPTION] = col.getCollection(false, DESORPTION);
    sites[2] = col.getCollection(false, (byte) 2); // just to keep all the values
    sites[DIFFUSION] = col.getCollection(false, DIFFUSION);
    sites[ROTATION] = col.getCollection(false, ROTATION);
    sites[TRANSFORM] = col.getCollection(false,TRANSFORM);
    
    totalRate = new double[6]; // adsorption, diffusion, island diffusion, multi-atom

    outputData = parser.outputData();
    maxSteps = parser.getNumberOfSteps();
    if (outputData) {
      outputEvery = parser.getOutputEvery();
    }
    maxCoverage = (float) parser.getCoverage() / 100;
    extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    
    steps = new long[6];
    doP = new boolean[6];
    String[] processes = {"Adsorption", "Desorption", "2", "Diffusion", "Rotation", "Transformation"};
    for (int i = 0; i < doP.length; i++) {
      doP[i] = parser.doBdaProcess(processes[i]);
    }
    adsorptionStopped = false;
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
    intermediateWrites = 0;

    while (true) {
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
        if (getLattice().getCoverage() > maxCoverage) {
          stopAdsorption();
        }
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
    steps[reaction]++;
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
      case TRANSFORM:
        transformMolecule();
        break;
    }
            
    simulatedSteps++;
    if (outputData && simulatedSteps % outputEvery == 0) {
      intermediateWrites++;
      int[] sizes = new int[2];
      sizes[0] = getLattice().getHexaSizeI();
      sizes[1] = getLattice().getHexaSizeJ();
      restart.writeSvg(simulationNumber * 10000 + intermediateWrites, lattice);
    }
    return simulatedSteps == maxSteps;
  }
  
  /**
   * A simple version of the surface. Only central points are written.
   * 
   * @param binX
   * @param binY
   * @return 
   */
  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];

    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        surface[i][j] = -1;
      }
    }
    int x;
    int y;
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      double posUcX = uc.getPos().getX();
      double posUcY = uc.getPos().getY();
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getSite(j).isOccupied()) {
          double posAtomX = uc.getSite(j).getPos().getX();
          double posAtomY = uc.getSite(j).getPos().getY();
          x = (int) (posUcX + posAtomX);
          y = (int) (posUcY + posAtomY);

          surface[x][y] = 0;
        }
      }
    }
    return surface;
  }
  
  private void depositNewMolecule() {
    depositNewMolecule(-1);
  }
  
  private void depositNewMolecule(int id) {
    if (sites[ADSORPTION].isEmpty()) {
      // can not deposit anymore
      return;
    }
    
    boolean rotated = false;
    BdaAgSurfaceSite destinationSite;
    if (id < 0) {
      destinationSite = (BdaAgSurfaceSite) sites[ADSORPTION].randomElement();
      if (doP[ROTATION] && StaticRandom.raw() < 0.5) {
        rotated = false;
      }
    } else {
      destinationSite = (BdaAgSurfaceSite) sites[ADSORPTION].search(new BdaAgSurfaceSite(id, (short) -1, (short) -1));
    }
    lattice.deposit(destinationSite, rotated);
    
    //updateRates(lattice.getModifiedSites(null, destinationSite));
    //updateRates(lattice.getModifiedSitesDiffusion(null,destinationSite));
    updateRates(lattice.getModifiedSitesRotation(destinationSite));
  }
  
  private void desorbMolecule() {
    BdaAgSurfaceSite destinationSite = (BdaAgSurfaceSite) sites[DESORPTION].randomElement();
    lattice.extract(destinationSite);
    //updateRates(lattice.getModifiedSites(null, destinationSite));
    //updateRates(lattice.getModifiedSitesDiffusion(null,destinationSite));
    updateRates(lattice.getModifiedSitesRotation(destinationSite));
    
  }
  
  private void diffuseMolecule() {
    BdaAgSurfaceSite origin = (BdaAgSurfaceSite) sites[DIFFUSION].randomElement();
    AbstractGrowthSite destination = (AbstractGrowthSite) origin.getRandomNeighbour(DIFFUSION);
    lattice.diffuse(origin, (BdaAgSurfaceSite) destination, origin.getRandomNeighbourDirection());
    
    updateRates(lattice.getModifiedSitesRotation(destination));
  }
  
  private void rotateMolecule() {
    BdaAgSurfaceSite origin = (BdaAgSurfaceSite) sites[ROTATION].randomElement();
    lattice.rotate(origin);
    updateRates(lattice.getModifiedSitesRotation(origin));
  }
  
  private void transformMolecule() {
    BdaAgSurfaceSite origin = (BdaAgSurfaceSite) sites[TRANSFORM].randomElement();
    origin.getBdaUc().getSite(0).setType((byte) BETA);    
    updateRates(lattice.getModifiedSitesRotation(origin));
  }
  
  private void updateRates(List<ISite> modifiedSites) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    Iterator i = modifiedSites.iterator();
    BdaAgSurfaceSite site;
    while (i.hasNext()) {
      site = (BdaAgSurfaceSite) i.next();
      if (doP[ADSORPTION]) recomputeAdsorptionRate(site);
      if (doP[DESORPTION]) recomputeDesorptionRate(site);
      if (doP[DIFFUSION])  recomputeDiffusionRate(site);
      if (doP[ROTATION])   recomputeRotationRate(site);
      if (doP[TRANSFORM])  recomputeTransformRate(site);
    }
    
    // recalculate total rate, if needed
    if (totalRate[ADSORPTION] / previousRate[ADSORPTION] < 1e-1) {
      updateRateFromList(ADSORPTION);
    }
    if (totalRate[DESORPTION] / previousRate[DESORPTION] < 1e-1) {
      updateRateFromList(DESORPTION);
    }
    if (totalRate[DIFFUSION] / previousRate[DIFFUSION] < 1e-1) {
      updateRateFromList(DIFFUSION);
    }
    if (totalRate[ROTATION] / previousRate[ROTATION] < 1e-1) {
      updateRateFromList(ROTATION);
    }
  }
  
  // copied from CatalysisCoKmc
  private void updateRateFromList(byte process) {
    sites[process].recomputeTotalRate(process);
    totalRate[process] = sites[process].getTotalRate(process);
  }

  private void recomputeAdsorptionRate(BdaAgSurfaceSite site) {
    double oldAdsorptionRate = site.getRate(ADSORPTION);
    totalRate[ADSORPTION] -= oldAdsorptionRate;
    if (site.isAvailable(ADSORPTION)) {
      site.setRate(ADSORPTION, adsorptionRatePerSite);
    } else {
      site.setRate(ADSORPTION, 0);
    }
    recomputeCollection(ADSORPTION, site, oldAdsorptionRate);
  }
  
  private void recomputeDesorptionRate(BdaAgSurfaceSite site) {
    totalRate[DESORPTION] -= site.getRate(DESORPTION);
    if (!site.isOccupied()) {
      if (site.isOnList(DESORPTION)) {
        sites[DESORPTION].removeAtomRate(site);
      }
      site.setOnList(DESORPTION, false);
      return;
    }
    double oldDesorptionRate = site.getRate(DESORPTION);
    site.setRate(DESORPTION, 0);
    double rate = getDesorptionRate(site);
    site.setRate(DESORPTION, rate);
    
    recomputeCollection(DESORPTION, site, oldDesorptionRate);
  }
  
  private double getDesorptionRate(BdaAgSurfaceSite agSite) {
    return desorptionRatePerMolecule[0];
  }
  
  private void recomputeDiffusionRate(BdaAgSurfaceSite agSite) {
    totalRate[DIFFUSION] -= agSite.getRate(DIFFUSION);
    double oldDiffusionRate = agSite.getRate(DIFFUSION);
    if (!agSite.isOccupied()) {
      if (agSite.isOnList(DIFFUSION)) {
        sites[DIFFUSION].removeAtomRate(agSite);
      }
      agSite.setOnList(DIFFUSION, false);
      return;
    }
    agSite.setRate(DIFFUSION, 0);
    agSite.getBdaUc().resetNeighbourhood();
    boolean[] canDiffuse = new boolean[4];
    // it needs a first check, to get all the neighbourhood occupancy
    for (int i = 0; i < agSite.getNumberOfNeighbours(); i++) {
      canDiffuse[i] = lattice.canDiffuse(agSite, i);
    }
    for (int i = 0; i < agSite.getNumberOfNeighbours(); i++) {
      if (canDiffuse[i]) {
        double rate = getDiffusionRate(agSite);
        agSite.addRate(DIFFUSION, rate, i);
      }
    }
    recomputeCollection(DIFFUSION, agSite, oldDiffusionRate);
  }
  
  private double getDiffusionRate(BdaAgSurfaceSite origin) {
    byte type = origin.getBdaUc().getSite(0).getType();
    return rates.getDiffusionRate(origin.getBdaUc(), type);
  }
  
  private void recomputeRotationRate(BdaAgSurfaceSite agSite) {
    totalRate[ROTATION] -= agSite.getRate(ROTATION);
    if (!agSite.isOccupied()) {
      if (agSite.isOnList(ROTATION)) {
        sites[ROTATION].removeAtomRate(agSite);
      }
      agSite.setOnList(ROTATION, false);
      return;
    }
    double oldRotationRate = agSite.getRate(ROTATION);
    agSite.setRate(ROTATION, 0); 
    if (lattice.canRotate(agSite)) {
      double rate = getRotationRate(agSite);
      agSite.setRate(ROTATION, rate);
    }
    recomputeCollection(ROTATION, agSite, oldRotationRate);
  }
  
  private double getRotationRate(BdaAgSurfaceSite origin) {
    return rates.getRotationRate(origin.getBdaUc());
  }
  
  private void recomputeTransformRate(BdaAgSurfaceSite agSite) {
    totalRate[TRANSFORM] -= agSite.getRate(TRANSFORM);
    if (!agSite.isOccupied()) {
      if (agSite.isOnList(TRANSFORM)) {
        sites[TRANSFORM].removeAtomRate(agSite);
      }
      agSite.setOnList(TRANSFORM, false);
      return;
    }
    if (agSite.isOccupied() && agSite.getBdaUc().getSite(0).getType() == BETA) {
      if (agSite.isOnList(TRANSFORM)) {
        sites[TRANSFORM].removeAtomRate(agSite);
      }
      agSite.setOnList(TRANSFORM, false);
      return;
    }
    double oldRotationRate = agSite.getRate(TRANSFORM);
    agSite.setRate(TRANSFORM, 0); 
    if (lattice.canTransform(agSite)) {
      double rate = getTransformRate();
      agSite.setRate(TRANSFORM, rate);
    }
    recomputeCollection(TRANSFORM, agSite, oldRotationRate);
  }
  
  private double getTransformRate() {
    if (//maxCoverage <= getCoverage() 
            simulatedSteps > 1e7) {
      return 1e7;//rates.getTransformRate(origin.getBdaUc());
    }
    return 0;
      
  }
  
  /**
   * Exact copy from ConcertedKmc. It can be generalised. Remove this if changed!!!
   * @param process
   * @param site
   * @param oldRate 
   */
  private void recomputeCollection(byte process, AbstractSurfaceSite site, double oldRate) {
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
    adsorptionStopped = false;
    doP[ADSORPTION] = true;
    lattice.resetOccupied();
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
  
  /**
   * Iterates over all lattice sites and sets to 0 the adsorption probabilities.
   */
  private void stopAdsorption() {
    if (adsorptionStopped){
      return;
    }
    totalRate[ADSORPTION] = 0;
    for (int i = 0; i < lattice.size(); i++) {
      BdaSurfaceUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) { // UC has two atoms
        BdaAgSurfaceSite a = (BdaAgSurfaceSite) uc.getSite(j);
        a.setRate(ADSORPTION, 0);
        a.setOnList(ADSORPTION, false);
        sites[ADSORPTION].removeAtomRate(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORPTION].populate();
    adsorptionStopped = true;
    doP[ADSORPTION] = false;
  }
    
  public void setRates(AbstractBdaRates rates) {
    adsorptionRatePerSite = rates.getDepositionRatePerSite();
    desorptionRatePerMolecule = rates.getDesorptionRates();
    this.rates = rates;
  }
}
