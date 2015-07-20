/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public class SyntheticRates implements IDiffusionRates {

 private double[][] rates;
 private double depositionRateInML_second=0.000035;
 private double islandDensity_per_site=1/60000f;
    
    
    @Override
    public double getRate(int sourceType, int destinationType, double temperature) {
        if (rates==null){
            rates=new double[8][8];
            initializeRates();
        }
        return rates[sourceType][destinationType];
    }

    @Override
    public double getDepositionRate() {
        return depositionRateInML_second;
    }
    
 @Override
    public double getIslandsDensityML(double temperature) {
        return islandDensity_per_site;
    }
    
    private void initializeRates(){
        
 rates[0][0]=1e9;
 rates[0][1]=1e9;
 rates[0][2]=1e9;
 rates[0][3]=1e9;
 rates[0][4]=1e9;
 rates[0][5]=1e9;
 rates[0][6]=1e9;
 rates[0][7]=1e9;
 
 rates[1][0]=0;
 rates[1][1]=100;
 rates[1][2]=100;
 rates[1][3]=100;
 rates[1][4]=100;
 rates[1][5]=100;
 rates[1][6]=100;
 rates[1][7]=100;
 
 rates[2][0]=0;
 rates[2][1]=100;
 rates[2][2]=100;
 rates[2][3]=100;
 rates[2][4]=100;
 rates[2][5]=100;
 rates[2][6]=100;
 rates[2][7]=100;
 
 
 rates[3][0]=0;
 rates[3][1]=10;
 rates[3][2]=10;
 rates[3][3]=10;
 rates[3][4]=10;
 rates[3][5]=10;
 rates[3][6]=10;
 rates[3][7]=10;
 
 rates[4][0]=0;
 rates[4][1]=0.01;
 rates[4][2]=0.01;
 rates[4][3]=0.01;
 rates[4][4]=0.01;
 rates[4][5]=0.01;
 rates[4][6]=0.01;
 rates[4][7]=0.01;
 
 rates[5][0]=0;
 rates[5][1]=0.00001;
 rates[5][2]=0.00001;
 rates[5][3]=0.00001;
 rates[5][4]=0.00001;
 rates[5][5]=0.00001;
 rates[5][6]=0.00001;
 rates[5][7]=0.00001;
 
 rates[6][0]=0;
 rates[6][1]=0;
 rates[6][2]=0;
 rates[6][3]=0;
 rates[6][4]=0;
 rates[6][5]=0;
 rates[6][6]=0;
 rates[6][7]=0;

 rates[7][0]=0;
 rates[7][1]=0;
 rates[7][2]=0;
 rates[7][3]=0;
 rates[7][4]=0;
 rates[7][5]=0;
 rates[7][6]=0;
 rates[7][7]=0;
        
        
    }
    
    
}
