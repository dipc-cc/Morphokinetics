package basic;

import kineticMonteCarlo.kmcCore.growth.ConcertedKmc;
import ratesLibrary.AgRatesFromPrbCox;
import ratesLibrary.AgSimpleRates;
import ratesLibrary.Concerted6Rates;
import ratesLibrary.IRates;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedSimulation  extends AbstractGrowthSimulation {

  public ConcertedSimulation(Parser parser) {
    super(parser);
  }
  
 @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new Concerted6Rates(getParser().getTemperature()));
    
    setKmc(new ConcertedKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    Concerted6Rates r = (Concerted6Rates) rates;
    
    double depositionRatePerSite;
    rates.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    ((ConcertedKmc) getKmc()).setRates(r);
  }
  
}
