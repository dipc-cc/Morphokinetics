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
import basic.io.CatalysisData;
import basic.io.OutputType;
import basic.io.Restart;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import kineticMonteCarlo.site.CatalysisSite;
import kineticMonteCarlo.kmcCore.growth.AbstractSurfaceKmc;
import kineticMonteCarlo.kmcCore.growth.ActivationEnergy;
import kineticMonteCarlo.lattice.CatalysisCoLattice;
import kineticMonteCarlo.lattice.CatalysisLattice;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.O;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import kineticMonteCarlo.unitCell.AbstractSurfaceUc;
import kineticMonteCarlo.unitCell.CatalysisUc;
import utils.list.atoms.AtomsArrayList;
import utils.list.atoms.AtomsAvlTree;
import utils.list.atoms.AtomsCollection;
import utils.list.atoms.IAtomsCollection;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisKmc extends AbstractSurfaceKmc {

  private final boolean outputData;
  private long simulatedSteps;
  private long[] steps;
  private long[] co2; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  private long co2sum;
  /** Previous instant co2sum. For output */
  private long co2prv;
  private final int co2max;
  private final long maxSteps;
  private int outputEvery;
  private ArrayList<CatalysisData> adsorptionData;
  // Adsorption
  private double adsorptionRateCOPerSite;
  private double adsorptionRateOPerSite;
  // Desorption
  double[] desorptionRateCOPerSite; // BRIDGE or CUS
  private double[] desorptionRateOPerSite;  // [BR][BR], [BR][CUS], [CUS][BR], [CUS][CUS]
  // Reaction
  double[] reactionRateCoO; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  double[] diffusionRateCO;
  double[] diffusionRateO;
  // Total rates
  private double[] totalRate;
  private final IAtomsCollection[] sites;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private final boolean doAdsorption;
  final boolean doDesorption;
  final boolean doReaction;
  final boolean doDiffusion;
  final boolean doPrintAllIterations;
  private final boolean doO2Dissociation;
  private final String start;
  private final Restart restart;
  private final ActivationEnergy activationEnergy;
  /**
   * Activation energy output during the execution
   */
  private final boolean outputAe;
  private final boolean outputAeTotal;
  private int numGaps;
  private int counterSitesWith4OccupiedNeighbours;
  private boolean stationary;
  private long stationaryStep;
  /** Stores all collections of atoms; either in a tree or an array. */
  AtomsCollection col;
  private final boolean automaticCollections;
  private final double goMultiplier;
  
  public CatalysisKmc(Parser parser, String restartFolder) {
    super(parser);
    init(parser);
    totalRate = new double[4]; // adsorption, desorption, reaction, diffusion
    numGaps = 0;

    simulatedSteps = 0;
    outputData = parser.outputData();
    maxSteps = parser.getNumberOfSteps();
    if (outputData) {
      outputEvery = parser.getOutputEvery();
      adsorptionData = new ArrayList<>();
    }
    co2max = parser.getNumberOfCo2();
    restart = new Restart(outputData, restartFolder);
    sites = new IAtomsCollection[4];
    col = new AtomsCollection(getLattice(), "catalysis");
    // Either a tree or array 
    sites[ADSORPTION] = col.getCollection(parser.useCatalysisTree(ADSORPTION), ADSORPTION);
    sites[DESORPTION] = col.getCollection(parser.useCatalysisTree(DESORPTION), DESORPTION);
    sites[REACTION] = col.getCollection(parser.useCatalysisTree(REACTION), REACTION);
    sites[DIFFUSION] = col.getCollection(parser.useCatalysisTree(DIFFUSION), DIFFUSION);
    doDiffusion = parser.doCatalysisDiffusion();
    doAdsorption = parser.doCatalysisAdsorption();
    doDesorption = parser.doCatalysisDesorption();
    doReaction = parser.doCatalysisReaction();
    start = parser.catalysisStart();
    doPrintAllIterations = parser.doPrintAllIterations();
    doO2Dissociation = parser.doCatalysisO2Dissociation();
    if (start.equals("empty")) {
      maxCoverage = (float) parser.getCoverage() / 100;
    } else {
      maxCoverage = 2; // it will never end because of coverage
    }
    steps = new long[4];
    co2 = new long[4];
    co2sum = 0;
    activationEnergy = new ActivationEnergy(parser);
    outputAe = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    outputAeTotal = parser.getOutputFormats().contains(OutputType.formatFlag.AETOTAL);
    counterSitesWith4OccupiedNeighbours = 0;
    stationary = false;
    stationaryStep = -1;
    automaticCollections = parser.areCollectionsAutomatic();
    goMultiplier = parser.getGOMultiplier();
  }
  
  void init(Parser parser) {
    CatalysisLattice catalysisLattice = new CatalysisCoLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), parser.getRatesLibrary());
    catalysisLattice.init();
    setLattice(catalysisLattice);
  }
  
  @Override
  public CatalysisLattice getLattice() {
    return (CatalysisLattice) super.getLattice();
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
  
  public void setRates(CatalysisRates rates) {
    desorptionRateCOPerSite = new double[2]; // empty
    desorptionRateOPerSite = new double[4]; // empty 
    reactionRateCoO = new double[4]; // empty
    diffusionRateCO = new double[4]; // empty
    diffusionRateO = new double[4]; // empty
    if (doAdsorption) {
      adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
      adsorptionRateOPerSite = rates.getAdsorptionRate(O);
    }
    if (doDesorption) {
      desorptionRateCOPerSite = rates.getDesorptionRates(CO);
      desorptionRateOPerSite = rates.getDesorptionRates(O);
      for (int i = 0; i < desorptionRateOPerSite.length; i++) {
        desorptionRateOPerSite[i] = desorptionRateOPerSite[i] * goMultiplier;
      }
    }
    if (doReaction) {
      reactionRateCoO = rates.getReactionRates();
    }
    if (doDiffusion) {
      diffusionRateCO = rates.getDiffusionRates(CO);
      diffusionRateO = rates.getDiffusionRates(O);
    }
    
    if (outputAe || outputAeTotal) {
      double[][] processProbs2D = new double[2][2];

      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
          processProbs2D[i][j] = rates.getReactionRates()[i * 2 + j];
        }
      }
      activationEnergy.setRates(processProbs2D);
    }
    numGaps = getLattice().getHexaSizeI() * getLattice().getHexaSizeJ();
  }

  public double[][] getOutputAdsorptionData() {
    double[][] adsorptionSimulationData = new double[adsorptionData.size()][5];
    for (int i = 0; i < adsorptionData.size(); i++) {
      adsorptionSimulationData[i] = adsorptionData.get(i).getCatalysisData();
    }
    
    return adsorptionSimulationData;
  }
  
  public float getCoverage(byte type) {
    return ((CatalysisLattice) getLattice()).getCoverage(type);
  }
  
  @Override
  public float getCoverage() {
    return ((CatalysisLattice) getLattice()).getCoverage();
  }
  
  public float[] getCoverages() {
    return ((CatalysisLattice) getLattice()).getCoverages();
  }
  
  public float getGapCoverage() {
    return ((CatalysisLattice) getLattice()).getGapCoverage();
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
        depositNewAtom();
        break;
      case DESORPTION:
        desorpAtom(); 
        break;
      case REACTION:
        reactAtom();
        break;
      case DIFFUSION:
        diffuseAtom();
        break;
    }
    simulatedSteps++;
    if (outputData && simulatedSteps % outputEvery == 0) {
      if (!stationary && ((CatalysisLattice) getLattice()).isStationary(getTime())) {
        System.out.println("stationary");
        stationary = true;
        stationaryStep = simulatedSteps;
        getList().resetTime();
        restart.resetCatalysis();
        co2prv = 0;
        co2 = new long[4];
        int[] sizes = new int[2];
        sizes[0] = getLattice().getHexaSizeI();
        sizes[1] = getLattice().getHexaSizeJ();
        restart.writeSurfaceStationary(getSampledSurface(sizes[0], sizes[1]));
      }
    }
    if ((stationary && co2sum % 10 == 0 && co2prv != co2sum) || (doPrintAllIterations && simulatedSteps % outputEvery == 0)) {
      if (outputData) {
        adsorptionData.add(new CatalysisData(getCoverage(), getTime(), getCoverage(CO), getCoverage(O),
                (float) (counterSitesWith4OccupiedNeighbours / (float) getLattice().size()),
                getGapCoverage()));
        int[] sizes = new int[4];
        sizes[ADSORPTION] = sites[ADSORPTION].size();
        sizes[DESORPTION] = sites[DESORPTION].size();
        sizes[REACTION] = sites[REACTION].size();
        sizes[DIFFUSION] = sites[DIFFUSION].size();
        restart.writeExtraCatalysisOutput(getTime(), getCoverages(), steps, co2, sizes);
        restart.flushCatalysis();
      }

      if (outputAe || outputAeTotal) {
        activationEnergy.printAe(restart.getExtraWriters(), getTime());
      }
      co2prv = co2sum;
    }
    return simulatedSteps == maxSteps;
  }

  @Override
  public int simulate() {
    int returnValue = 0;

    while (getLattice().getCoverage() < maxCoverage && co2sum != co2max) {
      if (outputAeTotal) {
        activationEnergy.updatePossibles((CatalysisLattice) getLattice(), getList().getDeltaTime(true), stationary);
      } else {
        activationEnergy.updatePossibles(sites[REACTION].iterator(), getList().getDeltaTime(true), stationary);
      }	      
      if (performSimulationStep()) {
        break;
      }
      checkSizes();
    }
    
    if (outputData) {
      adsorptionData.add(new CatalysisData(getCoverage(), getTime(), getCoverage(CO), getCoverage(O), 
              (float) (counterSitesWith4OccupiedNeighbours / (float) getLattice().size()),
                getGapCoverage()));
      if (stationary) {
        int[] sizes = new int[4];
        sizes[ADSORPTION] = sites[ADSORPTION].size();
        sizes[DESORPTION] = sites[DESORPTION].size();
        sizes[REACTION] = sites[REACTION].size();
        sizes[DIFFUSION] = sites[DIFFUSION].size();
        restart.writeExtraCatalysisOutput(getTime(), getCoverages(), steps, co2, sizes);
        restart.flushCatalysis();
      }
    }
    return returnValue;
  }

  @Override
  public void depositSeed() {
    totalRate = new double[4];
    getLattice().resetOccupied();
    if (!start.equals("empty")) {
      initCovered();
    } else if (doAdsorption) {
      initAdsorptionProbability();
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

  @Override
  public void reset() {
    activationEnergy.reset();
    Iterator iter = getList().getIterator();
    while (iter.hasNext()) {
      CatalysisSite atom = (CatalysisSite) iter.next();
      atom.clear();
    }
    getLattice().reset();
    getList().reset();
    restart.reset();
    if (outputData) {
      adsorptionData = new ArrayList<>();
    }
    steps = new long[4];
    co2 = new long[4];
    co2sum = 0;
    sites[ADSORPTION].clear();
    sites[DESORPTION].clear();
    sites[REACTION].clear();
    sites[DIFFUSION].clear();
    counterSitesWith4OccupiedNeighbours = 0;
    stationary = false;
    stationaryStep = -1;
    numGaps = getLattice().getHexaSizeI() * getLattice().getHexaSizeJ();
  }
  
  private boolean depositAtom(CatalysisSite atom) {
    if (atom.isOccupied()) {
      return false;
    }
    getLattice().deposit(atom, false);
    
    numGaps--;
    return true;
  }
  
  private CatalysisSite depositNewAtom() {
    CatalysisSite destinationAtom;
    int ucIndex = 0;
    byte atomType;
      
    CatalysisSite neighbourAtom = null;
    int random;
    
    if (sites[ADSORPTION].isEmpty()) {
      // can not deposit anymore
      return null;
    }

    destinationAtom = (CatalysisSite) sites[ADSORPTION].randomElement();

    if (destinationAtom == null || destinationAtom.getRate(ADSORPTION) == 0 || destinationAtom.isOccupied()) {
      boolean isThereAnAtom = destinationAtom == null;
      System.out.println("Something is wrong " + isThereAnAtom);
    }
    double randomNumber = StaticRandom.raw() * destinationAtom.getRate(ADSORPTION);
    if (randomNumber < adsorptionRateCOPerSite) {
      atomType = CO;
    } else {
      atomType = O;
    }
    destinationAtom.setType(atomType);
    if (!doO2Dissociation || atomType == CO) {
      depositAtom(destinationAtom);
    } else { // it has to deposit two O (dissociation of O2 -> 2O)
      depositAtom(destinationAtom);
      random = StaticRandom.rawInteger(4);
      neighbourAtom = destinationAtom.getNeighbour(random);
      while (neighbourAtom.isOccupied()) {
        random = (random + 1) % 4;
        neighbourAtom = destinationAtom.getNeighbour(random);
      }
      neighbourAtom.setType(O);
      depositAtom(neighbourAtom);
    }
    
    updateRates(destinationAtom);
    if (neighbourAtom != null) {
      updateRates(neighbourAtom);
    }
    
    return destinationAtom;
  }
  
  private void desorpAtom() {    
    CatalysisSite atom = (CatalysisSite) sites[DESORPTION].randomElement();
    int atomsToDesorp = 1;
    CatalysisSite neighbour = null;
    if (atom.getType() == O) { // it has to desorp with another O to create O2
      neighbour = atom.getRandomNeighbour(DESORPTION);
      atomsToDesorp = 2;
      getLattice().extract(neighbour);
    }
    
    getLattice().extract(atom);
    numGaps += atomsToDesorp;
    if (neighbour != null) {
      updateRates(neighbour);
    }
    updateRates(atom);
  }
  
  private void reactAtom() {
    CatalysisSite atom = (CatalysisSite) sites[REACTION].randomElement();
    // it has to react with another atom
    CatalysisSite neighbour = atom.getRandomNeighbour(REACTION);
    getLattice().extract(neighbour);
    getLattice().extract(atom);
    numGaps = numGaps-2;
    // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
    int index;
    if (atom.getType() == CO) {
      index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    } else {
      index = 2 * neighbour.getLatticeSite() + atom.getLatticeSite();
    }
    co2[index]++;
    if (stationary)
      co2sum++;

    updateRates(neighbour);
    updateRates(atom);
  }
  
  /**
   * Moves an atom.
   */
  private void diffuseAtom() {
    CatalysisSite originAtom = (CatalysisSite) sites[DIFFUSION].randomElement();
    CatalysisSite destinationAtom = originAtom.getRandomNeighbour(DIFFUSION);
    destinationAtom.setType(originAtom.getType());
    getLattice().extract(originAtom);    
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    updateRates(originAtom);
    updateRates(destinationAtom);
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initAdsorptionProbability() {
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        a.setRate(ADSORPTION, adsorptionRateCOPerSite + adsorptionRateOPerSite); // there is no neighbour
        a.setOnList(ADSORPTION, true);
        totalRate[ADSORPTION] += a.getRate(ADSORPTION);
        sites[ADSORPTION].insert(a);
        sites[DESORPTION].insert(a);
        sites[REACTION].insert(a);
        sites[DIFFUSION].insert(a);
      }
    }
    getList().setRates(totalRate);
    sites[ADSORPTION].populate();
    sites[DESORPTION].populate();
    sites[REACTION].populate();
    sites[DIFFUSION].populate();
  }
  
  /**
   * Start with fully covered surface.
   * 
   * @param randomTypes if true, CO and O types randomly chosen. If false, only oxygen.
   */
  private void initCovered() {
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        switch (start) {
          case "O":
            a.setType(O);
            break;
          case "CO":
            a.setType(CO);
            break;
          default:
            a.setType((byte) StaticRandom.rawInteger(2));
            break;
        }
        getLattice().deposit(a, false);
      }
    }

    totalRate[DESORPTION] = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        a.setOnList(ADSORPTION, false);
        sites[ADSORPTION].insert(a);
        sites[DIFFUSION].insert(a);
        if (a.getType() == CO) {
          a.setRate(DESORPTION, desorptionRateCOPerSite[a.getLatticeSite()]);
          totalRate[DESORPTION] += a.getRate(DESORPTION);
        } else { //O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = desorptionRateOPerSite[index];
              a.addRate(DESORPTION, rate, k);
            }
          }
          totalRate[DESORPTION] += a.getRate(DESORPTION);
        }
        if (a.getRate(DESORPTION) > 0) {
          a.setOnList(DESORPTION, true);
        }
        sites[DESORPTION].insert(a);
      }
    }
    sites[DESORPTION].populate();
    totalRate[DESORPTION] = sites[DESORPTION].getTotalRate(DESORPTION);
    
    totalRate[REACTION] = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        if (a.getType() == CO) {
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              //               CO                   +         O
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        } else { // O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisSite neighbour = a.getNeighbour(k);
            if (neighbour.getType() == CO) {
              //               CO                   +         O
              int index = 2 * neighbour.getLatticeSite() + a.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        }
        if (a.getRate(REACTION) > 0) {
          a.setOnList(REACTION, true);
        }
        sites[REACTION].insert(a);
        totalRate[REACTION] += a.getRate(REACTION);
      }
    }
    sites[REACTION].populate();
    
    getList().setRates(totalRate);
    getLattice().resetOccupied();
  }
  
  /**
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(CatalysisSite atom) {
    // save previous rates
    double[] previousRate = totalRate.clone();
    
    // recompute the probability of the current atom
    if (doAdsorption) recomputeAdsorptionProbability(atom);
    if (doDesorption) recomputeDesorptionProbability(atom);
    if (doReaction) recomputeReactionProbability(atom);
    if (doDiffusion) recomputeDiffusionProbability(atom);
    // recompute the probability of the neighbour atoms
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (doAdsorption) recomputeAdsorptionProbability(neighbour);
      if (doDesorption) recomputeDesorptionProbability(neighbour);
      if (doReaction) recomputeReactionProbability(neighbour);
      if (doDiffusion) recomputeDiffusionProbability(neighbour);
    }
    
    // recalculate total probability, if needed
    if (totalRate[ADSORPTION] / previousRate[ADSORPTION] < 1e-1) {
      updateRateFromList(ADSORPTION);
    }
    if (totalRate[DESORPTION] / previousRate[DESORPTION] < 1e-1 || 1.0 - totalRate[DESORPTION] / sites[DESORPTION].getTotalRate(DESORPTION) > 1e-3 || simulatedSteps % 10000000 == 0) {
      //System.out.println(simulatedSteps+" "+previousDesorptionRate + " " + totalDesorptionRate + " " + sites[DESORPTION].getDesorptionRate());
      updateRateFromList(DESORPTION);
    }
    if (totalRate[REACTION] / previousRate[REACTION] < 1e-1) {
      updateRateFromList(REACTION);
    }
    if (totalRate[DIFFUSION] / previousRate[DIFFUSION] < 1e-1) {
      updateRateFromList(DIFFUSION);
    }
    
    // tell to the list new probabilities
    getList().setRates(totalRate);
  }
            
  private void recomputeAdsorptionProbability(CatalysisSite atom) {
    double oldAdsorptionRate = atom.getRate(ADSORPTION);
    totalRate[ADSORPTION] -= oldAdsorptionRate;
    if (atom.isOccupied()) {
      atom.setRate(ADSORPTION, 0);
    } else {
      int canAdsorbO2 = atom.isIsolated() && doO2Dissociation ? 0 : 1;
      atom.setRate(ADSORPTION, adsorptionRateCOPerSite + canAdsorbO2 * adsorptionRateOPerSite);
      if (atom.isIsolated()) {
        counterSitesWith4OccupiedNeighbours++;
      }
    }
    recomputeCollection(ADSORPTION, atom, oldAdsorptionRate);
  }
  
  private void recomputeDesorptionProbability(CatalysisSite atom) {
    totalRate[DESORPTION] -= atom.getRate(DESORPTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DESORPTION)) {
        sites[DESORPTION].removeAtomRate(atom);
      }
      atom.setOnList(DESORPTION, false);
      return;
    }
    double oldDesorptionProbability = atom.getRate(DESORPTION);
    atom.setRate(DESORPTION, 0);
    if (atom.getType() == CO) {
      double rate = getDesorptionRate(atom);
      atom.setRate(DESORPTION, rate);
    } else { // O
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          double rate = getDesorptionProbability(atom, neighbour);
          atom.addRate(DESORPTION, rate, i);
        }
      }
    }
    recomputeCollection(DESORPTION, atom, oldDesorptionProbability);
  }
  
  private void recomputeReactionProbability(CatalysisSite atom) {
    totalRate[REACTION] -= atom.getRate(REACTION);
    double oldReactionRate = atom.getRate(REACTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(REACTION)) {
        sites[REACTION].removeAtomRate(atom);
      }
      atom.setOnList(REACTION, false);
      return;
    }
    atom.setRate(REACTION, 0);
    byte otherType = (byte) ((atom.getType() + 1) % 2);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (neighbour.isOccupied() && neighbour.getType() == otherType) {
        double rate = getReactionRate(atom, neighbour); 
        atom.addRate(REACTION, rate/2.0, i);
      }
    }
    recomputeCollection(REACTION, atom, oldReactionRate);
  }

  private void recomputeDiffusionProbability(CatalysisSite atom) {
    totalRate[DIFFUSION] -= atom.getRate(DIFFUSION);
    double oldDiffusionRate = atom.getRate(DIFFUSION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DIFFUSION)) {
        sites[DIFFUSION].removeAtomRate(atom);
      }
      atom.setOnList(DIFFUSION, false);
      return;
    }
    atom.setRate(DIFFUSION, 0);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisSite neighbour = atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionRate(atom, neighbour);
        atom.addRate(DIFFUSION, probability, i);
      }
    }
    recomputeCollection(DIFFUSION, atom, oldDiffusionRate);
  }
  
  private void recomputeCollection(byte process, CatalysisSite atom, double oldRate) {
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

  /**
   * Computes desorption probability.
   * 
   * @param atom CO molecule.
   * @return 
   */
  double getDesorptionRate(CatalysisSite atom) {
    return desorptionRateCOPerSite[atom.getLatticeSite()];
  }
  
  /**
   * Computes desorption probability.
   * 
   * @param atom O atom.
   * @param neighbour O atom.
   * @return 
   */
  private double getDesorptionProbability(CatalysisSite atom, CatalysisSite neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    return desorptionRateOPerSite[index];
  }
  
  /**
   * One atom is O and the other CO, for sure.
   * 
   * @param atom
   * @param neighbour
   * @return 
   */
  double getReactionRate(CatalysisSite atom, CatalysisSite neighbour) {
    int index;
    if (atom.getType() == CO) {
      index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    } else {
      index = 2 * neighbour.getLatticeSite() + atom.getLatticeSite();
    }
    double probability = reactionRateCoO[index];
    
    return probability;
  }
  
  double getDiffusionRate(CatalysisSite atom, CatalysisSite neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    double probability;
    if (atom.getType() == CO) {
      probability = diffusionRateCO[index];
    } else {
      probability = diffusionRateO[index];
    }
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
      // ADSORPTION, DESORPTION, REACTION, DIFFUSION
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
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        sites[process].insert(a);
      }
    }
    sites[process].populate();
  }
  
  /**
   * Method to print rates. Equivalent to table 1 of Temel et al. J. Chem. Phys. 126 (2007).
   */
  public void printRates() {
    System.out.println(" Process ");
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
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^CUS  ", diffusionRateO[3]);
  }
  
  public void printIteration() {
    System.out.format("\t%.4f", getCoverage(CO));
    System.out.format("\t%.4f", getCoverage(O));
    System.out.format("\t%d", co2sum);
    System.out.format("\t%1.1e", (double)simulatedSteps);
    System.out.format("\t%d", stationaryStep);
  }
}
