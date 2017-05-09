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
  private double adsorptionRatePerSite; 
  private double adsorptionRateCOPerSite;
  private double adsorptionRateOPerSite;
  private double totalAdsorptionRate;
  private ArrayList<CatalysisAtom> adsorptionSites;
  private double desorptionRatePerSite; 
  private double[] desorptionRateCOPerSite;
  private double desorptionRateOPerSite;
  private double totalDesorptionRate;
  private ArrayList<CatalysisAtom> desorptionSites;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private int[] numAtomsInSimulation;
  private final boolean doDiffusion;
  private final boolean doAdsorption;
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    maxCoverage =(float) parser.getCoverage() / 100;
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    totalAdsorptionRate = 0.0;
    adsorptionRatePerSite = 0.0;
    totalDesorptionRate = 0.0;
    desorptionRatePerSite = 0.0;

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
    doDiffusion = parser.doCatalysisDiffusion();
    doAdsorption = parser.doCatalysisAdsorption();
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  public void setRates(CatalysisRates rates) {
    if (doAdsorption) {
      adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
      adsorptionRatePerSite = rates.getTotalAdsorptionRate();
      adsorptionRateOPerSite = adsorptionRatePerSite - adsorptionRateCOPerSite;
      desorptionRateCOPerSite = rates.getDesorptionRate(CO);
    }
    desorptionRateOPerSite = rates.getDesorptionRate(O);
  }

  public double[][] getOutputAdsorptionData() {
    double[][] adsorptionSimulationData = new double[adsorptionData.size()][4];
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
    CatalysisAtom originAtom = (CatalysisAtom) getList().nextEvent();
    CatalysisAtom destinationAtom = null;
    if (originAtom == null) { // adsorption
      destinationAtom = depositNewAtom();
      if (destinationAtom == null || getList().getGlobalProbability() == 0) {
        return true;
      }
    } else if (originAtom.isRemoved()) { // desorption
      desorpAtom();
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
                numAtomsInSimulation[O] / (getLattice().getCartSizeX() * getLattice().getCartSizeY())));
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
    getLattice().resetOccupied();
    if (doAdsorption) {
      initDepositionProbability();
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
    updateAdsorptionRateDiffusion(originAtom, destinationAtom);
    updateDesorptionRateDiffusion(originAtom, destinationAtom);

    return true;
  }

  private CatalysisAtom depositNewAtom() {
    CatalysisAtom destinationAtom = null;
    int ucIndex = 0;
    byte atomType;
      
    boolean deposited;
    CatalysisAtom neighbourAtom = null;
    int random;
    
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
    
    updateAdsorptionRateDeposition(destinationAtom);
    updateDesorptionRateDeposition(destinationAtom);
    if (neighbourAtom != null) {
      updateAdsorptionRateDeposition(neighbourAtom);
      neighbourAtom.setDepositionTime(getTime());
      //neighbourAtom.setDepositionPosition(-1);
      numAtomsInSimulation[atomType]++;
    }
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    getList().setDepositionProbability(totalAdsorptionRate);
    getList().setDesorptionProbability(totalDesorptionRate);
    
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
    atom.setOccupied(false);
    getLattice().subtractOccupied();
    totalDesorptionRate -= atom.getDesorptionProbability();
    desorptionSites.remove(atom);
    atom.setDesorptionProbability(0);
    
    if (atom.getOccupiedNeighbours() == 4) {
      atom.setAdsorptionProbability(adsorptionRateCOPerSite);
    } else {
      atom.setAdsorptionProbability(adsorptionRatePerSite);
    }
    totalAdsorptionRate += atom.getAdsorptionProbability();
    adsorptionSites.add(atom);
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initDepositionProbability() {
    totalAdsorptionRate = 0.0;
    adsorptionSites = new ArrayList<>();
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        a.setAdsorptionProbability(adsorptionRatePerSite);
        adsorptionSites.add(a);
        totalAdsorptionRate += adsorptionRatePerSite;
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
        a.setType(O);
        getLattice().deposit(a, false);
        for (int k = 0; k < a.getNumberOfNeighbours(); k++) {
          CatalysisAtom neighbour = a.getNeighbour(k);
          int index = 2 * a.getLatticeSite() + neighbour.getLatticeSite();
          double probability = desorptionRateOPerSite[index];
          a.addDesorptionProbability(probability, k);
        }
        //a.setDesorptionProbability(xxx);
        desorptionSites.add(a);
        totalDesorptionRate += a.getDesorptionProbability();
      }
    }
    getList().setDepositionProbability(0);
    getList().setDesorptionProbability(totalDesorptionRate);
  }
    
  
  /**
   * Updates total adsorption probability. 
   *
   * @param atom just moved or deposited atom.
   */
  private void updateAdsorptionRateDeposition(CatalysisAtom atom) {
    if (adsorptionSites.remove(atom)) {
      totalAdsorptionRate -= atom.getAdsorptionProbability();
      if (totalAdsorptionRate / adsorptionRatePerSite < 1e-10) {
        totalAdsorptionRate = 0;
      }
      atom.setAdsorptionProbability(0);
    }
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 4) {
        // can not adsorb O2
        totalAdsorptionRate -= neighbour.getAdsorptionProbability();
        neighbour.setAdsorptionProbability(adsorptionRateCOPerSite);
        if (neighbour.getAdsorptionProbability() == 0) {
          adsorptionSites.remove(neighbour);
        }
        totalAdsorptionRate += adsorptionRateCOPerSite;
      }
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() < 4) {
        neighbour.setAdsorptionProbability(adsorptionRatePerSite);
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
  
  private void updateAdsorptionRateDiffusion(CatalysisAtom originAtom, CatalysisAtom destinationAtom) {
    // destination atom
    totalAdsorptionRate -= destinationAtom.getAdsorptionProbability();
    destinationAtom.setAdsorptionProbability(0);
    adsorptionSites.remove(destinationAtom);
    
    // origin atom
    if (originAtom.getOccupiedNeighbours() < 4) {
      originAtom.setAdsorptionProbability(adsorptionRatePerSite);
      adsorptionSites.add(originAtom);
    } else {
      originAtom.setAdsorptionProbability(adsorptionRateCOPerSite);
      if (adsorptionRateCOPerSite == 0) {
        adsorptionSites.remove(originAtom);
      } else {
        adsorptionSites.add(originAtom);
      }
    }
    totalAdsorptionRate += originAtom.getAdsorptionProbability(); // adsorption probability was 0 (always).

    // neighbours of origin atom
    for (int i = 0; i < originAtom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = originAtom.getNeighbour(i);
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 3) {
        if (neighbour.getAdsorptionProbability() == adsorptionRateCOPerSite) {
          totalAdsorptionRate += adsorptionRateOPerSite;
          neighbour.setAdsorptionProbability(adsorptionRatePerSite);
          if (!adsorptionSites.contains(neighbour)) {
            adsorptionSites.add(neighbour);
          }
        }
      }
    }
    
    // neighbours of destination atom
    for (int i = 0; i < destinationAtom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = destinationAtom.getNeighbour(i);
      if (neighbour.equals(originAtom)){
        continue;
      }
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 3) {
        if (neighbour.getAdsorptionProbability() == adsorptionRateCOPerSite) {
          neighbour.setAdsorptionProbability(adsorptionRatePerSite);
          if (!adsorptionSites.contains(neighbour)) {
            adsorptionSites.add(neighbour);
            totalAdsorptionRate += adsorptionRatePerSite; ///-adsorptionRateCOPerSite
          }
        }
      }     
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 4) {
        if (neighbour.getAdsorptionProbability() == adsorptionRatePerSite) {
          totalAdsorptionRate -= adsorptionRateOPerSite;
          neighbour.setAdsorptionProbability(adsorptionRateCOPerSite);
        }
      }
    }
  }
}
