/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth;


import Kinetic_Monte_Carlo.KMC_core.diffusion.Abstract_2D_diffusion_KMC;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Round_perimeter;
import Kinetic_Monte_Carlo.atom.diffusion.Ag_Ag_growth.AgAg_Atom;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaAccelerator;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaHopsConfig;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.lattice.diffusion.Ag_Ag_growth.Ag_Ag_Growth_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class Ag_Ag_KMC extends Abstract_2D_diffusion_KMC {

    public Ag_Ag_KMC(List_configuration config, int sizeX, int sizeY, boolean justCentralFlake) {
        super(config, justCentralFlake);

        Hops_per_step distance_per_step = new Hops_per_step();

        this.lattice = new Ag_Ag_Growth_lattice(sizeX, sizeY, modified_buffer, distance_per_step);
        
        configureDevitaAccelerator(distance_per_step);
    }



    @Override
    protected void depositSeed() {
        
        if (justCentralFlake) {
            this.perimeter = new Round_perimeter("Ag_Ag_growth");
     
            this.perimeter.setAtomPerimeter(lattice.setInside(perimeter.getCurrentRadius()));

            int Ycenter = (lattice.getSizeY() / 2);
            int Xcenter = (lattice.getSizeX() / 2)-(lattice.getSizeY() / 4);
            
            this.depositAtom(Xcenter , Ycenter);
            this.depositAtom(Xcenter+1 , Ycenter);
            
            this.depositAtom(Xcenter-1 , Ycenter+1);
            this.depositAtom(Xcenter , Ycenter+1);
            this.depositAtom(Xcenter+1 , Ycenter+1);
            
            this.depositAtom(Xcenter , Ycenter+2);
            this.depositAtom(Xcenter-1 , Ycenter+2);
            this.depositAtom(Xcenter-1 , Ycenter+3);
                        
            
        } else {
            
            for (int i = 0; i < 3; i++) {
                int X = (int) (StaticRandom.raw() * lattice.getSizeX());
                int Y = (int) (StaticRandom.raw() * lattice.getSizeY());
                depositAtom(X, Y);
            }
        }   
    }

    private void configureDevitaAccelerator(Hops_per_step distance_per_step) {
        this.accelerator = new DevitaAccelerator(this.lattice, distance_per_step);
       
        
        this.accelerator.tryToSpeedUp(0,
                new DevitaHopsConfig()
                .setMin_accumulated_steps(100)
                .setMax_accumulated_steps(200)
                .setMin_distance_hops(1)
                .setMax_distance_hops(8));
        
        this.accelerator.tryToSpeedUp(2,
                new DevitaHopsConfig()
                .setMin_accumulated_steps(30)
                .setMax_accumulated_steps(100)
                .setMin_distance_hops(1)
                .setMax_distance_hops(5));
        
         
    }

}
