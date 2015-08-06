/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import ratesLibrary.AgAgRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimulation extends AbstractGrowthSimulation {

  public AgSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    this.ratesFactory = new AgAgRatesFactory();
    this.kmc = new AgAgKmc(config, parser.getSizeX(), parser.getSizeY(),
            parser.justCentralFlake(), parser.randomSeed(), parser.useMaxPerimeter());
  }

  @Override
  public void createFrame() {
    if (parser.withGui()) {
      try {
        frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
      if (parser.visualize()) {
        frame.setVisible(true);
      }
    }
  }
}
