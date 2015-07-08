/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicInterfaces.siliconEtching.mouseBehaviors;





import com.sun.j3d.utils.behaviors.mouse.*;
import javax.vecmath.*;
import java.util .*;
import javax.media.j3d.*;
import java.awt.*;
import java.awt.event.*;




public class MouseRocking extends MouseBehavior {
    double x_angle, y_angle;
    double x_factor = .03;
    double y_factor = .03;

  private MouseBehaviorCallback callback = null;

  public MouseRocking(TransformGroup transformGroup) {
    super(transformGroup);
  }

 
  public MouseRocking() {
      super(0);
  }


  public MouseRocking(int flags) {
      super(flags);
   }

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
		    !((MouseEvent)event[i]).isAltDown() &&
                     ((MouseEvent)event[i]).isControlDown()){

                  x = ((MouseEvent)event[i]).getX();
                  y = ((MouseEvent)event[i]).getY();

                  dx = x - x_last;
                  dy = y - y_last;

		  if (!reset){
		    x_angle = dy * y_factor;
		    y_angle = dx * x_factor;


                       
                   transformX.rotZ(x_angle);
		   transformY.rotZ(y_angle);
                   

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


  public void transformChanged( Transform3D transform ) {
  }

  public void setupCallback( MouseBehaviorCallback callback ) {
      this.callback = callback;
  }
}
