/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library.basic;

import Rates_library.Si_etching.*;

/**
 *
 * @author Nestor
 * 
 * Etch rates datas obtained from Gosalvez Et al - Physical Review E 68 (2003) 031604
 * 
 */
public class Rates_case_F implements IRates {
 
      private  double E0=1.0;
      private  double E1=1.0;
      private  double E2=5.0e3;
      private  double E3=5.0e5;
      
      private  double P0=1.0;
      private  double P1=1.0;
      private  double P2=5.0e3;
      private  double P3=5.0e5;

      private  double[] prefactors=new double[4];
      private  double[] energies   =new double[4];
                    
public Rates_case_F(){

energies[0]=E0;  prefactors[0]=P0;
energies[1]=E1;  prefactors[1]=P1;
energies[2]=E2;  prefactors[2]=P2;
energies[3]=E3;  prefactors[3]=P3;

}   
      
public double getPrefactor(int i)     {return prefactors[i];}
public double getEnergy(int i)        {return energies[i];}
}
