/* 
 * Copyright (C) 2018 N. Ferrando
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
package graphicInterfaces.etching;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

/**
 *
 * @author N. Ferrando
 */

/**
 * Esto es un simple canvas3D: elemento donde se renderiza el universo creado con Java3D Tiene
 * operaciones de post-Render para dibujar el GUI por encima del entorno 3D.
 */
public class Canvas3DOverlay extends Canvas3D {

  private String overlayText = "Silicon etching KMC viewer 1.0";

  public void setOverlayText(String p) {
    overlayText = p;
    postRender();
  }

  public Canvas3DOverlay(java.awt.GraphicsConfiguration graphicsConfiguration) {
    super(graphicsConfiguration);
  }

  @Override
  public void postRender() {
    super.postRender();

    J3DGraphics2D g2d = getGraphics2D();
    try {
      postRenderActions(g2d);
    } catch (Exception e) {
    }
    g2d.flush(true);

  }

  private void postRenderActions(Graphics2D g2d) {

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    g2d.drawString(overlayText, 5, 15);

  }

}
