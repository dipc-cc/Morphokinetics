/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author nesferjo
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
