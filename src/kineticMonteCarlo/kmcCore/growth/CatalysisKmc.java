/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.CatalysisData;
import basic.io.OutputType;
import basic.io.Restart;
import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;
import static kineticMonteCarlo.atom.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.atom.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.atom.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.atom.CatalysisProcess.REACTION;
import utils.list.atoms.AtomsCollection;
import utils.list.atoms.IAtomsCollection;

/**
 *
 * @author Karmele Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisKmc extends AbstractGrowthKmc {

  private final boolean measureDiffusivity;
  private long simulatedSteps;
  private long[] steps;
  private long[] co2; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  private int totalNumOfSteps;
  private int numStepsEachData;
  private ArrayList<CatalysisData> simulationData;
  private ArrayList<CatalysisData> adsorptionData;
  // Adsorption
  private double adsorptionRateCOPerSite;
  private double adsorptionRateOPerSite;
  private double totalAdsorptionRate;
  // Desorption
  private ArrayList<CatalysisAtom> adsorptionSites;
  private double[] desorptionRateCOPerSite; // BRIDGE or CUS
  private double[] desorptionRateOPerSite;  // [BR][BR], [BR][CUS], [CUS][BR], [CUS][CUS]
  private double totalDesorptionRate;
  private final IAtomsCollection desorptionSites;
  // Reaction
  private double[] reactionRateCoO; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  private double totalReactionRate;
  private float currentAdsorptionP;
  private ArrayList<CatalysisAtom> reactionSites;
  private double[] diffusionRateCO;
  private double[] diffusionRateO;
  private double totalDiffusionRate;
  private ArrayList<CatalysisAtom> diffusionSites;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private final boolean doDiffusion;
  private final boolean doAdsorption;
  private final boolean doDesorption;
  private final boolean doReaction;
  private final boolean startOxygen;
  private Restart restart;
  private final ActivationEnergy activationEnergy;
  /**
   * Activation energy output during the execution
   */
  private final boolean aeOutput;
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    currentAdsorptionP = 1.0f;
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    totalAdsorptionRate = 0.0;
    totalDesorptionRate = 0.0;
    totalReactionRate = 0.0;

    simulatedSteps = 0;
    measureDiffusivity = parser.outputData();
    if (measureDiffusivity) {
      totalNumOfSteps = parser.getNumberOfSteps();
      numStepsEachData = parser.getOutputEvery();
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();
    }
    restart = new Restart(measureDiffusivity);
    adsorptionSites = new ArrayList<>();
    AtomsCollection col = new AtomsCollection(parser, DESORPTION);
    desorptionSites = col.getCollection(); // Either a tree or array
    reactionSites = new ArrayList<>();
    diffusionSites = new ArrayList<>();
    doDiffusion = parser.doCatalysisDiffusion();
    doAdsorption = parser.doCatalysisAdsorption();
    doDesorption = parser.doCatalysisDesorption();
    doReaction = parser.doCatalysisReaction();
    startOxygen = parser.catalysisStartOxigenCov();
    if (startOxygen) {
      maxCoverage = 2; // it will never end because of coverage
    } else {
      maxCoverage = (float) parser.getCoverage() / 100;
    }
    steps = new long[4];
    co2 = new long[4];
    activationEnergy = new ActivationEnergy(parser);
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
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
    }
    if (doReaction) {
      reactionRateCoO = rates.getReactionRates();
    }
    if (doDiffusion) {
      diffusionRateCO = rates.getDiffusionRates(CO);
      diffusionRateO = rates.getDiffusionRates(O);
    }
    printRates();
    double[][] processProbs2D = new double[2][2];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        processProbs2D[i][j] = rates.getReactionRates()[i * 2 + j];
      }
    }
    activationEnergy.setRates(processProbs2D);
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
  
  public float[] getCoverages() {
    return ((CatalysisLattice) getLattice()).getCoverages();
  }
  
  @Override
  public void initialiseRates(double[] rates) {
    //we modify the 1D array into a 3D array;
    int length = 2;
    double[][][] processProbs3D = new double[length][length][length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        for (int k = 0; k < length; k++) {
          processProbs3D[i][j][k] = rates[(i * length * length) + (j * length) + k];
        }
      }
    }
    if (!doDiffusion) {
      processProbs3D = new double[length][length][length]; // reset to zero, there is no diffusion at all.
    }
    ((CatalysisLattice) getLattice()).initialiseRates(processProbs3D);
    //activationEnergy.setRates(processProbs3D);
  }

  /**
   * Performs a simulation step.
   *
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    currentAdsorptionP = 1.0f;
    if (getList().getGlobalProbability() == 0) {
      return true; // there is nothing more we can do
    }
    byte reaction = getList().nextReaction();
    steps[reaction]++;
    CatalysisAtom destinationAtom = null;
    switch (reaction) {
      case ADSORPTION:
        destinationAtom = depositNewAtom();
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
    if (measureDiffusivity && (simulatedSteps + 1) % numStepsEachData == 0) {
      if (destinationAtom != null) {
        simulationData.add(new CatalysisData(destinationAtom.getiHexa(), destinationAtom.getjHexa(), getTime()));
        
        adsorptionData.add(new CatalysisData(getCoverage(), getTime(), getCoverage(CO), getCoverage(O), currentAdsorptionP));
      }
      getCoverages();
      restart.writeExtraCatalysisOutput(getTime(), getCoverages(), steps, co2);
      if (aeOutput) {
        activationEnergy.printAe(restart.getExtraWriter(), (float) getTime());
      }
    }
    if (measureDiffusivity && (simulatedSteps + 1) % (numStepsEachData * 10) == 0) {
      restart.flushCatalysis();
    }
    return simulatedSteps + 1 == totalNumOfSteps;
  }

  @Override
  public int simulate() {
    int returnValue = 0;

    while (getLattice().getCoverage() < maxCoverage) {
      activationEnergy.updatePossibles(reactionSites.listIterator(), getList().getDeltaTime(true));
      if (performSimulationStep()) {
        break;
      }
    }
    if (measureDiffusivity) {
      restart.flushCatalysis();
    }
    return returnValue;
  }

  @Override
  public void depositSeed() {
    totalAdsorptionRate = 0.0;
    totalDesorptionRate = 0.0;
    totalReactionRate = 0.0;
    getLattice().resetOccupied();
    if (startOxygen) {
      initCovered();
    } else if (doAdsorption) {
      initAdsorptionProbability();
    } else {
      initCovered();
    }
    simulatedSteps = 0;
  }

  @Override
  public void reset() {
    ListIterator iter = getList().getIterator();
    while (iter.hasNext()){
      CatalysisAtom atom = (CatalysisAtom) iter.next();
      atom.clear();
    }
    getLattice().reset();
    getList().reset();
    if (measureDiffusivity) {
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();
    }
    steps = new long[4];
    co2 = new long[4];
  }
  
  private boolean depositAtom(CatalysisAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }

    getLattice().deposit(atom, false);

    return true;
  }
  
  private CatalysisAtom depositNewAtom() {
    CatalysisAtom destinationAtom = null;
    int ucIndex = 0;
    byte atomType;
      
    boolean deposited;
    CatalysisAtom neighbourAtom = null;
    int random;
    
    currentAdsorptionP = getCurrentP();
 
    do {
      if (adsorptionSites.isEmpty()) {
        // can not deposit anymore
        return null;
      }
      
      double randomNumber = StaticRandom.raw() * totalAdsorptionRate;
      
      double sum = 0.0;
      int i;
      for (i = 0; i < adsorptionSites.size(); i++) {
        sum += adsorptionSites.get(i).getRate(ADSORPTION);
        if (sum > randomNumber) {
          destinationAtom = adsorptionSites.get(i);
          break;
        }
      }

      if (destinationAtom == null || destinationAtom.getRate(ADSORPTION) == 0) {
        boolean isThereAnAtom = destinationAtom == null;
        System.out.println("Something is wrong " + isThereAnAtom);
      }
      randomNumber = StaticRandom.raw() * destinationAtom.getRate(ADSORPTION);
      if (randomNumber < adsorptionRateCOPerSite) {
        atomType = CO;
      } else {
        atomType = O;
      }
      destinationAtom.setType(atomType);
      deposited = depositAtom(destinationAtom);
      if (atomType == O) { // it has to deposit two O (dissociation of O2 -> 2O)
        random = StaticRandom.rawInteger(4);
        neighbourAtom = destinationAtom.getNeighbour(random);
        while (neighbourAtom.isOccupied()) {
          random = (random + 1) % 4;
          neighbourAtom = destinationAtom.getNeighbour(random);
        }
        neighbourAtom.setType(O);
        depositAtom(neighbourAtom);
      }
    } while (!deposited);
    
    updateRates(destinationAtom);
    if (neighbourAtom != null) {
      updateRates(neighbourAtom);
      neighbourAtom.setDepositionTime(getTime());
      //neighbourAtom.setDepositionPosition(-1);
    }
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    getList().setDesorptionProbability(totalDesorptionRate);
    getList().setReactionProbability(totalReactionRate);
    
    return destinationAtom;
  }
  
  private void desorpAtom() {
    double randomNumber;
    
    CatalysisAtom atom = (CatalysisAtom) desorptionSites.randomAtom();
    double sum;
    CatalysisAtom neighbour = null;
    if (atom.getType() == O) { // it has to desorp with another O to create O2
      randomNumber = StaticRandom.raw() * atom.getRate(DESORPTION);
      sum = 0.0;
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        sum += atom.getEdgeRate(DESORPTION, j);
        if (sum > randomNumber) {
          neighbour = atom.getNeighbour(j);
          break;
        }
      }
      getLattice().extract(neighbour);
    }
    
    getLattice().extract(atom);
   
    if (neighbour != null) {
      updateRates(neighbour);
    }
    updateRates(atom);
  }
  
  private void reactAtom() {
    CatalysisAtom atom = null;
    double randomNumber = StaticRandom.raw() * totalReactionRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < reactionSites.size(); i++) {
      sum += reactionSites.get(i).getRate(REACTION);
      if (sum > randomNumber) {
        atom = reactionSites.get(i);
        break;
      }
    }
    CatalysisAtom neighbour = null;
    // it has to react with another atom
    randomNumber = StaticRandom.raw() * atom.getRate(REACTION);
    sum = 0.0;
    for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
      sum += atom.getEdgeRate(REACTION, j);
      if (sum > randomNumber) {
        neighbour = atom.getNeighbour(j);
        break;
      }
    }
    getLattice().extract(neighbour);
    getLattice().extract(atom);
    // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
    int index;
    if (atom.getType() == CO) {
      index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    } else {
      index = 2 * neighbour.getLatticeSite() + atom.getLatticeSite();
    }
    co2[index]++;

    updateRates(neighbour);
    updateRates(atom);
  }
  
  /**
   * Moves an atom.
   */
  private void diffuseAtom() {
    CatalysisAtom originAtom = null;
    double randomNumber = StaticRandom.raw() * totalDiffusionRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < diffusionSites.size(); i++) {
      sum += diffusionSites.get(i).getRate(DIFFUSION);
      if (sum > randomNumber) {
        originAtom = diffusionSites.get(i);
        break;
      }
    }
    CatalysisAtom destinationAtom = null;
    // it has to react with another atom
    randomNumber = StaticRandom.raw() * originAtom.getRate(DIFFUSION);
    sum = 0.0;
    for (int j = 0; j < originAtom.getNumberOfNeighbours(); j++) {
      sum += originAtom.getEdgeRate(DIFFUSION, j);
      if (sum > randomNumber) {
        destinationAtom = originAtom.getNeighbour(j);
        break;
      }
    }
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
    adsorptionSites = new ArrayList<>();
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        a.setRate(ADSORPTION, adsorptionRateCOPerSite + adsorptionRateOPerSite); // there is no neighbour
        adsorptionSites.add(a);
        a.setOnList(ADSORPTION, true);
        totalAdsorptionRate += a.getRate(ADSORPTION);
        desorptionSites.insert(a);
      }
    }
    getList().setDepositionProbability(totalAdsorptionRate);
    getList().setDesorptionProbability(0);
    desorptionSites.populate();
  }
  
  /**
   * Start with fully covered surface.
   * 
   * @param randomTypes if true, CO and O types randomly chosen. If false, only oxygen.
   */
  private void initCovered() {
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        if (startOxygen) {
          a.setType(O);
        } else {
          a.setType((byte) StaticRandom.rawInteger(2));
        }
        getLattice().deposit(a, false);
      }
    }

    totalDesorptionRate = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        a.setOnList(ADSORPTION, false);
        if (a.getType() == CO) {
          a.setOnList(DESORPTION, true);
          desorptionSites.insert(a);
          a.setRate(DESORPTION, desorptionRateCOPerSite[a.getLatticeSite()]);
          totalDesorptionRate += a.getRate(DESORPTION);
        } else { //O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = desorptionRateOPerSite[index];
              a.addRate(DESORPTION, rate, k);
            }
          }
          if (a.getRate(DESORPTION) > 0) {          
            desorptionSites.insert(a);
            a.setOnList(DESORPTION, true);
          }
          totalDesorptionRate += a.getRate(DESORPTION);
        }
      }
    }
    desorptionSites.populate();
    totalDesorptionRate = desorptionSites.getTotalRate(DESORPTION);
    //((AtomsAvlTree) desorptionSites).PrintTree();
    
    totalReactionRate = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        if (a.getType() == CO) {
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              //               CO                   +         O
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        } else { // O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == CO) {
              //               CO                   +         O
              int index = 2 * neighbour.getLatticeSite() + a.getLatticeSite();
              double rate = reactionRateCoO[index];
              a.addRate(REACTION, rate/2.0, k);
            }
          }
        }
        if (a.getRate(REACTION) > 0) {
          reactionSites.add(a);
          a.setOnList(REACTION, true);
        }
        totalReactionRate += a.getRate(REACTION);
      }
    }
    
    getList().setDepositionProbability(0);
    getList().setDesorptionProbability(totalDesorptionRate);
    getList().setReactionProbability(totalReactionRate);
    getLattice().resetOccupied();
  }
  
  /**
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
   *
   * @param atom
   */
  private void updateRates(CatalysisAtom atom) {
    // save previous rates
    double previousAdsorptionRate = totalAdsorptionRate;
    double previousDesorptionRate = totalDesorptionRate;
    double previousReactionRate = totalReactionRate;
    double previousDiffusionRate = totalDiffusionRate;
    
    // recompute the probability of the current atom
    recomputeAdsorptionProbability(atom);
    recomputeDesorptionProbability(atom);
    recomputeReactionProbability(atom);
    recomputeDiffusionProbability(atom);
    // recompute the probability of the neighbour atoms
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      recomputeAdsorptionProbability(neighbour);
      recomputeDesorptionProbability(neighbour);
      recomputeReactionProbability(neighbour);
      recomputeDiffusionProbability(neighbour);
    }
    
    // recalculate total probability, if needed
    if (totalAdsorptionRate / previousAdsorptionRate < 1e-1) {
      updateAdsorptionRateFromList();
    }
    if (totalDesorptionRate / previousDesorptionRate < 1e-1 || 1.0 - totalDesorptionRate / desorptionSites.getTotalRate(DESORPTION) > 1e-3 || simulatedSteps % 10000000 == 0) {
      //System.out.println(simulatedSteps+" "+previousDesorptionRate + " " + totalDesorptionRate + " " + desorptionSites.getDesorptionRate());
      updateDesorptionRateFromList();
    }
    if (totalReactionRate / previousReactionRate < 1e-1) {
      updateReactionRateFromList();
    }
    if (totalDiffusionRate / previousDiffusionRate < 1e-1) {
      updateDiffusionRateFromList();
    }
    
    // tell to the list new probabilities
    getList().setDepositionProbability(totalAdsorptionRate);
    getList().setDesorptionProbability(totalDesorptionRate);
    getList().setReactionProbability(totalReactionRate);
    getList().setDiffusionProbability(totalDiffusionRate);
  }
            
  private void recomputeAdsorptionProbability(CatalysisAtom atom) {
    double oldAdsorptionRate = atom.getRate(ADSORPTION);
    totalAdsorptionRate -= oldAdsorptionRate;
    if (atom.isOccupied()) {
      atom.setRate(ADSORPTION, 0);
    } else {
      int canAdsorbO2 = atom.isIsolated() ? 0 : 1;
      atom.setRate(ADSORPTION, adsorptionRateCOPerSite + canAdsorbO2 * adsorptionRateOPerSite);
    }
    totalAdsorptionRate += atom.getRate(ADSORPTION);
    if (atom.getRate(ADSORPTION) == 0) {
      if (atom.isOnList(ADSORPTION)) {
        adsorptionSites.remove(atom);
      }
      atom.setOnList(ADSORPTION, false);
    } else if (oldAdsorptionRate == 0) {
      adsorptionSites.add(atom);
      atom.setOnList(ADSORPTION, true);
    }
  }
  
  private void recomputeDesorptionProbability(CatalysisAtom atom) {
    totalDesorptionRate -= atom.getRate(DESORPTION);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DESORPTION)) {
        desorptionSites.removeAtomRate(atom);
      }
      atom.setOnList(DESORPTION, false);
      return;
    }
    double oldDesorptionProbability = atom.getRate(DESORPTION);
    atom.setRate(DESORPTION, 0);
    if (atom.getType() == CO) {
      atom.setRate(DESORPTION, desorptionRateCOPerSite[atom.getLatticeSite()]);
      totalDesorptionRate += atom.getRate(DESORPTION);
      if (!atom.isOnList(DESORPTION)) {
        //desorptionSites.add(atom);
        desorptionSites.addRate(atom);
      }
      atom.setOnList(DESORPTION, true);
    } else { // O
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisAtom neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          double rate = getDesorptionProbability(atom, neighbour);
          atom.addRate(DESORPTION, rate, i);
        }
      }
      if (atom.getRate(DESORPTION) > 0) {
        totalDesorptionRate += atom.getRate(DESORPTION);
        if (!atom.isOnList(DESORPTION)) {
          desorptionSites.addRate(atom);
        }
        atom.setOnList(DESORPTION, true);
      }
      if (oldDesorptionProbability != atom.getRate(DESORPTION)) {
        desorptionSites.removeRate(atom, oldDesorptionProbability - atom.getRate(DESORPTION));
      }
    }
  }
  
  private void recomputeReactionProbability(CatalysisAtom atom) {
    totalReactionRate -= atom.getRate(REACTION);
    atom.setRate(REACTION, 0);
    if (!atom.isOccupied()) {
      if (atom.isOnList(REACTION)) {
        reactionSites.remove(atom);
      }
      atom.setOnList(REACTION, false);
      return;
    }
    byte otherType = (byte) ((atom.getType() + 1) % 2);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      if (neighbour.isOccupied() && neighbour.getType() == otherType) {
        double rate = getReactionProbability(atom, neighbour); 
        atom.addRate(REACTION, rate/2.0, i);
      }
    }
    if (atom.getRate(REACTION) > 0) {
      totalReactionRate += atom.getRate(REACTION);
      if (!atom.isOnList(REACTION)) {
        reactionSites.add(atom);
      }
      atom.setOnList(REACTION, true);
    } else if (atom.isOnList(REACTION)) {
      atom.setOnList(REACTION, false);
      reactionSites.remove(atom);
    }
  }

  private void recomputeDiffusionProbability(CatalysisAtom atom) {
    totalDiffusionRate -= atom.getRate(DIFFUSION);
    atom.setRate(DIFFUSION, 0);
    if (!atom.isOccupied()) {
      if (atom.isOnList(DIFFUSION)) {
        diffusionSites.remove(atom);
      }
      atom.setOnList(DIFFUSION, false);
      return;
    }
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionProbability(atom, neighbour);
        atom.addRate(DIFFUSION, probability, i);
      }
    }
    if (atom.getRate(DIFFUSION) > 0) {
      totalDiffusionRate += atom.getRate(DIFFUSION);
      if (!atom.isOnList(DIFFUSION)) {
        diffusionSites.add(atom);
        atom.setOnList(DIFFUSION, true);
      }
    } else if (atom.isOnList(DIFFUSION)) {
      atom.setOnList(DIFFUSION, false);
      diffusionSites.remove(atom);
    }
  }
    
  private double getDesorptionProbability(CatalysisAtom atom, CatalysisAtom neighbour) {
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
  private double getReactionProbability(CatalysisAtom atom, CatalysisAtom neighbour) {
    int index;
    if (atom.getType() == CO) {
      index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    } else {
      index = 2 * neighbour.getLatticeSite() + atom.getLatticeSite();
    }
    double probability = reactionRateCoO[index];
    
    return probability;
  }
  
  private double getDiffusionProbability(CatalysisAtom atom, CatalysisAtom neighbour) {
    int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
    double probability;
    if (atom.getType() == CO) {
      probability = diffusionRateCO[index];
    } else {
      probability = diffusionRateO[index];
    }
    return probability;
  }
  
  private void updateAdsorptionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < adsorptionSites.size(); i++) {
      sum += adsorptionSites.get(i).getRate(ADSORPTION);
    }
    totalAdsorptionRate = sum;
  }

  private void updateDesorptionRateFromList() {
    desorptionSites.clear();
    desorptionSites.populate();
    totalDesorptionRate = desorptionSites.getTotalRate(DESORPTION);
  }
  
  private void updateReactionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < reactionSites.size(); i++) {
      sum += reactionSites.get(i).getRate(REACTION);
    }
    totalReactionRate = sum;
  }
  
  private void updateDiffusionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < diffusionSites.size(); i++) {
      sum += diffusionSites.get(i).getRate(DIFFUSION);
    }
    totalDiffusionRate = sum;
  }
  
  private float getCurrentP() {
    float numOccupiedNeighbours = 0f;
    for (int i = 0; i < adsorptionSites.size(); i++) {
      numOccupiedNeighbours += (1 - (float) adsorptionSites.get(i).getOccupiedNeighbours() / 4);
    }
    return (float) numOccupiedNeighbours / adsorptionSites.size();
  }
  
  /**
   * Method to print rates. Equivalent to table 1 of Temel et al. J. Chem. Phys. 126 (2007).
   */
  private void printRates() {
    System.out.println(" Process ");
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "CO adsorption\t", adsorptionRateCOPerSite);
    System.out.format("%s\t%1.1e\t%1.1e\n", " O adsorption\t",adsorptionRateOPerSite, adsorptionRateOPerSite * 2);
    System.out.println(" ------- ");
    System.out.println("Desorption");
    System.out.format("%s\t\t%1.1e\n","CO^BR", desorptionRateCOPerSite[0]);
    System.out.format("%s\t\t%1.1e\n", "CO^CUS", desorptionRateCOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^BR", desorptionRateOPerSite[0]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^CUS", desorptionRateOPerSite[3]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^CUS", desorptionRateOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^BR", desorptionRateOPerSite[2]);
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "CO^BR + O^CUS", reactionRateCoO[1]);
    System.out.format("%s\t%1.1e\n", "CO^BR + O^BR", reactionRateCoO[0]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^CUS", reactionRateCoO[3]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^BR", reactionRateCoO[2]);
  }
}
