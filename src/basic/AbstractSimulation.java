/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.AbstractKmc;
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

  /**
   * Initialises Kmc, the basic simulation class
   */
  public void initialiseKmc() {
    switch (currentParser.getListType()) {
      case "linear": {
        this.config = new ListConfiguration().setListType(ListConfiguration.LINEAR_LIST);
        break;
      }
      case "binned": {
        this.config = new ListConfiguration().setListType(ListConfiguration.BINNED_LIST)
                .setBinsPerLevel(currentParser.getBinsLevels())
                .setExtraLevels(currentParser.getExtraLevels());
        break;
      }
      default:
        System.err.println("listType is not properly set");
        System.err.println("listType currently is " + currentParser.getListType());
        System.err.println("Available options are \"linear\" and \"binned\" ");
        this.config = null;
    }
    sizeX = currentParser.getSizeX();
    sizeY = currentParser.getSizeY();
    this.kmc = null;
    this.ratesFactory = null;
  }

  /**
   * Creates the simulation frame.
   */
  public abstract void createFrame();

  public void doSimulation() {
    float[][] sampledSurface = null;
    long startTime = System.currentTimeMillis();

    if (currentParser.doPsd()) {
      //it is a good idea to divide the sample surface dimensions by two (e.g. 256->128)
      psd = new PsdSignature2D(currentParser.getSizeX() / 2, currentParser.getSizeY() / 2);
      sampledSurface = new float[currentParser.getSizeX() / 2][currentParser.getSizeY() / 2];
    }

    // Main loop
    for (int simulations = 0; simulations < currentParser.getNumberOfSimulations(); simulations++) {
      long iterStartTime = System.currentTimeMillis();
      initializeRates(ratesFactory, kmc, currentParser);
      kmc.simulate();
      if (currentParser.printToImage()) {
        frame.printToImage(simulations);
      }
      if (currentParser.doPsd()) {
        kmc.getSampledSurface(sampledSurface);
        psd.addSurfaceSample(sampledSurface);
        if (currentParser.outputData()) {
          psd.printToFile(simulations);
          psd.printSurfaceToFile(simulations, sampledSurface);
        }
      }
      System.out.println("Simulation number " + simulations + " executed in "
              + (System.currentTimeMillis() - iterStartTime) + " ms");
    }
    System.out.println("All " + currentParser.getNumberOfSimulations() + " simulations executed in "
            + ((System.currentTimeMillis() - startTime) / currentParser.getNumberOfSimulations()) + " ms");
    System.out.println("Executed " + currentParser.getNumberOfSimulations() + " simulations in "
            + (System.currentTimeMillis() - startTime) + ". Average iteration time = "
            + ((System.currentTimeMillis() - startTime) / currentParser.getNumberOfSimulations()) + " ms");

    if (currentParser.doPsd()) {
      psd.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
      psd.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);
      new Frame2D("PSD analysis").setMesh(MathUtils.avgFilter(psd.getPsd(), 1))
              .setLogScale(true)
              .setShift(true)
              .performDrawToImage(1);

      new Frame2D("Sampled surface").setMesh(sampledSurface).performDrawToImage(2);
    }
  }

  protected abstract void initializeRates(IRatesFactory ratesFactory, AbstractKmc kmc, Parser myParser);

  public abstract void finishSimulation();

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
