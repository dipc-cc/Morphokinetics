/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import static java.lang.String.format;
import java.util.ListIterator;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;

/**
 *
 * @author Karmele Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisKmc extends AbstractGrowthKmc {

  private long simulatedSteps;
  private int simulationNumber;
  private final int totalNumOfSteps;
  private final int numStepsEachData;
  private final double[][][] simulationData;

  private final int numberOfSimulations;

  public CatalysisKmc(Parser parser) {
    super(parser);
    numberOfSimulations = parser.getNumberOfSimulations();
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);

    simulatedSteps = 0;
    simulationNumber = -1;
    totalNumOfSteps = 10000;
    numStepsEachData = 100;
    simulationData = new double[numberOfSimulations][totalNumOfSteps / numStepsEachData + 1][3];

    for (int i = 0; i < numberOfSimulations; i++) {
      for (int j = 0; j < totalNumOfSteps / numStepsEachData + 1; j++) {
        simulationData[i][j][0] = Double.NEGATIVE_INFINITY;
        simulationData[i][j][1] = Double.NEGATIVE_INFINITY;
        simulationData[i][j][2] = Double.NEGATIVE_INFINITY;
      }
    }
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
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
    boolean shouldEnd = false;
    if (originAtom == null) {
      //destinationAtom = depositNewAtom();
      destinationAtom = null;
    } else {
      do {
        destinationAtom = chooseRandomHop(originAtom);
      } while (!diffuseAtom(originAtom, destinationAtom));
    }
    simulatedSteps++;
    if ((simulatedSteps + 1) % numStepsEachData == 0) {
      if (destinationAtom != null) {
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][0] = destinationAtom.getiHexa();
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][1] = destinationAtom.getjHexa();
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][2] = getTime();

        if (destinationAtom.getiHexa() > getLattice().getHexaSizeI() || destinationAtom.getiHexa() < 0 || destinationAtom.getjHexa() > getLattice().getHexaSizeJ() || destinationAtom.getjHexa() < 0) {
          shouldEnd = true;
        }
      } else {
        System.out.println("atomoa hutsik dago");
      }
    }
    if (simulatedSteps + 1 == totalNumOfSteps || shouldEnd) {
      //printSimulationData(simulationNumber);
      if (simulationNumber == numberOfSimulations - 1) {
        //grabar fichero de datos
        String fileName = format("%skarmele%03d.txt", "results/", 0);
        writeSimulationDataText(simulationData, fileName);
      }
      return true;
    } else {
      return false;
    }
  }

  private void printSimulationData(int numSim) {
    for (int i = 0; i < simulationData[numSim].length; i++) {
      System.out.println(i + ": " + simulationData[numSim][i][0] + "; " + simulationData[numSim][i][1]);
    }
  }

  private void writeSimulationDataText(double[][][] data, String fileName) {
    // create file descriptor. It will be automatically closed.
    try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
      // for each byte in the buffer
      int[] dataAmount = new int[totalNumOfSteps / numStepsEachData + 1];

      for (int i = 0; i < dataAmount.length; i++) {
        dataAmount[i] = 0;
        double R2 = 0;
        double t = 0;
        if (i > 0) {
          for (int j = 0; j < numberOfSimulations; j++) {
            if (data[j][i][0] > Double.NEGATIVE_INFINITY) {
              R2 += Math.pow(data[j][i][0] - data[j][0][0], 2) + Math.pow(data[j][i][1] - data[j][0][1], 2);
              t += data[j][i][2];
              dataAmount[i] = dataAmount[i] + 1;
            }
          }
          System.out.println(i + " - R2: " + R2 + " - numData: " + dataAmount[i]);
          R2 = R2 / dataAmount[i];
          t = t / dataAmount[i];
        }
        out.write((i + ";" + t + ";" + R2 + "\n").replace('.', ','));
      }
    } catch (Exception e) {
      // if any I/O error occurs
      e.printStackTrace();
    }
  }
  
  @Override
  public int simulate() {
    int returnValue = 0;
    while (true) {
      if (performSimulationStep()) {
        break;
      }
    }
    
    return returnValue;
  }

  @Override
  public void depositSeed() {
    getList().setDepositionProbability(0); // this line has to be changed with the proper deposition rates.
    getLattice().resetOccupied();
    simulatedSteps = 0;
    simulationNumber++;
    depositNewAtom();
  }

  @Override
  public void reset() {
    ListIterator iter = getList().getIterator();
    while (iter.hasNext()){
      CatalysisAtom atom = (CatalysisAtom) iter.next();
      atom.clear();
    }
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
    CatalysisAtom destinationAtom;
    int ucIndex = 0;

    do {
      int random = (getLattice().getHexaSizeI() * (getLattice().getHexaSizeJ() + 1) / 2)+1;//StaticRandom.rawInteger(getLattice().size() * getLattice().getUnitCellSize());
      ucIndex = Math.floorDiv(random, getLattice().getUnitCellSize());
      int atomIndex = random % getLattice().getUnitCellSize();
      destinationAtom = (CatalysisAtom) getLattice().getUc(ucIndex).getAtom(atomIndex);
      destinationAtom.setType(CatalysisAtom.O);
    } while (!depositAtom(destinationAtom));

    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));

    simulationData[simulationNumber][0][0] = destinationAtom.getiHexa();
    simulationData[simulationNumber][0][1] = destinationAtom.getjHexa();
    simulationData[simulationNumber][0][2] = getTime();
    return destinationAtom;
  }
}
