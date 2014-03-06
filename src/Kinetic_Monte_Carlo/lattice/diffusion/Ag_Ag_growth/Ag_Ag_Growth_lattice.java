/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.diffusion.Ag_Ag_growth;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.atom.diffusion.Ag_Ag_growth.AgAg_Atom;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.atom.diffusion.Modified_Buffer;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import java.awt.geom.Point2D;

/**
 *
 * @author Nestor
 */
public class Ag_Ag_Growth_lattice extends Abstract_2D_diffusion_lattice {
    
    
private final Modified_Buffer modified;
public static final float Y_ratio=(float)Math.sqrt(3)/2.0f;


    public Ag_Ag_Growth_lattice(int sizeX, int sizeY, Modified_Buffer modified, Hops_per_step distance_per_step) {

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        sizeZ = 1;
        unit_cell_size = 4;
        atoms = new AgAg_Atom[sizeX][sizeY];
        this.modified = modified;

        create_atoms(distance_per_step);
        setAngles();
    }


    private void create_atoms(Hops_per_step distance_per_step) {
        instantiate_atoms(distance_per_step);   
        interconnect_atoms();
    }
    
    private void instantiate_atoms(Hops_per_step distance_per_step) {
        for(int i=0;i<sizeX;i++){
           for(int j=0;j<sizeY;j++){
               atoms[i][j]=new AgAg_Atom((short)i,(short)j,distance_per_step);
               }}
    }
    
    private void interconnect_atoms(){

       for(int j=0;j<sizeY;j++){
       for(int i=0;i<sizeX;i++){
         AgAg_Atom atom=(AgAg_Atom)atoms[i][j];
        int X=i;
        int Y=j-1;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 0);
        X=i+1;
        Y=j-1;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 1);
         X=i+1;
         Y=j;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 2);
         X=i;
         Y=j+1;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 3);
         X=i-1;
         Y=j+1;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 4);
         X=i-1;
         Y=j;
        if (X<0) X=sizeX-1; if (X==sizeX) X=0;
        if (Y<0) Y=sizeY-1; if (Y==sizeY) Y=0;
        atom.setNeighbor((AgAg_Atom)atoms[X][Y], 5);   
}}
}  
    
    
    @Override
    public void configure(double[][] probabilities) {

        for (int i = 0; i < atoms[0].length; i++) { //X
            for (int j = 0; j < atoms.length; j++) { //Y   

                atoms[j][i].initialize(this, probabilities, modified);
            }
        }
    }

    @Override
    public Abstract_2D_diffusion_atom getNeighbor(int Xpos, int Ypos, int neighbor) {
       return ((AgAg_Atom)atoms[Xpos][Ypos]).getNeighbor(neighbor);
    }
    
    
    @Override
    public int getAvailableDistance(int atomType, short Xpos, short Ypos, int thresholdDistance) {

        int[] point = new int[2];
        switch (atomType) {
            default:
                return 0;
        }
    }

    @Override
    public Abstract_2D_diffusion_atom getFarAtom(int originType, short Xpos, short Ypos, int distance) {

        int[] point = new int[2];
        switch (originType) {
            default:
                return null;
        }
    }  
    
    @Override
    public Abstract_2D_diffusion_atom getAtom(int X, int Y) {
        return atoms[X][Y];
    }

    @Override
    public Point2D getCentralLatticeLocation() {
       return new Point2D.Float(sizeX/2.0f,(float)(sizeY*Y_ratio/2.0));
    }
    
    
    @Override
    public Point2D getSpatialLocation(int Xpos, int Ypos) {

           float XLocation=Xpos+Ypos*0.5f;
           if (XLocation>=sizeX) XLocation-=sizeX;
           float YLocation=Ypos*Y_ratio;
           return new Point2D.Double(XLocation, YLocation);
    } 
}
