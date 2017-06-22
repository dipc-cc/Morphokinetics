/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

import java.util.ArrayList;
import kineticMonteCarlo.atom.CatalysisAtom;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 * @param <T>
 */
public class AtomsArrayList<T extends Comparable<T>> implements IAtomsCollection<T> {

  private final ArrayList<CatalysisAtom> atomsArray;
  private double totalRate;
  /**
   * Adsorption, desorption, reaction or diffusion.
   */
  private final byte process;
  
  public AtomsArrayList(byte process) {
    atomsArray = new ArrayList();
    totalRate = 0.0;
    this.process = process;
  }
  
  /**
   * Current atom is added.
   * @param atom 
   */
  @Override
  public void insert(T atom) {
    add(atom);
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
    CatalysisAtom a = (CatalysisAtom) atom;
    if (a.isOnList(process)) {
      totalRate += a.getRate(process);
      atomsArray.add(a);
    }
  }

  @Override
  public void remove(T atom) {
    atomsArray.remove(atom);
  }

  @Override
  public void removeAtomRate(T atom) {
    CatalysisAtom a = (CatalysisAtom) atom;
    totalRate -= a.getRate(process);
    a.setRate(process, 0.0);
    atomsArray.remove(a);
  }

  @Override
  public void removeRate(T atom, double diff) {
    totalRate += diff;
  }

  /**
   * Does nothing.
   */
  @Override
  public void populate() {
  }

  @Override
  public double getTotalRate(byte process) {
    return totalRate;
  }
  
  /**
   * Used to recompute totalRate.
   */
  @Override
  public void clear() {
    double sum = 0.0;
    for (int i = 0; i < atomsArray.size(); i++) {
      sum += atomsArray.get(i).getRate(process);
    }
    totalRate = sum;
  }

  @Override
  public T randomAtom() {
    CatalysisAtom atom = null;
    double randomNumber = StaticRandom.raw() * totalRate;
    
    double sum = 0.0;
    int i;
    for (i = 0; i < atomsArray.size(); i++) {
      sum += atomsArray.get(i).getRate(process);
      if (sum > randomNumber) {
        atom = atomsArray.get(i);
        break;
      }
    }
    
    if (atom == null) { // Search has failed
      // Recompute total rate and try again
      clear();
      atom = (CatalysisAtom) randomAtom();
    }
    
    return (T) atom;
  }  
}
