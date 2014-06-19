/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.etching.basic_KMC;

import Kinetic_Monte_Carlo.KMC_core.etching.Abstract_etching_KMC;
import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.atom.etching.basic.Basic_atom;
import Kinetic_Monte_Carlo.lattice.etching.basic.Basic_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import java.util.ListIterator;

/**
 *
 * @author Nestor
 */
public class Basic_KMC extends Abstract_etching_KMC {

    
    private double minHeight;
    
    public Basic_KMC(List_configuration listCconfig,int sizeX,int sizeY) {
        super(listCconfig);
        lattice = new Basic_lattice(sizeX,sizeY);     
        
    }

    //this models ignore the deposition rate
    @Override
    public void initializeRates(double[] rates){

        lattice.setProbabilities(rates);   
        list.reset();
        lattice.reset();
        minHeight=4;
        
        for (int i=0;i<lattice.getSizeX();i++){
            for (int j=0;j<lattice.getSizeY();j++){
                for (int k=0;k<lattice.getSizeZ();k++){
                    for (int l=0;l<lattice.getSizeUC();l++){
                        
           Basic_atom atom= (Basic_atom)lattice.getAtom(i, j, k, l);
           if (atom.getType()<4 && atom.getType()>0 && !atom.isRemoved()) {
               list.add_Atom(atom);
           }
                        
        }}}}            
    }

    @Override
    protected boolean perform_simulation_step() {

                Basic_atom atom = (Basic_atom) list.next_event(RNG);
        if (atom.getY() > lattice.getSizeY()-minHeight) {

            return true;
        }
      
        atom.remove();
        atom.setOnList(null);
      for (int k = 0; k < 4; k++) {
            if (atom.getHeighbor(k).getType()== 3) {
                list.add_Atom(atom.getHeighbor(k));
            }
        }
        return false;
        
    }

    @Override
    public void getSampledSurface(float[][] surface) 
    {
      int binY=surface.length;
      int binX=surface[0].length;
        
        double scaleX=binX/(float)lattice.getSizeX();
        ListIterator<Abstract_atom> iterator=list.getIterator();
        
        while (iterator.hasNext())
        {
            Basic_atom atom=(Basic_atom)iterator.next();
            int sampledPosX=(int)(atom.getX()*scaleX);
            if (surface[0][sampledPosX]<atom.getY()) 
                surface[0][sampledPosX]=atom.getY();
        }
    
    } 
}
