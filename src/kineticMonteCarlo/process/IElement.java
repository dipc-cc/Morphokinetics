/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.process;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public interface IElement {
  
  public double getRate(byte process);
  
  public void setRate(byte process, double rate);
  
  public void setOnList(byte process, boolean onList);
  
  public double getSumRate(byte process);
  
  public void addToSumRate(byte process, double rate);
  
  public void setSumRate(byte process, double rate);
  
  public void equalRate(byte process);
  
  public void clear();
}
