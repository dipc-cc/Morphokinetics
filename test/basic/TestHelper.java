/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class TestHelper {
    
  public static String getBaseDir() {
    String baseDir = Restart.getJarBaseDir();
    if (baseDir.endsWith("/build")) {
      return baseDir.substring(0, baseDir.length()-6);
    } 
    return baseDir;
  }
}
