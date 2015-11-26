package utils.akting.operations;

public class SinOperation implements Operation {

  @Override
  public double apply(double value) {
    return Math.sin(value);
  }

}
