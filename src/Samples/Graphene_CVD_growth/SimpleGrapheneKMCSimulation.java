/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.Graphene_CVD_growth;

import graphicInterfaces.difussion2DGrowth.DifussionKmcFrame;
import graphicInterfaces.difussion2DGrowth.grapheneCvdGrowth.GrapheneKmcCanvas;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Graphene_CVD_growth.Graphene_KMC;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.List_configuration;
import Rates_library.diffusion.Graphene_CVD_Growth.Graphene_CVD_deposition_rates_factory;

/**
 *
 * @author Nestor
 */
public class SimpleGrapheneKMCSimulation {

    private static final double cos30 = Math.cos(30 * Math.PI / 180);

    public static void main(String args[]) {

        System.out.println("Simple simulation of the Graphene KMC");

        Graphene_CVD_deposition_rates_factory ratesFactory = new Graphene_CVD_deposition_rates_factory();
        Graphene_KMC kmc = initialize_kmc();
        DifussionKmcFrame frame = create_graphics_frame(kmc);
        frame.setVisible(true);

        for (int i = 0; i < 10; i++) {
            initializeRates(ratesFactory, kmc);
            kmc.simulate();
        }

        float[][] surface = new float[256][256];
        kmc.getSampledSurface(surface);

    }

    private static DifussionKmcFrame create_graphics_frame(Graphene_KMC kmc) {
        DifussionKmcFrame frame = new DifussionKmcFrame(new GrapheneKmcCanvas((Abstract_2D_diffusion_lattice) kmc.getLattice()));
        return frame;
    }

    private static Graphene_KMC initialize_kmc() {

        List_configuration config = new List_configuration()
                .setList_type(List_configuration.LINEAR_LIST);

        int sizeX = 256;
        int sizeY = (int) (sizeX * (2 * cos30));
        if ((sizeY & 1) != 0) {
            sizeY++;
        }
        Graphene_KMC kmc = new Graphene_KMC(config, sizeX, sizeY, false);
        return kmc;
    }

    private static void initializeRates(Graphene_CVD_deposition_rates_factory reatesFactory, Graphene_KMC kmc) {

        double deposition_rate = reatesFactory.getDepositionRate("synthetic", 0);
        double island_density = reatesFactory.getIslandDensity("synthetic", 0);
        kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
        kmc.initializeRates(reatesFactory.getRates("synthetic", 0));

    }
}
