package Kinetic_Monte_Carlo.list;

import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.list.Abstract_list;
import java.util.*;
import utils.edu.cornell.lassp.houle.RngPack.RandomSeedable;

/**
 *
 *
 * Linked list basic implementation
 *
 */
public class Linear_list extends Abstract_list {

    private final ArrayList<Abstract_atom> surface;
    
    

    public Linear_list() {
        surface = new ArrayList();
        this.level=-1;
    }

    @Override
    public double getTotalProbability_from_list() {
        double totalprobability = 0;

        ListIterator<Abstract_atom> LI = surface.listIterator();
        while (LI.hasNext()) {
            Abstract_atom AC = LI.next();
            if (AC.isEligible()) {
                totalprobability += AC.getProbability();

            }
        }
        return totalprobability;
    }

    @Override
    public void reset() {
        surface.clear();
        time = 0;
        totalProbability = 0;
        totalAtoms=0;
    }

    @Override
    public int cleanup() {

        int temp = totalAtoms;
        ListIterator<Abstract_atom> LI = surface.listIterator();
        while (LI.hasNext()) {
            Abstract_atom AC = LI.next();
            if (!AC.isEligible()) {
                AC.setOnList(null);
                LI.remove();
                totalAtoms--;
            }
        }
        return (temp - totalAtoms);
    }

    @Override
    public void add_Atom(Abstract_atom a) {
        surface.add(0,a);
        a.setOnList(this);
        totalProbability += a.getProbability();
        totalAtoms++;
    }

    @Override
    public Abstract_atom next_event(RandomSeedable RNG) {
        
        removals_since_last_cleanup++;  
        if (auto_cleanup && removals_since_last_cleanup>EVENTS_PER_CLEANUP) {this.cleanup(); removals_since_last_cleanup=0;}
        
        double position = RNG.raw() * (totalProbability + deposition_probability);

        time -= Math.log(RNG.raw()) / (totalProbability + deposition_probability);

        if (position < deposition_probability) {
            return null; //toca añadir un átomo nuevo
        }
        position -= deposition_probability;
        double prob_current = 0;

        Abstract_atom AC = null;
        outside:
        for (int i=0;i<surface.size();i++){
            AC = surface.get(i);
            prob_current += AC.getProbability();
            if (prob_current >= position) {
                surface.remove(i);
                totalAtoms--;
                return AC;
            }
        }
        
        if (AC!=null){
               surface.remove(surface.size()-1);
                totalAtoms--;}
        
       return AC;
    }

    @Override
    public int getSize() {
        return surface.size();
    }
        
    @Override
    public double getTotalProbability() {
        return totalProbability;
    }

    @Override
    public Abstract_atom getAtomAt(int position) {
        return surface.get(position);

    }

    @Override
    public ListIterator<Abstract_atom> getIterator() {
        return surface.listIterator();
    }
    

    
}
