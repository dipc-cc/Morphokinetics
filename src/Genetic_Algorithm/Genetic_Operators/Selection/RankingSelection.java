/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Selection;

import Genetic_Algorithm.Couple;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public class RankingSelection implements ISelection {
 
    
  private  float   SP = 2f; //selective pressure, choose in interval [1, 2]  
    
  
   public Couple[] Select(Population p, int couples){
    
    float[] Fitness=new float[p.size()];
    for(int i=0;i<p.size();i++){Fitness[p.size()-i-1]=2.0f - SP + 2.0f*(SP - 1.0f)*(i)/(p.size() - 1.0f); }

    Couple[] Couples=new Couple[couples];
   
    for(int i=0;i<couples;i++){
        Couples[i]=new Couple();
        Couples[i].individual1=p.getIndividual(linearSearch(Fitness));
        
        do{ Couples[i].individual2=p.getIndividual(linearSearch(Fitness));}  
        while(Couples[i].individual1==Couples[i].individual2);
   }
   return Couples;
   }
    
   
   
   
    private int linearSearch(float[] probs){

float total=0;
for(int i=0;i<probs.length;i++){total+=probs[i];}

float selected=((float)utils.StaticRandom.raw())*total;

float acc=0;
int i;
for(i=0;i<probs.length;i++){
acc+=probs[i];
if (acc>selected) return i;
}
return i;
}
   
   
}
