/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package android.fakeGraphicInterfaces.surfaceViewer2D;

import graphicInterfacesCommon.surfaceViewer2D.IFrame2D;

/**
 * Created by jalberdi004 on 10/3/16.
 */

public class Frame2D implements IFrame2D{
  @Override
  public IFrame2D setMesh(float[][] mesh) {
    return null;
  }

  @Override
  public void redrawPSD() {

  }

  @Override
  public IFrame2D setLogScale(boolean log) {
    return null;
  }

  @Override
  public IFrame2D setShift(boolean shift) {
    return null;
  }

  @Override
  public IFrame2D showPSDControls(boolean enabled) {
    return null;
  }

  @Override
  public IFrame2D setColorMap(int colormap) {
    return null;
  }

  @Override
  public double getMax() {
    return 0;
  }

  @Override
  public double getMin() {
    return 0;
  }

  @Override
  public IFrame2D setMin(double min) {
    return null;
  }

  @Override
  public IFrame2D setMax(double max) {
    return null;
  }

  @Override
  public void printToImage(int i) {

  }

  @Override
  public void printToImage(String folder, int i) {

  }

  @Override
  public void setVisible(boolean visible) {

  }

  @Override
  public void toBack() {

  }
}
