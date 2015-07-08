/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.diffusion;

import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import kineticMonteCarlo.list.AbstractList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Nestor
 */
public class ModifiedBuffer {

    private final Set<Abstract2DDiffusionAtom> buffer;
    private final Set<Abstract2DDiffusionAtom> bufferL;


    public ModifiedBuffer() {

        buffer = new HashSet(8, 0.9f);
        bufferL = new HashSet(8, 0.9f);
    }

    public void addAtomPropio(Abstract2DDiffusionAtom a) {
        buffer.add(a);

    }

    public void addAtomLigaduras(Abstract2DDiffusionAtom a) {
        bufferL.add(a);
    }
    
    public void updateAtoms(AbstractList list, Abstract2DDiffusionLattice lattice) {
    
        
        
        Iterator<Abstract2DDiffusionAtom> it = buffer.iterator();
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

    private void update_allRates(Abstract2DDiffusionAtom atom,AbstractList list) {
      
        if (atom.isEligible() && !atom.isOnList()) {
            list.add_Atom(atom);
        }
        atom.update_all_rates();
        
    }
    
    private void update_all_neighbors(Abstract2DDiffusionAtom atom,Abstract2DDiffusionLattice lattice) {
        for (int i = 0; i < atom.getNeighbourCount(); i++) {
            update_neighbor(atom, i,lattice);
        }
    }

    private void update_neighbor(Abstract2DDiffusionAtom atom, int neighborPos,Abstract2DDiffusionLattice lattice) {

        Abstract2DDiffusionAtom vecino = lattice.getNeighbor(atom.getX(), atom.getY(), neighborPos);
        if ( vecino.isEligible() && !buffer.contains(vecino)) {
            vecino.update_one_bound(neighborPos);
        }
    }

    public void clear() {
        buffer.clear();
        bufferL.clear();
    }
}
