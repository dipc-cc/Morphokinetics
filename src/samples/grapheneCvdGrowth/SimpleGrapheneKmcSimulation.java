/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.grapheneCvdGrowth;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.grapheneCvdGrowth.GrapheneKmcCanvas;
import kineticMonteCarlo.kmcCore.diffusion.GrapheneKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import utils.list.ListConfiguration;
import ratesLibrary.GrapheneCvdDepositionRatesFactory;

/**
 *
 * @author Nestor
 */
public class SimpleGrapheneKmcSimulation {

    private static final double cos30 = Math.cos(30 * Math.PI / 180);

    public static void main(String args[]) {

        System.out.println("Simple simulation of the Graphene KMC");

        GrapheneCvdDepositionRatesFactory ratesFactory = new GrapheneCvdDepositionRatesFactory();
        GrapheneKmc kmc = initialize_kmc();
        DiffusionKmcFrame frame = create_graphics_frame(kmc);
        frame.setVisible(true);

        for (int i = 0; i < 10; i++) {
            initializeRates(ratesFactory, kmc);
            kmc.simulate();
        }

        float[][] surface = new float[256][256];
        kmc.getSampledSurface(surface);

    }

    private static DiffusionKmcFrame create_graphics_frame(GrapheneKmc kmc) {
        DiffusionKmcFrame frame = new DiffusionKmcFrame(new GrapheneKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
        return frame;
    }

    private static GrapheneKmc initialize_kmc() {

        ListConfiguration config = new ListConfiguration()
                .setListType(ListConfiguration.LINEAR_LIST);

        int sizeX = 256;
        int sizeY = (int) (sizeX * (2 * cos30));
        if ((sizeY & 1) != 0) {
            sizeY++;
        }
        GrapheneKmc kmc = new GrapheneKmc(config, sizeX, sizeY, false);
        return kmc;
    }

    private static void initializeRates(GrapheneCvdDepositionRatesFactory reatesFactory, GrapheneKmc kmc) {

        double deposition_rate = reatesFactory.getDepositionRate("synthetic", 0);
        double island_density = reatesFactory.getIslandDensity("synthetic", 0);
        kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
        kmc.initializeRates(reatesFactory.getRates("synthetic", 0));

    }
}
