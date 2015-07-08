/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Samples.AgAg_growth;

import graphicInterfaces.difussion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.difussion2DGrowth.DifussionKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import ratesLibrary.diffusion.agAgGrowth.AgAgGrowthRatesFactory;
import static Samples.AgAg_growth.SimpleAgAgGrowthKMCSimulation.constant_Y;
import utils.MathUtils;
import utils.PSD_analysis.PSD_signature_2D;

/**
 *
 * @author Nestor
 */
public class AgAgPSDTest {
    
    public static float constant_Y=(float)Math.sqrt(3)/2.0f;
    
  
         public static void main(String args[]) {
         
       System.out.println("Simple simulation of the Ag/Ag growth KMC");
       
        AgAgGrowthRatesFactory ratesFactory = new AgAgGrowthRatesFactory();

        Ag_Ag_KMC kmc = initialize_kmc();
        
        //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
        PSD_signature_2D PSD = new PSD_signature_2D(128, 128);
        float[][] sampledSurface=new float[128][128];
         
         for (int i=0;i<30;i++){
        initializeRates(ratesFactory,kmc); 
       
        kmc.simulate();
        
        
        
        kmc.getSampledSurface(sampledSurface);
               
        PSD.addSurfaceSample(sampledSurface);
        System.out.println("flake "+i);
         }
        PSD.apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
       PSD.apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);
       
        new Frame2D("PSD analysis")
        .setMesh(MathUtils.avg_Filter(PSD.getPSD(),1)  );
        
        new Frame2D("Sampled surface")
        .setMesh(sampledSurface);   
        
        
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

        Ag_Ag_KMC kmc = new Ag_Ag_KMC(config, sizeX, sizeY, true);
        
       
        return kmc;
    }
    
    
    private static void initializeRates(AgAgGrowthRatesFactory reatesFactory, Ag_Ag_KMC kmc) {

        double deposition_rate = reatesFactory.getDepositionRate("COX_PRB", 135);
        double island_density = reatesFactory.getIslandDensity("COX_PRB", 135);
        kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
        kmc.initializeRates(reatesFactory.getRates("COX_PRB", 135));

    }
    
    
}
