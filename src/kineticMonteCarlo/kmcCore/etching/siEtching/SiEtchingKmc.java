/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching.siEtching;

import kineticMonteCarlo.kmcCore.etching.AbstractEtchingKmc;
import kineticMonteCarlo.atom.etching.siEtching.SiAtom;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.lattice.etching.siEtching.SiLattice;
import java.util.ListIterator;
import utils.MathUtils;

/**
 *
 * @author Nestor
 */
public class SiEtchingKmc extends AbstractEtchingKmc {

    private final double minHeight;

    public SiEtchingKmc(SiEtchingKmcConfig config) {
        super(config.listConfig);
        lattice = new SiLattice(config.millerX, config.millerY, config.millerZ,
                config.sizeX_UC, config.sizeY_UC, config.sizeZ_UC);

        
        minHeight = ((SiLattice) lattice).getUnit_Cell().getLimitZ();
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

                        SiAtom atom = (SiAtom) lattice.getAtom(i, j, k, l);
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
        SiAtom atom = (SiAtom) list.next_event(RNG);
        atom.remove();
        atom.setOnList(null);
        for (int k = 0; k < 4; k++) {
            SiAtom neighbor=atom.getNeighbor(k);
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
        
        double scaleX=binX/(lattice.getSizeX()*((SiLattice)lattice).getUnit_Cell().getLimitX());
        double scaleY=binY/(lattice.getSizeY()*((SiLattice)lattice).getUnit_Cell().getLimitY());
        ListIterator<AbstractAtom> iterator=list.getIterator();
        
        for (int i=0;i<binY;i++){
            for (int j=0;j<binX;j++){
            surface[i][j]=0;
            }
        }
        
        while (iterator.hasNext())
        {
            SiAtom atom=(SiAtom)iterator.next();
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