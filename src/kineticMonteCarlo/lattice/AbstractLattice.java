/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;

/**
 *
 * @author Nestor
 */
public abstract class AbstractLattice {
 
  protected int  sizeX;
  protected int  sizeY;
  protected int  sizeZ; 
        
  protected int unit_cell_size;
    
  public int getSizeX(){return sizeX;}
  public int getSizeY(){return sizeY;}
  public int getSizeZ(){return sizeZ;}
  public int getSizeUC(){return unit_cell_size;}
    
  public abstract AbstractAtom getAtom(int X,int Y,int Z,int Unit_cell_pos);

  public abstract void reset();

}
