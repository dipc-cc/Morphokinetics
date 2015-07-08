/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.kmcCore.diffusion.agAgGrowth;

import kineticMonteCarlo.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public class AgAgKmcConfig {
    
    
    private int Xsize;
    private int Ysize;
    private ListConfiguration listConfig;
    private double deposition_rate;
    private double island_density;

    public AgAgKmcConfig(int Xsize, int Ysize, ListConfiguration listConfig, double deposition_rate, double island_density) {
        this.Xsize = Xsize;
        this.Ysize = Ysize;
        this.listConfig = listConfig;
        this.deposition_rate = deposition_rate;
        this.island_density = island_density;
    }



    public int getXsize() {
        return Xsize;
    }

    public int getYsize() {
        return Ysize;
    }

    public ListConfiguration getListConfig() {
        return listConfig;
    }

    public double getDeposition_rate() {
        return deposition_rate;
    }

    public double getIsland_density() {
        return island_density;
    }
    
    
    
    
}
