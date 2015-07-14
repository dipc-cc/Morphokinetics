/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgPerimeterStatistics;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth.Graphene_perimeter_statistics;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class PerimeterStatisticsFactory {

  private static Map<String, AbstractPerimeterStatistics> perimeter_statistics;

  public PerimeterStatisticsFactory() {

    perimeter_statistics = new HashMap();
    perimeter_statistics.put("Graphene_CVD_growth", new Graphene_perimeter_statistics());
    perimeter_statistics.put("Ag_Ag_growth", new AgAgPerimeterStatistics());
  }

  public AbstractPerimeterStatistics getStatistics(String statisticsName) {

    return perimeter_statistics.get(statisticsName);
  }
}
