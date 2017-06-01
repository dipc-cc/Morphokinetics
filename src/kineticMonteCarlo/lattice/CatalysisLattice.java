/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.io.PrintWriter;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.BR;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;
import static kineticMonteCarlo.atom.CatalysisAtom.CUS;
import static kineticMonteCarlo.atom.CatalysisAtom.O;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;

/**
 *
 * @author Karmele Valencia
 */
public class CatalysisLattice extends AbstractGrowthLattice {

  /**
   * Current CO and O coverages, for sites BR, CUS.
   */
  private int[][] coverage;
  
  public CatalysisLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    super(hexaSizeI, hexaSizeJ, modified);
    coverage = new int[2][2];
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

    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      atom.getNeighbour(i).addOccupiedNeighbour(1);
    }
    addOccupied();
    coverage[a.getType()][atom.getLatticeSite()]++;
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    CatalysisAtom atom = (CatalysisAtom) a;
    atom.setOccupied(false);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      atom.getNeighbour(i).addOccupiedNeighbour(-1);
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
}