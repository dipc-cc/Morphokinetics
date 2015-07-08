package samples.basic;


import graphicInterfaces.basic.BasicFrame;
import kineticMonteCarlo.kmcCore.etching.basicKmc.BasicKmc;
import kineticMonteCarlo.list.ListConfiguration;
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
        
      ListConfiguration listConfig=  new ListConfiguration()
              .setList_type(ListConfiguration.BINNED_LIST)
              .setBins_per_level(100)
              .set_extra_levels(0);
              
      BasicKmc KMC=new BasicKmc(listConfig,512,128);
      
      BasicFrame panel=new BasicFrame(3);  
      
      KMC.initializeRates(new BasicEtchRatesFactory().getRates("Basic_OTHER", 350));
      
      
      for (int i=0;i<1000;i++){
      
      KMC.simulate(500);
      panel.drawKMC(KMC);
      Wait.manymsec(300);
      }
           
    }
    
    
    
    
}
