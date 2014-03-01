/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom;

import Kinetic_Monte_Carlo.list.IProbability_holder;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_atom {   
    
protected IProbability_holder list;

public void setOnList(IProbability_holder list) {
    this.list=list;}

public boolean isOnList()        {return list!=null;}

public abstract double getProbability();

public abstract boolean isEligible();

public abstract boolean isRemoved();
    
}
