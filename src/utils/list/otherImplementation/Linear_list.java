package utils.list.otherImplementation;

import java.util.LinkedList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import utils.list.AbstractList;
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
    public double getTotalProbabilityFromList() {
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
    public void addAtom(AbstractAtom a) {
        surface.addFirst(a);
        a.setOnList(this);
        totalProbability += a.getProbability();
        totalAtoms++;
    }

    @Override
    public AbstractAtom nextEvent(RandomSeedable RNG) {
        
        removalsSinceLastCleanup++;  
        if (autoCleanup && removalsSinceLastCleanup>EVENTS_PER_CLEANUP) {this.cleanup(); removalsSinceLastCleanup=0;}
        
        double position = RNG.raw() * (totalProbability + depositionProbability);

        time -= Math.log(RNG.raw()) / (totalProbability + depositionProbability);

        if (position < depositionProbability) {
            return null; //toca añadir un átomo nuevo
        }
        position -= depositionProbability;
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
