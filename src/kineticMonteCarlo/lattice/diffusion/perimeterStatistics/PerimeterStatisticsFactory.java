/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgPerimeterStatistics;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth.GraphenePerimeterStatistics;
import java.util.HashMap;
import java.util.Map;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgRawStatisticDataAtomCount1Million;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgRawStatisticDataHopsCount1Million;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth.GrapheneRawStatisticDataAtomCount1Million;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth.GrapheneRawStatisticDataHopsCount1Million;

/**
 *
 * @author Nestor
 */
public class PerimeterStatisticsFactory {

  private AbstractPerimeterStatistics perimeterStatistics;
  
  public PerimeterStatisticsFactory(String statisticsName) {

    switch (statisticsName) {
      case "Graphene_CVD_growth": {
        perimeterStatistics = new GraphenePerimeterStatistics(new GrapheneRawStatisticDataAtomCount1Million(),
                new GrapheneRawStatisticDataHopsCount1Million());
        break;
      }
      case "Ag_Ag_growth": {
        perimeterStatistics = new AgAgPerimeterStatistics(new AgAgRawStatisticDataAtomCount1Million(),
                new AgAgRawStatisticDataHopsCount1Million());
        break;
      }
      default: {
        perimeterStatistics = null;
        System.err.println("Trying to get statistics for "+statisticsName);
        throw new UnsupportedOperationException("This execution mode is not supported");
      }
    }
  }

  public AbstractPerimeterStatistics getStatistics() {
    return perimeterStatistics;
  }
}
