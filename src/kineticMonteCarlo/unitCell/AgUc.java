/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.unitCell;

import java.util.List;
import javafx.geometry.Point3D;
import kineticMonteCarlo.site.AgSite;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;

/**
 * Unit cell of Ag (Silver) lattice.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUc extends AbstractGrowthUc implements IUc {

  private final List<AgSite> atoms;

  public AgUc(int posI, int posJ, List<AgSite> atoms) {
    super(posI, posJ, null);
    this.atoms = atoms;
    setSizeX(1.0f);
    setSizeY(2.0f * AbstractGrowthLattice.Y_RATIO);
  }
  
  @Override
  public AgSite getSite(int pos) {
    return atoms.get(pos);
  }

  /**
   * Number of elements per unit cell.
   *
   * @return quantity of unit cells
   */
  @Override
  public int size() {
    return atoms.size();
  }

  @Override
  public Point3D getPos() {
    return new Point3D(getSizeX() * getPosI(), getSizeY() * getPosJ(), 0);
  }
}