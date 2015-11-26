package utils.akting.operations;

public class PowOperation implements Operation {

  private final double exponent;

  public PowOperation(double exponent) {
    this.exponent = exponent;
  }

  public double apply(double value) {
    return Math.pow(value, exponent);
  }

}
