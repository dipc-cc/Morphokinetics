/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.diffusion.Graphene_CVD_growth;

import Kinetic_Monte_Carlo.KMC_core.diffusion.Abstract_2D_diffusion_KMC;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Round_perimeter;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaAccelerator;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaHopsConfig;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.lattice.diffusion.Graphene_CVD_Growth.Graphene_CVD_Growth_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class Graphene_KMC extends Abstract_2D_diffusion_KMC {

    public Graphene_KMC(List_configuration config, int sizeX, int sizeY, boolean justCentralFlake) {
        super(config, justCentralFlake);

        Hops_per_step distance_per_step = new Hops_per_step();

        this.lattice = new Graphene_CVD_Growth_lattice(sizeX, sizeY, modified_buffer, distance_per_step);
        
        if (justCentralFlake) configureDevitaAccelerator(distance_per_step);
    }


    @Override
    protected void depositSeed() {
        if (justCentralFlake) {
            this.perimeter = new Round_perimeter("Graphene_CVD_growth");
            this.perimeter.setAtomPerimeter(lattice.setInside(perimeter.getCurrentRadius()));

            int Ycenter = lattice.getSizeY() / 2;
            int Xcenter = (lattice.getSizeX() / 2);
            for (int j = -1; j < 2; j++) {
                for (int i = -1; i < 1; i++) {
                    this.depositAtom(Xcenter + i, Ycenter + j);
                }
            }
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
                .setMax_distance_hops(10));
        
        //accelerating types 2 and 3 does not improve performance and introduce some morphology differences
        
        this.accelerator.tryToSpeedUp(2,
                new DevitaHopsConfig()
                .setMin_accumulated_steps(30)
                .setMax_accumulated_steps(100)
                .setMin_distance_hops(1)
                .setMax_distance_hops(5));
        
        this.accelerator.tryToSpeedUp(3,
                new DevitaHopsConfig()
                .setMin_accumulated_steps(30)
                .setMax_accumulated_steps(100)
                .setMin_distance_hops(1)
                .setMax_distance_hops(5));
    }
}
