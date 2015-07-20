/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.grapheneCvdGrowth.GrapheneKmcCanvas;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.kmcCore.diffusion.GrapheneKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import ratesLibrary.GrapheneRatesFactory;
import ratesLibrary.IRatesFactory;

/**
 *
 * @author jalberdi004
 */
public class GrapheneSimulation extends AbstractSimulation {

  public GrapheneSimulation(Parser myParser) {
    super(myParser);
  }

  @Override
  public void initialiseKmc(AbstractKmc kmc, IRatesFactory ratesFactory) {
    super.initialiseKmc(kmc, ratesFactory);

    System.out.println("Is it here?");
    this.ratesFactory = new GrapheneRatesFactory();
    this.kmc = new GrapheneKmc(config, sizeX, sizeY, currentParser.justCentralFlake());

  }

  public void createFrame() {
    try {
      frame = new DiffusionKmcFrame(new GrapheneKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
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
