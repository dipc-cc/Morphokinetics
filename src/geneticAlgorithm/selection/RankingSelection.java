/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.selection;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public class RankingSelection implements ISelection {

  private float SP = 2f; // selective pressure, choose in interval [1, 2]

  public IndividualGroup[] Select(Population p, int groupCount) {

    float[] Fitness = new float[p.size()];
    for (int i = 0; i < p.size(); i++) {
      Fitness[p.size() - i - 1] = 2.0f - SP + 2.0f * (SP - 1.0f) * (i)
              / (p.size() - 1.0f);
    }

    IndividualGroup[] groups = new IndividualGroup[groupCount];

    for (int i = 0; i < groupCount; i++) {
      groups[i] = new IndividualGroup(2);
      groups[i].set(0, p.getIndividual(linearSearch(Fitness)));

      do {
        groups[i].set(1, p.getIndividual(linearSearch(Fitness)));
      } while (groups[i].get(0) == groups[i].get(1));
    }
    return groups;
  }

  private int linearSearch(float[] probs) {

    float total = 0;
    for (int i = 0; i < probs.length; i++) {
      total += probs[i];
    }

    float selected = ((float) utils.StaticRandom.raw()) * total;

    float acc = 0;
    int i;
    for (i = 0; i < probs.length; i++) {
      acc += probs[i];
      if (acc > selected) {
        return i;
      }
    }
    return i;
  }

}
