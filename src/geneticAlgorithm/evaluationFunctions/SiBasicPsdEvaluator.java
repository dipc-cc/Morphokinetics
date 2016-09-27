/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import basic.Parser;
import geneticAlgorithm.Individual;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
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
        if (kmc.getIterations() < getMeasureInterval()) {
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
