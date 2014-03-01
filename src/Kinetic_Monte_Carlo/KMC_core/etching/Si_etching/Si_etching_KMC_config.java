/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.etching.Si_etching;

import Kinetic_Monte_Carlo.list.List_configuration;

/**
 *
 * @author Nestor
 */
public class Si_etching_KMC_config {

   
 public int millerX;
 public int millerY;
 public int millerZ;
 
 public int sizeX_UC;
 public int sizeY_UC;
 public int sizeZ_UC;
 
 public List_configuration listConfig;


    
    public Si_etching_KMC_config setMillerX(int millerX) {
        this.millerX = millerX;
        return this;
    }

    public Si_etching_KMC_config setMillerY(int millerY) {
        this.millerY = millerY;
        return this;
    }

    public Si_etching_KMC_config setMillerZ(int millerZ) {
        this.millerZ = millerZ;
        return this;
    }

    public Si_etching_KMC_config setSizeX_UC(int sizeX_UC) {
        this.sizeX_UC = sizeX_UC;
        return this;
    }
    

    public Si_etching_KMC_config setSizeY_UC(int sizeY_UC) {
        this.sizeY_UC = sizeY_UC;
        return this;
    }

    public Si_etching_KMC_config setSizeZ_UC(int sizeZ_UC) {
        this.sizeZ_UC = sizeZ_UC;
        return this;
    }
 
        public Si_etching_KMC_config setListConfig(List_configuration listConfig) {
        this.listConfig = listConfig;
        return this;
    }
}
