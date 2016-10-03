/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.AgUcSimulation;
import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.BasicGrowthSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import basic.SiSimulation;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 * Morphokinetics is a software to simulate kinetics Monte Carlo (KMC) processes. It can simulate
 * etching and CVD growing processes. To specify simulation mode "parameters" file must be present
 * in the current working directory (more details in {@link Parser}).
 *
 * @author J. Alberdi-Rodriguez
 */
public class Morphokinetics {
  
  public static void main(String[] args) throws JSONException {
    AbstractSimulation.printHeader();

    Parser parser = new Parser();
    parser.readFile("parameters");
    parser.print();
    try {
      // check we whether are in android or not
      String className;
      if (System.getProperty("java.vm.name").equals("Dalvik")) {
        className = "main.AndroidConfigurator";
      } else {
        className = "main.PcConfigurator";
      }
      Class<?> genericClass = Class.forName(className);
      IConfigurator configuration = (IConfigurator) genericClass.getConstructors()[0].newInstance();
      AbstractSimulation.printHeader();

      switch (parser.getCalculationType()) {
        case "batch":
          batchSimulation(parser);
          break;
        case "evolutionary":
          configuration.evolutionarySimulation(parser);
          break;
        case "psd":
          configuration.psdFromSurfaces(parser);
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
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(Morphokinetics.class.getName()).log(Level.SEVERE, null, ex);
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

  private static void printEnd() {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    System.out.println("Execution finished on " + dateFormat.format(date)); //2014/08/06 15:59:48
  }
}
