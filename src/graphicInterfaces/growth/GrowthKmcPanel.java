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

public class GrowthKmcPanel extends javax.swing.JPanel {

  private boolean noStartDragData = false;
  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas canvas1;

  /**
   * Creates new JPanel
   *
   * @param canvas1
   */
  public GrowthKmcPanel(KmcCanvas canvas1) {

    initComponents();
    this.canvas1 = canvas1;
    //this.canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());    
    System.out.println("peer? "+canvas1.getPeer());
    this.add(canvas1);
    System.out.println("peer? "+canvas1.getPeer());
    //this.canvas1.initialize();
    this.jSpinner2.setValue(((KmcCanvas) canvas1).getScale());
    //this.setResizable(true);
    this.setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);

    canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
    canvas1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });

    paintLoop p = new paintLoop();
    p.start();
  }
  
  public void initCanvas(){
    this.canvas1.initialize();
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

    jLabel2 = new javax.swing.JLabel();
    jSpinner2 = new javax.swing.JSpinner();

    jLabel2.setText("Scale");

    jSpinner2.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
    jSpinner2.setFocusCycleRoot(true);
    jSpinner2.setFocusable(false);
    jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        jSpinner2StateChanged(evt);
      }
    });

    this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });
    this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        jPanel1MouseWheelMoved(evt);
      }
    });
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(this);
    this.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addContainerGap())
    );

  }

  private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {
    canvas1.setScale((Integer) jSpinner2.getValue());
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    this.setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);
  }

  private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();        // TODO add your handling code here:
  }

  private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {
    mouseX = mouseY = startMouseX = startMouseY = 0;
  }

  private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {
    if (noStartDragData) {
      startMouseX = evt.getX();
      startMouseY = evt.getY();
      noStartDragData = false;
    } else {
      mouseX = evt.getX() - startMouseX;
      mouseY = evt.getY() - startMouseY;
    }

  }

  private void jPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
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
    this.setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);
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
  
  private javax.swing.JLabel jLabel2;
  private javax.swing.JSpinner jSpinner2;
  
}
