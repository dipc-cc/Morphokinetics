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

import basic.Parser;
import geneticAlgorithm.Individual;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class SiBasicPsdEvaluator extends AbstractPsdEvaluator {

  private SiKmc kmc;
  private final SiFrame frame;
  private PsdSignature2D psd;
  private float[][] sampledSurface;

  public SiBasicPsdEvaluator(Parser parser, int measureInterval) {
    super(parser.getRepetitions(), measureInterval, parser.getEvaluatorTypes(), null);

    setPsdSizeX(parser.getCartSizeX() * 2);
    setPsdSizeY(parser.getCartSizeY() * 2);
    kmc = new SiKmc(parser);
    psd = new PsdSignature2D(getPsdSizeY(), getPsdSizeX(), 1);
    frame = new SiFrame();
  }

  @Override
  public float[][] calculatePsdFromIndividual(Individual ind) {
    psd.reset();
    kmc.initialiseRates(ind.getGenes());
    for (int i = 0; i < getRepeats(); i++) {
      kmc.reset();
      kmc.depositSeed();
      kmc.simulate(getMeasureInterval() / 2);
      while (true) {
        kmc.simulate(getMeasureInterval());
        sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());
        psd.addSurfaceSample(sampledSurface);
        if (kmc.getSimulatedSteps() < getMeasureInterval()) {
          break;
        }
      }
      setCurrentSimulation(getCurrentSimulation()+1);
    }

    psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
    frame.drawKmc(kmc);
    return psd.getPsd();
  }

  @Override
  public void dispose() {
    psd = null;
    kmc = null;
    sampledSurface = null;
  }
  
  /**
   * It is not implemented
   * @param i
   * @return 0
   */
  @Override
  protected double calculateHierarchyError(Individual i) {
    return 0;
  }

  /**
   * It is not implemented
   * @param i
   * @return 0
   */
  @Override
  protected double calculateHierarchyErrorFromReference(Individual i) {
    return 0;
  }

  @Override
  protected double calculateHierarchyErrorDiscrete(Individual ind) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setHierarchy(double[] genes) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
