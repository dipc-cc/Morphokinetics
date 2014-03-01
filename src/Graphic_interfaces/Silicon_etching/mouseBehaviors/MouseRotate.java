/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Graphic_interfaces.Silicon_etching.mouseBehaviors;

/**
 *
 * @author nestor
 */



import com.sun.j3d.utils.behaviors.mouse.*;
import javax.vecmath.*;
import java.util .*;
import javax.media.j3d.*;
import java.awt.*;
import java.awt.event.*;



public class MouseRotate extends MouseBehavior {
    double x_angle, y_angle;
    double x_factor = .03;
    double y_factor = .03;

  private MouseBehaviorCallback callback = null;


  public MouseRotate(TransformGroup transformGroup) {
    super(transformGroup);
  }

 
  public MouseRotate() {
      super(0);
  }

  public MouseRotate(int flags) {
      super(flags);
   }

      public Transform3D getcurrXform(){return currXform;}

  public void initialize() {
    super.initialize();
    x_angle = 0;
    y_angle = 0;
    if ((flags & INVERT_INPUT) == INVERT_INPUT) {
       invert = true;
       x_factor *= -1;
       y_factor *= -1;
    }
  }


  public double getXFactor() {
    return x_factor;
  }

  public double getYFactor() {
    return y_factor;
  }


  public void setFactor( double factor) {
    x_factor = y_factor = factor;

  }

        public void setX(){

                   Matrix4f pepe=new Matrix4f();
                currXform.get(pepe);
                pepe.m00=0;pepe.m01=-1;pepe.m02=0;
                pepe.m10=0;pepe.m11=0;pepe.m12=1;
                pepe.m20=-1;pepe.m21=0;pepe.m22=0;
                currXform.set(pepe);

                                    if (transformGroup!=null){
                                    transformGroup.setTransform(currXform);
                                    transformChanged( currXform );
                                    if (callback!=null)
                                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                        currXform );}}


  public void setZ(){

                   Matrix4f pepe=new Matrix4f();
                currXform.get(pepe);
                pepe.m00=1;pepe.m01=0;pepe.m02=0;
                pepe.m10=0;pepe.m11=1;pepe.m12=0;
                pepe.m20=0;pepe.m21=0;pepe.m22=1;
                currXform.set(pepe);

                                    if (transformGroup!=null){
                                    transformGroup.setTransform(currXform);
                                    transformChanged( currXform );
                                    if (callback!=null)
                                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                               currXform );}}



   public void setY(){

                   Matrix4f pepe=new Matrix4f();
                currXform.get(pepe);
                pepe.m00=-1;pepe.m01=0;pepe.m02=0;
                pepe.m10=0;pepe.m11=0;pepe.m12=1;
                pepe.m20=0;pepe.m21=1;pepe.m22=0;
                currXform.set(pepe);

                                    if (transformGroup!=null){
                                    transformGroup.setTransform(currXform);
                                    transformChanged( currXform );
                                    if (callback!=null)
                                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                               currXform );}
 }


      public void set111(){

                   Matrix4f pepe=new Matrix4f();
                currXform.get(pepe);
                pepe.m00=0.707f;pepe.m01=-0.707f;pepe.m02=0;
                pepe.m10=0.3f;pepe.m11=0.3f;pepe.m12=0.95f;
                pepe.m20=-0.707f;pepe.m21=-0.707f;pepe.m22=0.3f;
                currXform.set(pepe);

                                    if (transformGroup!=null){
                                    transformGroup.setTransform(currXform);
                                    transformChanged( currXform );
                                    if (callback!=null)
                                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                               currXform );}

  }

      public void setM4f(Matrix4f origen){


                currXform.set(origen);

                                    if (transformGroup!=null){
                                    transformGroup.setTransform(currXform);
                                    transformChanged( currXform );
                                    if (callback!=null)
                                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                               currXform );}

  }
  /**
   * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
   * respectively.
   **/

  public void setFactor( double xFactor, double yFactor) {
    x_factor = xFactor;
    y_factor = yFactor;
  }

  public void processStimulus (Enumeration criteria) {
      WakeupCriterion wakeup;
      AWTEvent[] event;
      int id;
      int dx, dy;

      while (criteria.hasMoreElements()) {
         wakeup = (WakeupCriterion) criteria.nextElement();
         if (wakeup instanceof WakeupOnAWTEvent) {
            event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
            for (int i=0; i<event.length; i++) {
	      processMouseEvent((MouseEvent) event[i]);

	      if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
		  ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){

		id = event[i].getID();
		if ((id == MouseEvent.MOUSE_DRAGGED) &&
		    !((MouseEvent)event[i]).isMetaDown() &&
		    !((MouseEvent)event[i]).isAltDown()  &&
                    !((MouseEvent)event[i]).isControlDown()
                    ){

                  x = ((MouseEvent)event[i]).getX();
                  y = ((MouseEvent)event[i]).getY();

                  dx = x - x_last;
                  dy = y - y_last;

		  if (!reset){
		    x_angle = dy * y_factor;
		    y_angle = dx * x_factor;

                   //if ( ((MouseEvent)event[i]).isControlDown()) {transformX.rotZ(x_angle);

                                                                // transformX.rotX(0);
                                                               //  transformX.rotY(0);
                                                                 //transformY.rotZ(x_angle);
                                                                 //transformY.rotX(0);
                                                            //     transformY.rotY(y_angle);
                  // System.out.println(x_angle);
                //   }
                    
                  //  else{
                       
                     // transformX.rotZ(x_angle);
		    transformX.rotX(x_angle);
		    transformY.rotY(y_angle);
                //    }


		    transformGroup.getTransform(currXform);

		    //Vector3d translation = new Vector3d();
		    //Matrix3f rotation = new Matrix3f();
		    Matrix4d mat = new Matrix4d();

		    // Remember old matrix
		    currXform.get(mat);

		    // Translate to origin
		    currXform.setTranslation(new Vector3d(0.0,0.0,0.0));
		    if (invert) {
			currXform.mul(currXform, transformX);
			currXform.mul(currXform, transformY);
		    } else {
			currXform.mul(transformX, currXform);
			currXform.mul(transformY, currXform);
		    }




		    // Set old translation back
		    Vector3d translation = new
		      Vector3d(mat.m03, mat.m13, mat.m23);
		    currXform.setTranslation(translation);

		    // Update xform
		    transformGroup.setTransform(currXform);

		    transformChanged( currXform );

                    if (callback!=null)
                        callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
                                               currXform );


                Matrix4f pepe=new Matrix4f();
                currXform.get(pepe);
                //System.out.println(pepe.m00+" "+pepe.m01+" "+pepe.m02+" "+pepe.m03);
                //System.out.println(pepe.m10+" "+pepe.m11+" "+pepe.m12+" "+pepe.m13);
                //System.out.println(pepe.m20+" "+pepe.m21+" "+pepe.m22+" "+pepe.m23);
                //System.out.println(pepe.m30+" "+pepe.m31+" "+pepe.m32+" "+pepe.m33);
                //System.out.println("----------------");

		  }
		  else {
		    reset = false;
		  }

                  x_last = x;
                  y_last = y;
               }
               else if (id == MouseEvent.MOUSE_PRESSED) {
                  x_last = ((MouseEvent)event[i]).getX();
                  y_last = ((MouseEvent)event[i]).getY();
               }
	      }
	    }
         }
      }

      wakeupOn (mouseCriterion);

   }

  /**
    * Users can overload this method  which is called every time
    * the Behavior updates the transform
    *
    * Default implementation does nothing
    */
  public void transformChanged( Transform3D transform ) {
  }

  /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
  public void setupCallback( MouseBehaviorCallback callback ) {
      this.callback = callback;
  }
}
