/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.etching.basic;

import kineticMonteCarlo.atom.etching.AbstractEtchingAtom;

/**
 *
 * @author Nestor
 */
public class BasicAtom extends AbstractEtchingAtom {




    protected BasicAtom[] neighs = new BasicAtom[4];
    protected byte type;
    protected boolean removed = false;
    protected short X,Y;

    public short getX() {
        return X;
    }

    public short getY() {
        return Y;
    }
    
    public BasicAtom(short X, short Y) {
        this.X = X;
        this.Y = Y;
    }
    
    public void setNeighbor(BasicAtom a, int pos) {
        neighs[pos] = a;
    }

    public BasicAtom getHeighbor(int pos) {
        return neighs[pos];
    }
        
    @Override
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
        list.addTotalProbability(probabilities[type]-probabilities[type+1]); 
    }

    
    public void remove(){

    if (!removed) {
       if (list!=null)  list.addTotalProbability  (-probabilities[type]);
        removed=true;
        for (int i=0;i<4;i++){
            if (neighs[i]!=null) neighs[i].remove1st();}
    }
}

    @Override
    public double getProbability() {
         return probabilities[type];
    }

    @Override
    public boolean isEligible() {
        return (type>=0 && type<4);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }
    public void unRemove() {
        removed = false;
    }

}
