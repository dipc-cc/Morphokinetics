/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

/**
 * We have only 10 genes, which correspond to all different rates we can find in an Ag/Ag
 * simulation.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReducedRestriction extends RestrictionOperator {

  public void initialize(double temperature) {

  }

  public AgReducedRestriction() {
    //Negative and 0 values are not valid
    for (int currentGene = 0; currentGene < 10; currentGene++) {
      genesRestriction.add(new BoundedGeneRestriction(1e-8, 1e20, currentGene));
    }

    // For deposition rate a minimum value has to be defined
    genesRestriction.add(new BoundedGeneRestriction(100, 1e20, 0));
  }

  @Override
  public void initialise() {

  }

}
