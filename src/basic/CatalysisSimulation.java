/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package basic;

import kineticMonteCarlo.kmcCore.growth.CatalysisFarkasKmc;
import kineticMonteCarlo.kmcCore.growth.CatalysisKmc;
import ratesLibrary.CatalysisKiejnaRates;
import ratesLibrary.CatalysisRates;
import ratesLibrary.CatalysisReuterRates;
import ratesLibrary.CatalysisSeitsonenRates;
import ratesLibrary.CatalysisFarkasRates;
import ratesLibrary.IRates;

/**
 *
 * @author karmele, J. Alberdi-Rodriguez
 */
public class CatalysisSimulation extends AbstractGrowthSimulation {
    
  public CatalysisSimulation(Parser parser) {
    super(parser);
  } 
  
  @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    switch (getParser().getRatesLibrary()) {
      case "reuter":
        setRates(new CatalysisReuterRates(getParser().getTemperature()));
        break;
      case "kiejna":
        setRates(new CatalysisKiejnaRates(getParser().getTemperature()));
        break;
      case "seitsonen":
        setRates(new CatalysisSeitsonenRates(getParser().getTemperature()));
        break;
      case "farkas":
        setRates(new CatalysisFarkasRates(getParser().getTemperature()));
        break;
      default:
        System.out.println("Rates not set. Execution will fail.");
    }
    if (getParser().getRatesLibrary().equals("farkas")) {
      setKmc(new CatalysisFarkasKmc(getParser()));
    } else {
      setKmc(new CatalysisKmc(getParser()));
    }
    initialiseRates(getRates(), getParser());
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    CatalysisRates r = (CatalysisRates) rates;
    r.setPressureO2(parser.getPressureO2());
    r.setPressureCO(parser.getPressureCO());
    r.computeAdsorptionRates();
    ((CatalysisKmc) getKmc()).setRates(r);
    getKmc().initialiseRates(rates.getRates(parser.getTemperature()));
  }
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());
    //we modify the 1D array into a 3D array;
    int length = 2;

    for (int i = 0; i < length; i++) { // CO or O
      for (int j = 0; j < length; j++) { // from BR or CUS
        for (int k = 0; k < length; k++) { // to BR or CUS
          System.out.printf("%1.3E  ", rates[i * length * length + j * length + k]);
        }
        System.out.println(" ");
      }
    }
  }
}
