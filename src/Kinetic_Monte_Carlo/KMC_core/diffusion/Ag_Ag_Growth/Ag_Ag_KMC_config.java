/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth;

import Kinetic_Monte_Carlo.list.List_configuration;

/**
 *
 * @author Nestor
 */
public class Ag_Ag_KMC_config {
    
    
    private int Xsize;
    private int Ysize;
    private List_configuration listConfig;
    private double deposition_rate;
    private double island_density;

    public Ag_Ag_KMC_config(int Xsize, int Ysize, List_configuration listConfig, double deposition_rate, double island_density) {
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

    public List_configuration getListConfig() {
        return listConfig;
    }

    public double getDeposition_rate() {
        return deposition_rate;
    }

    public double getIsland_density() {
        return island_density;
    }
    
    
    
    
}
