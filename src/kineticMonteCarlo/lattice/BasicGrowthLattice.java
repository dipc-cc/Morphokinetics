/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.BasicGrowthAtom;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import kineticMonteCarlo.atom.ModifiedBuffer;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthLattice extends AbstractGrowthLattice {

  public BasicGrowthLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    super(hexaSizeI, hexaSizeJ, modified);
  }
  
  public void init() {
    setAtoms(createAtoms());
    setAngles();
  }    

  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  private BasicGrowthAtom[][] createAtoms() {
    //Instantiate atoms
    BasicGrowthAtom[][] atoms = new BasicGrowthAtom[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = new BasicGrowthAtom(createId(i, j), (short) i, (short) j);
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current atom
        BasicGrowthAtom atom = (BasicGrowthAtom) atoms[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        atom.setNeighbour((BasicGrowthAtom) atoms[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        atom.setNeighbour((BasicGrowthAtom) atoms[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        atom.setNeighbour((BasicGrowthAtom) atoms[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((BasicGrowthAtom) atoms[i][j], 3);
      }
    }
    return atoms;
  }

  @Override
  public AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return ((BasicGrowthAtom) getUc(index).getAtom(0)).getNeighbour(neighbour);
  }

  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    BasicGrowthAtom atom = (BasicGrowthAtom) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(ISLAND);
    }
    byte originalType = atom.getType();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        addOccupiedNeighbour(atom.getNeighbour(i), originalPosition, forceNucleation);
      }
    }

    addAtom(atom);
    if (atom.getOccupiedNeighbours()> 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
  
  /**
   * A new occupied atom was added before calling this method, here, updating the first and the
   * second neighbourhood.
   *
   * @param neighbourAtom current atom.
   * @param neighbourPosition the position of the neighbour.
   * @param forceNucleation
   */
  private void addOccupiedNeighbour(BasicGrowthAtom neighbourAtom, int neighbourPosition, boolean forceNucleation) {
    byte newType;

    newType = neighbourAtom.getNewType(1); 
    neighbourAtom.addOccupiedNeighbour(1);
    
    if (forceNucleation && neighbourAtom.isOccupied()) {
      newType = ISLAND;
    }

    if (neighbourAtom.getType() != newType) { // the type of neighbour has changed
      neighbourAtom.setType(newType);
      addAtom(neighbourAtom);
      if (neighbourAtom.getOccupiedNeighbours() > 0 && !neighbourAtom.isOccupied()) {
        addBondAtom(neighbourAtom);
      }
      // update second neighbours 
      for (int pos = 0; pos < neighbourAtom.getNumberOfNeighbours(); pos++) {
        // skip if the neighbour is the original atom or it is an island.
        if (neighbourPosition != pos && !neighbourAtom.getNeighbour(pos).isPartOfImmobilSubstrate()) {
          updateSecondNeighbour(neighbourAtom.getNeighbour(pos));
        }
      }
    }
  }

  private void updateSecondNeighbour(BasicGrowthAtom secondNeighbourAtom) {
    if (secondNeighbourAtom.isOccupied()) {
      addAtom(secondNeighbourAtom);
    }
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    BasicGrowthAtom atom = (BasicGrowthAtom) a;
    atom.setOccupied(false);
    double probabilityChange = a.getProbability();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        removeOccupied(atom.getNeighbour(i), originalPosition);
      }
    }

    if (atom.getOccupiedNeighbours()> 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
  }

  /**
   * Computes the removal of one atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void removeOccupied(BasicGrowthAtom neighbourAtom, int neighbourPosition) {
    byte newType = neighbourAtom.getNewType(-1); // one less atom
    neighbourAtom.addOccupiedNeighbour(-1); // remove one atom (original atom has been extracted)

    if (neighbourAtom.getType() != newType) {
      neighbourAtom.setType(newType);
      addAtom(neighbourAtom);
      if (neighbourAtom.getOccupiedNeighbours() > 0 && !neighbourAtom.isOccupied()) {
        addBondAtom(neighbourAtom);
      }
      
      // update second neighbours 
      for (int pos = 0; pos < neighbourAtom.getNumberOfNeighbours(); pos++) {
        // skip if the neighbour is the original atom or is an island.
        if (neighbourPosition != pos && !neighbourAtom.getNeighbour(pos).isPartOfImmobilSubstrate()) {
          updateSecondNeighbour(neighbourAtom.getNeighbour(pos));
        }
      }
    }
  }
  
  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    return new Point2D.Double(iHexa, jHexa);
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI()/2, getHexaSizeJ()/2);
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
  public int getAvailableDistance(AbstractGrowthAtom atom, int thresholdDistance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthAtom getFarSite(AbstractGrowthAtom atom, int distance) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
