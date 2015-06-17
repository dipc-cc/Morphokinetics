package Genetic_Algorithm.Genetic_Operators.Reinsertion;

import Genetic_Algorithm.DcmaEsConfig;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;

public class ElitistAllReinsertion implements IReinsertion {
	
	private DcmaEsConfig config;
	
	public ElitistAllReinsertion(DcmaEsConfig config) {
		this.config = config;
	}

	@Override
	public Population Reinsert(Population origin, Population offpring,
			int substitutions) {
		
		for (int k = 0; k < config.offSize; k++) {
			
			Individual original = origin.getIndividual(k);
			Individual candidate = offpring.getIndividual(k);
			
			if (candidate.getTotalError() <= original.getTotalError()) {
				origin.setIndividual(candidate, k);
				config.offFitness.set(k, candidate.getTotalError());
			}
		}
		
		return origin;
	}

}
