/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

import geneticAlgorithm.restrictions.BoundedGeneRestriction;
import geneticAlgorithm.restrictions.FixedGeneRestriction;
import geneticAlgorithm.restrictions.ReplicatedGeneRestriction;
import geneticAlgorithm.restrictions.RestrictionOperator;
import java.util.ArrayList;

/**
 *
 * @author Nestor
 */
public class SiRestriction extends RestrictionOperator {

  public SiRestriction() {

    genesRestriction = new ArrayList();

    for (int n1 = 0; n1 < 4; n1++) {
      for (int n2 = 0; n2 < 16; n2++) {
        int currentGene = n1 * 16 + n2;
        genesRestriction.add(new BoundedGeneRestriction(1e-8, 1.0, currentGene));

        if (n1 == 0 || n2 == 0) {
          genesRestriction.add(new FixedGeneRestriction(1.0, currentGene));
        }
        if (n1 == 4) {
          genesRestriction.add(new FixedGeneRestriction(1e-8, currentGene));
        }
        if (n1 == 1) {
          genesRestriction.add(new ReplicatedGeneRestriction(1 * 16 + 1, currentGene));
        }
        if (n1 == 2 && n2 != 8 && n2 != 7) {
          genesRestriction.add(new ReplicatedGeneRestriction(2 * 16 + 1, currentGene));
        }
        if (n1 == 3 && n2 != 9) {
          genesRestriction.add(new ReplicatedGeneRestriction(3 * 16 + 1, currentGene));
        }

      }
    }
  }

  /*        
   double[] rates ={1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,1.0
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,2.3468367615935622E-4
   ,1.0
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.11
   ,0.020402569297370686
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,0.19841374831740952
   ,1.0
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,3.473193543434439E-8
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4
   ,2.474119175993515E-4}; 
   for(int it=0;it<64;it++)  individual.setGene(it, rates[it]);

   */
  /*
   double[] ratesBad={1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,0.00218210390261232,1,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.150378557818612,0.208070792237219,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,0.362206502188478,1,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,7.74898197837514e-05,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482,0.000375270668236482}; 
   for(int it=0;it<64;it++)   individual.setGene(it, ratesBad[it]);
   */
  @Override
  public void initialize() {

  }

}
