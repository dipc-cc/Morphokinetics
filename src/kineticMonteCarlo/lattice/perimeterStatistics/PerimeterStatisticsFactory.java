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
        perimeterStatistics = new PerimeterStatistics(new ReentrancesPerAngleGraphene1million(),
                new HopsPerAngleGraphene1million());
        break;
      }      
      case "grapheneNew": {
        ReentrancesPerAngleGraphene10million atoms = new ReentrancesPerAngleGraphene10million();
        HopsPerAngleGraphene10million hops =  new HopsPerAngleGraphene10million();
        perimeterStatistics = new PerimeterStatistics(atoms, hops);
        break;
      }
      case "Ag": {
        perimeterStatistics = new PerimeterStatistics(new ReentrancesPerAngleAg1million(),
                new HopsPerAngle1million());
        break;
      }
      case "AgNew": {
        ReentrancesPerAngleAg10million atoms = new ReentrancesPerAngleAg10million();
        HopsPerAngleAg10million hops =  new HopsPerAngleAg10million();
        perimeterStatistics = new PerimeterStatistics(atoms, hops);
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
