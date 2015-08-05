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
    
    
    private int axonSizeI;
    private int axonSizeJ;
    private ListConfiguration listConfig;
    private double depositionRate;
    private double islandDensity;

    public AgAgKmcConfig(int axonSizeI, int axonSizeJ, ListConfiguration listConfig, double depositionRate, double islandDensity) {
        this.axonSizeI = axonSizeI;
        this.axonSizeJ = axonSizeJ;
        this.listConfig = listConfig;
        this.depositionRate = depositionRate;
        this.islandDensity = islandDensity;
    }

    public int getAxonSizeI() {
        return axonSizeI;
    }

    public int getAxonSizeJ() {
        return axonSizeJ;
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
