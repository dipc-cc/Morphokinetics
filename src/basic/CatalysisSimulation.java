/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package basic;

import kineticMonteCarlo.kmcCore.growth.CatalysisKmc;
import ratesLibrary.CatalysisRates;
import ratesLibrary.IRates;

/**
 *
 * @author karmele
 */
public class CatalysisSimulation extends AbstractGrowthSimulation {
    
  public CatalysisSimulation(Parser parser) {
    super(parser);
  } 
  
  @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    setRates(new CatalysisRates());
    setKmc(new CatalysisKmc(getParser()));
    initialiseRates(getRates(), getParser());
    
  } 
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    super.initialiseRates(rates,parser);
    double[] adsorptionRates = ((CatalysisRates) rates).getAdsorptionRates(parser.getTemperature(), parser.getPresure());
    ((CatalysisKmc) getKmc()).setAdsorptionRates(adsorptionRates);
  }
}
