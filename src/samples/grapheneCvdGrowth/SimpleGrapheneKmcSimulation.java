/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.grapheneCvdGrowth;

import basic.Parser;
import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.GrapheneKmc;
import ratesLibrary.GrapheneSyntheticRates;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SimpleGrapheneKmcSimulation {

  private static final double COS30 = Math.cos(30 * Math.PI / 180);
  private static GrowthKmcFrame  frame;
  private static paintLoop p;

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Graphene KMC");

    GrapheneSyntheticRates ratesFactory = new GrapheneSyntheticRates();
    GrapheneKmc kmc = initialiseKmc();
    createGraphicsFrame(kmc);

    for (int i = 0; i < 10; i++) {
      initialiseRates(ratesFactory, kmc);
      kmc.simulate();
    }

    float[][] surface = kmc.getSampledSurface(256, 256);

  }

  private static void createGraphicsFrame(GrapheneKmc kmc) {
    frame = new GrowthKmcFrame(kmc.getLattice(), kmc.getPerimeter(), 1);
    frame.setVisible(true);
    p = new paintLoop();
    p.start();
  }

  private static GrapheneKmc initialiseKmc() {
    new StaticRandom();

    int sizeX = 256;
    int sizeY = (int) (sizeX * (2 * COS30));
    if ((sizeY & 1) != 0) {
      sizeY++;
    }
    Parser parser = new Parser();
    parser.setCartSizeX(sizeX);
    parser.setCartSizeY(sizeY);
    GrapheneKmc kmc = new GrapheneKmc(parser);
    return kmc;
  }

  private static void initialiseRates(GrapheneSyntheticRates ratesFactory, GrapheneKmc kmc) {
    double depositionRatePerSite = ratesFactory.getDepositionRatePerSite();
    double islandDensity = ratesFactory.getIslandDensity(0);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.reset();
    kmc.initialiseRates(ratesFactory.getRates(0));
    kmc.depositSeed(); //might not be needed, it is a multiflake simulation
  }
     
  /**
   * Private class responsible to repaint every 100 ms the KMC frame.
   */
  static final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        frame.repaintKmc();
        try {
          paintLoop.sleep(250);
        } catch (Exception e) {
        }
      }
    }
  }

}
