/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;
import utils.StaticRandom;
import utils.list.LinearList;

/**
 *
 * @author Karmele Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisKmc extends AbstractGrowthKmc {
    
    
  private long simulatedSteps;
  private double sumProbabilities;
  
  private double terraceToTerraceProbability;
  private final boolean aeOutput;
  
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage; 
  private ActivationEnergy activationEnergy;
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice);
    
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    
    
    float coverage = (float) parser.getCoverage() / 100;
    if ((0f > coverage) || (1f < coverage)) {
      System.err.println("Chosen coverage is not permitted. Selecting the default one: 30%");
      maxCoverage = 0.3f;
    } else {
      maxCoverage = coverage;
    }
    activationEnergy = new ActivationEnergy(parser);
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

    if (originAtom == null) {
      //destinationAtom = depositNewAtom();
      System.out.println("atomoa hutsik dago");
    } else {
        System.out.println("atomoa betea dago");
      do {
        destinationAtom = chooseRandomHop(originAtom);
        if (destinationAtom.equals(originAtom)) {
          destinationAtom.equals(originAtom);
          System.out.println("difusioa?");
          break;
        }
      } while (!diffuseAtom(originAtom, destinationAtom));
    }

    return false;
  }
  
  @Override
  public void depositSeed() {
    System.out.println("depositSeed");
    getLattice().resetOccupied();
    depositNewAtom();  
  }
  
  @Override
  public int simulate() {
    
    System.out.println("Simulation starts");  
    //depositSeed();
    
    int returnValue = 0;
    boolean computeTime = false;
    simulatedSteps = 0;
    sumProbabilities = 0.0d;
    terraceToTerraceProbability = getLattice().getUc(0).getAtom(0).getProbability(0, 0);
    
    while (simulatedSteps<100) {
        if (getLattice().isPaused()) {
          try {
            Thread.sleep(250);
          } catch (InterruptedException ex) {
            Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, ex);
          }
        } else {
          activationEnergy.updatePossibles(getList().getIterator(), getList().getGlobalProbability(), getList().getDeltaTime(computeTime));
          computeTime = true;
          
          if (performSimulationStep()) {
              System.out.println("algo pasa");
            break;
          }
          simulatedSteps++;
          //System.out.println(simulatedSteps);
          sumProbabilities += getList().getTotalProbabilityFromList();
        }
    }
    if (aeOutput) {
      double ri = ((LinearList) getList()).getRi_DeltaI();
      double time = getList().getTime();
      System.out.println("Needed steps " + simulatedSteps + " time " + time + " Ri_DeltaI " + ri + " R " + ri / time + " R " + simulatedSteps / time);
      PrintWriter standardOutputWriter = new PrintWriter(System.out);
      activationEnergy.printAe(standardOutputWriter, -1);
    } 
    
    // Dirty mode to have only one interface of countIslands
    PrintWriter standardOutputWriter = new PrintWriter(System.out);
    getLattice().countIslands(standardOutputWriter);
    getLattice().countPerimeter(standardOutputWriter);
    getLattice().getCentreOfMass();
    getLattice().getAverageGyradius();
    getLattice().getDistancesToCentre();
    standardOutputWriter.flush();
    
    return returnValue;
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
      int random = getLattice().getHexaSizeI()*(getLattice().getHexaSizeJ()+1)/2;//StaticRandom.rawInteger(getLattice().size() * getLattice().getUnitCellSize());
      ucIndex = Math.floorDiv(random, getLattice().getUnitCellSize());
      int atomIndex = random % getLattice().getUnitCellSize();
      destinationAtom = (CatalysisAtom) getLattice().getUc(ucIndex).getAtom(atomIndex);
      destinationAtom.setType(CatalysisAtom.O);
      System.out.println("depositNewAtom");
    } while (!depositAtom(destinationAtom));
    
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(getLattice().getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    return destinationAtom;
  }
}
