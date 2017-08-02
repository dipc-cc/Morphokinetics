/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AtomsCollection {
  
  /**
   * The tree is constructed only once. Thus, changing from array to tree is much faster.
   */
  private final AvlTree tree;

  public AtomsCollection(CatalysisLattice lattice) {
    tree = new AvlTree();
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) { // it will be always 0
        CatalysisAtom a = (CatalysisAtom) uc.getAtom(j);
        tree.insert(a);
      }
    }
    tree.createList();
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
