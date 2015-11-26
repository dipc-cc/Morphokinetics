package utils.akting.tests;

import utils.akting.RichArray;
import utils.akting.operations.Operation;
import utils.akting.operations.OperationFactory;

public class TestSuite {

  public static double fschwefel(RichArray individual) {
    double f = 1e3;

    for (double value : individual) {
      f = f + (-1 * value * Math.sin(Math.sqrt(Math.abs(value))));
    }

    return f;
  }

  public static double fackley(RichArray individual) {
    int size = individual.size();
    double f = 20 - 20 * Math.exp(-0.2 * Math.sqrt(individual.apply(OperationFactory.pow(2)).sum() / size));
    return f + Math.exp(1) - Math.exp(individual.apply(OperationFactory.customPi()).apply(OperationFactory.cos()).sum() / size);
  }

  public static double frosenbrock(RichArray individual) {
    int size = individual.size();
    Operation pow2 = OperationFactory.pow(2);
    return 100 * individual.subArray(0, size - 1).apply(pow2).deduct(individual.subArray(1, size - 1)).apply(pow2).sum()
            + individual.subArray(0, size - 1).apply(OperationFactory.deduct(1)).apply(pow2).sum();
  }

  public static double fsphere(RichArray individual) {
    return individual.apply(OperationFactory.pow(2)).sum();
  }

  public static double fssphere(RichArray individual) {
    return Math.sqrt(fsphere(individual));
  }

  public static double fcigar(RichArray individual) {
    return Math.pow(individual.get(0), 2) + 1e6 * individual.subArray(1, individual.size() - 1).apply(OperationFactory.pow(2)).sum();
  }

  public static double fcigtab(RichArray individual) {
    return Math.pow(individual.get(0), 2) + 1e8 * Math.pow(individual.get(individual.size() - 1), 2)
            + 1e4 * individual.subArray(1, individual.size() - 2).apply(OperationFactory.pow(2)).sum();
  }

  public static double ftablet(RichArray individual) {
    return 1e6 * Math.pow(individual.get(0), 2) + individual.subArray(1, individual.size() - 1).apply(OperationFactory.pow(2)).sum();
  }

  public static double felli(RichArray individual) {
    final int size = individual.size();
    Operation pow2 = OperationFactory.pow(2);
    return RichArray.createArrayInRange(size - 1).apply(new Operation() {

      @Override
      public double apply(double value) {
        return Math.pow(1e4, value / (size - 1));
      }
    }).transpose().multiply(individual.apply(pow2)).sum();
  }

  public static double fplane(RichArray individual) {
    return individual.get(0);
  }

  public static double frastrigin(RichArray individual) {
    int size = individual.size();
    RichArray scale = RichArray.createArrayInRange(0, size - 1).apply(OperationFactory.divide(size - 1)).apply(new Operation() {

      @Override
      public double apply(double value) {
        return Math.pow(10, value);
      }
    });

    return 10 * size + scale.multiply(individual).apply(OperationFactory.pow(2)).deduct(scale.multiply(individual).apply(new Operation() {

      @Override
      public double apply(double value) {
        return 10 * Math.cos(2 * Math.PI * value);
      }
    })).sum();
  }

}
