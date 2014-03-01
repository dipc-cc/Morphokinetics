/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class KMCRatesConfig {
    
 public static int PROCESS_RATES=1;
 public static int DEPOSITION_RATE=2;
 public static int ISLAND_DENSITY=3;
 
 private Map<Integer,Object> data=new HashMap();
    
  
 public Object getConfigData(int config_type){
     return data.get(config_type);
     
 }
 
 public KMCRatesConfig addConfig(int config_type,Object config){
     data.put(config_type, config);
     return this;
 }
 
}
