package geneticAlgorithm;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class IndividualGroup extends ArrayList<Individual> {

	public IndividualGroup(int capacity) {		
		for (int i = 0; i < capacity; i++) {
			add(null);
		}
	}
}