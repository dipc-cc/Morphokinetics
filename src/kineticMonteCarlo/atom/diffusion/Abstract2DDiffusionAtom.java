/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.diffusion;

import kineticMonteCarlo.atom.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionAtom extends AbstractAtom {

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + X;
		result = prime * result + Y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		Abstract2DDiffusionAtom other = (Abstract2DDiffusionAtom) obj;
		if (X != other.X)
			return false;
		if (Y != other.Y)
			return false;
		return true;
	}

	protected byte type;
    protected double[][] probabilities;
    protected double total_probability;
    protected double[] bonds_probability;
    protected float angle;
    protected boolean occupied = false;
    protected boolean outside = true;
    protected short X, Y;
    protected int multiplier = 1;
    protected ModifiedBuffer modified;
    protected HopsPerStep distance_per_step;
   

    public abstract byte get_type_without_neighbor(int neigh_pos);

    public abstract void deposit(boolean force_nucleation);

    public abstract void extract();

    public abstract boolean two_terrace_together();

    public abstract Abstract2DDiffusionAtom choose_random_hop();

    public abstract int getOrientation();

    public abstract void update_all_rates();

    public abstract void update_one_bound(int bond);
    
    public abstract void clear();
    
    public abstract void initialize(Abstract2DDiffusionLattice lattice,double[][] probabilities, ModifiedBuffer modified);

        
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
       
    
    public double getProbability(int pos) {
        if (bonds_probability != null) {
            return bonds_probability[pos];
        } else {
            return total_probability / getNeighbourCount();
        }
    }

    @Override
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
    
    protected void initialize(double[][] probabilities, ModifiedBuffer modified)
    {
        this.probabilities=probabilities;
        this.modified=modified;
    }
    
    
    
}