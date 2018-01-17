/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package geneticAlgorithm.evaluationFunctions;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
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
