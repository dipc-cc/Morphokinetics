/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library;

/**
 *
 * @author Nestor
 */
public interface IRatesFactory {
    
    public double[] getRates(String experiment,double temperature);
    
    
}
