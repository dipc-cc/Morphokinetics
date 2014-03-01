/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Restrictions.ag_ag;

import Genetic_Algorithm.Genetic_Operators.Restrictions.IRestriction;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public abstract class Ag_Ag_restriction implements IRestriction {
    
    
    
    private final double Pd=10e11; //proposed adatom diffusion prefactor
    private final double Ed=0.1;   //proposed adatom diffusion activation energy
    
    private double diffusion_rate;
    
    public void initialize(double temperature){
    
  //  diffusion_rate=(Pd*Math.exp(-Ed/(kB*temperature)));
    
    }
    
    
        public void apply(Population p) {
        
            
for (int ind=0;ind<p.size();ind++){
   
Individual i=p.getIndividual(ind);
 
//negative values are not valid
for (int a=0;a<i.getGeneSize();a++)   if (i.getGene(a)<0.0) i.setGene(a,0.0);

//Diffusion rate
i.setGene(0*7+0,diffusion_rate);
i.setGene(0*7+1,diffusion_rate);
i.setGene(0*7+2,diffusion_rate);
i.setGene(0*7+3,diffusion_rate);
i.setGene(0*7+4,diffusion_rate);
i.setGene(0*7+5,diffusion_rate);
i.setGene(0*7+6,diffusion_rate);     


//non-mobile dimers
i.setGene(1*7+0,0.0);


i.setGene(2*7+0,0.0);
i.setGene(2*7+1,0.0);

i.setGene(5*7+0,0.0);
i.setGene(5*7+1,0.0);

for (int j=0;j<7;j++){  i.setGene(3*7+j,0.0);
                        i.setGene(4*7+j,0.0);
                        i.setGene(6*7+j,0.0);}


//We set the following atomistic configurations to the same rate (according to the Ag/Ag diffuion paper):
//(2,3)=(2,4)=(2,5)=(2,6)=(5,2)=(5,3)=(5,4)=(5,6)

double value=i.getGene(2*7+3);

i.setGene(2*7+4,value);
i.setGene(2*7+5,value);
i.setGene(2*7+6,value);


i.setGene(5*7+2,value);
i.setGene(5*7+3,value);
i.setGene(5*7+4,value);
i.setGene(5*7+6,value);
        }
        
        }
    
    
}
