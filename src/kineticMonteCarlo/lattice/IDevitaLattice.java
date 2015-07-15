/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;

/**
 *
 * @author Nestor
 */
public interface IDevitaLattice {
    
    
    public int getAvailableDistance(int atomType,short Xpos, short Ypos,int thresholdDistance);
    public Abstract2DDiffusionAtom getFarSite(int originType,short Xpos, short Ypos,int distance);
    
}
