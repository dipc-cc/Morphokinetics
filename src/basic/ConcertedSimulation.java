package basic;

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

    switch (getParser().getRatesLibrary()) {
      case "CuNi":
        setRates(new ConcertedCuNiRates(getParser().getTemperature()));
        break;
      case "NiCu":
        setRates(new ConcertedNiCuRates(getParser().getTemperature()));
        break;
      case "AgAg":
        setRates(new ConcertedAgAgRates(getParser().getTemperature()));
        break;
      case "PdPd":
        setRates(new ConcertedPdPdRates(getParser().getTemperature()));
        break;   
      default:
        System.out.println("Rates not set. Execution will fail.");
    }
    
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
