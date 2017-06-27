/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

import java.util.Iterator;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public interface IAtomsCollection<T extends Comparable<T>> {

  public void insert(T atom);

  public void addRate(T atom);
  
  public void remove(T atom);

  /**
   * Removes atom's rate, with its old desorption rate and sets to zero.
   * 
   * @param atom
   */
  public void removeAtomRate(T atom);

  public void updateRate(T atom, double diff);

  public void populate();

  public double getTotalRate(byte process);
  
  public void recomputeTotalRate(byte process);
  
  public void clear();
  
  public T randomAtom();
  
  public Iterator<T> iterator();
  
  public int size();
  
  public boolean isEmpty();
}
