package geneticAlgorithm.selection;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;
import utils.StaticRandom;

public class RandomSelection implements ISelection {

  @Override
  public IndividualGroup[] Select(Population p, int groupCount) {
    IndividualGroup[] groups = new IndividualGroup[groupCount];

    for (int k = 0; k < groupCount; k++) {
      int k1 = k; while (k1 == k) k1 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;
      int k2 = k; while (k2 == k) k2 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;
      int k3 = k; while (k3 == k) k3 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;

      IndividualGroup group = new IndividualGroup(3);
      group.set(0, p.getIndividual(k1));
      group.set(1, p.getIndividual(k2));
      group.set(2, p.getIndividual(k3));

      groups[k] = group;
    }

    return groups;
  }

}
