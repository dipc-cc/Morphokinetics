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
import basic.io.Restart;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.AbstractGeneticAlgorithm;
import geneticAlgorithm.GeneticAlgorithmDcmaEs;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import graphicInterfaces.MainInterface;
import graphicInterfaces.growth.KmcCanvas;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.lattice.AgLattice;
import ratesLibrary.AgRatesFactory;
import ratesLibrary.SiRatesFactory;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Morphokinetics {

  public static void main(String[] args) {
    AbstractSimulation.printHeader();

    Parser parser = new Parser();
    parser.readFile("parameters");
    parser.print();

    switch (parser.getCalculationType()) {
      case "batch":
        batchSimulation(parser);
        break;
      case "evolutionary":
        evoluationarySimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation type. This simulation mode is not implemented!");
        System.err.println("Current value: " + parser.getCalculationType() + ". Possible values are batch or evolutionary");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    System.out.println("Execution has finished");
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
      case "graphene":
        simulation = new GrapheneSimulation(parser);
        break;
      case "Si":
        simulation = new SiSimulation(parser);
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

  private static void evoluationarySimulation(Parser parser) {
    AbstractGeneticAlgorithm ga;
    switch (parser.getEvolutionaryAlgorithm()) {
      case "original":
        ga = new GeneticAlgorithm(parser);
        break;
      case "dcma":
        ga = new GeneticAlgorithmDcmaEs(parser);
        break;
      default:
        System.err.println("Error: Default evolutionary algorithm. This evolutionary algorithm is not implemented!");
        System.err.println("Current value: "+parser.getEvolutionaryAlgorithm());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

    AgKmc myKmc = (AgKmc) ga.getKmc();
    AgLattice myLattice = (AgLattice) myKmc.getLattice();
    KmcCanvas myCanvas = new KmcCanvas(myLattice);
    MainInterface mainInterface = new MainInterface(myCanvas);
    mainInterface.setVisible(true);
    ga.setMainInterface(mainInterface);
    //float[][] experimentalPsd = createExperimentalData(parser, ga);
    float[][] experimentalPsd;
    if (parser.getReadReference())
      experimentalPsd = readExperimentalData();
    else 
      experimentalPsd = createExperimentalData(parser, ga);
    mainInterface.setExperimentalMesh(MathUtils.avgFilter(experimentalPsd, 1));
    ga.setExperimentalPsd(experimentalPsd);
    //ga.setExpectedSimulationTime(simulationTime);
    ga.initialise();
    
    ga.iterate(parser.getTotalIterations());
    printResult(ga);
    /*experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    new Frame2D("Calculated PSD analysis").setMesh(MathUtils.avgFilter(experimentalPsd, 1))
            .setLogScale(true)
            .setShift(true)
            .performDrawToImage(1);*/
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
        readSurface = restart.readSurfaceBinary2D(surfaceFileName+i+".mko",2);
        if (psd == null) {
          sizes = new int[2];
          sizes[0] = restart.getSizeX();
          sizes[0] = readSurface.length;
          sizes[1] = restart.getSizeY();
          sizes[1] = readSurface[0].length;
          psd = new PsdSignature2D(sizes[0], sizes[1]);
        }
      } catch (Exception e){
        System.err.println("Provided filename ["+surfaceFileName+i+".mko] does not exist. Exiting");
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
  
  private static float[][] createExperimentalData(Parser parser, AbstractGeneticAlgorithm ga) {
    
    AbstractPsdEvaluator evaluator = ga.getMainEvaluator();
    evaluator.setRepeats(evaluator.getRepeats() * 5);
    
    double[] rates = null;
    switch (parser.getCalculationMode()) {
      case "Ag":
        rates = new AgRatesFactory().getRates(parser.getTemperature());
        break;
      case "Si":
        rates = new SiRatesFactory().getRates(parser.getTemperature());
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        System.err.println("Current value: "+parser.getCalculationMode());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    Individual individual = new Individual(rates);
    float[][] experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    double simulationTime = individual.getSimulationTime();
    evaluator.setRepeats(evaluator.getRepeats() / 5);
    return experimentalPsd;
  }
  
  private static void printResult(IGeneticAlgorithm ga) {
    Individual individual = ga.getIndividual(0);
    
    System.out.println("These are the results: ");
    System.out.println("Total error is:" + individual.getTotalError());
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(individual.getGene(gene) + " ");
    }
    System.out.println();
  }
}
