/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion;

import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_2D_diffusion_atom extends Abstract_atom {

    protected byte type;
    protected double[][] probabilities;
    protected double total_probability;
    protected double[] bonds_probability;
    protected float angle;
    protected boolean occupied = false;
    protected boolean outside = true;
    protected short X, Y;
    protected int multiplier = 1;
    protected Modified_Buffer modified;
    protected Hops_per_step distance_per_step;
    

    public abstract byte get_type_without_neighbor(int neigh_pos);

    public abstract void deposit(boolean force_nucleation);

    public abstract void extract();

    public abstract boolean two_terrace_together();

    public abstract Abstract_2D_diffusion_atom choose_random_hop();

    public abstract int getOrientation();

    public abstract void update_all_rates();

    public abstract void update_one_bound(int bond);
    
    public abstract void clear();
    
    public abstract void initialize(Abstract_2D_diffusion_lattice lattice,double[][] probabilities, Modified_Buffer modified) ;

        
    @Override
    public boolean isRemoved() {
       return !occupied;
    }

    public short getX() {
        return X;
    }

    public short getY() {
        return Y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean is_outside() {
        return outside;
    }

    public void setOutside(boolean outside) {
        this.outside = outside;
    }

    @Override
    public double getProbability() {
        return total_probability;
    }

    public boolean isOccupied() {
        return occupied;
    }
    
    /*
    public void unOccupy(){
    occupied=false;}*/

    
    
    public double getProbability(int pos) {
        if (bonds_probability != null) {
            return bonds_probability[pos];
        } else {
            return total_probability / getNeighbourCount();
        }
    }

    public byte getType() {
        return type;
    }

    public void setMultiplier(int multiplier) {
        
        this.multiplier = multiplier;
    }

    public float multiplier() {
        return multiplier;
    }

    public abstract int getNeighbourCount();
    
    protected void initialize(double[][] probabilities, Modified_Buffer modified)
    {
        this.probabilities=probabilities;
        this.modified=modified;
    }
    
    
    
}