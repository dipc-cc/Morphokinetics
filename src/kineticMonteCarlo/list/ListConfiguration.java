/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.list;

/**
 *
 * @author Nestor
 */
public class ListConfiguration {
  
    
 public static final int LINEAR_LIST=0;           
 public static final int BINNED_LIST=1;
 
 public int bin_levels;
 public int bins_per_level;
 public int list_type;

    public ListConfiguration set_extra_levels(int bin_levels) {
        
        this.bin_levels = bin_levels;
        return this;
    }

    public ListConfiguration setList_type(int list_type) {
        
        this.list_type = list_type;
        return this;
    }

    public ListConfiguration setBins_per_level(int bins_per_level) {
        
        this.bins_per_level = bins_per_level;
        return this;
    }

    public AbstractList create_list(){

        AbstractList list=null;
        if (list_type==LINEAR_LIST) list=new LinearList();
        if (list_type==BINNED_LIST) list=new BinnedList(bins_per_level,bin_levels);

        return list;
    }    
       
}
