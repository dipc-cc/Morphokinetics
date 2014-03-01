/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library.Graphene_CVD_Growth;

/**
 *
 * @author Nestor
 */
public interface IRates {
    
    
public double getRate(int i,int j);

public double getDepositionRate();

public double getIslandsDensity();

}
