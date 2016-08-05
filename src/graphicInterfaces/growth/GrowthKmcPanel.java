/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on 31-ene-2012, 13:09:43
 */
package graphicInterfaces.growth;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GrowthKmcPanel extends JPanel {

  private boolean noStartDragData = false;
  private int mouseX;
  private int mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas canvas;
  
  private JLabel labelScale;
  private JSpinner spinnerScale;

  /**
   * Creates new JPanel
   *
   * @param canvas
   */
  public GrowthKmcPanel(KmcCanvas canvas) {

    initComponents();
    this.canvas = canvas;
    //canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());    
    System.out.println("peer? "+canvas.getPeer());
    add(canvas);
    System.out.println("peer? "+canvas.getPeer());
    //canvas1.initialize();
    spinnerScale.setValue(((KmcCanvas) canvas).getScale());
    //setResizable(true);
    setSize(canvas.getSizeX() + 25, canvas.getSizeY() + 50);

    canvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      @Override
      public void mousePressed(MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
    canvas.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });

    paintLoop p = new paintLoop();
    p.start();
  }
  
  public void initCanvas(){
    canvas.initialise();
  }

  public void repaintKmc() {
    canvas.performDraw();
  }

  public void printToImage(int i) {
    canvas.performDrawToImage(i);
  }

  /**
   * This method is called from within the constructor to initialise the form.
   * 
   */
  private void initComponents() {

    labelScale = new JLabel();
    spinnerScale = new JSpinner();

    labelScale.setText("Scale");

    spinnerScale.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spinnerScale.setFocusCycleRoot(true);
    spinnerScale.setFocusable(false);
    spinnerScale.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent evt) {
        jSpinner2StateChanged(evt);
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });
    addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent evt) {
        jPanel1MouseWheelMoved(evt);
      }
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
      @Override
      public void mouseReleased(MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }
    });

    GroupLayout jPanel1Layout = new GroupLayout(this);
    setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(labelScale)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(spinnerScale, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(TRAILING, layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(BASELINE)
          .addComponent(labelScale)
          .addComponent(spinnerScale, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
        .addPreferredGap(RELATED)
        .addContainerGap())
    );

  }

  private void jSpinner2StateChanged(ChangeEvent evt) {
    canvas.setScale((Integer) spinnerScale.getValue());
    canvas.setSize(canvas.getSizeX(), canvas.getSizeY());
    setSize(canvas.getSizeX() + 25, canvas.getSizeY() + 50);
  }

  private void jPanel1MousePressed(MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();        // TODO add your handling code here:
  }

  private void jPanel1MouseReleased(MouseEvent evt) {
    mouseX = mouseY = startMouseX = startMouseY = 0;
  }

  private void jPanel1MouseDragged(MouseEvent evt) {
    if (noStartDragData) {
      startMouseX = evt.getX();
      startMouseY = evt.getY();
      noStartDragData = false;
    } else {
      mouseX = evt.getX() - startMouseX;
      mouseY = evt.getY() - startMouseY;
    }

  }

  private void jPanel1MouseWheelMoved(MouseWheelEvent evt) {
    int zoom = (Integer) spinnerScale.getValue();
    if ((Integer) evt.getWheelRotation() == -1) {
      zoom *= 2;
    } else {
      zoom /= 2;
    }
    if (zoom <= 0) {
      zoom = 1;
    }
    if (zoom >= 32) {
      zoom = 32;
    }
    spinnerScale.setValue(zoom);
    canvas.setScale(zoom);
    canvas.setSize(canvas.getSizeX(), canvas.getSizeY());
    setSize(canvas.getSizeX() + 25, canvas.getSizeY() + 50);
  }
 
  final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        repaintKmc();
        try {
          paintLoop.sleep(100);
          canvas.setBaseLocation(mouseX, mouseY);
          noStartDragData = true;
          mouseX = 0;
          mouseY = 0;
        } catch (Exception e) {
        }
      }
    }
  }
}
