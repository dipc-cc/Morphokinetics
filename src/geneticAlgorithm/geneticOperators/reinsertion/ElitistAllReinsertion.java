package geneticAlgorithm.geneticOperators.reinsertion;

import geneticAlgorithm.DcmaEsConfig;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

public class ElitistAllReinsertion implements IReinsertion {
	
	private DcmaEsConfig config;
	
	public ElitistAllReinsertion(DcmaEsConfig config) {
		this.config = config;
	}

	@Override
	public Population Reinsert(Population origin, Population offpring,
			int substitutions) {
		
		for (int k = 0; k < config.getOffSize(); k++) {
			
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
