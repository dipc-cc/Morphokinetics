package samples.basic;

import graphicInterfaces.basic.BasicFrame;
import kineticMonteCarlo.kmcCore.etching.BasicKmc;
import utils.list.ListConfiguration;
import ratesLibrary.basic.RatesCaseOther;
import utils.StaticRandom;
import utils.Wait;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleBasicKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Basic KMC");

    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(100)
            .setExtraLevels(0);

    BasicKmc KMC = new BasicKmc(listConfig, 512, 128, true);

    BasicFrame panel = new BasicFrame(3);

    //KMC.initializeRates(new BasicEtchRatesFactory().getRates("Basic_OTHER", 350));
    KMC.reset();
    KMC.initialiseRates(new RatesCaseOther().getRates(350));
    KMC.depositSeed();

    for (int i = 0; i < 1000; i++) {

      KMC.simulate(500);
      panel.drawKmc(KMC);
      Wait.manyMilliSec(300);
    }

  }

}
