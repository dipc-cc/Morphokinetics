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
package kineticMonteCarlo.activationEnergy;

import basic.Parser;
import java.util.Iterator;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.site.AbstractCatalysisSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractCatalysisActivationEnergy extends ActivationEnergy {
  
  public AbstractCatalysisActivationEnergy(Parser parser) {
    super(parser);
  }
  
  abstract public void updatePossibles(CatalysisLattice lattice, double elapsedTime, boolean stationary);
  abstract public void updatePossibles(Iterator<AbstractCatalysisSite> surface, double elapsedTime, boolean stationary);
}
