/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Restrictions.ag_ag;

import Genetic_Algorithm.Genetic_Operators.Restrictions.Bounded_gene_restriction;
import Genetic_Algorithm.Genetic_Operators.Restrictions.Fixed_gene_restriction;
import Genetic_Algorithm.Genetic_Operators.Restrictions.Replicated_gene_restriction;
import Genetic_Algorithm.Genetic_Operators.Restrictions.RestrictionOperator;

/**
 *
 * @author Nestor
 */
public class Ag_Ag_restriction extends RestrictionOperator {
    
        
    public void initialize(double temperature){
        
    }

    public Ag_Ag_restriction(double diffusion_rate) {
        
        
        //negative values are not valid
    for (int currentGene=0;currentGene<7*7;currentGene++){
         genesRestriction.add(new Bounded_gene_restriction(0, 1e20, currentGene));
    }
    
  //Diffusion rate
    for (int i=0;i<7;i++){
    
    genesRestriction.add(new Fixed_gene_restriction(diffusion_rate, 0*7+i));
    }
    
    
   //non-mobile dimers
   genesRestriction.add(new Fixed_gene_restriction(0, 1*7+0)); 
    
   genesRestriction.add(new Fixed_gene_restriction(0, 2*7+0)); 
   genesRestriction.add(new Fixed_gene_restriction(0, 2*7+1)); 
   
   genesRestriction.add(new Fixed_gene_restriction(0, 5*7+0)); 
   genesRestriction.add(new Fixed_gene_restriction(0, 5*7+1)); 
   
   
for (int j=0;j<7;j++){ 
                        genesRestriction.add(new Fixed_gene_restriction(0, 3*7+j)); 
                        genesRestriction.add(new Fixed_gene_restriction(0, 4*7+j)); 
                        genesRestriction.add(new Fixed_gene_restriction(0, 6*7+j));}
   
     
//We set the following atomistic configurations to the same rate (according to the Ag/Ag diffuion paper):
//(2,3)=(2,4)=(2,5)=(2,6)=(5,2)=(5,3)=(5,4)=(5,6)


genesRestriction.add(new Replicated_gene_restriction(2*7+3,2*7+4));
genesRestriction.add(new Replicated_gene_restriction(2*7+3,2*7+5));
genesRestriction.add(new Replicated_gene_restriction(2*7+3,2*7+6));

genesRestriction.add(new Replicated_gene_restriction(2*7+3,5*7+2));
genesRestriction.add(new Replicated_gene_restriction(2*7+3,5*7+3));
genesRestriction.add(new Replicated_gene_restriction(2*7+3,5*7+4));
genesRestriction.add(new Replicated_gene_restriction(2*7+3,5*7+6));

}
    
    
    
    /*
    @Override
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
  */  

    @Override
    public void initialize() {
    
    }
    
}
