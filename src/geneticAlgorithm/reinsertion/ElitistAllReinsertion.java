package geneticAlgorithm.reinsertion;

import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

public class ElitistAllReinsertion implements IReinsertion {

  private DcmaEsConfig config;

  public ElitistAllReinsertion(DcmaEsConfig config) {
    this.config = config;
  }

  /**
   * The offspring individual is accepted if its error is lower than the corresponding original
   * error
   *
   * @param origin Original population
   * @param offpring Offspring population
   * @param substitutions Completely ignored
   * @return
   */
  @Override
  public Population Reinsert(Population origin, Population offpring, int substitutions) {
    for (int k = 0; k < origin.size(); k++) {

      Individual original = origin.getIndividual(k);
      Individual candidate = offpring.getIndividual(k);

      if (candidate.getTotalError() <= original.getTotalError()) {
        origin.setIndividual(candidate, k);
        config.getOffFitness().set(k, candidate.getTotalError());
      }
    }

    return origin;
  }

}
