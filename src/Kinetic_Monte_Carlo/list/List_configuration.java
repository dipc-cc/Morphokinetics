/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.list;

/**
 *
 * @author Nestor
 */
public class List_configuration {
  
    
 public static final int LINEAR_LIST=0;           
 public static final int BINNED_LIST=1;
 
 public int bin_levels;
 public int bins_per_level;
 public int list_type;

    public List_configuration set_extra_levels(int bin_levels) {
        
        this.bin_levels = bin_levels;
        return this;
    }

    public List_configuration setList_type(int list_type) {
        
        this.list_type = list_type;
        return this;
    }

    public List_configuration setBins_per_level(int bins_per_level) {
        
        this.bins_per_level = bins_per_level;
        return this;
    }

    public Abstract_list create_list(){

        Abstract_list list=null;
        if (list_type==LINEAR_LIST) list=new Linear_list();
        if (list_type==BINNED_LIST) list=new Binned_list(bins_per_level,bin_levels);

        return list;
    }    
       
}
