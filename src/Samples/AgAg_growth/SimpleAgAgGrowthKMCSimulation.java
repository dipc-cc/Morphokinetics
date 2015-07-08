/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.AgAg_growth;

import graphicInterfaces.difussion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.difussion2DGrowth.DifussionKmcFrame;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import ratesLibrary.diffusion.agAgGrowth.AgAgGrowthRatesFactory;

/**
 *
 * @author Nestor
 */
public class SimpleAgAgGrowthKMCSimulation {
   
  public static float constant_Y=(float)Math.sqrt(3)/2.0f;
    
     public static void main(String args[]) {
         
       System.out.println("Simple simulation of the Ag/Ag growth KMC");
       
        AgAgGrowthRatesFactory ratesFactory = new AgAgGrowthRatesFactory();

        Ag_Ag_KMC kmc = initialize_kmc();
         
        
        DifussionKmcFrame frame = create_graphics_frame(kmc);
        frame.setVisible(true);
        
        for (int simulations=0;simulations<10;simulations++){
        initializeRates(ratesFactory,kmc); 
        kmc.simulate();
        }
     }     
     
     private static DifussionKmcFrame create_graphics_frame(Ag_Ag_KMC kmc) {
        DifussionKmcFrame frame = new DifussionKmcFrame(new AgAgKmcCanvas((Abstract_2D_diffusion_lattice) kmc.getLattice()));
        return frame;
    }
     
     
    private static Ag_Ag_KMC initialize_kmc() {

        List_configuration config = new List_configuration()
                .setList_type(List_configuration.LINEAR_LIST);

        int sizeX = 256;
        int sizeY = (int) (sizeX /constant_Y);

        Ag_Ag_KMC kmc = new Ag_Ag_KMC(config, (int)(sizeX*1.71), (int)(sizeY*1.71), true);
        
       
        return kmc;
    }
    
    
    private static void initializeRates(AgAgGrowthRatesFactory reatesFactory, Ag_Ag_KMC kmc) {

        double deposition_rate = reatesFactory.getDepositionRate("COX_PRB", 135);
        double island_density = reatesFactory.getIslandDensity("COX_PRB", 135);
        kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
        kmc.initializeRates(reatesFactory.getRates("COX_PRB", 135));

    }
    
}
