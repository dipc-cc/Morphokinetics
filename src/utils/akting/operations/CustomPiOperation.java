package utils.akting.operations;

public class CustomPiOperation implements Operation {

  @Override
  public double apply(double value) {
    return 2 * Math.PI * value;
  }

}
