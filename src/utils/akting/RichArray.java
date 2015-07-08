package utils.akting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import geneticAlgorithm.Individual;
import utils.akting.operations.Operation;

@SuppressWarnings("serial")
public class RichArray extends ArrayList<Double> {
	
	private final int capacity;
	
	/**
	 * Support for {@link Individual}
	 */
	private double[] errors;
	private double simulationTime;
	
	public RichArray(int initialCapacity) {
		super(initialCapacity);
		
		capacity = initialCapacity;
	}
	
	public RichArray(double[] values) {
		super(values.length);
		
		capacity = values.length;
		
		for (int i = 0; i < capacity; i++) {
			add(values[i]);
		}
	}
	
	public RichArray(int initialCapacity, double value) {
		this(initialCapacity);
		
		for(int i = 0; i < initialCapacity; i++) {
			add(value);
		}
	}
	
	public RichArray(Individual individual) {
		this(individual.getGeneSize());
		
		for (double value : individual.getGenes()) {
			add(value);
		}
		
		errors = individual.getErrors();
		simulationTime = individual.getSimulationTime();
	}
	
	public static RichArray createArrayInRange(double end) {
		int endFloored = Double.valueOf(Math.floor(end)).intValue();
		RichArray richArray = new RichArray(endFloored);
		
		for(double i = 0; i < endFloored; i++) {
			richArray.add(i + 1);
		}
		
		return richArray;
	}
	
	public static RichArray createArrayInRange(double start, double end) {
		int startFloored = Double.valueOf(Math.floor(start)).intValue();
		int endFloored = Double.valueOf(Math.floor(end)).intValue();
		RichArray richArray = new RichArray(endFloored + 1 - startFloored);
		
		for(double i = startFloored; i <= endFloored; i++) {
			richArray.add(i);
		}
		
		return richArray;
	}
	
	public static RichArray rand(int initialCapacity) {
		RichArray newArray = new RichArray(initialCapacity);
		
		for (int i = 0; i < initialCapacity; i++) {
			newArray.add(Math.random());
		}
		
		return newArray;
	}
	
	public static RichArray randn(int initialCapacity) {
		RichArray newArray = new RichArray(initialCapacity);
		Random random = new Random();
		
		for (int i = 0; i < initialCapacity; i++) {
			newArray.add(random.nextGaussian());
		}
		
		return newArray;
	}
	
	public static RichArray max(RichArray a, RichArray b) {
		RichArray maxArray = new RichArray(a.capacity);
		
		for (int i = 0; i < a.capacity; i++) {
			maxArray.add(Math.max(a.get(i), b.get(i)));
		}
		
		return maxArray;
	}
	
	public static RichArray min(RichArray a, RichArray b) {
		RichArray minArray = new RichArray(a.capacity);
		
		for (int i = 0; i < a.capacity; i++) {
			minArray.add(Math.min(a.get(i), b.get(i)));
		}
		
		return minArray;
	}
	
	public double sum() {
		double sum = 0;
		
		for(double value : this) {
			sum += value;
		}
		
		return sum;
	}
	
	public double min() {
		double min = Double.MAX_VALUE;
		
		for (double value : this) {
			min = Math.min(min, value);
		}
		
		return min;
	}
	
	public double max() {
		double max = Double.MIN_VALUE;
		
		for (double value : this) {
			max = Math.max(max, value);
		}
		
		return max;
	}
	
	public double std() {
		double diffs = 0;
		double avg = avg();
		
		for (double value : this) {
			diffs += Math.pow(value - avg, 2);
		}
		
		return Math.sqrt(diffs / (capacity - 1));
	}
	
	public double avg() {
		return sum() / capacity;
	}

	public RichArray sum(RichArray richArray) {
		RichArray newArray = new RichArray(capacity);
		
		for (int i = 0; i < capacity; i++) {
			newArray.add(get(i) + richArray.get(i));
		}
		
		return newArray;
	}
	
	public RichArray deduct(RichArray richArray) {
		RichArray newArray = new RichArray(capacity);
		
		for (int i = 0; i < capacity; i++) {
			newArray.add(get(i) - richArray.get(i));
		}
		
		return newArray;
	}
	
	public RichArray multiply(RichArray richArray) {
		RichArray newArray = new RichArray(capacity);
		
		for(int i = 0; i < capacity; i++) {
			newArray.add(get(i) * richArray.get(i));
		}
		
		return newArray;
	}
	
	public RichMatrix multiply(RichMatrix matrix) {
		RichMatrix newMatrix = new RichMatrix(capacity);
		
		for(double value : this) {
			RichArray array = new RichArray(capacity);
			
			for(RichArray row : matrix) {
				array.add(value * row.get(0));
			}
			
			newMatrix.add(array);
		}
		
		return newMatrix.transpose();
	}
	
	public RichMatrix transpose() {
		RichMatrix matrix = new RichMatrix(capacity);
		
		for (double value : this) {
			RichArray row = new RichArray(1, value);
			matrix.add(row);
		}
		
		return matrix;
	}
	
	public RichArray apply(Operation operation) {
		RichArray newArray = new RichArray(capacity);
		
		for(double value : this) {
			newArray.add(operation.apply(value));
		}
		
		return newArray;
	}
	
	public double norm() {
		double sum = 0;
		
		for (double value : this) {
			sum += Math.pow(value, 2);
		}
		
		return Math.sqrt(sum);
	}
	
	public RichArray triu(int k) {
		RichArray newArray = new RichArray(capacity);
		
		for (int i = 0; i < capacity; i++) {
			newArray.add((i <= k) ? get(i) : 0);
		}
		
		return newArray;
	}
	
	public RichArray normalize() {
		final double sum = sum();
		
		return apply(new Operation() {
			
			public double apply(double value) {
				return value / sum;
			}
		});
	}
	
	public boolean allLessOrEqualThan(double comparisonValue) {
		for (double value : this) {
			if (value > comparisonValue) {
				return false;
			}
		}
		
		return true;
	}
	
	public RichArray copy() {
		RichArray newArray = new RichArray(capacity);
		
		for(double value : this) {
			newArray.add(value);
		}
		
		return newArray;
	}
	
	public Integer[] sortedIndexes() {
		RichArrayComparator comparator = new RichArrayComparator(this);
		Integer[] indexes = comparator.createIndexArray();		
		Arrays.sort(indexes, comparator);
		
		return indexes;
	}
	
	public RichArray recombinate(Integer[] indexes) {
		RichArray newArray = new RichArray(indexes.length);
		
		for (int i = 0; i < indexes.length; i++) {
			newArray.add(get(indexes[i]));
		}
		
		return newArray;
	}
	
	public double[] getPureArray() {
		double[] pureArray = new double[capacity];
		
		for (int i = 0; i < capacity; i++) {
			pureArray[i] = get(i);
		}
		
		return pureArray;
	}
	
	public RichArray subArray(int start, int length) {
		RichArray richArray = new RichArray(length);
		
		for(int i = 0; i < length; i++) {
			richArray.add(get(i + start));
		}
		
		return richArray;
	}
	
	public Individual toIndividual(int errorsNumber) {
		Individual individual;
		if (errors == null) {
			individual = new Individual(getPureArray(), new double[errorsNumber]);
		} else {
			individual = new Individual(getPureArray(), errors);
		}
		individual.setSimulationTime(simulationTime);
		
		return individual;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(double value : this) {
			sb.append(value).append("\t");
		}
		sb.append("\n");
		return sb.toString();
	}
}
