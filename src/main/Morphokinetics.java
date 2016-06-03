/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import org.json.JSONException;

import basic.AgUcSimulation;
import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.BasicGrowthSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import basic.SiSimulation;
import basic.io.Restart;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFactory;
import ratesLibrary.BasicGrowthRatesFactory;
import ratesLibrary.SiRatesFactory;
import utils.MathUtils;
import utils.Wait;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Morphokinetics {

  private static double simulationTime;
  
  public static void main(String[] args) throws JSONException {
    AbstractSimulation.printHeader();

    Parser parser = new Parser();
    parser.readFile("parameters");
    parser.print();

    switch (parser.getCalculationType()) {
      case "batch":
        batchSimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation type. This simulation mode is not implemented!");
        System.err.println("Current value: " + parser.getCalculationType() + ". Possible values are batch or evolutionary");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    printEnd();
    if (!parser.withGui() || !parser.visualise()) {
      System.exit(0);
    }
  }

  private static void batchSimulation(Parser parser) {
    AbstractSimulation simulation = null;
    switch (parser.getCalculationMode()) {
      case "Ag":
        simulation = new AgSimulation(parser);
        break;
      case "AgUc":
        simulation = new AgUcSimulation(parser);
        break;
      case "graphene":
        simulation = new GrapheneSimulation(parser);
        break;
      case "Si":
        simulation = new SiSimulation(parser);
        break;
      case "basic":
        simulation = new BasicGrowthSimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
  }

  
  private static float[][] readExperimentalData() {
    Restart restart = new Restart();
    int[] sizes = null;
    float[][] readSurface = null;
    int islandCount = 0;
    int numberOfFiles = 43;
    String surfaceFileName = "psdFromImage/islands/3.OuterIsolatedSmall/island";
    PsdSignature2D psd = null;
    for (int i = 1; i <= numberOfFiles; i++) {
      try {
        readSurface = restart.readSurfaceBinary2D(surfaceFileName + i + ".mko", 2);
        if (psd == null) {
          sizes = new int[2];
          sizes[0] = restart.getSizeX();
          sizes[0] = readSurface.length;
          sizes[1] = restart.getSizeY();
          sizes[1] = readSurface[0].length;
          psd = new PsdSignature2D(sizes[0], sizes[1], 1);
        }
      } catch (Exception e){
        System.err.println("Provided filename [" + surfaceFileName + i + ".mko] does not exist. Exiting");
        //throw e;
        continue;
      }
      islandCount++;
      MathUtils.applyGrowthAccordingDistanceToPerimeter(readSurface);
      psd.addSurfaceSample(readSurface);
    }

    psd.printAvgToFile();
    return psd.getPsd();
  }

  private static void printEnd() {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    System.out.println("Execution finished on " + dateFormat.format(date)); //2014/08/06 15:59:48
  }
}
