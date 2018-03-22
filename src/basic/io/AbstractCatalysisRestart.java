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
package basic.io;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractCatalysisRestart extends Restart {
  
  private String folder;
  private final boolean output;
  
  public AbstractCatalysisRestart(boolean catalysisOutput, String restartFolder){
    if (catalysisOutput) {
      folder = restartFolder;
      if (!folder.endsWith("/")) {
        folder += "/";
      }
      createFolder(restartFolder);
    }
    output = catalysisOutput;
  }
  
  abstract public void resetCatalysis();
  abstract public void flushCatalysis();
  abstract void initCatalysis(int simulationNumber);

  public String getFolder() {
    return folder;
  }

  public boolean isOutput() {
    return output;
  }
}
