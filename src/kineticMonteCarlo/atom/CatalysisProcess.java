/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 * Class to store a process of catalysis. Possible processes are adsorption, desorption, reaction
 * and diffusion.
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisProcess {

  public static final byte ADSORPTION = 0;
  public static final byte DESORPTION = 1;
  public static final byte REACTION = 2;
  public static final byte DIFFUSION = 3;
  
  private double rate;
  /** Attribute for AVL tree. */
  private double sumRate;
  private double[] edgeRate;
  private boolean active;
  
  public CatalysisProcess() {
    edgeRate = new double[4];
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
      edgeRate = new double[4];
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
    for (int i = 0; i < 4; i++) { // to avoid numerical error. Recompute
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
}
