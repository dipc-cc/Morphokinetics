package geneticAlgorithm.geneticOperators.mutation;

import java.util.List;
import java.util.Random;

import utils.akting.RichArray;
import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

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
			Random random = new Random();
			double jr = Math.ceil(config.n * random.nextDouble());
		
			
			for (int j = 0; j < config.n; j++) {
				// Normal distribution with mean Crm and standard deviation Crs.
				double cr = config.crm + config.crs * random.nextGaussian();
				
				if (random.nextDouble() > cr && j != jr) {
					child.setGene(j, config.offX.get(k).get(j));
				}
			}
		}
	}

}
