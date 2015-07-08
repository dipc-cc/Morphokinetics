/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public interface IRatesFactory {
    
    public double[] getRates(String experiment,double temperature);
    
    public double getDepositionRate(String experimentName, double temperature);
    
    public double getIslandDensity(String experimentName, double temperature); 
    
    
}
