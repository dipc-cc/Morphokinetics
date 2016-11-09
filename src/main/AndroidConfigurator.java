/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import android.content.Context;

import basic.Parser;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AndroidConfigurator implements IConfigurator{

  private static AndroidConfigurator configurator;

  private Context androidContext;

  private AndroidConfigurator(Context context){
    androidContext = context;
  }

  public static AndroidConfigurator getConfigurator(Context context){
    if (configurator == null){
      configurator = new AndroidConfigurator(context);
    }
    return configurator;
  }

  public static AndroidConfigurator getConfigurator() {
    if (configurator == null) {
      System.err.println("You must instantiate configurator before trying to use it.");
    }
    return configurator;
  }
  
  @Override
  public Context getContext(){
    return androidContext;
  }

  @Override
  public void evolutionarySimulation(Parser parser) {
    throw new UnsupportedOperationException("Evolutionary mode is not yet supported in mobile devices."); 
  }

  @Override
  public void psdFromSurfaces(Parser parser) {
    throw new UnsupportedOperationException("PSD tool is not yet supported in mobile devices."); 
  }
  
}
