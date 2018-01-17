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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.J3DBuffer;
import javax.media.j3d.PointArray;


/**
 *
 * @author N. Ferrando
 */
public class SiPointArray extends PointArray {

  private static final int MAX_POINTS = 500000;

  private ByteOrder order = ByteOrder.nativeOrder();

  private FloatBuffer buffer = ByteBuffer.allocateDirect(4 * MAX_POINTS).order(order).asFloatBuffer();

  private J3DBuffer floatBufferCoord = new J3DBuffer(buffer);

  public SiPointArray(float[] surface) {

    super(MAX_POINTS, GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE | GeometryArray.USE_NIO_BUFFER);

    setCapability(ALLOW_REF_DATA_WRITE);
    setCapability(ALLOW_COUNT_WRITE);
    setValidVertexCount(0);
    setCoordRefBuffer(floatBufferCoord);
    actualizadata(surface);
  }

  public void actualizadata(final float[] coords) {

    updateData(new GeometryUpdater() {
      public void updateData(Geometry geometry) {
        ((SiPointArray) geometry).updateBuffers(coords);
      }
    });
  }

  public void updateBuffers(float[] coords) {
    int validVertex = Math.min(MAX_POINTS, coords.length / 3);
    setValidVertexCount(validVertex);
    buffer.rewind();
    buffer.put(coords);
  }

}
