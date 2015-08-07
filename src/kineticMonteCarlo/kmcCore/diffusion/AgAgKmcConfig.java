/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.kmcCore.diffusion;

import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public class AgAgKmcConfig {
    
    
    private int hexaSizeI;
    private int hexaSizeJ;
    private ListConfiguration listConfig;
    private double depositionRate;
    private double islandDensity;

    public AgAgKmcConfig(int hexaSizeI, int hexaSizeJ, ListConfiguration listConfig, double depositionRate, double islandDensity) {
        this.hexaSizeI = hexaSizeI;
        this.hexaSizeJ = hexaSizeJ;
        this.listConfig = listConfig;
        this.depositionRate = depositionRate;
        this.islandDensity = islandDensity;
    }

    public int getHexaSizeI() {
        return hexaSizeI;
    }

    public int getHexaSizeJ() {
        return hexaSizeJ;
    }

    public ListConfiguration getListConfig() {
        return listConfig;
    }

    public double getDepositionRate() {
        return depositionRate;
    }

    public double getIslandDensity() {
        return islandDensity;
    }
    
    
    
    
}
