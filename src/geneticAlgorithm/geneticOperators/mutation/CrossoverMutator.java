package geneticAlgorithm.geneticOperators.mutation;

import java.util.List;

import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

public class CrossoverMutator implements IMutation {

  private DcmaEsConfig config;

  public CrossoverMutator(DcmaEsConfig config) {
    this.config = config;
  }

  @Override
  public void mutate(Population p, List nonFixedGenes) {
    for (int k = 0; k < p.size(); k++) {
      Individual child = p.getIndividual(k);

      // Crossover.
      double jr = Math.ceil(config.getN() * StaticRandom.raw());

      for (int j = 0; j < config.getN(); j++) {
        // Normal distribution with mean Crm and standard deviation Crs.
        double cr = config.getCrm() + config.getCrs() * StaticRandom.raw();

        if (StaticRandom.raw() > cr && j != jr) {
          child.setGene(j, config.getOffX().get(k).get(j));
        }
      }
    }
  }

}
