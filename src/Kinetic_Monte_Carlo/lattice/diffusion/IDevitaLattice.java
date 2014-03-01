/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Kinetic_Monte_Carlo.lattice.diffusion;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;

/**
 *
 * @author Nestor
 */
public interface IDevitaLattice {
    
    
    public int getAvailableDistance(int atomType,short Xpos, short Ypos,int thresholdDistance);
    public Abstract_2D_diffusion_atom getFarAtom(int originType,short Xpos, short Ypos,int distance);
    
}
