/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import javafx.geometry.Point3D;

/**
 * This interface defines the minimum methods that an atom class have to have.
 * @author J. Alberdi-Rodriguez
 */
public interface IAtom {
    
  public void setProbabilities(double[] probabilities);

  public double[] getProbabilities();

  public void setList(Boolean list);

  public boolean isOnList();

  public double getProbability();

  public boolean isEligible();

  public boolean isRemoved();
  
  public boolean isOccupied();

  public void unRemove();
  
  public void setRemoved();
  
  public double remove();
  
  public byte getType();
  
  public byte getRealType();

  public int getNumberOfNeighbours();

  public void setNumberOfNeighbours(int numberOfNeighbours);

  public void setNeighbour(AbstractAtom atom, int i);
  
  public Point3D getPos();
  
}
