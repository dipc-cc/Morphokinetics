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
import kineticMonteCarlo.unitCell.CatalysisUc;
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
  private double totalAdsorptionRate; 
  private double adsorptionRateCO;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  private int[] numAtomsInSimulation;
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    maxCoverage =(float) parser.getCoverage() / 100;
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    totalAdsorptionRate = 0.0;

    simulatedSteps = 0;
    measureDiffusivity = parser.outputData();
    if (measureDiffusivity) {
      totalNumOfSteps = parser.getNumberOfSteps();
      numStepsEachData = 10;
      simulationData = new ArrayList<>();
      adsorptionData = new ArrayList<>();

      numAtomsInSimulation = new int[2];
    }
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  public void setAdsorptionRates(CatalysisRates rates) {
    adsorptionRateCO = rates.getAdsorptionRate(CO);
    totalAdsorptionRate = rates.getTotalAdsorptionRate();
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
    //commented this line for future combinig of adsorption and diffusion
    //if (originAtom == null) {
    if (true) {
      destinationAtom = depositNewAtom();
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
                numAtomsInSimulation[CO]/(getLattice().getCartSizeX()*getLattice().getCartSizeY()), 
                numAtomsInSimulation[O]/(getLattice().getCartSizeX()*getLattice().getCartSizeY())));
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
      if (performSimulationStep()) {
        break;
      }
    }
    System.out.println("coverage_CO: " + numAtomsInSimulation[CO] / (getLattice().getCartSizeX() * getLattice().getCartSizeY())
            + " - coverage_O: " + numAtomsInSimulation[O] / (getLattice().getCartSizeX() * getLattice().getCartSizeY()));

    System.out.println("coverage: " + getLattice().getCoverage() + " - time: " + getTime());
    System.out.println("k_i(CO): " + adsorptionRateCO + " k_i(O): " + (totalAdsorptionRate - adsorptionRateCO));
    return returnValue;
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    getList().setDepositionProbability(totalAdsorptionRate * (1 - getCoverage()));
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
    destinationAtom.setDepositionTime(originAtom.getDepositionTime());
    destinationAtom.setDepositionPosition(originAtom.getDepositionPosition());
    destinationAtom.setHops(originAtom.getHops() + 1);
    originAtom.setDepositionTime(0);
    originAtom.setDepositionPosition(null);
    originAtom.setHops(0);
    getModifiedBuffer().updateAtoms(getList());

    return true;
  }

  private CatalysisAtom depositNewAtom() {
    CatalysisAtom destinationAtom = null;
    int ucIndex = 0;
    byte atomType;
      
    do {
      double randomNumber = StaticRandom.raw() * totalAdsorptionRate;
      if (randomNumber < adsorptionRateCO) {
        atomType = CO;
      } else {
        atomType = O;
      }
      int random = StaticRandom.rawInteger(getLattice().size() * getLattice().getUnitCellSize());
      ucIndex = Math.floorDiv(random, getLattice().getUnitCellSize());
      int atomIndex = random % getLattice().getUnitCellSize();
      destinationAtom = (CatalysisAtom) getLattice().getUc(ucIndex).getAtom(atomIndex);

      destinationAtom.setType(atomType);
    } while (!depositAtom(destinationAtom));
    
    numAtomsInSimulation[atomType]++;
    
    getList().setDepositionProbability(totalAdsorptionRate * (1-getCoverage()));
    //System.out.println(destinationAtom.getType()+" --- "+getTime());
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    return destinationAtom;
    
  }
}
