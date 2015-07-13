package kineticMonteCarlo.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.edu.cornell.lassp.houle.rngPack.RandomSeedable;

public abstract class AbstractList implements IProbabilityHolder {

    
    protected static final int EVENTS_PER_CLEANUP=2048;
    protected int removals_since_last_cleanup = 0;
    protected boolean auto_cleanup=false;
    
    protected double time;
    protected double deposition_probability = 0;
    protected int totalAtoms;
    protected double totalProbability;
    protected IProbabilityHolder parent;
    protected int level;

    public abstract void add_Atom(AbstractAtom a);

    public abstract AbstractAtom next_event(RandomSeedable RNG);

    public double getTime() {
        return time;
    }

    public abstract int cleanup();

    public int getTotalAtoms() {
        return totalAtoms;
    }
    
    public AbstractList autoCleanup(boolean auto) {
        this.auto_cleanup=auto;
        return this;
    }

    public double getDepositionProbability() {
        return deposition_probability;
    }

    public void setDepositionProbability(double deposition_probability) {
        this.deposition_probability = deposition_probability;
    }

    @Override
    public void addTotalProbability(double prob) {
        if (prob != 0) {
            totalProbability += prob;
            if (this.parent != null) {
                this.parent.addTotalProbability(prob);
            }
        }
    }

    public void setParent(IProbabilityHolder parent) {
        this.parent = parent;
    }

    public abstract double getTotalProbabilityFromList();

    public abstract double getTotalProbability();

    public abstract void reset();

    public abstract AbstractAtom getAtomAt(int pos);

    public abstract int getSize();

    public abstract ListIterator getIterator();
}
