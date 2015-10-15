package geneticAlgorithm.geneticOperators.mutation;

import java.util.List;
import java.util.Random;

import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

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
      Random random = new Random();
      double jr = Math.ceil(config.getN() * random.nextDouble());

      for (int j = 0; j < config.getN(); j++) {
        // Normal distribution with mean Crm and standard deviation Crs.
        double cr = config.getCrm() + config.getCrs() * random.nextGaussian();

        if (random.nextDouble() > cr && j != jr) {
          child.setGene(j, config.getOffX().get(k).get(j));
        }
      }
    }
  }

}
