/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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

import kineticMonteCarlo.site.CatalysisSite;

/**
 * Really simple unit cell, which will contain only one atom.
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisUc extends AbstractSurfaceUc implements IUc {

  public CatalysisUc(int posI, int posJ, CatalysisSite atom) {
    super(posI, posJ, atom);
  }

  /**
   * Always returns the current atom
   *
   * @param pos ignored
   * @return current atom
   */
  @Override
  public CatalysisSite getSite(int pos) {
    return (CatalysisSite) super.getSite(pos);
  }
}
