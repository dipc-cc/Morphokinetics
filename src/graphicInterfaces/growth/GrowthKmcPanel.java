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
  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas canvas1;
  
  private JLabel jLabel2;
  private JSpinner jSpinner2;

  /**
   * Creates new JPanel
   *
   * @param canvas1
   */
  public GrowthKmcPanel(KmcCanvas canvas1) {

    initComponents();
    this.canvas1 = canvas1;
    //canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());    
    System.out.println("peer? "+canvas1.getPeer());
    add(canvas1);
    System.out.println("peer? "+canvas1.getPeer());
    //canvas1.initialize();
    jSpinner2.setValue(((KmcCanvas) canvas1).getScale());
    //setResizable(true);
    setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);

    canvas1.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      @Override
      public void mousePressed(MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
    canvas1.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });

    paintLoop p = new paintLoop();
    p.start();
  }
  
  public void initCanvas(){
    canvas1.initialise();
  }

  public void repaintKmc() {
    canvas1.performDraw();
  }

  public void printToImage(int i) {
    canvas1.performDrawToImage(i);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * 
   */
  private void initComponents() {

    jLabel2 = new JLabel();
    jSpinner2 = new JSpinner();

    jLabel2.setText("Scale");

    jSpinner2.setModel(new SpinnerNumberModel(1, 1, null, 1));
    jSpinner2.setFocusCycleRoot(true);
    jSpinner2.setFocusable(false);
    jSpinner2.addChangeListener(new ChangeListener() {
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
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(TRAILING, layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(BASELINE)
          .addComponent(jLabel2)
          .addComponent(jSpinner2, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
        .addPreferredGap(RELATED)
        .addContainerGap())
    );

  }

  private void jSpinner2StateChanged(ChangeEvent evt) {
    canvas1.setScale((Integer) jSpinner2.getValue());
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);
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
    int zoom = (Integer) jSpinner2.getValue();
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
    jSpinner2.setValue(zoom);
    canvas1.setScale(zoom);
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);
  }
 
  final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        repaintKmc();
        try {
          paintLoop.sleep(100);
          canvas1.setBaseLocation(mouseX, mouseY);
          noStartDragData = true;
          mouseX = 0;
          mouseY = 0;
        } catch (Exception e) {
        }
      }
    }
  }
}
