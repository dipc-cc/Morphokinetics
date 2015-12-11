/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public class AgHierarchyEvaluator extends AbstractEvaluator {

  @Override
  public void dispose() {
    //nothing to dispose
  }

  @Override
  public double[] evaluate(Population p) {

    double[] errors = new double[p.size()];

    for (int i = 0; i < p.size(); i++) {

      Individual individual = p.getIndividual(i);

      double stepA = Math.log10(individual.getGene(2 * 7 + 2));
      double stepB = Math.log10(individual.getGene(5 * 7 + 5));

      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 3))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 4))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 5))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 6))));

      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 2))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 3))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 4))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 6))));

      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 1)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 2)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 3)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 4)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 5)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 6)) - Math.max(stepA, stepB)));

    }
    return errors;
  }

  @Override
  public float getProgressPercent() {
    return 0.0f;
  }
   
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public int getIndividualCount() {
    return -1;
  }

  /**
   * Undefined here
   * @return -1
   */
  @Override
  public int getSimulationCount() {
    return -1;
  }
}
