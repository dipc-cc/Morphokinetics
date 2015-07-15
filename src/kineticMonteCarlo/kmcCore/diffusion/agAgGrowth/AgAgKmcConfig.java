/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.kmcCore.diffusion.agAgGrowth;

import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public class AgAgKmcConfig {
    
    
    private int sizeX;
    private int sizeY;
    private ListConfiguration listConfig;
    private double deposition_rate;
    private double island_density;

    public AgAgKmcConfig(int sizeX, int sizeY, ListConfiguration listConfig, double deposition_rate, double island_density) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.listConfig = listConfig;
        this.deposition_rate = deposition_rate;
        this.island_density = island_density;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public ListConfiguration getListConfig() {
        return listConfig;
    }

    public double getDepositionRate() {
        return deposition_rate;
    }

    public double getIslandDensity() {
        return island_density;
    }
    
    
    
    
}
