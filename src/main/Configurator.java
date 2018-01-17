/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
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
      
  
  public static Configurator getConfigurator() {
    if (configurator == null) {
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
