/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.etching;

import Kinetic_Monte_Carlo.atom.*;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_etching_atom extends Abstract_atom {   
    

protected double[] probabilities;

    public void initialize(double[] probabilities) {
        this.probabilities = probabilities;
    }


    
}
