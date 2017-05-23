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
  private double adsorptionRatePerSite; 
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
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private int[] numAtomsInSimulation;
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
    adsorptionRatePerSite = 0.0;
    totalDesorptionRate = 0.0;
    totalReactionRate = 0.0;

    simulatedSteps = 0;
    measureDiffusivity = parser.outputData();
    if (measureDiffusivity) {
      totalNumOfSteps = parser.getNumberOfSteps();
      numStepsEachData = 10;
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();

      numAtomsInSimulation = new int[2];
    }
    adsorptionSites = new ArrayList<>();
    desorptionSites = new ArrayList<>();
    reactionSites = new ArrayList<>();
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
    if (doAdsorption) {
      adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
      adsorptionRatePerSite = rates.getTotalAdsorptionRate();
      adsorptionRateOPerSite = adsorptionRatePerSite - adsorptionRateCOPerSite;
    }
    if (doDesorption) {
      desorptionRateCOPerSite = rates.getDesorptionRates(CO);
      desorptionRateOPerSite = rates.getDesorptionRates(O);
    }
    if (doReaction) {
      reactionRateCoO = rates.getReactionRates();
    }
  }

  public double[][] getOutputAdsorptionData() {
    double[][] adsorptionSimulationData = new double[adsorptionData.size()][5];
    for (int i = 0; i < adsorptionData.size(); i++) {
      adsorptionSimulationData[i] = adsorptionData.get(i).getCatalysisData();
    }
    
    return adsorptionSimulationData;
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
    CatalysisAtom originAtom = (CatalysisAtom) getList().nextEvent();
    CatalysisAtom destinationAtom = null;
    if (originAtom == null) { // adsorption
      destinationAtom = depositNewAtom();
    } else if (originAtom.isRemoved()) { // desorption
      desorpAtom(); 
    } else if (!originAtom.isOnList()) { // reaction
      reactAtom();
    } else {
      do { // diffusion
        destinationAtom = chooseRandomHop(originAtom);
      } while (!diffuseAtom(originAtom, destinationAtom));
    }
    simulatedSteps++;
    if (measureDiffusivity && (simulatedSteps + 1) % numStepsEachData == 0) {
      if (destinationAtom != null) {
        int step = (int) (simulatedSteps + 1) / numStepsEachData;
        simulationData.add(new CatalysisData(destinationAtom.getiHexa(), destinationAtom.getjHexa(), getTime()));
        
        adsorptionData.add(new CatalysisData(getCoverage(), getTime(),
                numAtomsInSimulation[CO] / (getLattice().getCartSizeX() * getLattice().getCartSizeY()),
                numAtomsInSimulation[O] / (getLattice().getCartSizeX() * getLattice().getCartSizeY()), currentAdsorptionP));
      }
    }
    return simulatedSteps + 1 == totalNumOfSteps;
  }

  private void printSimulationData() {
    for (int i = 0; i < simulationData.size(); i++) {
      double[] data = simulationData.get(i).getAdsorptionData();
      System.out.println(i + ": " + data[0] + "; " + data[1]);
    }
  }

  @Override
  public int simulate() {
    int returnValue = 0;

    if (measureDiffusivity) {
      numAtomsInSimulation[O] = 0;
      numAtomsInSimulation[CO] = 0;
    }
    while (getLattice().getCoverage() < maxCoverage) {
      getList().getDeltaTime(true);
      if (performSimulationStep()) {
        break;
      }
    }
    System.out.println("coverage_CO: " + numAtomsInSimulation[CO] / (getLattice().getCartSizeX() * getLattice().getCartSizeY())
            + " - coverage_O: " + numAtomsInSimulation[O] / (getLattice().getCartSizeX() * getLattice().getCartSizeY()));

    System.out.println("coverage: " + getLattice().getCoverage() + " - time: " + getTime());
    System.out.println("k_i(CO): " + adsorptionRateCOPerSite + " k_i(O): " + (adsorptionRatePerSite - adsorptionRateCOPerSite));
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

      numAtomsInSimulation = new int[2];
    }
  }
  
  private boolean depositAtom(CatalysisAtom atom, byte type) {
    if (depositAtom(atom)) {
      atom.setType(type);
      return true;
    }
    return false;
  }    
  
  private boolean depositAtom(CatalysisAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }

    getLattice().deposit(atom, false);
    getLattice().addOccupied();
    getModifiedBuffer().updateAtoms(getList());

    return true;
  }

  /**
   * Selects the next step randomly. If there is not accelerator, an neighbour atom of originAtom is
   * chosen.
   *
   * @param originAtom atom that has to be moved.
   * @return destinationAtom.
   */
  private CatalysisAtom chooseRandomHop(CatalysisAtom originAtom) {
    return (CatalysisAtom) originAtom.chooseRandomHop();
  }

  /**
   * Moves an atom from origin to destination.
   *
   * @param originAtom origin atom.
   * @param destinationAtom destination atom.
   * @return true if atom has moved, false otherwise.
   */
  private boolean diffuseAtom(CatalysisAtom originAtom, CatalysisAtom destinationAtom) {
    //Si no es elegible, sea el destino el mismo o diferente no se puede difundir.
    if (!originAtom.isEligible()) {
      return false;
    }

    // if the destination atom is occupied do not diffuse (even if it is itself)
    if (destinationAtom.isOccupied()) {
      return false;
    }
    destinationAtom.setType(originAtom.getType());

    double probabilityChange = getLattice().extract(originAtom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom

    getLattice().deposit(destinationAtom, false);
    destinationAtom.swapAttributes(originAtom);
    getModifiedBuffer().updateAtoms(getList());
    updateAdsorptionRate(originAtom, true);
    updateAdsorptionRate(destinationAtom, false);
    updateDesorptionRateDiffusion(originAtom, destinationAtom);
    updateReactionRate(originAtom);
    updateReactionRate(destinationAtom);

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
      double randomNumber = StaticRandom.raw() * adsorptionRatePerSite;
      if (randomNumber < adsorptionRateCOPerSite) {
        atomType = CO;
      } else {
        atomType = O;
      }
      
      randomNumber = StaticRandom.raw() * totalAdsorptionRate;
      
      double sum = 0.0;
      int i;
      for (i = 0; i < adsorptionSites.size(); i++) {
        sum += adsorptionSites.get(i).getAdsorptionProbability();
        if (sum > randomNumber) {
          destinationAtom = adsorptionSites.get(i);
          if (adsorptionSites.get(i).getAdsorptionProbability() == adsorptionRateCOPerSite){
            // it has to be CO, because this site didn't have adsorption rate for O.
            atomType = CO;
          }
          break;
        }
      }

      if (destinationAtom == null || destinationAtom.getAdsorptionProbability() == 0) {
        boolean isThereAnAtom = destinationAtom == null;
        System.out.println("Something is wrong " + isThereAnAtom);
      }
      destinationAtom.setType(atomType);
      deposited = depositAtom(destinationAtom);
      if (atomType == O) { // it has to deposit two O (dissociation of O2 -> 2O)
        boolean depositedNeighbour;
        random = StaticRandom.rawInteger(4);
        do {
          neighbourAtom = destinationAtom.getNeighbour(random);
          random = (random + 1) % 4;
          depositedNeighbour = depositAtom(neighbourAtom, O);
        } while (!depositedNeighbour);
      }
    } while (!deposited);
    numAtomsInSimulation[atomType]++;
    
    updateAdsorptionRate(destinationAtom, false);
    updateDesorptionRateAdsorption(destinationAtom);
    updateReactionRate(destinationAtom);
    if (neighbourAtom != null) {
      updateAdsorptionRate(neighbourAtom, false);
      updateDesorptionRateAdsorption(neighbourAtom);
      updateReactionRate(neighbourAtom);
      neighbourAtom.setDepositionTime(getTime());
      //neighbourAtom.setDepositionPosition(-1);
      numAtomsInSimulation[atomType]++;
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
      neighbour.setOccupied(false);
      numAtomsInSimulation[neighbour.getType()]--;
      getLattice().subtractOccupied();
      double probabilityChange = getLattice().extract(neighbour);
      getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    }

    atom.setOccupied(false);
    numAtomsInSimulation[atom.getType()]--;
    getLattice().subtractOccupied();
    double probabilityChange = getLattice().extract(atom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    
    updateAdsorptionRate(atom, true);
   
    if (neighbour != null) {
      updateAdsorptionRate(neighbour, true);
      updateDesorptionRateDesorption(neighbour);
      updateReactionRate(neighbour);
    }
    updateDesorptionRateDesorption(atom);
    updateReactionRate(atom);
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
    neighbour.setOccupied(false);
    numAtomsInSimulation[neighbour.getType()]--;
    getLattice().subtractOccupied();
    double probabilityChange = getLattice().extract(neighbour);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom

    atom.setOccupied(false);
    numAtomsInSimulation[atom.getType()]--;
    getLattice().subtractOccupied();
    probabilityChange = getLattice().extract(atom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom
    
    updateAdsorptionRate(atom, true);
    updateAdsorptionRate(neighbour, true);
    updateReactionRate(neighbour);
    updateReactionRate(atom);
    updateDesorptionRateDesorption(neighbour);
    updateDesorptionRateDesorption(atom);
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
        numAtomsInSimulation[a.getType()]++;
        getLattice().deposit(a, false);
        //getLattice().addOccupied();
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
        if (a.getType() == O) {
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == CO) {
              //               CO                   +         O
              int index = 2 * neighbour.getLatticeSite() + a.getLatticeSite();
              double probability = reactionRateCoO[index];
              a.addReactionProbability(probability, k);
            }
          }
        } else { // CO
          for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
            CatalysisAtom neighbour = a.getNeighbour(k);
            if (neighbour.getType() == O) {
              //               CO                   +         O
              int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
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
  }
 
  /**
   * Updates total adsorption probability.
   *
   * @param atom
   * @param add wheter to add to the adsorption sites list or not.
   */
  private void updateAdsorptionRate(CatalysisAtom atom, boolean add) {
    double previousAdsorptionRate = totalAdsorptionRate;
    recomputeAdsorptionProbability(atom);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      recomputeAdsorptionProbability(atom.getNeighbour(i));
    }
    if (totalAdsorptionRate / previousAdsorptionRate < 1e-1) {
      updateAdsorptionRateFromList();
    }
    getList().setDepositionProbability(totalAdsorptionRate);
    if (add) {
      adsorptionSites.add(atom);
    }
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
  
  private void updateDesorptionRateAdsorption(CatalysisAtom destinationAtom) {
    desorptionSites.add(destinationAtom); // CO is always added, and O2 deposition ensures that O has (at least) one neighbour
    if (destinationAtom.getType() == CO) {
      destinationAtom.setDesorptionProbability(desorptionRateCOPerSite[destinationAtom.getLatticeSite()]);
      totalDesorptionRate += destinationAtom.getDesorptionProbability();
    } else { // O
      // current atom
      ArrayList<CatalysisAtom> neighbours = new ArrayList<>();
      for (int i = 0; i < destinationAtom.getNumberOfNeighbours(); i++) {
        CatalysisAtom neighbour = destinationAtom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          int index = 2 * destinationAtom.getLatticeSite() + neighbour.getLatticeSite();
          double probability = desorptionRateOPerSite[index];
          destinationAtom.addDesorptionProbability(probability, i);
          //neighbour.addDesorptionProbability(probability);
          neighbours.add(neighbour);
        }
      }
      totalDesorptionRate += destinationAtom.getDesorptionProbability();

      CatalysisAtom atom;
      ArrayList<CatalysisAtom> secondNeighbours = new ArrayList<>();
      // first neighbours
      for (int i = 0; i < neighbours.size(); i++) {
        atom = neighbours.get(i);
        totalDesorptionRate -= atom.getDesorptionProbability();
        desorptionSites.remove(atom);
        atom.setDesorptionProbability(0);
        for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
          CatalysisAtom neighbour = atom.getNeighbour(j);
          if (neighbour.isOccupied() && neighbour.getType() == O) {
            int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
            double probability = desorptionRateOPerSite[index];
            atom.addDesorptionProbability(probability, j);
            if (!neighbour.equals(destinationAtom)) {
              //desorptionSites.add(neighbour);
              secondNeighbours.add(neighbour);
            }
          }
        }
        totalDesorptionRate += atom.getDesorptionProbability();
        desorptionSites.add(atom);
      }
      // second neighbours
      for (int i = 0; i < secondNeighbours.size(); i++) {
        atom = secondNeighbours.get(i);
        totalDesorptionRate -= atom.getDesorptionProbability();
        desorptionSites.remove(atom);
        atom.setDesorptionProbability(0);
        for (int j = 0; j < atom.getNumberOfNeighbours(); j++) {
          CatalysisAtom neighbour = atom.getNeighbour(j);
          if (neighbour.isOccupied() && neighbour.getType() == O) {
            int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
            double probability = desorptionRateOPerSite[index];
            atom.addDesorptionProbability(probability, j);
          }
        }
        totalDesorptionRate += atom.getDesorptionProbability();
        desorptionSites.add(atom);
      }
    }
  }

  private void updateDesorptionRateDiffusion(CatalysisAtom originAtom, CatalysisAtom destinationAtom) {
    if (originAtom.getType() == CO) {
      desorptionSites.remove(originAtom);
      totalDesorptionRate -= originAtom.getDesorptionProbability();
      originAtom.setDesorptionProbability(0);
      desorptionSites.add(destinationAtom);
      destinationAtom.setDesorptionProbability(desorptionRateCOPerSite[destinationAtom.getLatticeSite()]);
      totalDesorptionRate = destinationAtom.getDesorptionProbability();
    }
  }

  private void updateDesorptionRateDesorption(CatalysisAtom atom) {
    double previousDesorptionRate = totalDesorptionRate;
    totalDesorptionRate -= atom.getDesorptionProbability();
    desorptionSites.remove(atom);
    atom.setDesorptionProbability(0);
    if (atom.getType() == O) { // update neighbours if necessary
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        CatalysisAtom neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == O) {
          int origPos = (i + 2) % 4;
          int index = 2 * atom.getLatticeSite() + neighbour.getLatticeSite();
          double probability = desorptionRateOPerSite[index];
          neighbour.addDesorptionProbability(-probability, origPos);
          totalDesorptionRate -= probability;
          if (neighbour.getDesorptionProbability() == 0) {
            desorptionSites.remove(neighbour); // O without O neighbour
          }
        }
      }
    }
    if (totalDesorptionRate / previousDesorptionRate < 1e-1) {
      updateDesorptionRateFromList();
    }

    getList().setDesorptionProbability(totalDesorptionRate);
  }
  
  private void recomputeAdsorptionProbability(CatalysisAtom atom) {
    totalAdsorptionRate -= atom.getAdsorptionProbability();
    if (atom.isOccupied()) {
      atom.setAdsorptionProbability(0);
    } else {
      atom.setAdsorptionProbability(adsorptionRateCOPerSite + (4 - atom.getOccupiedNeighbours()) * adsorptionRateOPerSite);
    }
    totalAdsorptionRate += atom.getAdsorptionProbability();
    if (atom.getAdsorptionProbability() == 0) {
      adsorptionSites.remove(atom);
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
  
  private float getCurrentP() {
    float numOccupiedNeighbours = 0f;
    for (int i = 0; i < adsorptionSites.size(); i++) {
      numOccupiedNeighbours += (1 - (float) adsorptionSites.get(i).getOccupiedNeighbours() / 4);
    }
    return (float) numOccupiedNeighbours / adsorptionSites.size();
  }
}
