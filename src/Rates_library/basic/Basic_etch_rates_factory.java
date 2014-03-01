/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library.basic;

import Rates_library.Si_etching.*;
import Rates_library.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class Basic_etch_rates_factory implements IRatesFactory {
    
    private static Map<String,IRates> experiments;
    private static double kB=8.617332e-5;
   
    
  public Basic_etch_rates_factory(){
  
    experiments=new HashMap();  
    experiments.put("Basic_E", new Rates_case_E());
    experiments.put("Basic_F", new Rates_case_F());
    experiments.put("Basic_OTHER", new Rates_case_OHTER());  
    experiments.put("Basic_H", new Rates_case_H());  
  }  
    
    @Override
 public double[] getRates(String experimentName, double temperature){
    
   IRates experiment=experiments.get(experimentName);
   double[] rates=new double[4];
        
     for(int i=0;i<4;i++){
        rates[i]=(experiment.getPrefactor(i)*Math.exp(-experiment.getEnergy(i)/(kB*temperature)));
      }
     return rates;       
    }
      
}
