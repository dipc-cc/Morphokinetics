/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import ratesLibrary.SiRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulation extends AbstractEtchingSimulation {

  private SiEtchingKmcConfig siConfig;

  public SiSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    this.rates = new SiRatesFactory();
    this.siConfig = configKmc();
    this.kmc = new SiEtchingKmc(siConfig);
  }

  /**
   * TODO This implementation is temporary, because it is to rigid and not tuneable.
   */
  private SiEtchingKmcConfig configKmc() {

    SiEtchingKmcConfig tmpConfig = new SiEtchingKmcConfig()
            .setMillerX(0)
            .setMillerY(1)
            .setMillerZ(1)
            .setSizeX_UC(96)
            .setSizeY_UC(96)
            .setSizeZ_UC(16)
            .setListConfig(this.config);
    return tmpConfig;
  }

  /**
   * Does nothing
   */
  @Override
  public void createFrame() {
  }

  /**
   * Show the result of the simulation in a frame
   */
  @Override
  public void finishSimulation() {
    if (parser.visualize()) {
      try {
        new SiliconFrame().drawKmc(kmc);
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
    }
  }
}
