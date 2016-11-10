/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

/**
 * Common interface for Android/PC configurators.
 *
 * @author J. Alberdi-Rodriguez
 */
interface IConfigurator {

  void setContext(Object context);

  Object getContext();

  BufferedReader getBufferedReader(String fileName) throws FileNotFoundException;
}
