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

import javax.swing.JScrollPane;

public class GrowthKmcFrame extends javax.swing.JFrame {

  private boolean noStartDragData = false;
  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas canvas1;

  /**
   * Creates new form NewJFrame
   *
   * @param canvas1
   */
  public GrowthKmcFrame(KmcCanvas canvas1) {

    initComponents();
    this.canvas1 = canvas1;
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    jPanel1.add(canvas1);
    canvas1.initialise();
    jSpinner2.setValue(((KmcCanvas) canvas1).getScale());
    setResizable(true);
    setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);

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
  }

  public void repaintKmc() {
    canvas1.performDraw();
    canvas1.setBaseLocation(mouseX, mouseY);
    noStartDragData = true;
    mouseX = 0;
    mouseY = 0;
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/results
   * @param i simulation number
   */
  public void printToImage(int i) {
    canvas1.performDrawToImage(i);
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/folder/results
   * @param folder folder to save the current image
   * @param i simulation number
   */
  public void printToImage(String folder, int i) {
    canvas1.performDrawToImage(folder, i);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel2 = new javax.swing.JLabel();
    jSpinner2 = new javax.swing.JSpinner();
    jPanel1 = new javax.swing.JPanel();

    JScrollPane scrollPane = new javax.swing.JScrollPane(jPanel1);
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Morphoniketics");
    setResizable(false);

    jLabel2.setText("Scale");

    jSpinner2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
    jSpinner2.setFocusCycleRoot(true);
    jSpinner2.setFocusable(false);
    jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        jSpinner2StateChanged(evt);
      }
    });

    jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });
    jPanel1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        jPanel1MouseWheelMoved(evt);
      }
    });
    jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner2StateChanged

      canvas1.setScale((Integer) jSpinner2.getValue());
      canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
      setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);
    }//GEN-LAST:event_jSpinner2StateChanged

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
      startMouseX = evt.getX();
      startMouseY = evt.getY();        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
      mouseX = mouseY = startMouseX = startMouseY = 0;

    }//GEN-LAST:event_jPanel1MouseReleased

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
      if (noStartDragData) {
        startMouseX = evt.getX();
        startMouseY = evt.getY();
        noStartDragData = false;
      } else {
        mouseX = evt.getX() - startMouseX;
        mouseY = evt.getY() - startMouseY;
      }
      
    }//GEN-LAST:event_jPanel1MouseDragged

  private void jPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel1MouseWheelMoved

    int zoom = (Integer) jSpinner2.getValue();
    if ((Integer) evt.getWheelRotation() == -1) {
      zoom *=2;
    } else {
      zoom /=2;
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
    setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);
  }//GEN-LAST:event_jPanel1MouseWheelMoved

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JSpinner jSpinner2;
  // End of variables declaration//GEN-END:variables
}
