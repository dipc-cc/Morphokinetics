/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg;

import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.AbstractPerimeterStatistics;

/**
 *
 * @author Nestor, jalberdi004
 */
public class AgAgPerimeterStatistics extends AbstractPerimeterStatistics {

  public AgAgPerimeterStatistics(AgAgRawStatisticDataAtomCount1Million atom,
          AgAgRawStatisticDataHopsCount1Million hops) {
    super(atom, hops);
  }

}
