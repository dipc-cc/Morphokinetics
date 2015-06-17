package utils.akting.operations;

public class OperationFactory {

	public static Operation sin() {
		return new SinOperation();
	}
	
	public static Operation cos() {
		return new CosOperation();
	}
	
	public static Operation pow(double exponent) {
		return new PowOperation(exponent);
	}
	
	public static Operation sqrt() {
		return new SqrtOperation();
	}
	
	public static Operation customPi() {
		return new CustomPiOperation();
	}
	
	public static Operation deduct(double deductValue) {
		return new DeductOperation(deductValue);
	}
	
	public static Operation multiply(double factor) {
		return new MultiplyOperation(factor);
	}
	
	public static Operation divide(double factor) {
		return new DivideOperation(factor);
	}
}
