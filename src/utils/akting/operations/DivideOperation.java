package utils.akting.operations;

public class DivideOperation implements Operation {

  private final double factor;

  public DivideOperation(double factor) {
    this.factor = factor;
  }

  public double apply(double value) {
    return value / factor;
  }

}
