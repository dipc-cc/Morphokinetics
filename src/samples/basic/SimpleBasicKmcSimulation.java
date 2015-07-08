package samples.basic;


import graphicInterfaces.basic.BasicFrame;
import Kinetic_Monte_Carlo.KMC_core.etching.basic_KMC.Basic_KMC;
import Kinetic_Monte_Carlo.list.List_configuration;
import ratesLibrary.basic.BasicEtchRatesFactory;
import utils.Wait;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nestor
 */
public class SimpleBasicKmcSimulation {
    
    
    public static void main(String args[]){
    
      System.out.println("Simple simulation of the Basic KMC");  
        
      List_configuration listConfig=  new List_configuration()
              .setList_type(List_configuration.BINNED_LIST)
              .setBins_per_level(100)
              .set_extra_levels(0);
              
      Basic_KMC KMC=new Basic_KMC(listConfig,512,128);
      
      BasicFrame panel=new BasicFrame(3);  
      
      KMC.initializeRates(new BasicEtchRatesFactory().getRates("Basic_OTHER", 350));
      
      
      for (int i=0;i<1000;i++){
      
      KMC.simulate(500);
      panel.drawKMC(KMC);
      Wait.manymsec(300);
      }
           
    }
    
    
    
    
}
