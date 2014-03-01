package Kinetic_Monte_Carlo.list;

import Kinetic_Monte_Carlo.atom.Abstract_atom;
import java.util.ListIterator;
import utils.edu.cornell.lassp.houle.RngPack.RandomSeedable;

public abstract class Abstract_list implements IProbability_holder {

    
    protected static final int EVENTS_PER_CLEANUP=2048;
    protected int removals_since_last_cleanup = 0;
    protected boolean auto_cleanup=false;
    
    protected double time;
    protected double deposition_probability = 0;
    protected int totalAtoms;
    protected double totalProbability;
    protected IProbability_holder parent;
    protected int level;

    public abstract void add_Atom(Abstract_atom a);

    public abstract Abstract_atom next_event(RandomSeedable RNG);

    public double getTime() {
        return time;
    }

    public abstract int cleanup();

    public int getTotalAtoms() {
        return totalAtoms;
    }
    
    public Abstract_list autoCleanup(boolean auto) {
        this.auto_cleanup=auto;
        return this;
    }

    public double get_deposition_probability() {
        return deposition_probability;
    }

    public void set_deposition_probability(double deposition_probability) {
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

    public void setParent(IProbability_holder parent) {
        this.parent = parent;
    }

    public abstract double getTotalProbability_from_list();

    public abstract double getTotalProbability();

    public abstract void reset();

    public abstract Abstract_atom getAtomAt(int pos);

    public abstract int getSize();

    public abstract ListIterator getIterator();
}
