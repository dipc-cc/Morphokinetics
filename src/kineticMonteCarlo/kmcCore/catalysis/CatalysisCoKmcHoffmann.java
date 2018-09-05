/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.kmcCore.catalysis;

import basic.Parser;
import basic.io.CatalysisCoRestart;
import basic.io.OutputType;
import kineticMonteCarlo.activationEnergy.CatalysisCoActivationEnergy;
import kineticMonteCarlo.lattice.CatalysisCoHoffmannLattice;
import static kineticMonteCarlo.lattice.CatalysisCoHoffmannLattice.N_REACT;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisSite.O;
import kineticMonteCarlo.unitCell.CatalysisUc;
import ratesLibrary.CatalysisRates;
import utils.StaticRandom;

/**
 * Based on paper "kmos: A lattice kinetic Monte Carlo framework". M.J.
 * Hoffmann, S. Matera, K. Reuter. Computer Physics Communications 185(2014)
 * 2138 - 2150
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoKmcHoffmann extends CatalysisKmc {
  
  private final boolean doAdsorption;
  // Adsorption
  private double adsorptionRateCOPerSite;
  private double adsorptionRateOPerSite;
  // Desorption
  private double[] desorptionRateCOPerSite; // BRIDGE or CUS
  private double[] desorptionRateOPerSite;  // [BR][BR], [BR][CUS], [CUS][BR], [CUS][CUS]
  // Reaction
  private double[] reactionRateCoO; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  private double[] diffusionRateCO;
  private double[] diffusionRateO;

  /**
   * Activation energy output during the execution
   */
  private final boolean outputAe;
  private final boolean outputAeTotal;
  private long co2sum;
  /** Previous instant co2sum. For output */
  private long co2prv;
  private final long maxSteps;
  private final int co2max;
  private long[] co2; // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
  /** For previous rates. It is a local variable, but this way, garbage collector works better.*/
  double[] previousRate;
  /// Hoffmann 
  private final double[] rateConstant;
  private final int[] numberOfSites; // same as the numberOfSites of lattice // Nr. of sites N^avail_a
  private final double[] accumRates; // rateConstant * numberOfSites
  private double totalRateKTot;
  
  private final CatalysisCoUpdate update;
  
  private CatalysisCoHoffmannLattice lattice;
  
  private final CatalysisSite[] affectedSites;
  private int affectedSitesLength;
  
  public CatalysisCoKmcHoffmann(Parser parser, String restartFolder) {
    super(parser, restartFolder);
    doAdsorption = parser.doCatalysisAdsorption();
    outputAe = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    outputAeTotal = parser.getOutputFormats().contains(OutputType.formatFlag.AETOTAL);
    boolean outputData = parser.outputData();
    CatalysisCoRestart restart = new CatalysisCoRestart(outputData, restartFolder);
    setRestart(restart);
    co2max = parser.getNumberOfCo2();
    co2 = new long[4];
    co2sum = 0;
    previousRate = new double[20];
    setActivationEnergy(new CatalysisCoActivationEnergy(parser));
    rateConstant = new double[N_REACT];
    numberOfSites = new int[N_REACT];
    accumRates = new double[N_REACT];
    update = new CatalysisCoUpdate();
    maxSteps = parser.getNumberOfSteps();
    affectedSites = new CatalysisSite[2];
    affectedSitesLength = 0;
  }
  
  @Override
  void init(Parser parser) {
    lattice = new CatalysisCoHoffmannLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), parser.getRatesLibrary());
    lattice.init();
    setLattice(lattice);
  }
  
  @Override
  int getNumberOfReactions() {
    return 20;
  }
  
  @Override
  public void setRates(CatalysisRates rates) {
    desorptionRateCOPerSite = new double[2]; // empty
    desorptionRateOPerSite = new double[4]; // empty 
    reactionRateCoO = new double[4]; // empty
    diffusionRateCO = new double[4]; // empty
    diffusionRateO = new double[4]; // empty
    if (doAdsorption) {
      adsorptionRateCOPerSite = rates.getAdsorptionRate(CO);
      adsorptionRateOPerSite = rates.getAdsorptionRate(O);
    }
    if (doDesorption) {
      desorptionRateCOPerSite = rates.getDesorptionRates(CO);
      desorptionRateOPerSite = rates.getDesorptionRates(O);
    }
    if (doReaction) {
      reactionRateCoO = rates.getReactionRates();
    }
    if (doDiffusion) {
      diffusionRateCO = rates.getDiffusionRates(CO);
      diffusionRateO = rates.getDiffusionRates(O);
    }
    
    if (outputAe || outputAeTotal) {
      double[][] processProbs2D = new double[2][2];

      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
          processProbs2D[i][j] = rates.getReactionRates()[i * 2 + j];
        }
      }
      getActivationEnergy().setRates(processProbs2D);
    }
    rateConstant[0] = adsorptionRateCOPerSite;
    rateConstant[1] = adsorptionRateOPerSite;
    rateConstant[2] = desorptionRateCOPerSite[BR];
    rateConstant[3] = desorptionRateCOPerSite[CUS];
    rateConstant[4] = desorptionRateOPerSite[BR+BR];
    rateConstant[5] = desorptionRateOPerSite[BR+CUS];
    rateConstant[6] = desorptionRateOPerSite[CUS+BR];
    rateConstant[7] = desorptionRateOPerSite[BR+BR+1];
    rateConstant[8] = reactionRateCoO[0];
    rateConstant[9] = reactionRateCoO[1];
    rateConstant[10] = reactionRateCoO[2];
    rateConstant[11] = reactionRateCoO[3];
    rateConstant[12] = diffusionRateCO[0];
    rateConstant[13] = diffusionRateCO[1];
    rateConstant[14] = diffusionRateCO[2];
    rateConstant[15] = diffusionRateCO[3];
    rateConstant[16] = diffusionRateO[0];
    rateConstant[17] = diffusionRateO[1];
    rateConstant[18] = diffusionRateO[2];
    rateConstant[19] = diffusionRateO[3];
  }
  
  @Override
  public int simulate() {
    int returnValue = 0;

    while (maxProduction() && simulatedSteps < maxSteps) {     
      if (performSimulationStep()) {
        break;
      }
    }
    
    return returnValue;
  }
  
  /**
   * Performs a simulation step.
   *
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    int reaction = nextEvent(StaticRandom.raw());
    switch (reaction) {
      case -1:
        return true;
      case 0:
        adsorbCo();
        break;
      case 1:
        adsorbO();
        break;
      case 2:
      case 3:
        desorbCo(reaction);
        break;
      case 4:
      case 5:
      case 6:
      case 7:
        desorbO2(reaction);
        break;
      case 8:
      case 9:
      case 10:
      case 11:
        react(reaction);
        break;
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
        diffuse(reaction);
        break;
    }
    updateRates();
    simulatedSteps++;
    return false;
  }
  
  private int nextEvent(double randomNumber) {
    double position = randomNumber * totalRateKTot;
    double sum = 0.0;
    
    for (byte process = 0; process < accumRates.length; process++) { //Adsorption, desorption, reaction, diffusion
      sum += accumRates[process];
      if (position < sum) {
        return process;
      }
    }
    return -1;
  }
  
  /**
   * Iterates over all lattice sites and initialises adsorption probabilities.
   */
  @Override
  void initAdsorptionRates() {
    //private int[] numberOfSites; // Nr. of sites N^avail_a
    for (int i = 0; i < getLattice().size(); i++) {
      CatalysisUc uc = getLattice().getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisSite a = uc.getSite(j);
        //a.setRate(ADSORPTION, adsorptionRateCOPerSite + adsorptionRateOPerSite); // there is no neighbour
        a.setOnList((byte) 0, true);
        a.setOnList((byte) 1, true);
        //totalRate[ADSORPTION] += a.getRate(ADSORPTION);
        //((CatalysisCoLattice) getLattice()).insert(100-i-1, 0);//
        lattice.insert(a.getId(), 0);
        //((CatalysisCoLattice) getLattice()).insert(100-i-1, 1);//
        lattice.insert(a.getId(), 1);
        numberOfSites[0]++;
        numberOfSites[1]++;
      }
    }
    accumRates[0] = numberOfSites[0] * rateConstant[0];
    accumRates[1] = numberOfSites[1] * rateConstant[1];
    totalRateKTot = accumRates[0];
    totalRateKTot += accumRates[1];
    
    //getList().setRates(totalRate);
  }
  
  private void adsorbCo() {
    byte atomType = CO;
    int randomNumber = StaticRandom.rawInteger(numberOfSites[0]);
    CatalysisSite site = lattice.getAvailableSite(0, randomNumber);
    site.setType(atomType);
    if (site.isOccupied()) {
      System.out.println("ERROR!! "+site.getId());
    }
    lattice.deposit(site, false);
    affectedSites[0] = site;
    affectedSitesLength = 1; 
  }
  
  private void adsorbO() {
    byte atomType = O;
    int randomNumber = StaticRandom.rawInteger(numberOfSites[1]);
    CatalysisSite site = lattice.getAvailableSite(1, randomNumber);
    site.setType(atomType);
    lattice.deposit(site, false);
    // it has to deposit two O (dissociation of O2 -> 2O)
    randomNumber = StaticRandom.rawInteger(4);
    CatalysisSite neighbour = site.getNeighbour(randomNumber);
    while (neighbour.isOccupied()) {
      randomNumber = (randomNumber + 1) % 4;
      neighbour = site.getNeighbour(randomNumber);
    }
    neighbour.setType(O);
    lattice.deposit(neighbour, false);
    affectedSites[0] = site;
    affectedSites[1] = neighbour;
    affectedSitesLength = 2;
  }

  private void desorbCo(int process) {
    int randomNumber = StaticRandom.rawInteger(numberOfSites[process]);
    CatalysisSite site = lattice.getAvailableSite(process, randomNumber);
   
    lattice.extract(site);
    affectedSites[0] = site;
    affectedSitesLength = 1;  
  }
  
  private void desorbO2(int process) {
    int randomNumber = StaticRandom.rawInteger(numberOfSites[process]);
    CatalysisSite site = lattice.getAvailableSite(process, randomNumber);
    lattice.extract(site);
    
    int startI;
    if (process == 4 || process == 7) startI = 0;
    else  startI = 1;
    if (StaticRandom.raw() < 0.5) {
      startI += 2;
    }
    CatalysisSite neighbour = site.getNeighbour(startI);
    if (neighbour.getType() == O && neighbour.isOccupied()) {
      lattice.extract(neighbour);
    } else {
      startI = (startI + 2) % 4;
      neighbour = site.getNeighbour(startI);
      lattice.extract(neighbour);
    }
    
    affectedSites[0] = site;
    affectedSites[1] = neighbour;
    affectedSitesLength = 2;
  }
  
  private void react(int process) {
    int randomNumber = StaticRandom.rawInteger(numberOfSites[process]);
    CatalysisSite site = lattice.getAvailableSite(process, randomNumber);
    lattice.extract(site);
    
    int startI;
    if (process == 8 || process == 11) startI = 0;
    else  startI = 1;
    if (StaticRandom.raw() < 0.5) {
      startI += 2;
    }
    CatalysisSite neighbour = site.getNeighbour(startI);
    if (neighbour.getType() == O && neighbour.isOccupied()) {
      lattice.extract(neighbour);
    } else {
      startI = (startI + 2) % 4;
      neighbour = site.getNeighbour(startI);
      lattice.extract(neighbour);
    }
    affectedSites[0] = site;
    affectedSites[1] = neighbour;
    affectedSitesLength = 2;
  }
  
  private void diffuse(int process) {
    int randomNumber = StaticRandom.rawInteger(numberOfSites[process]);
    CatalysisSite site = lattice.getAvailableSite(process, randomNumber);
    lattice.extract(site);
    
    int startI;
    if (process == 12 || process == 15 || process == 16 || process == 19) startI = 0;
    else  startI = 1;
    double rand = StaticRandom.raw();
    if (rand < 0.5) {
      startI += 2;
    }
    CatalysisSite neighbour = site.getNeighbour(startI);
    if (!neighbour.isOccupied()) {
      lattice.deposit(neighbour, false);
    } else {
      startI = (startI + 2) % 4;
      neighbour = site.getNeighbour(startI);
      lattice.deposit(neighbour, false);
    }
    neighbour.setType(site.getType());
    neighbour.swapAttributes(site);
    affectedSites[0] = site;
    affectedSites[1] = neighbour;
    affectedSitesLength = 2;
  }
  
  /**
   * Updates total adsorption, desorption, reaction and diffusion probabilities.
   *
   * @param sites
   */
  private void updateRates() {
    int length = affectedSitesLength;
    for (int i = 0; i < length; i++) {
      CatalysisSite site = affectedSites[i];
      if (length == 1) {
        updateOneRate(site);
      }
      for (int j = 0; j < affectedSites[i].getNumberOfNeighbours(); j++) {
        updateOneRate(site.getNeighbour(j));
      }
    }
    
    totalRateKTot = 0.0;
    for (int i = 0; i < accumRates.length; i++) {
      accumRates[i] = numberOfSites[i] * rateConstant[i];
      totalRateKTot += accumRates[i];
    }
  }
  
  private void updateOneRate(CatalysisSite site) {
    for (int e = 0; e < N_REACT; e++) {
      int diff = update.check(e, site);
      numberOfSites[e] += diff;
      if (diff == -1) {
        lattice.remove(site.getId(), e);
      }
      if (diff == 1) {
        lattice.insert(site.getId(), e);
      }
    }
  }
  
  @Override
  void initStationary() {
    co2prv = 0;
    co2 = new long[4];
  }

  @Override
  long[] getProduction() {
    return co2;
  }
  
  @Override
  long getSumProduction() {
    return co2sum;
  }

  @Override
  boolean writeNow() {
    return co2sum % 10 == 0 && co2prv != co2sum;
  }
  
  @Override
  void updatePrevious() {
    co2prv = co2sum;
  }
  
  @Override
  boolean maxProduction() {
    return co2sum != co2max;
  }
  
  @Override
  public void reset() {
    super.reset();
    co2 = new long[getNumberOfReactions()];
    co2sum = 0;
    
    totalRateKTot = 0.0;
    for (int i = 0; i < accumRates.length; i++) {
      accumRates[i] = 0;
      numberOfSites[i] = 0;
    }
  }
  
  /**
   * Method to print rates. Equivalent to table 1 of Temel et al. J. Chem. Phys. 126 (2007).
   */
  @Override
  public void printRates() {
    System.out.println(" Process ");
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "CO adsorption\t", adsorptionRateCOPerSite);
    System.out.format("%s\t%1.1e\t%1.1e\n", " O adsorption\t",adsorptionRateOPerSite, adsorptionRateOPerSite * 2);
    System.out.println(" ------- ");
    System.out.println("Desorption");
    System.out.format("%s\t\t%1.1e\n","CO^BR", desorptionRateCOPerSite[0]);
    System.out.format("%s\t\t%1.1e\n", "CO^CUS", desorptionRateCOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^BR", desorptionRateOPerSite[0]);
    System.out.format("%s\t%1.1e\n", "O^BR + O^CUS", desorptionRateOPerSite[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^BR", desorptionRateOPerSite[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS + O^CUS", desorptionRateOPerSite[3]);
    System.out.println(" ------- ");
    System.out.println("Reaction");
    System.out.format("%s\t%1.1e\n", "CO^BR + O^BR", reactionRateCoO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR + O^CUS", reactionRateCoO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^BR", reactionRateCoO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS + O^CUS", reactionRateCoO[3]);
    System.out.println(" ------- ");
    System.out.println("Diffusion");
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^BR  ", diffusionRateCO[0]);
    System.out.format("%s\t%1.1e\n", "CO^BR -> CO^CUS ", diffusionRateCO[1]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^BR ", diffusionRateCO[2]);
    System.out.format("%s\t%1.1e\n", "CO^CUS -> CO^CUS", diffusionRateCO[3]);
    System.out.println(" ------- ");
    System.out.format("%s\t%1.1e\n", "O^BR -> O^BR    ", diffusionRateO[0]);
    System.out.format("%s\t%1.1e\n", "O^BR -> O^CUS   ", diffusionRateO[1]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^BR   ", diffusionRateO[2]);
    System.out.format("%s\t%1.1e\n", "O^CUS -> O^CUS  ", diffusionRateO[3]);
  }

  @Override
  void depositNewAtom() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void desorbAtom() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void reactAtom() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void diffuseAtom() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void initCovered() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
