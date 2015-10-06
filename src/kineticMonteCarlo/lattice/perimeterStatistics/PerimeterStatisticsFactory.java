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
      case "graphene": {
        perimeterStatistics = new GeneralPerimeterStatistics(new GrapheneRawStatisticDataAtomCount1Million(),
                new GrapheneRawStatisticDataHopsCount1Million());
        break;
      }      
      case "grapheneNew": {
        ReentrancesPerAngleGraphene10million atoms = new ReentrancesPerAngleGraphene10million();
        HopsPerAngleGraphene10million hops =  new HopsPerAngleGraphene10million();
        perimeterStatistics = new GeneralPerimeterStatistics(atoms, hops);
        break;
      }
      case "Ag": {
        perimeterStatistics = new GeneralPerimeterStatistics(new AgAgRawStatisticDataAtomCount1Million(),
                new AgAgRawStatisticDataHopsCount1Million());
        break;
      }
      case "AgNew": {
        ReentrancesPerAngleAg10million atoms = new ReentrancesPerAngleAg10million();
        HopsPerAngleAg10million hops =  new HopsPerAngleAg10million();
        perimeterStatistics = new GeneralPerimeterStatistics(atoms, hops);
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
