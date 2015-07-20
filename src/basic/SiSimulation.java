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
import utils.list.ListConfiguration;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulation extends AbstractSimulation {

  private SiEtchingKmcConfig siConfig;

  public SiSimulation(Parser myParser) {
    super(myParser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    this.ratesFactory = new SiRatesFactory();
    siConfig = configKMC();
    this.kmc = new SiEtchingKmc(siConfig);
  }

  /**
   * TODO This implementation is temporary, because it is to rigid and not tuneable.
   */
  private SiEtchingKmcConfig configKMC() {
    SiEtchingKmcConfig tmpConfig = new SiEtchingKmcConfig()
            .setMillerX(0)
            .setMillerY(1)
            .setMillerZ(1)
            .setSizeX_UC(96)
            .setSizeY_UC(96)
            .setSizeZ_UC(16)
            .setListConfig(config);
    return tmpConfig;
  }

  @Override
  public void createFrame() {
    try {
      new SiliconFrame().drawKMC(kmc);
    } catch (Exception e) {
      System.err.println("Error: The execution is not able to create the X11 frame");
      System.err.println("Finishing");
      throw e;
    }
//    if (currentParser.isVisualize()) {
//      frame.setVisible(true);
//    }
  }
  
}
