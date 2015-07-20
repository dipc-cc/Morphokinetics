/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import ratesLibrary.AgAgRatesFactory;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author jalberdi004
 */
public class AgSimulation extends AbstractSimulation {

  public AgSimulation(Parser myParser) {
    super(myParser);
  }

  @Override
  public void initialiseKmc(AbstractKmc kmc, IRatesFactory ratesFactory) {
    super.initialiseKmc(kmc, ratesFactory);

    this.ratesFactory = new AgAgRatesFactory();
    this.kmc = new AgAgKmc(config, sizeX, sizeY, currentParser.justCentralFlake());
  }
  
  @Override
  public void createFrame() {
    try {
      frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
    } catch (Exception e) {
      System.err.println("Error: The execution is not able to create the X11 frame");
      System.err.println("Finishing");
      throw e;
    }
    if (currentParser.isVisualize()) {
      frame.setVisible(true);
    }
  }
  
}
