/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.AgAg_growth;

import Graphic_interfaces.Difussion2D_Growth.AgAg_growth.AgAgKMC_canvas;
import Graphic_interfaces.Difussion2D_Growth.DifussionKMC_frame;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import Rates_library.Ag_Ag_Growth.Ag_Ag_growth_rates_factory;

/**
 *
 * @author Nestor
 */
public class SimpleAgAgGrowthKMCSimulation {
   
  public static float constant_Y=(float)Math.sqrt(3)/2.0f;
    
     public static void main(String args[]) {
         
       System.out.println("Simple simulation of the Ag/Ag growth KMC");
       
        Ag_Ag_growth_rates_factory ratesFactory = new Ag_Ag_growth_rates_factory();

        Ag_Ag_KMC kmc = initialize_kmc();
         
        
        DifussionKMC_frame frame = create_graphics_frame(kmc);
        frame.setVisible(true);
        
        initializeRates(ratesFactory,kmc);
        
        kmc.simulate(20000000);
        
         
     }     
     
     private static DifussionKMC_frame create_graphics_frame(Ag_Ag_KMC kmc) {
        DifussionKMC_frame frame = new DifussionKMC_frame(new AgAgKMC_canvas((Abstract_2D_diffusion_lattice) kmc.getLattice()));
        return frame;
    }
     
     
    private static Ag_Ag_KMC initialize_kmc() {

        List_configuration config = new List_configuration()
                .setList_type(List_configuration.LINEAR_LIST);

        int sizeX = 256;
        int sizeY = (int) (sizeX /constant_Y);

        Ag_Ag_KMC kmc = new Ag_Ag_KMC(config, sizeX, sizeY, true);
        return kmc;
    }
    
    
    private static void initializeRates(Ag_Ag_growth_rates_factory reatesFactory, Ag_Ag_KMC kmc) {

        double deposition_rate = reatesFactory.getDepositionRate("COX_PRB", 135);
        double island_density = reatesFactory.getIslandDensity("COX_PRB", 135);
        kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
        kmc.initializeRates(reatesFactory.getRates("COX_PRB", 135));

    }
    
}
