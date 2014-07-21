/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.etching.Si_etching;

import Kinetic_Monte_Carlo.atom.etching.Abstract_etching_atom;

/**
 *
 * @author U010531
 */
public class Si_atom extends Abstract_etching_atom {


    //we reduce the amount of memory use by not using an array neighbour[4] and directly adding the neighbours as part of the object
    protected Si_atom neighbor0;
    protected Si_atom neighbor1;
    protected Si_atom neighbor2;
    protected Si_atom neighbor3;
    
    protected byte n1;
    protected byte n2;
    protected boolean removed = false;
    protected float X, Y, Z;
    

    public Si_atom(float X, float Y, float Z) {

        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getZ() {
        return Z;
    }

    public Si_atom getNeighbor(int pos){
        switch(pos){
            case 0:  return neighbor0;
            case 1:  return neighbor1;
            case 2:  return neighbor2;
            default: return neighbor3;         
        }
    }
    
    public void setNeighbor(Si_atom a, int pos) {
                switch(pos){
            case 0:   neighbor0=a; break;
            case 1:   neighbor1=a; break;
            case 2:   neighbor2=a; break;
            default:  neighbor3=a; break;         
        }
    }

    

    @Override
    public byte getType(){return (byte)((n1<<4)+n2) ;}

    public byte getn1() {
        return n1;
    }

    public byte getn2() {
        return n2;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    private void remove1st() {
        n1--;

        if (n1 < 3 && list!=null) { 
            list.addTotalProbability(probabilities[n1 * 16 + n2] - probabilities[(n1 + 1) * 16 + n2]);  
        } 
    }

    private void remove2nd() {

        n2--;
        if (n1 < 4 && list!=null) {
            list.addTotalProbability(probabilities[n1 * 16 + n2] - probabilities[n1 * 16 + n2 + 1]);
        } 
    }

    public void updaten1_from_scratch() {

        n1 = 0;
        for (int i = 0; i < 4; i++) {
            if (getNeighbor(i) != null && !getNeighbor(i).isRemoved()) {
                n1++;
            }
        }
    }

    public void updaten2_from_scratch() {
        
        n2 = 0;
        for (int i = 0; i < 4; i++) {
            if (getNeighbor(i) != null) {
                n2 += getNeighbor(i).getn1();
                if (!removed) {
                    n2--;
                }
            }
        }
    }

    public void set_as_bulk() {

        n1 = 4;
        n2 = 12;
    }

    public void remove() {

        if (!removed) {
            if (n1 < 4 && list!=null)    list.addTotalProbability(-probabilities[n1 * 16 + n2]);
            
            removed = true;

            for (int i = 0; i < 4; i++) {
                Si_atom atom1st=getNeighbor(i);
                if (atom1st != null) {
                        atom1st.remove1st();
                        for (int j = 0; j < 4; j++) {
                        Si_atom atom2nd = atom1st.getNeighbor(j);
                        if (atom2nd != null && atom2nd != this && !atom2nd.isRemoved()) {
                            atom2nd.remove2nd();
                        }
                    }
                }
            }
        }
    }

    public void unRemove() {
        removed = false;
    }

    @Override
    public double getProbability() {
        return probabilities[n1 * 16 + n2];
    }

    @Override
    public boolean isEligible() {
        return probabilities[n1 * 16 + n2] > 0 && probabilities[n1 * 16 + n2] < 4;
    }
}
