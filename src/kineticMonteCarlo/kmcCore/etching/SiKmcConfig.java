/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching;

import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public class SiKmcConfig {

  public int millerX;
  public int millerY;
  public int millerZ;

  public int sizeX_UC;
  public int sizeY_UC;
  public int sizeZ_UC;

  public ListConfiguration listConfig;

  public SiKmcConfig setMillerX(int millerX) {
    this.millerX = millerX;
    return this;
  }

  public SiKmcConfig setMillerY(int millerY) {
    this.millerY = millerY;
    return this;
  }

  public SiKmcConfig setMillerZ(int millerZ) {
    this.millerZ = millerZ;
    return this;
  }

  public SiKmcConfig setSizeX_UC(int sizeX_UC) {
    this.sizeX_UC = sizeX_UC;
    return this;
  }

  public SiKmcConfig setSizeY_UC(int sizeY_UC) {
    this.sizeY_UC = sizeY_UC;
    return this;
  }

  public SiKmcConfig setSizeZ_UC(int sizeZ_UC) {
    this.sizeZ_UC = sizeZ_UC;
    return this;
  }

  public SiKmcConfig setListConfig(ListConfiguration listConfig) {
    this.listConfig = listConfig;
    return this;
  }
}
