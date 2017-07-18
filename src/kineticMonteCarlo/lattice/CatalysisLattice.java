/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import utils.LinearRegression;

/**
 *
 * @author Karmele Valencia
 */
public class CatalysisLattice extends AbstractGrowthLattice {

  /**
   * Current CO and O coverages, for sites BR, CUS.
   */
  private final int[][] coverage;
  private final List<double[][]> last1000events;
  private final List<Double> last1000eventsTime;
  private final int MAX;
  ArrayList<LinearRegression> regressions;

  public CatalysisLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    super(hexaSizeI, hexaSizeJ, modified);
    coverage = new int[2][2];
    MAX = (int) Math.sqrt(hexaSizeI*hexaSizeJ)*20;
    last1000events = new LinkedList<>();
    last1000eventsTime = new LinkedList<>();
  }
  
  /**
   * Identifies stationary situation, when sufficient number of previous steps are saved and their
   * R² is lower than 0.1 for all the species.
   *
   * @param time
   * @return
   */
  public boolean isStationary(double time) {
    double hexaArea = (double) getHexaSizeI() * getHexaSizeJ() / 2.0;
    double[][] covTmp = new double[2][2];
   
    for (int j = 0; j < 2; j++) {
      for (int k = 0; k < 2; k++) {
        covTmp[j][k] = (double) coverage[j][k] / hexaArea;
      }
    }
    last1000events.add(covTmp);
    last1000eventsTime.add(time);
    if (last1000events.size() > MAX) {
      last1000events.remove(0);
      last1000eventsTime.remove(0);
    }

    double[][] y = new double[4][last1000events.size()];
    for (int i = 0; i < last1000events.size(); i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          int index = j*2  + k ;
          y[index][i] = last1000events.get(i)[j][k];
        }
      }
    }

    double[] x = new double[last1000events.size()];
    Iterator iter = last1000eventsTime.iterator();
    int i = 0;
    while (iter.hasNext() && i < last1000events.size()) {
      x[i++] = (double) iter.next();
    }
    regressions = new ArrayList();
    regressions.add(new LinearRegression(x, y[0]));
    regressions.add(new LinearRegression(x, y[1]));
    regressions.add(new LinearRegression(x, y[2]));
    regressions.add(new LinearRegression(x, y[3]));

    boolean stationary = false;
    if (last1000events.size() == MAX) {
      stationary = true;
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          int index = j * 2 + k;
          if (regressions.get(index).R2() > 0.1) {
            stationary = false;
          }
        }
      }
    }
    return stationary;
  }

  public double[] getR2() {
    double[] r2 = new double[4];
    for (int i = 0; i < 4; i++) {
      r2[i] = regressions.get(i).R2();
    }
    return r2;
  }

  @Override
  public CatalysisAtom getCentralAtom() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2);
    return (CatalysisAtom) getAtom(iCentre, jCentre, 0);
  }
  
  @Override
  public AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return ((CatalysisAtom) getUc(index).getAtom(0)).getNeighbour(neighbour);
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ();
  }

  @Override
  public double getCartX(int iHexa, int jHexa) {
    return iHexa;
  }

  @Override
  public double getCartY(int jHexa) {
    return jHexa;
  }

  @Override
  public int getiHexa(double xCart, double yCart) {
    return (int) xCart;
  }

  @Override
  public int getjHexa(double yCart) {
    return (int) yCart;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    return new Point2D.Double(iHexa, jHexa);
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI() / 2, getHexaSizeJ() / 2);
  }
  
  public float getCoverage(byte type) {
    float cov = (float) coverage[type][BR] + (float) coverage[type][CUS];
    float hexaArea = (float) getHexaSizeI() * getHexaSizeJ();
    return cov / hexaArea;
  }
  
  /**
   * Computes a partial coverage for CO and O in BR and CUS sites.
   * 
   * @return coverage CO^BR, CO^CUS, O^BR, CO^CUS
   */
  public float[] getCoverages() {
    float[] cov = new float[4];
    float hexaArea = (float) ((float) getHexaSizeI() * getHexaSizeJ() / 2.0);
    for (int i = 0; i < cov.length; i++) {
      cov[i] = coverage[i / 2][i % 2] / hexaArea;
    }
    return cov;
  }
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][][] probabilities) {
    //atomTypesAmount = probabilities.length;
    //atomTypesCounter = new int[atomTypesAmount];
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        atom.initialiseRates(probabilities);
      }
    }
  }
  
  public void init() {
    setAtoms(createAtoms());
    setAngles();
  }    

  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    CatalysisAtom atom = (CatalysisAtom) a;
    atom.setOccupied(true);

    updateCoCus(atom);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      atom.getNeighbour(i).addOccupiedNeighbour(1);
      updateCoCus(neighbour);
    }
    addOccupied();
    coverage[a.getType()][atom.getLatticeSite()]++;
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    CatalysisAtom atom = (CatalysisAtom) a;
    atom.setOccupied(false);
    atom.cleanCoCusNeighbours();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      CatalysisAtom neighbour = atom.getNeighbour(i);
      atom.getNeighbour(i).addOccupiedNeighbour(-1);
      updateCoCus(neighbour);
    }
    subtractOccupied();
    coverage[a.getType()][atom.getLatticeSite()]--;
    return 0;
  }
  
  @Override
  public void reset() {
    coverage[CO][BR] = 0;
    coverage[CO][CUS] = 0;
    coverage[O][BR] = 0;
    coverage[O][CUS] = 0;
    last1000events.clear();
    last1000eventsTime.clear();
    super.reset();
  }
  
  /**
   * Changes the occupation of the clicked atom from unoccupied to occupied, or vice versa. It is
   * experimental and only works with AgUc simulation mode. If fails, the execution continues
   * normally.
   *
   * @param xMouse absolute X location of the pressed point
   * @param yMouse absolute Y location of the pressed point
   * @param scale zoom level
   */
  @Override
  public void changeOccupationByHand(double xMouse, double yMouse, int scale) {
    int iLattice;
    int jLattice;
    // scale the position with respect to the current scale.
    double xCanvas = xMouse / scale;
    double yCanvas = yMouse / scale;
    // choose the correct lattice
    iLattice = (int) Math.floor(xCanvas);
    jLattice = (int) Math.floor(yCanvas);
    double j = yCanvas;
    int pos = 0;

    // for debugging
    System.out.println("scale " + scale + " " + (jLattice - j));
    System.out.println("x y " + xMouse + " " + yMouse + " | " + xCanvas + " " + yCanvas + " | " + iLattice + " " + jLattice + " | ");
    AbstractGrowthAtom atom = getUc(iLattice, jLattice).getAtom(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
  
  /**
   * There are no islands in catalysis  --- why?
   * @param print
   * @return -1
   */
  @Override
  public int countIslands(PrintWriter print) {
    return -1;
  }

  @Override
  public int getAvailableDistance(AbstractGrowthAtom atom, int thresholdDistance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthAtom getFarSite(AbstractGrowthAtom atom, int distance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  private CatalysisAtom[][] createAtoms() {
    //Instantiate atoms
    CatalysisAtom[][] atoms = new CatalysisAtom[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = new CatalysisAtom(createId(i, j), (short) i, (short) j);
      }
    }
    
    //Interconnect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current atom
        CatalysisAtom atom = (CatalysisAtom) atoms[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 3);
      }
    }
    return atoms;
  }

  /**
   * Check whether two CO^CUS atoms are together.
   * 
   * @param atom
   */
  private void updateCoCus(CatalysisAtom atom) {
    if (atom.isOccupied() && atom.getLatticeSite() == CUS && atom.getType() == CO) {
      atom.cleanCoCusNeighbours();
      for (int i = 0; i < atom.getNumberOfNeighbours(); i += 2) { // Only up and down neighbours
        CatalysisAtom neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == CO) {
          atom.addCoCusNeighbours(1);
        }
      }
    }
  }
}
