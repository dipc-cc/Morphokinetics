/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.etching.basic;

import Kinetic_Monte_Carlo.atom.etching.Abstract_etching_atom;

/**
 *
 * @author Nestor
 */
public class Basic_atom extends Abstract_etching_atom {



    protected double[] probs;
    protected Basic_atom[] neighs = new Basic_atom[4];
    protected byte type;
    protected boolean removed = false;
    protected short X,Y;

    public short getX() {
        return X;
    }

    public short getY() {
        return Y;
    }
    
    public Basic_atom(short X, short Y) {
        this.X = X;
        this.Y = Y;
    }
    
    public void setNeighbor(Basic_atom a, int pos) {
        neighs[pos] = a;
    }

    public Basic_atom getHeighbor(int pos) {
        return neighs[pos];
    }
        
    public byte getType() {
        return type;
    }
    
    public void set_bulk() {
        type = 3;
    }
    
    public void updateType_from_scratch(){
        type=0;
        for (int i=0;i<4;i++){
            if (neighs[i]!=null && !neighs[i].isRemoved()) 
                type++;
        }
    }
    
    public void remove1st(){
        
    type--; 
    if (type<3 && !removed && list!=null) 
        list.addTotalProbability(probs[type]-probs[type+1]); 
    }

    
    public void remove(){

    if (!removed) {
       if (list!=null)  list.addTotalProbability  (-probs[type]);
        removed=true;
        for (int i=0;i<4;i++){
            if (neighs[i]!=null) neighs[i].remove1st();}
    }
}

    @Override
    public double getProbability() {
         return probs[type];
    }

    @Override
    public boolean isEligible() {
        return (type>0 && type<4);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }
    public void unRemove() {
        removed = false;
    }

}
