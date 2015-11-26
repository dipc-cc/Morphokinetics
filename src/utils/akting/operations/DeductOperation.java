package utils.akting.operations;

public class DeductOperation implements Operation {

  private final double deductValue;

  public DeductOperation(double deductValue) {
    this.deductValue = deductValue;
  }

  @Override
  public double apply(double value) {
    return value - deductValue;
  }

}
