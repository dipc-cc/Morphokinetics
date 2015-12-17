/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import utils.list.IProbabilityHolder;

/**
 * This interface defines the minimum methods that an atom class have to have.
 * @author J. Alberdi-Rodriguez
 */
public interface IAtom {
    
  public void setProbabilities(double[] probabilities);

  public double[] getProbabilities();

  public void setList(IProbabilityHolder list);

  public boolean isOnList();

  public double getProbability();

  public boolean isEligible();

  public boolean isRemoved();

  public void unRemove();
  
  public void setRemoved();
  
  public void remove();
  
  public byte getType();

  public int getNumberOfNeighbours();

  public void setNumberOfNeighbours(int numberOfNeighbours);

  public void setAsBulk();

  public void updateN1FromScratch();

  public void updateN2FromScratch();

  public void setNeighbour(AbstractAtom atom, int i);
  
  public void addTotalProbability(double probabilityChanges);
}
