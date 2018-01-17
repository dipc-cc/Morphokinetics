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

import geneticAlgorithm.Population;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public interface IEvaluation {

  public double[] evaluate(Population p);

  public float getProgressPercent();

  public double getWheight();

  public IEvaluation setWheight(float wheight);
  
  public int getIndividualCount();
  
  public int getSimulationCount();
  
  public float[][] getSurface();
  
  public double getCurrentError();
  
  public float[][] getCurrentPsd();
  
  public float[][] getCurrentDifference();
}
