/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.diffusion.grapheneCvdGrowth;

import ratesLibrary.IRatesFactory;
import ratesLibrary.diffusion.IDiffusionRates;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class GrapheneCvdDepositionRatesFactory implements IRatesFactory{
    
    
   private static Map<String,IDiffusionRates> experiments;
   
   
  public GrapheneCvdDepositionRatesFactory(){
  
    experiments=new HashMap();  
    experiments.put("synthetic", new SyntheticRates());
  }  
  
  
  //we don't use the temperature by now.
    @Override
 public double[] getRates(String experimentName, double temperature){
    
   IDiffusionRates experiment=experiments.get(experimentName);
   double[] rates=new double[64];
        
     for(int i=0;i<8;i++){
     for(int j=0;j<8;j++){
        rates[i*8+j]=(experiment.getRate(i, j,temperature));
      }}
     return rates;       
    }
    
   @Override
    public double getDepositionRate(String experimentName, double temperature){
        
        return experiments.get(experimentName).getDepositionRate();
    }
    
   @Override
    public double getIslandDensity(String experimentName, double temperature){
        
        return experiments.get(experimentName).getIslandsDensityML(temperature);
    }
    
    
}
