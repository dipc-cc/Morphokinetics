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
  private ArrayList<CatalysisAtom> adsorptionRateSites;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private int[] numAtomsInSimulation;
  private final boolean doDiffusion;
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    maxCoverage =(float) parser.getCoverage() / 100;
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    totalAdsorptionRate = 0.0;
    adsorptionRatePerSite = 0.0;

    simulatedSteps = 0;
    measureDiffusivity = parser.outputData();
    if (measureDiffusivity) {
      totalNumOfSteps = parser.getNumberOfSteps();
      numStepsEachData = 10;
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();

      numAtomsInSimulation = new int[2];
    }
    adsorptionRateSites = new ArrayList<>();
    doDiffusion = parser.doCatalysisDiffusion();
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  public void setAdsorptionRates(CatalysisRates rates) {
    adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
    adsorptionRatePerSite = rates.getTotalAdsorptionRate();
    adsorptionRateOPerSite = adsorptionRatePerSite - adsorptionRateCOPerSite;
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
    CatalysisAtom destinationAtom;
    if (!doDiffusion || originAtom == null) {
      destinationAtom = depositNewAtom();
      if (destinationAtom == null || getList().getGlobalProbability() == 0) {
        return true;
      }
    } else {
      do {
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
    initDepositionProbability();
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
      if (adsorptionRateSites.isEmpty()) {
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
      for (i = 0; i < adsorptionRateSites.size(); i++) {
        sum += adsorptionRateSites.get(i).getAdsorptionProbability();
        if (sum > randomNumber) {
          destinationAtom = adsorptionRateSites.get(i);
          if (adsorptionRateSites.get(i).getAdsorptionProbability() == adsorptionRateCOPerSite){
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
    if (neighbourAtom != null) {
      updateAdsorptionRateDeposition(neighbourAtom);
    }
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    getList().setDepositionProbability(totalAdsorptionRate);
    
    return destinationAtom;
  }

  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  private void initDepositionProbability() {
    totalAdsorptionRate = 0.0;
    adsorptionRateSites = new ArrayList<>();
    for (int i = 0; i < getLattice().size(); i++) {
      AbstractGrowthUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        a.setAdsorptionProbability(adsorptionRatePerSite);
        adsorptionRateSites.add(a);
        totalAdsorptionRate += adsorptionRatePerSite;
      }
    }
    getList().setDepositionProbability(totalAdsorptionRate);
  }
  
  /**
   * Updates total adsorption probability. 
   *
   * @param atom just moved or deposited atom.
   */
  private void updateAdsorptionRateDeposition(CatalysisAtom atom) {
    if (adsorptionRateSites.remove(atom)) {
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
          adsorptionRateSites.remove(neighbour);
        }
        totalAdsorptionRate += adsorptionRateCOPerSite;
      }
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() < 4) {
        neighbour.setAdsorptionProbability(adsorptionRatePerSite);
      }
    }
  }
  
  private void updateAdsorptionRateDiffusion(CatalysisAtom originAtom, CatalysisAtom destinationAtom) {
    totalAdsorptionRate -= destinationAtom.getAdsorptionProbability();
    destinationAtom.setAdsorptionProbability(0);
    adsorptionRateSites.remove(destinationAtom);
    if (originAtom.getOccupiedNeighbours() < 4) {
      originAtom.setAdsorptionProbability(adsorptionRatePerSite);
      adsorptionRateSites.add(originAtom);
    } else {
      totalAdsorptionRate -= originAtom.getAdsorptionProbability();
      adsorptionRateSites.remove(originAtom);
      originAtom.setAdsorptionProbability(adsorptionRateCOPerSite);
    }
    totalAdsorptionRate += originAtom.getAdsorptionProbability();

    for (int i = 0; i < originAtom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = originAtom.getNeighbour(i);
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 3) {
        if (neighbour.getAdsorptionProbability() == adsorptionRateCOPerSite) {
          totalAdsorptionRate += adsorptionRateOPerSite;
          neighbour.setAdsorptionProbability(adsorptionRatePerSite);
          if (!adsorptionRateSites.contains(neighbour)) {
            adsorptionRateSites.add(neighbour);
          }
        }
      }
    }
    
    for (int i = 0; i < destinationAtom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = destinationAtom.getNeighbour(i);
      if (neighbour.equals(originAtom)){
        continue;
      }
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 3) {
        if (neighbour.getAdsorptionProbability() == adsorptionRateCOPerSite) {
          neighbour.setAdsorptionProbability(adsorptionRatePerSite);
          if (!adsorptionRateSites.contains(neighbour)) {
            adsorptionRateSites.add(neighbour);
            totalAdsorptionRate += adsorptionRatePerSite; ///-adsorptionRateCOPerSite
          }
        }
      }     
      if (!neighbour.isOccupied() && neighbour.getOccupiedNeighbours() == 4) {
        if (neighbour.getAdsorptionProbability() == adsorptionRateOPerSite) {
          totalAdsorptionRate -= adsorptionRateOPerSite;
          neighbour.setAdsorptionProbability(adsorptionRateCOPerSite);
          adsorptionRateSites.remove(neighbour);
        }
      }
    }
  }
}
