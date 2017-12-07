package basic;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.ConcertedKmc;
import ratesLibrary.concerted.ConcertedAgAgRates;
import ratesLibrary.concerted.ConcertedCuNiRates;
import ratesLibrary.concerted.ConcertedNiCuRates;
import ratesLibrary.IRates;
import ratesLibrary.concerted.AbstractConcertedRates;
import ratesLibrary.concerted.ConcertedPdPdRates;


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
  
}
