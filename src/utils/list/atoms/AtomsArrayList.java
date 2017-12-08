/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

import java.util.ArrayList;
import java.util.Iterator;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public class AtomsArrayList<T extends Comparable<T>> implements IAtomsCollection<T> {

  private final ArrayList<T> atomsArray;
  private double totalRate;
  /**
   * The "movements" that atom can do. For example: Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;
  
  /**
   * Array List implementation for atom collection storing.
   * 
   * @param process The "movements" that atom can do. For example: Adsorption, desorption, reaction or diffusion.
   */
  public AtomsArrayList(byte process) {
    atomsArray = new ArrayList();
    totalRate = 0.0;
    this.process = process;
  }
  
  /**
   * Current atom is added. This should only be called in the initialisation. Add atoms only if they
   * have a rate.
   *
   * @param atom
   */
  @Override
  public void insert(T atom) {
    AbstractGrowthAtom a = (AbstractGrowthAtom) atom;
    a.setOnList(process, a.getRate(process) > 0);
    if (a.getRate(process) > 0) {
      add(atom);
    }
  }

  /**
   * Current atom is added.
   * 
   * @param atom 
   */
  @Override
  public void addRate(T atom) {
    add(atom);
  }
  
  private void add(T atom) {
    AbstractGrowthAtom a = (AbstractGrowthAtom) atom;
    totalRate += a.getRate(process);
    atomsArray.add(atom);
  }

  @Override
  public void remove(T atom) {
    atomsArray.remove(atom);
  }

  @Override
  public void removeAtomRate(T atom) {
    AbstractGrowthAtom a = (AbstractGrowthAtom) atom;
    totalRate -= a.getRate(process);
    a.setRate(process, 0.0);
    atomsArray.remove(atom);
  }

  /**
   * Updates total rate.
   * @param atom Ignored.
   * @param diff Value to be added.
   */
  @Override
  public void updateRate(T atom, double diff) {
    totalRate += diff;
  }

  /**
   * Does nothing.
   */
  @Override
  public void populate() {
  }
  
  @Override
  public void recomputeTotalRate(byte process) {
    double sum = 0.0;
    for (int i = 0; i < atomsArray.size(); i++) {
      sum += ((AbstractGrowthAtom) atomsArray.get(i)).getRate(process);
    }
    totalRate = sum;
  }

  @Override
  public double getTotalRate(byte process) {
    return totalRate;
  }
  
  @Override
  public void clear() {
    atomsArray.clear();
    totalRate = 0.0;
  }

  @Override
  public T randomAtom() {
    T a = null;
    double randomNumber = StaticRandom.raw() * totalRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < atomsArray.size(); i++) {
      sum += ((AbstractGrowthAtom) atomsArray.get(i)).getRate(process);
      if (sum > randomNumber) {
        a = atomsArray.get(i);
        break;
      }
    }
    
    if (a == null) { // Search has failed
      // Recompute total rate and try again
      recomputeTotalRate(process);
      a = randomAtom();
    }
    
    return a;
  }
  
  @Override
  public Iterator<T> iterator() {
    return atomsArray.iterator();
  }
  
  @Override
  public int size() {
    return atomsArray.size();
  }
  
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }
  
  /**
   * Return an atom with the same atom Id that was given from the list.
   * 
   * @param atom fake atom with correct Id to search for.
   * @return found atom, null otherwise.
   */
  @Override
  public T search(T atom) {
    Iterator<T> iter = atomsArray.iterator();
    while (iter.hasNext()) {
      T current = iter.next();
      if (current.compareTo(atom) == 0) {
        return current;
      }
    }
    return null;
  }
}
