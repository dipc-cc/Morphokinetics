/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 *
 * @author Nestor
 */
@Deprecated
public class ReentrancesPerAngleAg1million extends AbstractStatistics {
  
    public ReentrancesPerAngleAg1million(){
      readAndSetStatistics("reentrancesPerAngleHexagonal1million.txt");
    }
    /**
     * This matrix is 22 x 181. Each row corresponds to a given radius. They are 22 different possible radius; from 20 to 125 (increasing 5 each time).  
     * Each column represents a value of a degree. The last column is the sum of all the columns.
     * This is shape of the data:
     *     
       1e+07 +------+-------+------+------+-------+------+------+-------+------+
             |                                                                 |
             |                                                                 |
       1e+06 *                                                                 *
             *                                                                 *
      100000 |*                                                                *
             |*                                                                *
             |*                                                                *
       10000 | *                                                               *
             |  **                                                             *
        1000 |   ***                                                           *
             |     *****                                                       *
             |         *******                                                *|
         100 |             *************      *                               *|
             |                     * ***************************** ************|
          10 |                               * * **   * * * *** ***** ** ***   |
             |                                                     *           |
             |                                                                 |
           1 +------+-------+------+------+-------+------+------+-------+------+
             0      20      40     60     80     100    120    140     160    180
                                            Angle

    */
}