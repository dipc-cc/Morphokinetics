/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import ratesLibrary.IRatesFactory;
import utils.MathUtils;
import utils.list.ListConfiguration;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSimulation {

  protected AbstractKmc kmc;
  protected IRatesFactory ratesFactory;
  protected DiffusionKmcFrame frame;
  protected PsdSignature2D psd;

  protected ListConfiguration config;
  protected int sizeX;
  protected int sizeY;
  protected Parser currentParser;

  public AbstractSimulation(Parser myParser) {
    kmc = null;
    ratesFactory = null;
    frame = null;
    psd = null;
    this.currentParser = myParser;
  }

  public void initialiseKmc(AbstractKmc kmc, IRatesFactory ratesFactory) {
    switch (currentParser.getListType()) {
      case "linear": {
        config = new ListConfiguration().setListType(ListConfiguration.LINEAR_LIST);
        break;
      }
      case "binned": {
        config = new ListConfiguration().setListType(ListConfiguration.BINNED_LIST)
                .setBinsPerLevel(100)
                .setExtraLevels(0);
        break;
      }
      default:
        System.err.println("listType is now properly set");
        System.err.println("listType currently is " + currentParser.getListType());
        System.err.println("Available options are \"linear\" and \"binned\" ");
        config = null;
    }
    sizeX = currentParser.getSizeX();
    sizeY = currentParser.getSizeY();
    this.kmc = null;
    this.ratesFactory = null;
  }

  /**
   * Creates the simulation frame. For the moment only the simple AgAgKmcCanvas class.
   */
  public abstract void createFrame();

  public void doSimulation() {
    float[][] sampledSurface = null;
    
    if (currentParser.doPsd()) {
      //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
      psd = new PsdSignature2D(currentParser.getSizeX() / 2, currentParser.getSizeY() / 2);
      sampledSurface = new float[currentParser.getSizeX() / 2][currentParser.getSizeY() / 2];
    }

    // Main loop
    for (int simulations = 0; simulations < currentParser.getNumberOfSimulations(); simulations++) {
      initializeRates(ratesFactory, kmc, currentParser);
      kmc.simulate();
      if (currentParser.isPrintToImage()) {
        frame.printToImage(simulations);
      }
      if (currentParser.doPsd()) {
        kmc.getSampledSurface(sampledSurface);
        psd.addSurfaceSample(sampledSurface);
      }
    }
    //frame.setVisible(false);

    if (currentParser.doPsd()) {
      psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
      psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
      new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(psd.getPsd(), 1));
      //sampledSurface[56][75] = -100;
      new Frame2D("Sampled XXXXsurface").setMesh(sampledSurface);
    }
  }

  private static void initializeRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser) {
    //myParser.getIslandDensityType()
    double depositionRate = ratesFactory.getDepositionRate(myParser.getTemperature());
    double islandDensity = ratesFactory.getIslandDensity(myParser.getTemperature());
    kmc.setIslandDensityAndDepositionRate(depositionRate, islandDensity);
    kmc.initializeRates(ratesFactory.getRates(myParser.getTemperature()));

  }

  public AbstractKmc getKmc() {
    return kmc;
  }

  public DiffusionKmcFrame getFrame() {
    return frame;
  }

  public PsdSignature2D getPsd() {
    return psd;
  }

  public IRatesFactory getRatesFactory() {
    return ratesFactory;
  }

}
