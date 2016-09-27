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
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;

public class MouseTranslate extends MouseBehavior {

  private float scale = 1.0f;
  double x_factor = .00005;
  double y_factor = .00005;
  Vector3d translation = new Vector3d();
  private int lastButton = 0;
  private int projection = 1;
  private MouseBehaviorCallback callback = null;

  private boolean moved = false;

  public MouseTranslate(float scale) {
    super(0);
    this.scale = scale;

  }

  public Transform3D getcurrXform() {
    return currXform;
  }

  public float getTranslationX() {
    Matrix4f pepe = new Matrix4f();
    currXform.get(pepe);
    return pepe.m03;
  }

  public float getTranslationY() {
    Matrix4f M4f = new Matrix4f();
    currXform.get(M4f);
    return M4f.m13;
  }

  public boolean isMoved() {
    return moved;
  }

  public void set_projection(int projection) {

    this.projection = projection;

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
      if (callback != null) {
        callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                currXform);
      }
    }

  }

  public void processStimulus(Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] events;
    MouseEvent evt;

    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();

      if (wakeup instanceof WakeupOnAWTEvent) {
        events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
        if (events.length > 0) {
          evt = (MouseEvent) events[events.length - 1];
          if (evt.getButton() != 0) {
            lastButton = evt.getButton();
          }
          doProcess(evt);
        }
      } else if (wakeup instanceof WakeupOnBehaviorPost) {
        while (true) {
          // access to the queue must be synchronized
          synchronized (mouseq) {
            if (mouseq.isEmpty()) {
              break;
            }
            evt = (MouseEvent) mouseq.remove(0);
            // consolodate MOUSE_DRAG events
            while ((evt.getID() == MouseEvent.MOUSE_DRAGGED)
                    && !mouseq.isEmpty()
                    && (((MouseEvent) mouseq.get(0)).getID()
                    == MouseEvent.MOUSE_DRAGGED)) {
              evt = (MouseEvent) mouseq.remove(0);
            }
          }
          doProcess(evt);
        }
      }

    }
    wakeupOn(mouseCriterion);
  }

  public void doProcess(MouseEvent evt) {
    int id;
    int dx, dy;

    processMouseEvent(evt);
    if (lastButton != 2) {
      return;
    }
    if ((((buttonPress) && ((flags & MANUAL_WAKEUP) == 0)) || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0)))) {
      id = evt.getID();
      if (evt.getButton() == 0) {
        x = evt.getX();
        y = evt.getY();
        moved = true;
        dx = x - x_last;
        dy = y - y_last;

        if ((!reset) && ((Math.abs(dy) < 50) && (Math.abs(dx) < 50))) {
          //System.out.println("dx " + dx + " dy " + dy);
          transformGroup.getTransform(currXform);
          if (projection == 0) {
            translation.x = dx * x_factor * 49;//*MZ2.getScale();
            translation.y = -dy * y_factor * 49;//*MZ2.getScale();

          } else {
            translation.x = dx * x_factor * scale;//*MZ2.getScale();
            translation.y = -dy * y_factor * scale;//*MZ2.getScale();
          }
          transformX.set(translation);

          if (invert) {
            currXform.mul(currXform, transformX);
          } else {
            currXform.mul(transformX, currXform);
          }

          transformGroup.setTransform(currXform);

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
        x_last = evt.getX();
        y_last = evt.getY();
      }
    }
  }
}
