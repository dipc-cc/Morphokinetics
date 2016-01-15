/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.growth.GrowthKmcFrame;
import graphicInterfaces.growth.KmcCanvas;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSimulation {

  private GrowthKmcFrame frame;
  
  public AbstractGrowthSimulation(Parser parser) {
    super(parser);
  }

  @Override
  protected void initialiseRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser parser) {
    double depositionRatePerSite = ratesFactory.getDepositionRatePerSite(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite);
    getKmc().initialiseRates(ratesFactory.getRates(parser.getTemperature()));
  }
  
  @Override
  public void createFrame() {
    if (getParser().withGui()) {
      try {
        frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) getKmc().getLattice()));
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
      if (getParser().visualise()) {
        frame.setVisible(true);
      }
    }
  }
  
  /**
   * Do nothing.
   */
  @Override
  public void finishSimulation() {

  }
  
  /**
   * Prints the current frame to a file
   * @param i simulation number
   */
  @Override
  protected void printToImage(int i) {
    frame.printToImage(i);
  }
}
