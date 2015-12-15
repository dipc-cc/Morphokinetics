package geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

public class CrossoverMutator implements IMutation {

  private DcmaEsConfig config;
  /** Crossover standard deviation. */
  private final double crs; 

  public CrossoverMutator(DcmaEsConfig config) {
    this.config = config;
    crs = 0.1;
  }

  @Override
  public void mutate(Population p, List nonFixedGenes) {
    for (int k = 0; k < p.size(); k++) {
      Individual child = p.getIndividual(k);

      // Crossover.
      Random random = new Random();
      double jr = Math.ceil(p.getIndividual(0).getGeneSize() * StaticRandom.raw());

      for (int j = 0; j < p.getIndividual(0).getGeneSize(); j++) {
        // Normal distribution with mean Crm and standard deviation Crs.
        double cr = config.getCrm() + crs * random.nextGaussian();

        if (StaticRandom.raw() > cr && j != jr) {
          child.setGene(j, p.getOffX().get(k).get(j));
        }
      }
    }
  }

}
