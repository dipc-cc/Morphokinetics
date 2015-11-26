package utils.akting.operations;

public class SqrtOperation implements Operation {

  /**
   * In the environment of the project we assume that we won't need to calculate negative sqrt.
   * Hence we take the absolute value.
   * @param value
   * @return 
   */
  @Override
  public double apply(double value) {
    return Math.sqrt(Math.abs(value));
  }

}
