/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.worker;

import graphicInterfaces.siliconEtching.SiliconFrame;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.KMC_core.worker.KMC_worker;
import Kinetic_Monte_Carlo.KMC_core.worker.IFinish_listener;
import Kinetic_Monte_Carlo.list.List_configuration;
import Rates_library.Si_etching.Si_etch_rates_factory;

/**
 *
 * @author Nestor
 */
public class SimpleKMCSimulationWithWorker implements IFinish_listener {

    
    private static KMC_worker worker;
    
    
    public static void main(String args[]){
        
        System.out.println("Simple simulation of a KMC using a non-blocking threaded worker");  
        
        int worker_ID=0;
        int work_ID=0;
        
        
        Si_etching_KMC_config config = configKMC();
        
        worker=new KMC_worker(new Si_etching_KMC(config),
                                         worker_ID); 
        worker.start();
                
        worker.initialize( new Si_etch_rates_factory().getRates("Gosalvez_PRE", 350));
     
        System.out.println("Launching worker...");
        worker.simulate(new SimpleKMCSimulationWithWorker(),work_ID);
        System.out.println("Continuing execution.");  
    }

    private static Si_etching_KMC_config configKMC() {
        List_configuration listConfig=  new List_configuration()
          .setList_type(List_configuration.BINNED_LIST)
          .setBins_per_level(16)
          .set_extra_levels(1);
        Si_etching_KMC_config config = new Si_etching_KMC_config()
                                    .setMillerX(1)
                                    .setMillerY(0)
                                    .setMillerZ(0)
                                    .setSizeX_UC(128)
                                    .setSizeY_UC(128)
                                    .setSizeZ_UC(32)
                                    .setListConfig(listConfig);
        return config;
    }
    
    
    @Override
    public void handleSimulationFinish(int workerID,int work_ID) {
       System.out.println("Worker simulation finished."); 
       new SiliconFrame().drawKMC(worker.getKMC()); 
       worker.destroy();
       
    }
    
    
}
