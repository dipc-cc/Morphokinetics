/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

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
