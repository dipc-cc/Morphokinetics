/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.etching;

import Kinetic_Monte_Carlo.KMC_core.*;
import Kinetic_Monte_Carlo.lattice.etching.Abstract_etching_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_etching_KMC extends Abstract_KMC {

    public Abstract_etching_KMC(List_configuration config) {
        super(config);
    }
       

    protected Abstract_etching_lattice lattice;

    @Override
    public Abstract_etching_lattice getLattice() {return lattice;}
    
         
}
