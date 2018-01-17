/* 
 * Copyright (C) 2018 N. Ferrando
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package samples.agAgGrowth;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFromPrbCox;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class AgAgMultithreadedEvaluatorTest {

  public static void main(String[] args) {

    float experimentalTemp = 135;

    //AgAgGrowthThreadedPsdEvaluation evaluation = new AgAgGrowthThreadedPsdEvaluation(config, 20, Integer.MAX_VALUE, 4);
    AgBasicPsdEvaluator evaluation = new AgBasicPsdEvaluator(localAgAgKmc(experimentalTemp), 20, Integer.MAX_VALUE, 128, 128, null, null, null, 135);

    Individual individual = new Individual(new AgRatesFromPrbCox().getRates(experimentalTemp));
    float[][] experimentalPsd = evaluation.calculatePsdFromIndividual(individual);

    evaluation.setPsd(experimentalPsd);

    Individual newIndividual = new Individual(new AgRatesFromPrbCox().getRates(125));
    Population population = new Population(1);
    population.setIndividual(newIndividual, 0);
    double[] populationErrors = evaluation.evaluate(population);

    System.out.println(populationErrors[0]);

  }
  
  private static AgKmc localAgAgKmc(float experimentalTemp) {
    new StaticRandom();
    double depositionRatePerSite = new AgRatesFromPrbCox().getDepositionRatePerSite();
    double islandDensity = new AgRatesFromPrbCox().getIslandDensity(experimentalTemp);
    
    Parser parser = new Parser();
    parser.setCartSizeX(256);
    parser.setCartSizeY((int) (256 / AbstractGrowthLattice.Y_RATIO));

    AgKmc kmc = new AgKmc(parser);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    return kmc;
  }

}
