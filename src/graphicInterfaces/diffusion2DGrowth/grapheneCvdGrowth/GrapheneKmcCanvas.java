/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.diffusion2DGrowth.grapheneCvdGrowth;

import graphicInterfaces.diffusion2DGrowth.AbstractKmcCanvas;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;

public class GrapheneKmcCanvas extends AbstractKmcCanvas {

  public GrapheneKmcCanvas(Abstract2DDiffusionLattice lattice) {
    super(lattice);
  }
  
  @Override
  public int getSizeX() {
    return (int) (lattice.getCartSizeX() * scale);
  }
  
  
}
