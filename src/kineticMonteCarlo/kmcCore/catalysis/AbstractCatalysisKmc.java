/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.kmcCore.catalysis;

import basic.Parser;
import basic.io.AbstractCatalysisRestart;
import basic.io.CatalysisData;
import basic.io.OutputType;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import kineticMonteCarlo.activationEnergy.AbstractCatalysisActivationEnergy;
import kineticMonteCarlo.site.AbstractCatalysisSite;
import kineticMonteCarlo.kmcCore.growth.AbstractSurfaceKmc;
import kineticMonteCarlo.lattice.AbstractCatalysisLattice;
import static kineticMonteCarlo.site.CatalysisCoSite.CO;
import static kineticMonteCarlo.site.CatalysisCoSite.O;
import kineticMonteCarlo.unitCell.AbstractSurfaceUc;
import kineticMonteCarlo.unitCell.CatalysisUc;
import ratesLibrary.CatalysisRates;
import utils.list.sites.SitesArrayList;
import utils.list.sites.SitesAvlTree;
import utils.list.sites.SitesCollection;
import utils.list.sites.ISitesCollection;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
abstract public class AbstractCatalysisKmc extends AbstractSurfaceKmc {

  private final boolean outputData;
  private long simulatedSteps;
  long[] steps;
  private final long maxSteps;
  private int outputEvery;
  private ArrayList<CatalysisData> adsorptionData;
  // Total rates
  double[] totalRate;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  final boolean doAdsorption;
  final boolean doDesorption;
  final boolean doReaction;
  final boolean doDiffusion;
  final boolean doPrintAllIterations;
  final String start;
  private AbstractCatalysisRestart restart;
  private AbstractCatalysisActivationEnergy activationEnergy;
  /**
   * Activation energy output during the execution
   */
  private final boolean outputAe;
  private final boolean outputAeTotal;
  int counterSitesWith4OccupiedNeighbours;
  boolean stationary;
  private long stationaryStep;
  /** Stores all collections of atoms; either in a tree or an array. */
  private SitesCollection col;
  private final boolean automaticCollections;
  
  public AbstractCatalysisKmc(Parser parser, String restartFolder) {
    super(parser);
    totalRate = new double[13]; // adsorption, desorption, reaction, diffusion

    simulatedSteps = 0;
    outputData = parser.outputData();
    maxSteps = parser.getNumberOfSteps();
    if (outputData) {
      outputEvery = parser.getOutputEvery();
      adsorptionData = new ArrayList<>();
    }
    doDiffusion = parser.doCatalysisDiffusion();
    doAdsorption = parser.doCatalysisAdsorption();
    doDesorption = parser.doCatalysisDesorption();
    doReaction = parser.doCatalysisReaction();
    start = parser.catalysisStart();
    doPrintAllIterations = parser.doPrintAllIterations();
    if (start.equals("empty")) {
      maxCoverage = (float) parser.getCoverage() / 100;
    } else {
      maxCoverage = 2; // it will never end because of coverage
    }
    steps = new long[13];
    outputAe = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    outputAeTotal = parser.getOutputFormats().contains(OutputType.formatFlag.AETOTAL);
    counterSitesWith4OccupiedNeighbours = 0;
    stationary = false;
    stationaryStep = -1;
    automaticCollections = parser.areCollectionsAutomatic();
  }
  
  abstract public void init(Parser parser);
  
  @Override
  public AbstractCatalysisLattice getLattice() {
    return (AbstractCatalysisLattice) super.getLattice();
  }
  
  final void setActivationEnergy(AbstractCatalysisActivationEnergy ae) {
    activationEnergy = ae;
  }
  
  AbstractCatalysisActivationEnergy getActivationEnergy() {
    return activationEnergy;
  }

  public void setCollection(SitesCollection col) {
    this.col = col;
  }
  
  public abstract void setRates(CatalysisRates rates);
  
  public final void setRestart(AbstractCatalysisRestart restart) {
    this.restart = restart;
  }
  
  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    Point2D corner1 = getLattice().getCartesianLocation(0, 0);
    double scaleX = binX / getLattice().getCartSizeX();
    double scaleY = binY / getLattice().getCartSizeY();

    if (scaleX > 1.01 || scaleY > 1.02) {
      System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
      System.err.println("The size of the surface should be " + binX + " and it is " + getLattice().getCartSizeX() + "/" + scaleX+" (hexagonal size is "+getLattice().getHexaSizeI()+")");
      System.err.println("The size of the surface should be " + binY + " and it is " + getLattice().getCartSizeY() + "/" + scaleY+" (hexagonal size is "+getLattice().getHexaSizeJ()+")");
      System.err.println("X scale is " + scaleX + " Y scale is " + scaleY);
      return null;
    }

    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        surface[i][j] = -1;
      }
    }
    int x;
    int y;
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractSurfaceUc uc = getLattice().getUc(i);
      double posUcX = uc.getPos().getX();
      double posUcY = uc.getPos().getY();
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getSite(j).isOccupied()) {
          double posAtomX = uc.getSite(j).getPos().getX();
          double posAtomY = uc.getSite(j).getPos().getY();
          x = (int) ((posUcX + posAtomX - corner1.getX()) * scaleX);
          y = (int) ((posUcY + posAtomY - corner1.getY()) * scaleY);

          surface[x][y] = uc.getSite(j).getType();
        }
      }
    }
    return surface;
  }

  public double[][] getOutputAdsorptionData() {
    double[][] adsorptionSimulationData = new double[adsorptionData.size()][5];
    for (int i = 0; i < adsorptionData.size(); i++) {
      adsorptionSimulationData[i] = adsorptionData.get(i).getCatalysisData();
    }
    
    return adsorptionSimulationData;
  }
  
  public float getCoverage(byte type) {
    return ((AbstractCatalysisLattice) getLattice()).getCoverage(type);
  }
  
  @Override
  public float getCoverage() {
    return ((AbstractCatalysisLattice) getLattice()).getCoverage();
  }
  
  public float[] getCoverages() {
    return ((AbstractCatalysisLattice) getLattice()).getCoverages();
  }
  
  public float getGapCoverage() {
    return ((AbstractCatalysisLattice) getLattice()).getGapCoverage();
  }

  abstract void initStationary();
  abstract long[] getProduction();
  abstract long getSumProduction();
  abstract boolean writeNow();
  abstract void updatePrevious();
  abstract boolean maxProduction();
  
  @Override
  public long getSimulatedSteps() {
    return simulatedSteps;
  }
  
  @Override
  public int simulate() {
    int returnValue = 0;

    while (notEnd()) {
      if (outputAeTotal) {
        activationEnergy.updatePossibles((AbstractCatalysisLattice) getLattice(), getList().getDeltaTime(true), stationary);
      } else {
        activationEnergy.updatePossibles(getReactionIterator(), getList().getDeltaTime(true), stationary);
      }      
      if (performSimulationStep()) {
        break;
      }
      simulatedSteps++;
    }
    
    if (outputData) {
      adsorptionData.add(new CatalysisData(getCoverage(), getTime(), getCoverage(CO), getCoverage(O), 
              (float) (counterSitesWith4OccupiedNeighbours / (float) getLattice().size()),
                getGapCoverage()));
      if (stationary) {
        int[] sizes = getSiteSizes();
        restart.writeExtraCatalysisOutput(getTime(), getCoverages(), steps, getProduction(), sizes);
        restart.flushCatalysis();
      }
    }
    
    if (outputData && simulatedSteps % outputEvery == 0) {
      if (!stationary && ((AbstractCatalysisLattice) getLattice()).isStationary(getTime())) {
        System.out.println("stationary in step " + simulatedSteps);
        stationary = true;
        stationaryStep = simulatedSteps;
        getList().resetTime();
        restart.resetCatalysis();
        initStationary();
        int[] sizes = new int[2];
        sizes[0] = getLattice().getHexaSizeI();
        sizes[1] = getLattice().getHexaSizeJ();
        restart.writeSurfaceStationary(getSampledSurface(sizes[0], sizes[1]));//*/
      }
    }
    
    if ((stationary && writeNow()) || (doPrintAllIterations && simulatedSteps % outputEvery == 0)) {
      if (outputData) {
        adsorptionData.add(new CatalysisData(getCoverage(), getTime(), getCoverage(CO), getCoverage(O),
                (float) (counterSitesWith4OccupiedNeighbours / (float) getLattice().size()),
                getGapCoverage()));
        int[] sizes = getSiteSizes();
        restart.writeExtraCatalysisOutput(getTime(), getCoverages(), steps, getProduction(), sizes);
        restart.flushCatalysis();
      }

      if (outputAe || outputAeTotal) {
        activationEnergy.printAe(restart.getExtraWriters(), getTime());
      }
      updatePrevious();
    }
    return returnValue;
  }
  
  private boolean notEnd() {
    boolean shouldContinue = getLattice().getCoverage() < maxCoverage && maxProduction();
    if (shouldContinue && maxSteps > 0) {
      shouldContinue = simulatedSteps < maxSteps;
    }
    return shouldContinue;
  }

  abstract void depositNewAtom();
  abstract void desorbAtom(); 
  abstract void reactAtom();
  abstract void diffuseAtom();
  abstract int[] getSiteSizes();
  abstract Iterator<AbstractCatalysisSite> getReactionIterator();
  
  @Override
  public void depositSeed() {
    totalRate = new double[13];
    getLattice().resetOccupied();
    if (!start.equals("empty")) {
      initCovered();
    } else if (doAdsorption) {
      initAdsorptionRates();
    }
    /*for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        updateRates(a);
      }
    }//*/
    simulatedSteps = 0;
  }

  abstract int getNumberOfReactions();
  
  @Override
  public void reset() {
    activationEnergy.reset();
    Iterator iter = getList().getIterator();
    while (iter.hasNext()) {
      AbstractCatalysisSite atom = (AbstractCatalysisSite) iter.next();
      atom.clear();
    }
    getLattice().reset();
    getList().reset();
    restart.reset();
    if (outputData) {
      adsorptionData = new ArrayList<>();
    }
    steps = new long[getNumberOfReactions()];
    counterSitesWith4OccupiedNeighbours = 0;
    stationary = false;
    stationaryStep = -1;
  }
  
  /**
   * Iterates over all lattice sites and initialises adsorption rates.
   */
  abstract void initAdsorptionRates();
  
  /**
   * Start with fully covered surface.
   */
  abstract void initCovered();

  /**
   * If a process is stored in a array and it is too big, change to be a tree. If a tree is too
   * small, change it to be an array.
   */
  void checkSizes(ISitesCollection[] sites) {
    if (automaticCollections) {
      // ADSORPTION, DESORPTION, REACTION, DIFFUSION
      for (byte i = 0; i < sites.length; i++) {
        long startTime = System.currentTimeMillis();
        if ((sites[i].size() > 1000) && sites[i] instanceof SitesArrayList) {
          changeCollection(sites[i], i, true);
          System.out.println("Changed to Tree " + i + " in " + (System.currentTimeMillis() - startTime) + " ms");
        }
        if ((sites[i].size() < 500) && sites[i] instanceof SitesAvlTree) {
          changeCollection(sites[i], i, false);
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
  private void changeCollection(ISitesCollection site, byte process, boolean toTree) {
    site = col.getCollection(toTree, process);
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        AbstractCatalysisSite a = uc.getSite(j);
        site.insert(a);
      }
    }
    site.populate();
  }
  
  /**
   * Method to print rates.
   */
  public abstract void printRates();
  
  public void printIteration() {
    System.out.format("\t%.4f", getCoverage(CO));
    System.out.format("\t%.4f", getCoverage(O));
    System.out.format("\t%d", getSumProduction());
    System.out.format("\t%1.1e", (double)simulatedSteps);
    System.out.format("\t%d", stationaryStep);
  }
  
  void updateRateFromList(ISitesCollection site, byte process) {
    site.recomputeTotalRate(process);
    totalRate[process] = site.getTotalRate(process);
  }
  
  void recomputeCollection(ISitesCollection site, byte process, AbstractCatalysisSite atom, double oldRate) {
    totalRate[process] += atom.getRate(process);
    if (atom.getRate(process) > 0) {
      if (atom.isOnList(process)) {
        if (oldRate != atom.getRate(process)) {
          site.updateRate(atom, -(oldRate - atom.getRate(process)));
        } else { // rate is the same as it was.
          //do nothing.
        }
      } else { // atom was not in the list
        site.addRate(atom);
      }
      atom.setOnList(process, true);
    } else { // reaction == 0
      if (atom.isOnList(process)) {
        if (oldRate > 0) {
          site.updateRate(atom, -oldRate);
          site.removeAtomRate(atom);
        }
      } else { // not on list
        // do nothing
      }
      atom.setOnList(process, false);
    }
  }
}
