/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth;


import Kinetic_Monte_Carlo.KMC_core.diffusion.Abstract_2D_diffusion_KMC;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Round_perimeter;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaAccelerator;
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
/*
            int Xcenter = lattice.getSizeY() / 2;
            int Ycenter = (lattice.getSizeX() / 2);
            for (int j = -1; j < 2; j++) {
                for (int i = -1; i < 1; i++) {
                    this.depositAtom(Ycenter + i, Xcenter + j);
                }
            }*/
        } else {
            /*
            for (int i = 0; i < 3; i++) {
                int X = (int) (StaticRandom.raw() * lattice.getSizeX());
                int Y = (int) (StaticRandom.raw() * lattice.getSizeY());
                depositAtom(X, Y);
            }*/
        }
    }

    private void configureDevitaAccelerator(Hops_per_step distance_per_step) {
        this.accelerator = new DevitaAccelerator(this.lattice, distance_per_step);
    }

}
