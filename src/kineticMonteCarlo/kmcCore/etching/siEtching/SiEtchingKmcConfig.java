/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching.siEtching;

import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public class SiEtchingKmcConfig {

  public int millerX;
  public int millerY;
  public int millerZ;

  public int sizeX_UC;
  public int sizeY_UC;
  public int sizeZ_UC;

  public ListConfiguration listConfig;

  public SiEtchingKmcConfig setMillerX(int millerX) {
    this.millerX = millerX;
    return this;
  }

  public SiEtchingKmcConfig setMillerY(int millerY) {
    this.millerY = millerY;
    return this;
  }

  public SiEtchingKmcConfig setMillerZ(int millerZ) {
    this.millerZ = millerZ;
    return this;
  }

  public SiEtchingKmcConfig setSizeX_UC(int sizeX_UC) {
    this.sizeX_UC = sizeX_UC;
    return this;
  }

  public SiEtchingKmcConfig setSizeY_UC(int sizeY_UC) {
    this.sizeY_UC = sizeY_UC;
    return this;
  }

  public SiEtchingKmcConfig setSizeZ_UC(int sizeZ_UC) {
    this.sizeZ_UC = sizeZ_UC;
    return this;
  }

  public SiEtchingKmcConfig setListConfig(ListConfiguration listConfig) {
    this.listConfig = listConfig;
    return this;
  }
}
