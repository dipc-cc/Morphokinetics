/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.AbstractPerimeterStatistics;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Nestor
 */
public class Graphene_perimeter_statistics extends AbstractPerimeterStatistics {

    
    
public Graphene_perimeter_statistics(){
    
  this.totalCount=Raw_statisticData_atomCount_1Million.data[0][180];
  this.atomsCountMap=new HashMap();
  this.hopsCountMap=new HashMap();
  int radius=20;
  
  for (int i=0;i<Raw_statisticData_atomCount_1Million.data.length;i++){
      Map<Integer,Integer> currentRadiusCountMap=new HashMap();
      Map<Integer,Integer> currentRadiusHopMap=new HashMap();
      
      this.atomsCountMap.put(radius, currentRadiusCountMap);
      this.hopsCountMap.put(radius, currentRadiusHopMap);
      for (int j=0;j<180;j++){
          currentRadiusCountMap.put(j, Raw_statisticData_atomCount_1Million.data[i][j]);
          currentRadiusHopMap.put(j, Raw_statisticData_hopsCount_1Million.data[i][j]);
      }
      radius+=5;
  }
}
        
}
