/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.Restart;
import java.util.ListIterator;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;
import static java.lang.String.format;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;

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
  private final Restart restart;
  private double totalAdsorptionRate; 
  private double adsorptionRateCO;
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage; 
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    numberOfSimulations = parser.getNumberOfSimulations();
    maxCoverage =(float) parser.getCoverage() / 100;
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    totalAdsorptionRate = 0.0;

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
    restart = new Restart("results");
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  public void setAdsorptionRates(CatalysisRates rates) {
    totalAdsorptionRate = rates.getTotalAdsorptionRate();
    adsorptionRateCO = rates.getAdsorptionRate(CO);
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
    if ((simulatedSteps + 1) % numStepsEachData == 0) {
      if (destinationAtom != null) {
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][0] = destinationAtom.getiHexa();
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][1] = destinationAtom.getjHexa();
        simulationData[simulationNumber][(int) (simulatedSteps + 1) / numStepsEachData][2] = getTime();
      }
    }
    if (simulatedSteps + 1 == totalNumOfSteps) {
      //printSimulationData(simulationNumber);
      if (simulationNumber == numberOfSimulations - 1) {
        // Save to a file
        String fileName = format("karmele%03d.txt", 0);
        restart.writeCatalysisDataText(simulationData, fileName);
        getLinearTrend();
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

  @Override
  public int simulate() {
    int returnValue = 0;
    while (getLattice().getCoverage() < maxCoverage) {
      if (performSimulationStep()) {
        break;
      }
    }
    
    return returnValue;
  }

  @Override
  public void depositSeed() {
    getList().setDepositionProbability(totalAdsorptionRate);
    getLattice().resetOccupied();
    simulatedSteps = 0;
    simulationNumber++;
    //depositNewAtom();
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
    CatalysisAtom destinationAtom = null;
    int ucIndex = 0;

    do {
      byte atomType;
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

    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    return destinationAtom;
    
  }
  
  private void getLinearTrend() {
    int MAXN = 1000;
    int n = 0;
    double[] x = new double[MAXN];
    double[] y = new double[MAXN];

    // first pass: read in data, compute xbar and ybar
    double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;

    for (n = 0; n < simulationData[0].length; n++) {
      double R2 = 0;
      double t = 0;
      if (n > 0) {
        int j;
        for (j = 0; j < simulationData.length; j++) {
          if (simulationData[j][n][0] > Double.NEGATIVE_INFINITY) {
            R2 += Math.pow(simulationData[j][n][0] - simulationData[j][0][0], 2) + Math.pow(simulationData[j][n][1] - simulationData[j][0][1], 2);
            t += simulationData[j][n][2];
          }
        }
        R2 = R2 / j;
        y[n] = R2;
        t = t / j;
        x[n] = t;
        sumx += x[n];
        sumx2 += x[n] * x[n];
        sumy += y[n];
      }

    }
    double xbar = sumx / n;
    double ybar = sumy / n;

    // second pass: compute summary statistics
    double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
    for (int i = 0; i < n; i++) {
      xxbar += (x[i] - xbar) * (x[i] - xbar);
      yybar += (y[i] - ybar) * (y[i] - ybar);
      xybar += (x[i] - xbar) * (y[i] - ybar);
    }
    double beta1 = xybar / xxbar;
    double beta0 = ybar - beta1 * xbar;

    // print results
    System.out.println("y   = " + beta1 + " * x + " + beta0);

    // analyze results
    int df = n - 2;
    double rss = 0.0;      // residual sum of squares
    double ssr = 0.0;      // regression sum of squares
    for (int i = 0; i < n; i++) {
      double fit = beta1 * x[i] + beta0;
      rss += (fit - y[i]) * (fit - y[i]);
      ssr += (fit - ybar) * (fit - ybar);
    }
    double R2 = ssr / yybar;
    double svar = rss / df;
    double svar1 = svar / xxbar;
    double svar0 = svar / n + xbar * xbar * svar1;
  }
}
