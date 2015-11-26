package utils.akting.operations;

public class CosOperation implements Operation {

  @Override
  public double apply(double value) {
    return Math.cos(value);
  }

}
