package graphicInterfaces.basic;

import graphicInterfaces.KmcGraphics;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.kmcCore.etching.BasicKmc;

public class BasicFrame extends javax.swing.JFrame implements KmcGraphics {

  private int zoom;

  public BasicFrame(int zoom) {
    this.zoom = zoom;
    initComponents();
    setVisible(true);
  }

  @Override
  public void drawKmc(AbstractKmc kmc) {

    setSize(kmc.getLattice().getHexaSizeI() * zoom + 4, kmc.getLattice().getHexaSizeJ() * zoom + 16);
    ((BasicPanel) jPanel1).setKMC((BasicKmc) kmc);
    ((BasicPanel) jPanel1).repaint();
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new graphicInterfaces.basic.BasicPanel(zoom);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Basic KMC surface viewer");
    setResizable(false);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 641, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 376, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  // End of variables declaration//GEN-END:variables
}
