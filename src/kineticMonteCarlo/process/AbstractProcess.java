/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.process;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractProcess {
  
  private double rate;
  /** Attribute for AVL tree. */
  private double sumRate;
  private double[] edgeRate;
  private boolean active;
  private final int numberOfProcesses;
  /** Stores the type of the neighbours. It is used for activation energy study; to be able to check old neighbour types. */
  private byte[] edgeType;
  
  public AbstractProcess(int numberOfProcesses) {
    edgeRate = new double[numberOfProcesses];
    this.numberOfProcesses = numberOfProcesses;
    resetEdgeType();
  }
  
  public void setSumRate(double rate) {
    sumRate = rate;
  }

  public double getSumRate() {
    return sumRate;
  }

  public void addSumRate(double rate) {
    sumRate += rate;
  }

  public double getRate() {
    return rate;
  }
  
  public void setRate(double rate) {
    this.rate = rate;
    if (rate == 0.0) {
      edgeRate = new double[numberOfProcesses];
      resetEdgeType();
    }
  }
  
  /**
   * This process needs a neighbour atom. This method stores current atom's rate. In the case of a
   * reaction, only the half is stored. To avoid double counting, otherwise, every reaction will be
   * counted twice.
   *
   * @param rate
   * @param neighbourPos
   */
  public void addRate(double rate, int neighbourPos) {
    //this.rate += rate; // it creates numerical error
    edgeRate[neighbourPos] += rate; // TODO, I don't know why is += and not just =
    this.rate = 0;
    for (int i = 0; i < numberOfProcesses; i++) { // to avoid numerical error. Recompute
      this.rate += edgeRate[i];
    }
  }

  /**
   * Current process can be executed.
   * 
   * @return 
   */
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public double getEdgeRate(int pos) {
    return edgeRate[pos];
  }
  
  public void equalRate() {
    sumRate = rate;
  }

  /**
   * Stores the type of the neighbours. It is used for activation energy study; to be able to check
   * old neighbour types.
   * 
   * @param type type of the neighbour.
   * @param pos position of the neighbour.
   */
  public void setEdgeType(byte type, int pos) {
    edgeType[pos] = type;
  }
  
  public byte getEdgeType(int pos) {
    return edgeType[pos];
  }
  
  public void clear() {
    sumRate = 0.0;
    rate = 0.0;
    active = false;
    resetEdgeType();
  }
  
  private void resetEdgeType() {
    edgeType = new byte[6];
    for (int i = 0; i < edgeType.length; i++) {
      edgeType[i] = (byte) -1;      
    }
    //return edgeType;
  }
}
