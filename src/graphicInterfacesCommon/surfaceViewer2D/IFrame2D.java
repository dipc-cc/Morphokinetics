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
package graphicInterfacesCommon.surfaceViewer2D;

public interface IFrame2D {

  public IFrame2D setMesh(float[][] mesh);

  public void redrawPSD();

  public IFrame2D setLogScale(boolean log);

  public IFrame2D setShift(boolean shift);

  public IFrame2D showPSDControls(boolean enabled);

  public IFrame2D setColorMap(int colormap);

  public double getMax();

  public double getMin();

  public IFrame2D setMin(double min);

  public IFrame2D setMax(double max);

  /**
   * This method prints the current canvas to a file.
   *
   * @param i Simulation number
   */
  public void printToImage(int i);

  /**
   * This method prints the current canvas to a file.
   *
   * @param folder the folder to be written
   * @param i Simulation number
   */
  public void printToImage(String folder, int i);

  public void setVisible(boolean visible);

  public void toBack();
}
