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

  private boolean searchEnergy;
  
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
   * @param searchEnergy searches for energy?
   */
  public AgReduced6Restriction(int dimensions, double min, double max, boolean searchEnergy) {
    //Negative and 0 values are not valid
    if (min > max) {
      System.out.println("Minimum has to be less than maximum");
      throw new IllegalArgumentException("Minimum has to be less than maximum");
    }
    if (min <= 0) {
      System.out.println("Minimum has to be greater than 0");
      throw new IllegalArgumentException("Minimum has to be greater than 0");
    }

    this.searchEnergy = searchEnergy;
    if (searchEnergy) {
      genesRestriction.add(new BoundedGeneRestriction(0.1, max, 0));
    } else {
      genesRestriction.add(new BoundedGeneRestriction(10000, max, 0));
    }
    for (int currentGene = 1; currentGene < dimensions; currentGene++) {
      genesRestriction.add(new BoundedGeneRestriction(min, max, currentGene));
    }
  }
  
  @Override
  public void initialise() {

  }

  /**
   * Fixes diffusion (terrace to terrace, or terrace to anywhere) ratio with the default value. The
   * fixed value changes with the search mode (energies or rates)
   */
  public void fixDiffusion() {
    if (searchEnergy) {
      genesRestriction.add(new FixedGeneRestriction(0.153573778552368, 0));
    } else {
      genesRestriction.add(new FixedGeneRestriction(1.8485467015993025E7, 0));
    }
  }
}
