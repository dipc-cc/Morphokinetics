package Samples.basic;


import Graphic_interfaces.basic.Basic_frame;
import Kinetic_Monte_Carlo.KMC_core.etching.basic_KMC.Basic_KMC;
import Kinetic_Monte_Carlo.list.List_configuration;
import Rates_library.basic.Basic_etch_rates_factory;
import utils.Wait;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nestor
 */
public class SimpleBasicKMCSimulation {
    
    
    public static void main(String args[]){
    
      System.out.println("Simple simulation of the Basic KMC");  
        
      List_configuration config=  new List_configuration()
              .setList_type(List_configuration.BINNED_LIST)
              .setBins_per_level(100)
              .set_extra_levels(0);
              
      Basic_KMC KMC=new Basic_KMC(config,512,128);
      
      Basic_frame panel=new Basic_frame(1);  
      
      KMC.initializeRates(new Basic_etch_rates_factory().getRates("Basic_OTHER", 350));
      
      
      for (int i=0;i<100;i++){
      
      KMC.simulate(1000);
      panel.drawKMC(KMC);
      Wait.manymsec(100);
      }
           
    }
    
    
    
    
}
