/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmc;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import kineticMonteCarlo.list.ListConfiguration;
import ratesLibrary.diffusion.agAgGrowth.AgAgGrowthRatesFactory;
import static samples.agAgGrowth.SimpleAgAgGrowthKmcSimulation.constant_Y;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class AgAgPsdTest {

  public static float constant_Y = (float) Math.sqrt(3) / 2.0f;

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgAgGrowthRatesFactory ratesFactory = new AgAgGrowthRatesFactory();

    AgAgKmc kmc = initialize_kmc();

    //it is a good idea to divide the sample surface dimensions by two ( e.g. 256->128)
    PsdSignature2D PSD = new PsdSignature2D(128, 128);
    float[][] sampledSurface = new float[128][128];

    for (int i = 0; i < 30; i++) {
      initializeRates(ratesFactory, kmc);
      kmc.simulate();

      kmc.getSampledSurface(sampledSurface);

      PSD.addSurfaceSample(sampledSurface);
      System.out.println("flake " + i);
    }
    PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    new Frame2D("PSD analysis").setMesh(MathUtils.avg_Filter(PSD.getPSD(), 1));

    new Frame2D("Sampled surface").setMesh(sampledSurface);

  }

  private static DiffusionKmcFrame create_graphics_frame(AgAgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
    return frame;
  }

  private static AgAgKmc initialize_kmc() {

    ListConfiguration config = new ListConfiguration()
            .setList_type(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / constant_Y);

    AgAgKmc kmc = new AgAgKmc(config, sizeX, sizeY, true);

    return kmc;
  }

  private static void initializeRates(AgAgGrowthRatesFactory reatesFactory, AgAgKmc kmc) {

    double deposition_rate = reatesFactory.getDepositionRate("COX_PRB", 135);
    double island_density = reatesFactory.getIslandDensity("COX_PRB", 135);
    kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
    kmc.initializeRates(reatesFactory.getRates("COX_PRB", 135));

  }

}
