/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import eus.ehu.dipc.morphokinetics.R;
import java.io.FileNotFoundException;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AndroidConfigurator implements IConfigurator{

  private Context androidContext;

  @Override
  public void setContext(Object context) {
    androidContext = (Context) context;
  }

  @Override
  public Context getContext() {
    return androidContext;
  }

  /**
   * Returns proper reader for Android file.
   *
   * @param fileName complete file name. It comes with a folder and extension that here is removed,
   * it is also lowercased.
   * @return BufferedReader
   * @throws FileNotFoundException
   */
  @Override
  public BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
    int id = androidContext.getResources().getIdentifier(fileName.split("/")[2].split("[.]")[0].toLowerCase(), "raw", androidContext.getPackageName());
    InputStream inputStream = androidContext.getResources().openRawResource(id);
    InputStreamReader inputreader = new InputStreamReader(inputStream);
    return new BufferedReader(inputreader);
  }
}
