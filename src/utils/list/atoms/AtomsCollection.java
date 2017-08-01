/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AtomsCollection {
  
  IAtomsCollection atomsCollection;

  public AtomsCollection(boolean isTree, byte process) {
    if (isTree) {
      atomsCollection = new AtomsAvlTree(process);
    } else {
      atomsCollection = new AtomsArrayList(process);
    }
  }
  
  public IAtomsCollection getCollection() {
    return atomsCollection;
  }
}
