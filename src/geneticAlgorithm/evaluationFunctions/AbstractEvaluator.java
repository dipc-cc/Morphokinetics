/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEvaluator implements IEvaluation {

  private double wheight;
  private boolean showGraphics;

  public AbstractEvaluator() {
    wheight = 1.0;
    showGraphics = false;
  }

  @Override
  public double getWheight() {
    return wheight;
  }

  public boolean showGraphics() {
    return showGraphics;
  }
  
  public AbstractEvaluator setShowGraphics(boolean showGraphics) {
    this.showGraphics = showGraphics;
    return this;
  }

  @Override
  public IEvaluation setWheight(float wheight) {
    this.wheight = wheight;
    return this;
  }

  public abstract void dispose();

  @Override
  public float[][] getSurface() {
    return null;
  } 
  
  @Override
  public float getProgressPercent() {
    return 0.0f;
  }
   
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public int getIndividualCount() {
    return -1;
  }

  /**
   * Undefined here
   * @return -1
   */
  @Override
  public int getSimulationCount() {
    return -1;
  }
  
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public double getCurrentError() {
    return -1;
  }
  
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public float[][] getCurrentPsd() {
    return null;
  }
  
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public float[][] getCurrentDifference() {
    return null;
  }
}
