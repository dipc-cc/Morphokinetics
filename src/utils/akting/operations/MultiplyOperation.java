package utils.akting.operations;

public class MultiplyOperation implements Operation {
	
	private final double factor;
	
	public MultiplyOperation(double factor) {
		this.factor = factor;
	}

	public double apply(double value) {
		return value * factor;
	}

}
