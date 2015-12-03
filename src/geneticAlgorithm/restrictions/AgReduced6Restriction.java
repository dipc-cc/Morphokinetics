/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

/**
 * We have only 6 genes, which correspond to all different rates we can find in an Ag/Ag
 * simulation.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReduced6Restriction extends RestrictionOperator {

  public void initialize(double temperature) {

  }

  public AgReduced6Restriction() {
    //Negative and 0 values are not valid
    for (int currentGene = 0; currentGene < 6; currentGene++) {
      genesRestriction.add(new BoundedGeneRestriction(1e-8, 1e20, currentGene));
    }
  }
  
  /**
   *
   * @param dimensions number of genes (or variables)
   * @param min the minimum value
   * @param max the maximum value
   */
  public AgReduced6Restriction(int dimensions, double min, double max) {
    //Negative and 0 values are not valid
    if (min > max) {
      System.out.println("Minimum has to be less than maximum");
      throw new IllegalArgumentException("Minimum has to be less than maximum");
    }
    if (min <= 0) {
      System.out.println("Minimum has to be greater than 0");
      throw new IllegalArgumentException("Minimum has to be greater than 0");
    }

    for (int currentGene = 0; currentGene < dimensions; currentGene++) {
      genesRestriction.add(new BoundedGeneRestriction(min, max, currentGene));
    }
  }
  
  @Override
  public void initialize() {

  }

}
