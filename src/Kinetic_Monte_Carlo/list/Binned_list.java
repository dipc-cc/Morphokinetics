/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.list;

import Kinetic_Monte_Carlo.atom.Abstract_atom;
import java.util.ListIterator;
import utils.edu.cornell.lassp.houle.RngPack.RandomSeedable;

/**
 *
 * @author Nestor
 *
 *
 */
public class Binned_list extends Abstract_list implements IProbability_holder {

    private static final float bin_difference_factor = 1.25f;
    private final Abstract_list[] bins;
    private int currentBin;
 

    public Binned_list(int binAmount, int extra_bin_levels) {

        this.level=extra_bin_levels;
        
        if (extra_bin_levels > 0) {
            bins = new Binned_list[binAmount];
            for (int i = 0; i < binAmount; i++) {
                bins[i] = new Binned_list(binAmount, extra_bin_levels - 1);
                bins[i].setParent(this);
            }
        } else {
            bins = new Linear_list[binAmount];
            for (int i = 0; i < binAmount; i++) {
                bins[i] = new Linear_list();
                 bins[i].setParent(this);
            }
        }
    }

    private void updateCurrentList() {

        while (bins[currentBin].getSize() > (totalAtoms * bin_difference_factor / bins.length)) {
            
            currentBin++;

            if (currentBin == bins.length) {
                currentBin = 0;
            }
        }
    }
    

    @Override
    public void add_Atom(Abstract_atom a) {
        
        updateCurrentList();
        totalAtoms++;
        totalProbability += a.getProbability();
        bins[currentBin].add_Atom(a);
    }

    
    
    @Override
    public Abstract_atom next_event(RandomSeedable RNG) {

        if (auto_cleanup && removals_since_last_cleanup>EVENTS_PER_CLEANUP) {this.cleanup(); removals_since_last_cleanup=0;}
        
        double position = RNG.raw() * (totalProbability + deposition_probability);
        if (this.parent==null)
            time -= Math.log(RNG.raw()) / (totalProbability + deposition_probability);

        if (position < deposition_probability) {
            return null; //we have to add a new atom
        }
        position -= deposition_probability;
        int selected = 0;
        double accumulation = bins[selected].getTotalProbability();
        
        
        while (position >= accumulation) {
           
             selected++;
             if (selected == bins.length - 1)  break;
             accumulation += bins[selected].getTotalProbability();
        }

       Abstract_atom atom=bins[selected].next_event(RNG);
       if (atom!=null) totalAtoms--;

       
        return atom;
    }

    @Override
    public int cleanup() {
        int totalAtomsOld = totalAtoms;
        for (Abstract_list bin : bins) {
            totalAtoms -= bin.cleanup();
        }
        return (totalAtomsOld - totalAtoms);
    }

    @Override
    public double getTotalProbability_from_list() {

        double totalProb = 0;
        for (Abstract_list bin : bins) {
            totalProb += bin.getTotalProbability_from_list();
        }

        return totalProb;
    }

    @Override
    public double getTotalProbability() {
        return totalProbability;
    }

    @Override
    public void reset() {
        time = 0;
        totalProbability = 0;
        totalAtoms = 0;
        currentBin = 0;
        for (Abstract_list bin : bins) {
            bin.reset();
        }
    }

    @Override
    public Abstract_atom getAtomAt(int pos) {

        int cont = 0;
        int i = 0;
        while (pos >= cont + bins[i].getSize()) {

            cont += bins[i].getSize();
            i++;
        }
        return bins[i].getAtomAt(pos - cont);
    }

    @Override
    public int getSize() {
        return totalAtoms;
    }

    public void traceSizes(String separator) {
        System.out.println(separator + bins.length);
        String lowerLevelSeparator = separator + "\t";
        for (Abstract_list bin : bins) {
            if (bin instanceof Binned_list) {
                ((Binned_list) bin).traceSizes(lowerLevelSeparator);
            } else {
                System.out.println(lowerLevelSeparator + bin.getSize());
            }
        }
    }
    
    
    public Abstract_list[] getBins() {
        return bins;
    }

    public int getLevel() {
        return level;
    }
    
    @Override
    public ListIterator getIterator() {
       return new BinnedListIterator(this);
    }
    
}
