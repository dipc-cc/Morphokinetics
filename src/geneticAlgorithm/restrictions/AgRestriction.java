/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.restrictions;

/**
 *
 * @author Nestor
 */
public class AgRestriction extends RestrictionOperator {

  public void initialize(double temperature) {

  }

  public AgRestriction(double diffusionRate) {

    //Negative values are not valid
    for (int currentGene = 0; currentGene < 7 * 7; currentGene++) {
      genesRestriction.add(new BoundedGeneRestriction(0, 1e20, currentGene));
    }

    //Diffusion rate
    for (int i = 0; i < 7; i++) {
      genesRestriction.add(new FixedGeneRestriction(diffusionRate, 0 * 7 + i));
    }

    //Non-mobile dimers
    genesRestriction.add(new FixedGeneRestriction(0, 1 * 7 + 0));

    genesRestriction.add(new FixedGeneRestriction(0, 2 * 7 + 0));
    genesRestriction.add(new FixedGeneRestriction(0, 2 * 7 + 1));

    genesRestriction.add(new FixedGeneRestriction(0, 5 * 7 + 0));
    genesRestriction.add(new FixedGeneRestriction(0, 5 * 7 + 1));

    for (int j = 0; j < 7; j++) {
      genesRestriction.add(new FixedGeneRestriction(0, 3 * 7 + j));
      genesRestriction.add(new FixedGeneRestriction(0, 4 * 7 + j));
      genesRestriction.add(new FixedGeneRestriction(0, 6 * 7 + j));
    }

    //We set the following atomistic configurations to the same rate (according to the Ag/Ag diffusion paper):
    //(2,3)=(2,4)=(2,5)=(2,6)=(5,2)=(5,3)=(5,4)=(5,6)
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 2 * 7 + 4));
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 2 * 7 + 5));
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 2 * 7 + 6));

    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 5 * 7 + 2));
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 5 * 7 + 3));
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 5 * 7 + 4));
    genesRestriction.add(new ReplicatedGeneRestriction(2 * 7 + 3, 5 * 7 + 6));

  }

  @Override
  public void initialize() {

  }

}
