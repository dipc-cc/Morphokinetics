package kineticMonteCarlo.list.otherImplementation;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.list.AbstractList;
import java.util.*;
import utils.edu.cornell.lassp.houle.rngPack.RandomSeedable;

/**
 *
 *
 * Linked list basic implementation
 *
 */
public class Linear_list extends AbstractList {

    private final LinkedList<AbstractAtom> surface;

    public Linear_list() {
        surface = new LinkedList();
        this.level=-1;
    }

    @Override
    public double getTotalProbability_from_list() {
        double totalprobability = 0;

        ListIterator<AbstractAtom> LI = surface.listIterator();
        while (LI.hasNext()) {
            AbstractAtom AC = LI.next();
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
        ListIterator<AbstractAtom> LI = surface.listIterator();
        while (LI.hasNext()) {
            AbstractAtom AC = LI.next();
            if (!AC.isEligible()) {
                AC.setOnList(null);
                LI.remove();
                totalAtoms--;
            }
        }
        return (temp - totalAtoms);
    }

    @Override
    public void add_Atom(AbstractAtom a) {
        surface.addFirst(a);
        a.setOnList(this);
        totalProbability += a.getProbability();
        totalAtoms++;
    }

    @Override
    public AbstractAtom next_event(RandomSeedable RNG) {
        
        removals_since_last_cleanup++;  
        if (auto_cleanup && removals_since_last_cleanup>EVENTS_PER_CLEANUP) {this.cleanup(); removals_since_last_cleanup=0;}
        
        double position = RNG.raw() * (totalProbability + deposition_probability);

        time -= Math.log(RNG.raw()) / (totalProbability + deposition_probability);

        if (position < deposition_probability) {
            return null; //toca añadir un átomo nuevo
        }
        position -= deposition_probability;
        double prob_current = 0;
        ListIterator<AbstractAtom> LI = surface.listIterator();
        AbstractAtom AC = null;
        outside:
        while (LI.hasNext()) {
            AC = LI.next();
            prob_current += AC.getProbability();
            if (prob_current > position) {
                LI.remove();
                totalAtoms--;
                return AC;
            }
        }
        
        if (AC!=null){
                LI.remove();
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
    public AbstractAtom getAtomAt(int position) {
        return surface.get(position);

    }

    @Override
    public ListIterator<AbstractAtom> getIterator() {
        return surface.listIterator();
    }
    

    
}
