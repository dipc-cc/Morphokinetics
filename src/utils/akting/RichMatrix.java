package utils.akting;

import java.util.ArrayList;

import geneticAlgorithm.Population;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import utils.akting.operations.Operation;
import utils.akting.operations.OperationFactory;

@SuppressWarnings("serial")
public class RichMatrix extends ArrayList<RichArray> {

  private int capacity;

  public RichMatrix(int initialCapacity) {
    super(initialCapacity);

    capacity = initialCapacity;
  }

  public RichMatrix(double[][] values) {
    super(values.length);

    capacity = values.length;

    for (int i = 0; i < capacity; i++) {
      add(new RichArray(values[i]));
    }
  }

  public RichMatrix(Population population) {
    this(population.size());

    for (int i = 0; i < population.size(); i++) {
      add(new RichArray(population.getIndividual(i)));
    }
  }

  private static RichMatrix create(int rows, int columns, double value) {
    RichMatrix matrix = new RichMatrix(columns);

    for (int i = 0; i < columns; i++) {
      RichArray row = new RichArray(rows, value);
      matrix.add(row);
    }

    return matrix;
  }

  public static RichMatrix zeros(int size) {
    return create(size, size, 0);
  }

  public static RichMatrix zeros(int rows, int columns) {
    return create(rows, columns, 0);
  }

  public static RichMatrix ones(int rows, int columns) {
    return create(rows, columns, 1);
  }

  public static RichMatrix eye(int size) {
    RichMatrix matrix = new RichMatrix(size);

    for (int i = 0; i < size; i++) {
      RichArray row = new RichArray(size, 0);
      row.set(i, 1D);
      matrix.add(row);
    }

    return matrix;
  }

  public static RichMatrix diag(RichArray row) {
    int size = row.size();
    RichMatrix matrix = new RichMatrix(size);

    for (int i = 0; i < size; i++) {
      RichArray richArray = new RichArray(size, 0);
      richArray.set(i, row.get(i));
      matrix.add(richArray);
    }

    return matrix;
  }

  public static RichMatrix repmat(RichArray row, int times) {
    RichMatrix newMatrix = new RichMatrix(times);

    for (int i = 0; i < times; i++) {
      newMatrix.add(row);
    }

    return newMatrix;
  }

  public static RichMatrix covariance(RichMatrix b, RichArray d) {
    return b.multiply(RichMatrix.diag(d.apply(OperationFactory.pow(2)))).multiply(b.transpose());
  }

  public static RichMatrix invsqrtCovariance(RichMatrix b, RichArray d) {
    return b.multiply(RichMatrix.diag(d.apply(OperationFactory.pow(-1)))).multiply(b.transpose());
  }

  public RichMatrix recombinate(Integer[] indexes) {
    RichMatrix newMatrix = new RichMatrix(indexes.length);

    for (int i = 0; i < indexes.length; i++) {
      newMatrix.add(get(indexes[i]));
    }

    return newMatrix;
  }

  public RichMatrix apply(Operation operation) {
    RichMatrix newMatrix = new RichMatrix(capacity);

    for (RichArray value : this) {
      newMatrix.add(value.apply(operation));
    }

    return newMatrix;
  }

  public RichMatrix transpose() {
    RichMatrix newMatrix = RichMatrix.zeros(capacity, get(0).size());

    for (int i = 0; i < capacity; i++) {
      RichArray row = get(i);
      for (int j = 0; j < row.size(); j++) {
        newMatrix.get(j).set(i, get(i).get(j));
      }
    }

    return newMatrix;
  }

  public RichMatrix sum(RichMatrix matrix) {
    RichMatrix newMatrix = new RichMatrix(capacity);

    for (int i = 0; i < capacity; i++) {
      newMatrix.add(get(i).sum(matrix.get(i)));
    }

    return newMatrix;
  }

  public RichMatrix deduct(RichMatrix matrix) {
    RichMatrix newMatrix = new RichMatrix(capacity);

    for (int i = 0; i < capacity; i++) {
      newMatrix.add(get(i).deduct(matrix.get(i)));
    }

    return newMatrix;
  }

  public RichArray multiply(RichArray row) {
    RichMatrix matrixT = transpose();
    RichArray newArray = new RichArray(matrixT.capacity);

    for (int i = 0; i < matrixT.capacity; i++) {
      newArray.add(matrixT.get(i).multiply(row).sum());
    }

    return newArray;
  }

  public RichMatrix multiply(RichMatrix matrix) {
    RichMatrix matrixT = transpose();
    RichMatrix newMatrix = RichMatrix.zeros(matrixT.capacity, matrix.capacity);

    for (int i = 0; i < matrixT.capacity; i++) {
      for (int j = 0; j < matrix.capacity; j++) {
        newMatrix.get(j).set(i, matrixT.get(i).multiply(matrix.get(j)).sum());
      }
    }

    return newMatrix;
  }

  /**
   * Calculates the standard deviation of a matrix
   * @return standard deviation
   */
  public RichArray std() {
    RichArray newArray = new RichArray(capacity);

    for (RichArray richArray : this) {
      newArray.add(richArray.std());
    }

    return newArray;
  }

  public RichMatrix triu(int k) {
    RichMatrix newMatrix = new RichMatrix(capacity);

    for (int i = 0; i < capacity; i++) {
      newMatrix.add(get(i).triu(i - k));
    }

    return newMatrix;
  }

  public RichArray diag() {
    RichArray diag = new RichArray(capacity);

    for (int i = 0; i < capacity; i++) {
      diag.add(get(i).get(i));
    }

    return diag;
  }

  public double[][] getPureMatrix() {
    double[][] pureMatrix = new double[capacity][];

    for (int i = 0; i < capacity; i++) {
      pureMatrix[i] = get(i).getPureArray();
    }

    return pureMatrix;
  }

  public Population toPopulation(int errorsNumber) {
    Population population = new Population(size());

    for (int i = 0; i < capacity; i++) {
      RichArray richArray = get(i);
      population.setIndividual(richArray.toIndividual(errorsNumber), i);
    }

    return population;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (RichArray value : this.transpose()) {
      sb.append(value);
    }
    sb.append("\n");
    return sb.toString();
  }

  public static void main(String[] args) {
    RichMatrix matrix = RichMatrix.eye(5);

    System.out.println(matrix);

    RichMatrix newMatrix = matrix.apply(new Operation() {

      public double apply(double value) {
        return value + 5;
      }
    });

    System.out.println(newMatrix);

    System.out.println(newMatrix.multiply(RichArray.createArrayInRange(5)));
    System.out.println();

    System.out.println(RichMatrix.diag(newMatrix.multiply(RichArray.createArrayInRange(5))));

    System.out.println(newMatrix.multiply(newMatrix));

    newMatrix.get(0).set(1, 3D);
    newMatrix.get(0).set(2, 4D);
    newMatrix.get(0).set(3, 6D);

    System.out.println(newMatrix);
    System.out.println(newMatrix.transpose());

    Integer[] indexes = {2, 1};

    System.out.println(newMatrix.recombinate(indexes));

    RichMatrix stdMatrix = new RichMatrix(2);
    RichArray row1 = new RichArray(3);
    row1.add(1D);
    row1.add(5D);
    row1.add(9D);
    stdMatrix.add(row1);
    RichArray row2 = new RichArray(3);
    row2.add(7D);
    row2.add(15D);
    row2.add(22D);
    stdMatrix.add(row2);
    System.out.println(stdMatrix.std().std());

    System.out.println(RichMatrix.repmat(row1, 4));

    System.out.println(RichMatrix.ones(5, 5).triu(-1));

    System.out.println("EIGEN");
    RichMatrix eigenMatrix = new RichMatrix(3);
    RichArray r1 = new RichArray(3);
    r1.add(1D);
    r1.add(2D);
    r1.add(3D);
    eigenMatrix.add(r1);
    RichArray r2 = new RichArray(3);
    r2.add(4D);
    r2.add(5D);
    r2.add(6D);
    eigenMatrix.add(r2);
    RichArray r3 = new RichArray(3);
    r3.add(7D);
    r3.add(8D);
    r3.add(9D);
    eigenMatrix.add(r3);

    System.out.println(eigenMatrix);

    eigenMatrix = eigenMatrix.triu(0).sum(eigenMatrix.triu(1).transpose());
    EigenvalueDecomposition eigen = new EigenvalueDecomposition(new DenseDoubleMatrix2D(eigenMatrix.getPureMatrix()));

    System.out.println(new RichMatrix(eigen.getV().toArray()));
    RichMatrix d = new RichMatrix(eigen.getD().toArray());
    System.out.println(d);
    System.out.println(d.diag().apply(OperationFactory.sqrt()));
  }
}
