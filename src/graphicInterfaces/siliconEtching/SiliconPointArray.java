/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicInterfaces.siliconEtching;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.media.j3d.*;


/**
 *
 * @author nesferjo
 */
public class SiliconPointArray extends PointArray {

    private static final int MAX_POINTS=500000;
    
    private ByteOrder order = ByteOrder.nativeOrder();  
    
    private FloatBuffer buffer=ByteBuffer.allocateDirect(4*MAX_POINTS).order(order).asFloatBuffer();
    
    private J3DBuffer floatBufferCoord=new  J3DBuffer(buffer);
    
    

    public SiliconPointArray(float[] surface){

        super(MAX_POINTS,GeometryArray.COORDINATES|  GeometryArray.BY_REFERENCE | GeometryArray.USE_NIO_BUFFER);

        this.setCapability(this.ALLOW_REF_DATA_WRITE);
        this.setCapability(this.ALLOW_COUNT_WRITE);
        this.setValidVertexCount(0);
        this.setCoordRefBuffer(floatBufferCoord);
        actualizadata(surface);
}
   
 public void  actualizadata(final float[] coords){

    this.updateData(new GeometryUpdater(){
        public void updateData(Geometry geometry){ 
            ((SiliconPointArray)geometry).updateBuffers(coords);}});
 }
  
 public void updateBuffers(float[] coords){ 
        int validVertex=Math.min(MAX_POINTS,coords.length/3);
        this.setValidVertexCount(validVertex);
        buffer.rewind();
        buffer.put(coords);   
    }
 

}