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
