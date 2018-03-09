/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package basic;

import kineticMonteCarlo.kmcCore.catalysis.CatalysisFarkasKmc;
import kineticMonteCarlo.kmcCore.catalysis.CatalysisKmc;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.O;
import ratesLibrary.CatalysisKiejnaRates;
import ratesLibrary.CatalysisRates;
import ratesLibrary.CatalysisReuterRates;
import ratesLibrary.CatalysisSeitsonenRates;
import ratesLibrary.CatalysisFarkasRates;
import ratesLibrary.CatalysisFarkasTestRates;
import ratesLibrary.CatalysisReuterOverRates;
import ratesLibrary.IRates;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisSimulation extends AbstractSurfaceSimulation {
    
  private final float[] coverage;
  
  public CatalysisSimulation(Parser parser) {
    super(parser);
    coverage = new float[3]; // Coverage, Coverage CO, Coverage O
  } 
  
  @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    switch (getParser().getRatesLibrary()) {
      case "reuter":
        setRates(new CatalysisReuterRates(getParser().getTemperature()));
        break;
      case "reuterOver":
        setRates(new CatalysisReuterOverRates(getParser().getTemperature()));
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
      case "test":
        setRates(new CatalysisFarkasTestRates(getParser().getTemperature()));
        break;
      default:
        System.out.println("Rates not set. Execution will fail.");
    }
    if (getParser().getRatesLibrary().equals("farkas")) {
      setKmc(new CatalysisFarkasKmc(getParser(), getRestartFolderName()));
    } else {
      setKmc(new CatalysisKmc(getParser(), getRestartFolderName()));
    }
    initialiseRates(getRates(), getParser());
  }
  
  @Override
  public CatalysisKmc getKmc() {
    return (CatalysisKmc) super.getKmc();
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    CatalysisRates r = (CatalysisRates) rates;
    r.setPressureO2(parser.getPressureO2());
    r.setPressureCO(parser.getPressureCO());
    r.computeAdsorptionRates();
    getKmc().setRates(r);
  }
  
  @Override
  public void printRates(Parser parser) {
    getKmc().printRates();
  }
  
  @Override
  String secondLine() {
    System.out.println("    I\tSimul time\tCover.\tCPU\tCoverage\tCO2\tSteps\tStationary");
    return "\tCO\tO";
  }
  
  @Override
  void printBottom() {
    getKmc().printIteration();
  }
  
  @Override
  float[] getCoverage() {
    CatalysisKmc kmc = (CatalysisKmc) getKmc();
    coverage[0] = kmc.getCoverage();
    coverage[CO+1] += kmc.getCoverage(CO);
    coverage[O+1] += kmc.getCoverage(O);
    return coverage;
  }
  
  @Override
  String printCoverages() {
    int i = getParser().getNumberOfSimulations();
    String kmcResult = "";
    kmcResult += "\t\t" + coverage[CO+1] / (float) (i);
    kmcResult += "\t" + coverage[O+1] / (float) i + "\n";
    return kmcResult;
  }
}
