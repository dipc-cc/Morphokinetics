/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.Parser;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AndroidConfigurator implements IConfigurator{

  @Override
  public void evolutionarySimulation(Parser parser) {
    throw new UnsupportedOperationException("Evolutionary mode is not yet supported in mobile devices."); 
  }

  @Override
  public void psdFromSurfaces(Parser parser) {
    throw new UnsupportedOperationException("PSD tool is not yet supported in mobile devices."); 
  }
  
}
