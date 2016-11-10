/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class PcConfigurator implements IConfigurator {

  public PcConfigurator() {
  }

  /**
   * It does not need any context.
   *
   * @param context nothing
   */
  @Override
  public void setContext(Object context) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * It does not need any context.
   *
   * @return nothing, it will fail if you try to use.
   */
  @Override
  public Object getContext() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Returns proper reader for PC file.
   * 
   * @param fileName
   * @return BufferedReader
   * @throws FileNotFoundException 
   */
  @Override
  public BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
    return new BufferedReader(new FileReader(fileName));
  }


}
