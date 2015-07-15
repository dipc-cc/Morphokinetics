package samples.silicon;

import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmc;
import ratesLibrary.siEtching.SiEtchRatesFactory;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import kineticMonteCarlo.list.ListConfiguration;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleSiliconKmcFftAnalisys {

    public static void main(String args[]) {

        System.out.println("Simple 2D FFT analisys of an etched silicon surface");
        
            SiEtchingKmcConfig config = configKMC();
        
        SiEtchingKmc KMC = new SiEtchingKmc(config);

        KMC.initializeRates(new SiEtchRatesFactory()
                .getRates("Gosalvez_PRE", 350));

        float[][] surface = new float[128][128];
        PsdSignature2D PSD = new PsdSignature2D(128, 128);

        KMC.simulate(5000);
        for (int i = 0; i < 100; i++) {
            KMC.simulate(5000);
            KMC.getSampledSurface(surface);
            PSD.addSurfaceSample(surface);
        }

        PSD.applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
        PSD.applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

         new Frame2D("PSD analysis")
                 .setMesh(MathUtils.avg_Filter(PSD.getPsd(),1));
    }

    private static SiEtchingKmcConfig configKMC() {
        ListConfiguration listConfig=  new ListConfiguration()
          .setListType(ListConfiguration.BINNED_LIST)
          .setBinsPerLevel(16)
          .setExtraLevels(0);
        SiEtchingKmcConfig config = new SiEtchingKmcConfig()
                                    .setMillerX(1)
                                    .setMillerY(1)
                                    .setMillerZ(0)
                                    .setSizeX_UC(64)
                                    .setSizeY_UC(64)
                                    .setSizeZ_UC(256)
                                    .setListConfig(listConfig);
        return config;
    }
}
