/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import ratesLibrary.SiRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulation extends AbstractEtchingSimulation {

  private SiKmcConfig siConfig;

  public SiSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new SiRatesFactory());
    siConfig = configKmc();
    setKmc(new SiKmc(siConfig));
    initialiseRates(getRates(), getKmc(), getParser());
  }

  /**
   * TODO This implementation is temporary, because it is to rigid and not tuneable.
   */
  private SiKmcConfig configKmc() {

    SiKmcConfig tmpConfig = new SiKmcConfig()
            .setMillerX(0)
            .setMillerY(1)
            .setMillerZ(1)
            .setSizeX_UC(96)
            .setSizeY_UC(96)
            .setSizeZ_UC(16)
            .setListConfig(getConfig());
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
    if (getParser().visualise()) {
      try {
        new SiFrame().drawKmc(getKmc());
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
    }
  }
}
