/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgPerimeterStatistics;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth.GraphenePerimeterStatistics;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class PerimeterStatisticsFactory {

  private static Map<String, AbstractPerimeterStatistics> perimeterStatistics;

  public PerimeterStatisticsFactory() {

    perimeterStatistics = new HashMap();
    perimeterStatistics.put("Graphene_CVD_growth", new GraphenePerimeterStatistics());
    perimeterStatistics.put("Ag_Ag_growth", new AgAgPerimeterStatistics());
  }

  public AbstractPerimeterStatistics getStatistics(String statisticsName) {

    return perimeterStatistics.get(statisticsName);
  }
}
