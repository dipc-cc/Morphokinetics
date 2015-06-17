package Genetic_Algorithm.Genetic_Operators.Mutation;

import java.util.List;
import java.util.Random;

import utils.akting.RichArray;
import Genetic_Algorithm.DcmaEsConfig;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;

public class Crossover_mutator implements IMutation {
	
	private DcmaEsConfig config;
	
	public Crossover_mutator(DcmaEsConfig config) {
		this.config = config;
	}

	@Override
	public void mutate(Population p, List nonFixedGenes) {
		for (int k = 0; k < p.size(); k++) {
			Individual child = p.getIndividual(k);
			
			// Crossover.
			RichArray jr = new RichArray(config.n, 1);
			Random random = new Random();				
			for (int j = 0; j < config.n; j++) {
				// Normal distribution with mean Crm and standard deviation Crs.
				double cr = config.crm + config.crs * random.nextGaussian();
				
				if (random.nextDouble() > cr && j != jr.get(j)) {
					child.setGene(j, config.offX.get(k).get(j));
				}
			}
		}
	}

}
