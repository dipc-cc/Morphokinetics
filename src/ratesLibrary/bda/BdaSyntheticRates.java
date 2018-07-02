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
package ratesLibrary.bda;

import static kineticMonteCarlo.site.BdaMoleculeSite.ALPHA;
import static kineticMonteCarlo.site.BdaMoleculeSite.BETA;

/**
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaSyntheticRates extends AbstractBdaRates {

  private final double[] diffusionEnergy;
  private final double rotationEnergy;
  
  public BdaSyntheticRates(float temperature) {
    super(temperature);
    diffusionEnergy = new double[3];
    diffusionEnergy[ALPHA] = 0.3; // alpha diffusion
    diffusionEnergy[BETA] = 0.2; // beta diffusion
    rotationEnergy = 0.2;
  }

  @Override
  public double[] getRates(double temperature) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double getEnergy(int i, int j) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double getIslandDensity(double temperature) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  } 
  
  @Override
  double getDiffusionEnergy(byte type) {
    return diffusionEnergy[type];
  }
  
  @Override
  double getRotationEnergy() {
    return rotationEnergy;
  }
}
