package utils.akting.operations;

public class PowOperation implements Operation {

  private final double exponent;

  public PowOperation(double exponent) {
    this.exponent = exponent;
  }

  @Override
  public double apply(double value) {
    return Math.pow(value, exponent);
  }

}
