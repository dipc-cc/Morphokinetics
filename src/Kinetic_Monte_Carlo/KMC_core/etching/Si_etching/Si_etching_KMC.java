/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.etching.Si_etching;

import Kinetic_Monte_Carlo.KMC_core.etching.Abstract_etching_KMC;
import Kinetic_Monte_Carlo.atom.etching.Si_etching.Si_atom;
import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.lattice.etching.Si_etching.Si_lattice;
import java.util.ListIterator;
import utils.MathUtils;

/**
 *
 * @author Nestor
 */
public class Si_etching_KMC extends Abstract_etching_KMC {

    private final double minHeight;

    public Si_etching_KMC(Si_etching_KMC_config config) {
        super(config.listConfig);
        lattice = new Si_lattice(config.millerX, config.millerY, config.millerZ,
                config.sizeX_UC, config.sizeY_UC, config.sizeZ_UC);

        
        minHeight = ((Si_lattice) lattice).getUnit_Cell().getLimitZ();
    }

    
    //this models ignore the deposition rate
    @Override
    public void initializeRates(double[] rates) {
        
        
        lattice.setProbabilities(rates);
        lattice.reset();
        list.reset();

        for (int i = 0; i < lattice.getSizeX(); i++) {
            for (int j = 0; j < lattice.getSizeY(); j++) {
                for (int k = 0; k < lattice.getSizeZ(); k++) {
                    for (int l = 0; l < lattice.getSizeUC(); l++) {

                        Si_atom atom = (Si_atom) lattice.getAtom(i, j, k, l);
                        if (atom.getn1() < 4 && atom.getn1() > 0 && !atom.isRemoved()) {

                            list.add_Atom(atom);
                        }
                    }
                }
            }
        }
    }
  

    @Override
    protected boolean perform_simulation_step() {
        Si_atom atom = (Si_atom) list.next_event(RNG);
        atom.remove();
        atom.setOnList(null);
        for (int k = 0; k < 4; k++) {
            Si_atom neighbor=atom.getNeighbor(k);
            if (neighbor.getn1() == 3) {
                list.add_Atom(neighbor);
            }
        }
        return atom.getZ() < minHeight*2;
    }
    
    @Override
    public void getSampledSurface(float[][] surface) 
    {
        int binY=surface.length;
        int binX=surface[0].length;
        
        double scaleX=binX/(lattice.getSizeX()*((Si_lattice)lattice).getUnit_Cell().getLimitX());
        double scaleY=binY/(lattice.getSizeY()*((Si_lattice)lattice).getUnit_Cell().getLimitY());
        ListIterator<Abstract_atom> iterator=list.getIterator();
        
        for (int i=0;i<binY;i++){
            for (int j=0;j<binX;j++){
            surface[i][j]=0;
            }
        }
        
        while (iterator.hasNext())
        {
            Si_atom atom=(Si_atom)iterator.next();
            int sampledPosX= (int)MathUtils.truncate(atom.getX()*scaleX,3);     
            if (sampledPosX==binX) sampledPosX--;
            int sampledPosY=(int)MathUtils.truncate(atom.getY()*scaleY,3); 
            if (sampledPosY==binY) sampledPosY--;
            
            if (surface[sampledPosY][sampledPosX]==0 || surface[sampledPosY][sampledPosX]<atom.getZ()) 
                    surface[sampledPosY][sampledPosX]=atom.getZ();   
        }
        
        
     MathUtils.fillSurfaceHoles(surface);       
    }   
}