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
package utils.list.atoms;

import kineticMonteCarlo.lattice.AbstractSurfaceLattice;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.unitCell.AbstractSurfaceUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AtomsCollection {
  
  /**
   * The tree is constructed only once. Thus, changing from array to tree is much faster.
   */
  private final AvlTree tree;

  public AtomsCollection(AbstractSurfaceLattice lattice, String type) {
    tree = new AvlTree();
    for (int i = 0; i < lattice.size(); i++) {
      AbstractSurfaceUc uc = (AbstractSurfaceUc) lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        AbstractSurfaceSite a = uc.getSite(j);
        tree.insert(a);
      }
    }
    tree.createList(type);
  }
  
  public IAtomsCollection getCollection(boolean isTree, byte process) {
    IAtomsCollection atomsCollection;
    if (isTree) {
      atomsCollection = new AtomsAvlTree(process, tree);
    } else {
      atomsCollection = new AtomsArrayList(process);
    }
    return atomsCollection;
  }
}
