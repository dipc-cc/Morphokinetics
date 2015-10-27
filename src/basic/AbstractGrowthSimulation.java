/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.growth.DiffusionKmcFrame;
import graphicInterfaces.growth.KmcCanvas;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSimulation {

  public AbstractGrowthSimulation(Parser myParser) {
    super(myParser);
  }

  @Override
  protected void initialiseRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser) {
    double depositionRate = ratesFactory.getDepositionRate(myParser.getTemperature());
    double islandDensity = ratesFactory.getIslandDensity(myParser.getTemperature());
    this.kmc.setIslandDensityAndDepositionRate(depositionRate, islandDensity);
    this.kmc.initialiseRates(ratesFactory.getRates(myParser.getTemperature()));
  }
  
  @Override
  public void createFrame() {
    if (parser.withGui()) {
      try {
        frame = new DiffusionKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
      if (parser.visualise()) {
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
}
