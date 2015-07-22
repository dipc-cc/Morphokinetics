/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import basic.SiSimulation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Morphokinetics {

  public static void main(String[] args) {
    printHeader();

    Parser myParser = new Parser();
    try {
      myParser.readFile("parameters");
    } catch (IOException ex) {
      Logger.getLogger(Morphokinetics.class.getName()).log(Level.SEVERE, null, ex);
    }

    myParser.print();
    AbstractSimulation currentSimulation = null;
    switch (myParser.getCalculationMode()) {
      case "Ag":
        currentSimulation = new AgSimulation(myParser);
        break;
      case "graphene":
        currentSimulation = new GrapheneSimulation(myParser);
        break;
      case "Si":
        currentSimulation = new SiSimulation(myParser);
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    currentSimulation.initialiseKmc();
    currentSimulation.createFrame();
    currentSimulation.doSimulation();
    currentSimulation.finishSimulation();

    System.out.println("Execution has finished");
  }

  private static void printHeader() {
    System.out.println("This is morphokinetics software");
  }
}
