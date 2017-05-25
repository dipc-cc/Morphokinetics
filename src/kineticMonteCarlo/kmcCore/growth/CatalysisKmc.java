/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.CatalysisData;
import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;
import utils.list.CatalysisLinearList;
import static utils.list.CatalysisLinearList.ADSORPTION;
import static utils.list.CatalysisLinearList.DESORPTION;
import static utils.list.CatalysisLinearList.DIFFUSION;
import static utils.list.CatalysisLinearList.REACTION;

/**
 *
 * @author Karmele Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisKmc extends AbstractGrowthKmc {

  private final boolean measureDiffusivity;
  private long simulatedSteps;
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
  // Reaction
  private ArrayList<CatalysisAtom> desorptionSites;
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
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    maxCoverage =(float) parser.getCoverage() / 100;
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
      numStepsEachData = 10;
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();
    }
    adsorptionSites = new ArrayList<>();
    desorptionSites = new ArrayList<>();
    reactionSites = new ArrayList<>();
    diffusionSites = new ArrayList<>();
    doDiffusion = parser.doCatalysisDiffusion();
    doAdsorption = parser.doCatalysisAdsorption();
    doDesorption = parser.doCatalysisDesorption();
    doReaction = parser.doCatalysisReaction();
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

    System.out.println("k_i(CO): " + adsorptionRateCOPerSite + " k_i(O): " + adsorptionRateOPerSite);
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
    byte reaction = ((CatalysisLinearList) getList()).nextReaction();
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
    }
    return simulatedSteps + 1 == totalNumOfSteps;
  }

  @Override
  public int simulate() {
    int returnValue = 0;

    while (getLattice().getCoverage() < maxCoverage) {
      getList().getDeltaTime(true);
      if (performSimulationStep()) {
        break;
      }
    }
    return returnValue;
  }

  @Override
  public void depositSeed() {
    totalAdsorptionRate = 0.0;
    totalDesorptionRate = 0.0;
    totalReactionRate = 0.0;
    getLattice().resetOccupied();
    if (doAdsorption) {
      initAdsorptionProbability();
    } else {
      initDesorptionOnly();
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
        sum += adsorptionSites.get(i).getAdsorptionProbability();
        if (sum > randomNumber) {
          destinationAtom = adsorptionSites.get(i);
          break;
        }
      }

      if (destinationAtom == null || destinationAtom.getAdsorptionProbability() == 0) {
        boolean isThereAnAtom = destinationAtom == null;
        System.out.println("Something is wrong " + isThereAnAtom);
      }
      randomNumber = StaticRandom.raw() * destinationAtom.getAdsorptionProbability();
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
    
    updateAdsorptionRate(destinationAtom);
    updateDesorptionRate(destinationAtom);
    updateReactionRate(destinationAtom);
    updateDiffusionRate(destinationAtom);
    if (neighbourAtom != null) {
      updateAdsorptionRate(neighbourAtom);
      updateDesorptionRate(neighbourAtom);
      updateReactionRate(neighbourAtom);
      updateDiffusionRate(neighbourAtom);
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
    CatalysisAtom atom = null;
    double randomNumber = StaticRandom.raw() * totalDesorptionRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < desorptionSites.size(); i++) {
      sum += desorptionSites.get(i).getDesorptionProbability();
      if (sum > randomNumber) {
        atom = desorptionSites.get(i);
        break;
      }
    }
    CatalysisAtom neighbour = null;
    if (atom.getType() == O) { // it has to desorp with another O to create O2
      randomNumber = StaticRandom.raw() * atom.getDesorptionProbability();
      sum = 0.0;
      for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
        sum += atom.getDesorptionOEdge(j);
        if (sum > randomNumber) {
          neighbour = atom.getNeighbour(j);
          break;
        }
      }
      double probabilityChange = getLattice().extract(neighbour);
      getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    }

    getLattice().subtractOccupied();
    double probabilityChange = getLattice().extract(atom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    
    updateAdsorptionRate(atom);
   
    if (neighbour != null) {
      updateAdsorptionRate(neighbour);
      updateDesorptionRate(neighbour);
      updateReactionRate(neighbour);
      updateDiffusionRate(neighbour);
    }
    updateDesorptionRate(atom);
    updateReactionRate(atom);
    updateDiffusionRate(atom);
  }
  
  private void reactAtom() {
    CatalysisAtom atom = null;
    double randomNumber = StaticRandom.raw() * totalReactionRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < reactionSites.size(); i++) {
      sum += reactionSites.get(i).getReactionProbability();
      if (sum > randomNumber) {
        atom = reactionSites.get(i);
        break;
      }
    }
    CatalysisAtom neighbour = null;
    // it has to react with another atom
    randomNumber = StaticRandom.raw() * atom.getReactionProbability();
    sum = 0.0;
    for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
      sum += atom.getReactionEdge(j);
      if (sum > randomNumber) {
        neighbour = atom.getNeighbour(j);
        break;
      }
    }
    double probabilityChange = getLattice().extract(neighbour);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom

    probabilityChange = getLattice().extract(atom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    
    updateAdsorptionRate(atom);
    updateAdsorptionRate(neighbour);
    updateReactionRate(neighbour);
    updateReactionRate(atom);
    updateDesorptionRate(neighbour);
    updateDesorptionRate(atom);
    updateDiffusionRate(neighbour);
    updateDiffusionRate(atom);
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
      sum += diffusionSites.get(i).getDiffusionProbability();
      if (sum > randomNumber) {
        originAtom = diffusionSites.get(i);
        break;
      }
    }
    CatalysisAtom destinationAtom = null;
    // it has to react with another atom
    randomNumber = StaticRandom.raw() * originAtom.getDiffusionProbability();
    sum = 0.0;
    for (int j = 0; j < originAtom.getNumberOfNeighbours(); j++) {
      sum += originAtom.getDiffusionEdge(j);
      if (sum > randomNumber) {
        destinationAtom = originAtom.getNeighbour(j);
        break;
      }
    }
    destinationAtom.setType(originAtom.getType());
    double probabilityChange = getLattice().extract(originAtom);
    getList().addTotalProbability(-probabilityChange);
    
    getLattice().deposit(destinationAtom, false);
    
    destinationAtom.swapAttributes(originAtom);
    updateAdsorptionRate(originAtom);
    updateAdsorptionRate(destinationAtom);
    updateDesorptionRate(originAtom);
    updateDesorptionRate(destinationAtom);
    updateReactionRate(originAtom);
    updateReactionRate(destinationAtom);
    updateDiffusionRate(originAtom);
    updateDiffusionRate(destinationAtom);
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
        a.setAdsorptionProbability(adsorptionRateCOPerSite + 4 * adsorptionRateOPerSite); // there is no neighbour
        adsorptionSites.add(a);
        totalAdsorptionRate += a.getAdsorptionProbability();
      }
    }
    getList().setDepositionProbability(totalAdsorptionRate);
    getList().setDesorptionProbability(0);
  }
  
  private void initDesorptionOnly() {
    desorptionSites = new ArrayList<>();
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        a.setType((byte) StaticRandom.rawInteger(2));
        getLattice().deposit(a, false);
      }
    }

    totalDesorptionRate = 0;
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        if (a.getType() == CO) {
          desorptionSites.add(a);
          a.setDesorptionProbability(desorptionRateCOPerSite[a.getLatticeSite()]);
          totalDesorptionRate += a.getDesorptionProbability();
        } else { //O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
              double probability = desorptionRateOPerSite[index];
              a.addDesorptionProbability(probability, k);
            }
          }
          if (a.getDesorptionProbability() > 0) {
            desorptionSites.add(a);
          }
          totalDesorptionRate += a.getDesorptionProbability();
        }
      }
    }
    
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
              double probability = reactionRateCoO[index];
              a.addReactionProbability(probability, k);
            }
          }
        } else { // O
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == CO) {
              //               CO                   +         O
              int index = 2 * neighbour.getLatticeSite() + a.getLatticeSite();
              double probability = reactionRateCoO[index];
              a.addReactionProbability(probability, k);
            }
          }
        }
        if (a.getReactionProbability() > 0) {
          reactionSites.add(a);
        }
        totalReactionRate += a.getReactionProbability();
      }
    }
    
    getList().setDepositionProbability(0);
    getList().setDesorptionProbability(totalDesorptionRate);
    getList().setReactionProbability(totalReactionRate);
    getLattice().resetOccupied();
  }
 
  /**
   * Updates total adsorption probability.
   *
   * @param atom
   */
  private void updateAdsorptionRate(CatalysisAtom atom) {
    double previousAdsorptionRate = totalAdsorptionRate;
    recomputeAdsorptionProbability(atom);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      recomputeAdsorptionProbability(atom.getNeighbour(i));
    }
    if (totalAdsorptionRate / previousAdsorptionRate < 1e-1) {
      updateAdsorptionRateFromList();
    }
    getList().setDepositionProbability(totalAdsorptionRate);
  }
  
  private void updateDesorptionRate(CatalysisAtom atom) {
    double previousDesorptionRate = totalDesorptionRate;
    recomputeDesorptionProbability(atom);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      recomputeDesorptionProbability(atom.getNeighbour(i));
    }
    if (totalDesorptionRate / previousDesorptionRate < 1e-1) {
      updateDesorptionRateFromList();
    }
    getList().setDesorptionProbability(totalDesorptionRate);
  }
  
  /**
   * Updates reaction rate for atom (at its neighbourhood).
   * 
   * @param atom 
   */
  private void updateReactionRate(CatalysisAtom atom) {
    double previousReactionRate = totalReactionRate;
    recomputeReactionProbability(atom);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      recomputeReactionProbability(neighbour);
    }
    if (totalReactionRate / previousReactionRate < 1e-1) {
      updateReactionRateFromList();
    }
    getList().setReactionProbability(totalReactionRate);
  }
  
  private void updateDiffusionRate(CatalysisAtom atom) {
    double previousDiffusionRate = totalDiffusionRate;
    recomputeDiffusionProbability(atom);
    for (int i=0;i<atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      recomputeDiffusionProbability(neighbour);
    }
    if (totalDiffusionRate / previousDiffusionRate < 1e-1) {
      updateDiffusionRateFromList();
    }
    getList().setTotalProbability(totalDiffusionRate);
  }
            
  private void recomputeAdsorptionProbability(CatalysisAtom atom) {
    double oldAdsorptionRate = atom.getAdsorptionProbability();
    totalAdsorptionRate -= oldAdsorptionRate;
    if (atom.isOccupied()) {
      atom.setAdsorptionProbability(0);
    } else {
      atom.setAdsorptionProbability(adsorptionRateCOPerSite + (4 - atom.getOccupiedNeighbours()) * adsorptionRateOPerSite);
    }
    totalAdsorptionRate += atom.getAdsorptionProbability();
    if (atom.getAdsorptionProbability() == 0) {
      adsorptionSites.remove(atom);
    } else if (oldAdsorptionRate == 0) {
      adsorptionSites.add(atom);
    }
  }
  
  private void recomputeDesorptionProbability(CatalysisAtom atom) {
    totalDesorptionRate -= atom.getDesorptionProbability();
    desorptionSites.remove(atom);
    atom.setDesorptionProbability(0);
    if (!atom.isOccupied()) {
      return;
    }
    if (atom.getType() == CO) {
      atom.setDesorptionProbability(desorptionRateCOPerSite[atom.getLatticeSite()]);
      totalDesorptionRate += atom.getDesorptionProbability();
      desorptionSites.add(atom);
    } else { // O
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisAtom neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          double probability = getDesorptionProbability(atom, neighbour);
          atom.addDesorptionProbability(probability, i);
        }
      }
      if (atom.getDesorptionProbability() > 0) {
        totalDesorptionRate += atom.getDesorptionProbability();
        desorptionSites.add(atom);
      }
    }
  }
  
  private void recomputeReactionProbability(CatalysisAtom atom) {
    totalReactionRate -= atom.getReactionProbability();
    reactionSites.remove(atom);
    atom.setReactionProbability(0);
    if (!atom.isOccupied()) {
      return;
    }
    byte otherType = (byte) ((atom.getType() + 1) % 2);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      if (neighbour.isOccupied() && neighbour.getType() == otherType) {
        double probability = getReactionProbability(atom, neighbour);
        atom.addReactionProbability(probability, i);
      }
    }
    if (atom.getReactionProbability() > 0) {
      totalReactionRate += atom.getReactionProbability();
      reactionSites.add(atom);
    }
  }

  private void recomputeDiffusionProbability(CatalysisAtom atom) {
    totalDiffusionRate -= atom.getDiffusionProbability();
    diffusionSites.remove(atom);
    atom.setDiffusionProbability(0);
    if (!atom.isOccupied()) {
      return;
    }
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      if (!neighbour.isOccupied()) {
        double probability = getDiffusionProbability(atom, neighbour);
        atom.addDiffusionProbability(probability, i);
      }
    }
    if (atom.getDiffusionProbability() > 0) {
      totalDiffusionRate += atom.getDiffusionProbability();
      diffusionSites.add(atom);
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
      sum += adsorptionSites.get(i).getAdsorptionProbability();
    }
    totalAdsorptionRate = sum;
  }

  private void updateDesorptionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < desorptionSites.size(); i++) {
      sum += desorptionSites.get(i).getDesorptionProbability();
    }
    totalDesorptionRate = sum;
  }
  
  private void updateReactionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < reactionSites.size(); i++) {
      sum += reactionSites.get(i).getReactionProbability();
    }
    totalReactionRate = sum;
  }
  
  private void updateDiffusionRateFromList() {
    double sum = 0.0;
    for (int i = 0; i < diffusionSites.size(); i++) {
      sum += diffusionSites.get(i).getDiffusionProbability();
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
}
