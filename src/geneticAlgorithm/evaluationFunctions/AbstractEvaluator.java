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

  protected double wheight;
  protected boolean showGraphics;

  public AbstractEvaluator() {
    this.wheight = 1.0;
    this.showGraphics = false;
  }

  @Override
  public double getWheight() {
    return wheight;
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

}
