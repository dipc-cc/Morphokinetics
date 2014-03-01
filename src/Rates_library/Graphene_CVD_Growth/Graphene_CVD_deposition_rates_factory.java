/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library.Graphene_CVD_Growth;

import Rates_library.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class Graphene_CVD_deposition_rates_factory implements IRatesFactory{
    
    
   private static Map<String,IRates> experiments;
   
   
  public Graphene_CVD_deposition_rates_factory(){
  
    experiments=new HashMap();  
    experiments.put("synthetic", new synthetic_rates());
  }  
  
  
  //we don't use the temperature by now.
    @Override
 public double[] getRates(String experimentName, double temperature){
    
   IRates experiment=experiments.get(experimentName);
   double[] rates=new double[64];
        
     for(int i=0;i<8;i++){
     for(int j=0;j<8;j++){
        rates[i*8+j]=(experiment.getRate(i, j));
      }}
     return rates;       
    }
    
    public double getDepositionRate(String experimentName, double temperature){
        
        return experiments.get(experimentName).getDepositionRate();
    }
    
    public double getIslandDensity(String experimentName, double temperature){
        
        return experiments.get(experimentName).getIslandsDensity();
    }
    
    
}
