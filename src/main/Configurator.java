/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configurator singleton class chooses between Android and PC executions. It is responsible of
 * those parts of the code that may differ.
 *
 * @author J. Alberdi-Rodriguez
 */
public class Configurator {

  private static Configurator configurator;
  private static IConfigurator concreteConfigurator;
  
  private Configurator() {
    String className;
    try {
      if (System.getProperty("java.vm.name").equals("Dalvik")) {
        className = "main.AndroidConfigurator";
      } else {
        className = "main.PcConfigurator";
      }
      Class<?> genericClass = Class.forName(className);
      concreteConfigurator = (IConfigurator) genericClass.getConstructors()[0].newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
      
  
  public static Configurator getConfigurator(){
    if (configurator == null){
      configurator = new Configurator();
    }
    return configurator;
  }
  
  public void setContext(Object context) {
    concreteConfigurator.setContext(context);
  }
  
  public Object getContext() {
    return concreteConfigurator.getContext();
  }

  public BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
      return concreteConfigurator.getBufferedReader(fileName);
  }
  
}
