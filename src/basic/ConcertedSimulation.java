package basic;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.ConcertedKmc;
import ratesLibrary.IRates;
import ratesLibrary.concerted.AbstractConcertedRates;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author J. Alberdi-Rodriguez, 
 */
public class ConcertedSimulation  extends AbstractGrowthSimulation {

  public ConcertedSimulation(Parser parser) {
    super(parser);
  }
  
 @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    AbstractConcertedRates rates = null;
    String className;
    className = "ratesLibrary.concerted.Concerted" + getParser().getRatesLibrary() + "Rates";
    try {
      Class<?> genericClass = Class.forName(className);
      rates = (AbstractConcertedRates) genericClass.getConstructors()[0].newInstance(getParser().getTemperature());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(ConcertedSimulation.class.getName()).log(Level.SEVERE, null, ex);
    }
    setRates(rates);
    setKmc(new ConcertedKmc(getParser(), getRestartFolderName()));
    initialiseRates(getRates(), getParser());
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    AbstractConcertedRates r = (AbstractConcertedRates) rates;
    
    double depositionRatePerSite;
    rates.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    ((ConcertedKmc) getKmc()).setRates(r);
  }
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());
    //we modify the 1D array into a 2D array;
    int columns = 16;
    int rows = 12;
    
    // print header
    System.out.println("   0          1          12         2          13         3          14         4"+
        "          5          15         6          7          8          9          10         11");
    System.out.println("   0          1           2         3           4         5           6         7"+
        "          8           9         10         11         12         13         14         15");

    for (int i = 0; i < rows; i++) {
      System.out.printf("%02d ",i);
      for (int j = 0; j < columns; j++) {
        if (rates[i * columns + j] < 1e-120) {
          System.out.printf("           ");
        } else {
          System.out.printf("%1.3E  ", rates[i * columns + j]);
        }
      }
      System.out.println(" ");
    }
    System.out.println("Deposition rate (per site): " + getRates().getDepositionRatePerSite());
    System.out.println("Island density:             " + getRates().getIslandDensity(parser.getTemperature()));
    
    double[] concertedRates = ((AbstractConcertedRates) getRates()).getIslandDiffusionRates();
    
    System.out.println("Island diffusion rates:");
    for (int i = 0; i < concertedRates.length; i++) {
      System.out.print(concertedRates[i] + " ");
    }
    System.out.println("");
  }
}
