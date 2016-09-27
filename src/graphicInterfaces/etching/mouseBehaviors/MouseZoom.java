/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.etching.mouseBehaviors;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Matrix4f;

/**
 *
 * @author nestor
 */
public class MouseZoom extends MouseBehavior {

  double z_factor = .04;

  private MouseBehaviorCallback callback = null;
  private float scale = 1.0f;

  public Transform3D getcurrXform() {
    return currXform;
  }

  public float getScale() {
    return scale;
  }

  public float getTranslation() {
    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
                //System.out.println(">>>"+pepe.m23);

    return pepe.m23;
  }

  public void setScale(float translation_z, float scale) {

    this.scale = scale;

		//transformX.set(translation);
    //if (invert) {currXform.mul(currXform, transformX);}
    // else {currXform.mul(transformX, currXform);}
    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    pepe.m23 = translation_z;
    pepe.m03 = 0;
    pepe.m13 = 0;
    currXform.set(pepe);

    currXform.setScale(scale);
    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }
  }

  public void setX() {

    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    pepe.m00 = 0;
    pepe.m01 = -1;
    pepe.m02 = 0;
    pepe.m10 = 0;
    pepe.m11 = 0;
    pepe.m12 = 1;
    pepe.m20 = -1;
    pepe.m21 = 0;
    pepe.m22 = 0;
    currXform.set(pepe);

    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }
  }

  public void setZ() {

    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    pepe.m00 = 1;
    pepe.m01 = 0;
    pepe.m02 = 0;
    pepe.m10 = 0;
    pepe.m11 = 1;
    pepe.m12 = 0;
    pepe.m20 = 0;
    pepe.m21 = 0;
    pepe.m22 = 1;
    currXform.set(pepe);

    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }
  }

  public void setY() {

    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    pepe.m00 = -1;
    pepe.m01 = 0;
    pepe.m02 = 0;
    pepe.m10 = 0;
    pepe.m11 = 0;
    pepe.m12 = 1;
    pepe.m20 = 0;
    pepe.m21 = 1;
    pepe.m22 = 0;
    currXform.set(pepe);

    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }
  }

  public void set111() {

    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    pepe.m00 = 0.707f;
    pepe.m01 = -0.707f;
    pepe.m02 = 0;
    pepe.m10 = 0.3f;
    pepe.m11 = 0.3f;
    pepe.m12 = 0.95f;
    pepe.m20 = -0.707f;
    pepe.m21 = -0.707f;
    pepe.m22 = 0.3f;
    currXform.set(pepe);

    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }

  }

  public void setM4f(Matrix4f origen) {

    currXform.set(origen);

    if (transformGroup != null) {
      transformGroup.setTransform(currXform);
      transformChanged(currXform);
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }

  }

  /**
   * Creates a zoom behavior given the transform group.
   *
   * @param transformGroup The transformGroup to operate on.
   */
  public MouseZoom(TransformGroup transformGroup) {
    super(transformGroup);
  }

  /**
   * Creates a default mouse zoom behavior.
   *
   */
  public MouseZoom() {
    super(0);
  }

  /**
   * Creates a zoom behavior. Note that this behavior still needs a transform group to work on (use
   * setTransformGroup(tg)) and the transform group must add this behavior.
   *
   * @param flags
   */
  public MouseZoom(int flags) {
    super(flags);
  }

  public void initialize() {
    super.initialize();
    if ((flags & INVERT_INPUT) == INVERT_INPUT) {
      z_factor *= -1;
      invert = true;
    }
  }

  /**
   * Return the y-axis movement multipler.
   *
   */
  public double getFactor() {
    return z_factor;
  }

  /**
   * Set the y-axis movement multipler with factor.
   *
   */
  public void setFactor(double factor) {
    z_factor = factor;

  }

  public void processStimulus(Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] event;
    int id;
    int dx, dy;

    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent) {
        event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
        for (int i = 0; i < event.length; i++) {
          processMouseEvent((MouseEvent) event[i]);

          if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0))
                  || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0))) {

            id = event[i].getID();
            //System.out.println(((MouseEvent)event[i]).isMetaDown());

            if ((id == MouseEvent.MOUSE_DRAGGED)
                    && ((MouseEvent) event[i]).isMetaDown()
                    && !((MouseEvent) event[i]).isAltDown()) {

              x = ((MouseEvent) event[i]).getX();
              y = ((MouseEvent) event[i]).getY();

              dx = x - x_last;
              dy = y - y_last;

              if (!reset) {
                transformGroup.getTransform(currXform);

                //para la proyeccion con perspectiva
                /*
                 * translation.z  = dy*z_factor;
                 transformX.set(translation);  
                 if (invert) {
                 currXform.mul(currXform, transformX);
                 } else {
                 currXform.mul(transformX, currXform);
                 }
                 */
                 //System.out.println("Z "+scale);
                //Para la proyeccion paralela!!
                scale += (dy * z_factor * 0.1f * scale);
                if (scale < 0.000001f) {
                  scale = 0.000001f;
                }
                currXform.setScale(scale);
                //System.out.println(scale);
                transformGroup.setTransform(currXform);

                transformChanged(currXform);

                if (callback != null) {
                  callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                          currXform);
                }

              } else {
                reset = false;
              }

              x_last = x;
              y_last = y;
            } else if (id == MouseEvent.MOUSE_PRESSED) {
              x_last = ((MouseEvent) event[i]).getX();
              y_last = ((MouseEvent) event[i]).getY();
            }
          }
        }
      }
    }

    wakeupOn(mouseCriterion);
  }

  /**
   * Users can overload this method which is called every time the Behavior updates the transform
   *
   * Default implementation does nothing
   */
  public void transformChanged(Transform3D transform) {
  }

  /**
   * The transformChanged method in the callback class will be called every time the transform is
   * updated
   */
  public void setupCallback(MouseBehaviorCallback callback) {
    this.callback = callback;
  }
}
