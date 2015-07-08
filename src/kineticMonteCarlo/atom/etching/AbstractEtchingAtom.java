/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.etching;

import kineticMonteCarlo.atom.*;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEtchingAtom extends AbstractAtom {   
    

protected double[] probabilities;

    public void initialize(double[] probabilities) {
        this.probabilities = probabilities;
    }


    
}
