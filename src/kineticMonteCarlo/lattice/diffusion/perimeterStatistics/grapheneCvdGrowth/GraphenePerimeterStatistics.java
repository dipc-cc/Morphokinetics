/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics.grapheneCvdGrowth;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.AbstractPerimeterStatistics;

/**
 *
 * @author Nestor, jalberdi004
 */
public class GraphenePerimeterStatistics extends AbstractPerimeterStatistics {

  public GraphenePerimeterStatistics(GrapheneRawStatisticDataAtomCount1Million atom,
          GrapheneRawStatisticDataHopsCount1Million hops) {
    super(atom, hops);
  }

}
