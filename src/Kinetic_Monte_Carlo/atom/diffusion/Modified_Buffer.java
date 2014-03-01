/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion;

import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.Abstract_list;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Nestor
 */
public class Modified_Buffer {

    private final Set<Abstract_2D_diffusion_atom> buffer;
    private final Set<Abstract_2D_diffusion_atom> bufferL;


    public Modified_Buffer() {

        buffer = new HashSet(8, 0.9f);
        bufferL = new HashSet(8, 0.9f);
    }

    public void addAtomPropio(Abstract_2D_diffusion_atom a) {
        buffer.add(a);

    }

    public void addAtomLigaduras(Abstract_2D_diffusion_atom a) {
        bufferL.add(a);
    }
    
    public void updateAtoms(Abstract_list list, Abstract_2D_diffusion_lattice lattice) {
    
        
        
        Iterator<Abstract_2D_diffusion_atom> it = buffer.iterator();
        while (it.hasNext()) {
            update_allRates(it.next(),list);
        }
        
        it = bufferL.iterator();
        out:
        while (it.hasNext()) {
            update_all_neighbors(it.next(),lattice);
        }
        clear();
    }

    private void update_allRates(Abstract_2D_diffusion_atom atom,Abstract_list list) {
      
        if (atom.isEligible() && !atom.isOnList()) {
            list.add_Atom(atom);
        }
        atom.update_all_rates();
        
    }
    
    private void update_all_neighbors(Abstract_2D_diffusion_atom atom,Abstract_2D_diffusion_lattice lattice) {
        for (int i = 0; i < atom.getNeighbourCount(); i++) {
            update_neighbor(atom, i,lattice);
        }
    }

    private void update_neighbor(Abstract_2D_diffusion_atom atom, int neighborPos,Abstract_2D_diffusion_lattice lattice) {

        Abstract_2D_diffusion_atom vecino = lattice.getNeighbor(atom.getX(), atom.getY(), neighborPos);
        if ( vecino.isEligible() && !buffer.contains(vecino)) {
            vecino.update_one_bound(neighborPos);
        }
    }

    public void clear() {
        buffer.clear();
        bufferL.clear();
    }
}
