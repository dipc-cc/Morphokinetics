package graphicInterfaces.surfaceViewer2D;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import utils.MathUtils;

public class SurfaceViewerPanel2D extends javax.swing.JPanel {

  public static int COLOR_BW = 0;
  public static int COLOR_HSV = 1;

  public SurfaceViewerPanel2D(String textInfo) {
    initComponents();
    //this.setVisible(true);
    ((Panel2D) jPanel1).setTextInfo(textInfo);

  }

  public SurfaceViewerPanel2D setMesh(float[][] mesh) {
    ((Panel2D) jPanel1).setPSD(mesh);
    jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), 5) + "");
    jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), 5) + "");
    return this;
  }

  public void redrawPSD() {
    ((Panel2D) jPanel1).repaint();
  }

  public SurfaceViewerPanel2D setLogScale(boolean log) {
    jCheckBox3.setSelected(log);
    jCheckBox3ActionPerformed(null);
    return this;
  }

  public SurfaceViewerPanel2D setShift(boolean shift) {
    jCheckBox2.setSelected(shift);
    jCheckBox2ActionPerformed(null);
    return this;
  }

  public SurfaceViewerPanel2D showPSDControls(boolean enabled) {
    jPanel4.setVisible(enabled);
    return this;
  }

  public SurfaceViewerPanel2D setColorMap(int colormap) {
    if (colormap == Panel2D.COLOR_HSV) {
      jComboBox1.setSelectedIndex(0);
    }
    if (colormap == Panel2D.COLOR_BW) {
      jComboBox1.setSelectedIndex(1);
    }
    jComboBox1ActionPerformed(null);
    return this;
  }

  public double getMax() {
    return ((Panel2D) jPanel1).getMax();
  }

  public double getMin() {
    return ((Panel2D) jPanel1).getMin();
  }

  public SurfaceViewerPanel2D setMin(double min) {
    jTextField1.setText(min + "");
    jTextField1ActionPerformed(null);
    return this;
  }

  public SurfaceViewerPanel2D setMax(double max) {
    jTextField2.setText(max + "");
    jTextField2ActionPerformed(null);
    return this;
  }
  
  /**
   * This method prints the current canvas to a file
   *
   * @param i Simulation number
   */
  public void performDrawToImage(int i) {
    BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    paint(graphics);
    graphics.dispose();
    try {
      String imageName = "results/psd" + i + ".png";
      System.out.println("Exporting image: " + imageName);
      FileOutputStream out = new FileOutputStream(imageName);
      ImageIO.write(image, "png", out);
      out.close();    
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
   * This method is called from within the constructor to initialize the form.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new graphicInterfaces.surfaceViewer2D.Panel2D();
    jPanel2 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jTextField1 = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jTextField2 = new javax.swing.JTextField();
    jCheckBox1 = new javax.swing.JCheckBox();
    jComboBox1 = new javax.swing.JComboBox();
    jPanel4 = new javax.swing.JPanel();
    jCheckBox2 = new javax.swing.JCheckBox();
    jCheckBox3 = new javax.swing.JCheckBox();

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel1.setPreferredSize(new java.awt.Dimension(256, 256));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 508, Short.MAX_VALUE)
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Colormap"));

    jLabel1.setText("Min");

    jTextField1.setText("jTextField1");
    jTextField1.setEnabled(false);
    jTextField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);
      }
    });

    jLabel2.setText("Max");

    jTextField2.setText("jTextField2");
    jTextField2.setEnabled(false);
    jTextField2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField2ActionPerformed(evt);
      }
    });

    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Auto");
    jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox1ActionPerformed(evt);
      }
    });

    jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HSV", "B/W" }));
    jComboBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jComboBox1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox1)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jLabel1)
        .addComponent(jLabel2)
        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jCheckBox1)
        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jCheckBox2.setText("Shift PSD");
    jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox2ActionPerformed(evt);
      }
    });

    jCheckBox3.setText("Log scale");
    jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox3ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jCheckBox3)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jCheckBox2)
          .addComponent(jCheckBox3))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
      .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		  /*.addGroup(layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 14, Short.MAX_VALUE)))
	    .addContainerGap())*/
		  )));
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
		//.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//.addContainerGap())
		));

  }

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {
      ((Panel2D) jPanel1).setLogScale(jCheckBox3.isSelected());
      jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), 5) + "");
      jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), 5) + "");
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
      if (jComboBox1.getSelectedIndex() == 0) {
        ((Panel2D) jPanel1).setColormap(Panel2D.COLOR_HSV);
      }
      if (jComboBox1.getSelectedIndex() == 1) {
        ((Panel2D) jPanel1).setColormap(Panel2D.COLOR_BW);
      }
    }

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {
      ((Panel2D) jPanel1).setShift(jCheckBox2.isSelected());
    }

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {
      jTextField1.setEnabled(!jCheckBox1.isSelected());
      jTextField2.setEnabled(!jCheckBox1.isSelected());

      ((Panel2D) jPanel1).setAuto(jCheckBox1.isSelected());
      jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), 5) + "");
      jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), 5) + "");
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
      try {
        ((Panel2D) jPanel1).setMin(Double.parseDouble(jTextField1.getText()));
      } catch (Exception e) {
        jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), 5) + "");
      }
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
      try {
        ((Panel2D) jPanel1).setMax(Double.parseDouble(jTextField2.getText()));
      } catch (Exception e) {
        jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), 5) + "");
      }
    }

  private javax.swing.JCheckBox jCheckBox1;
  private javax.swing.JCheckBox jCheckBox2;
  private javax.swing.JCheckBox jCheckBox3;
  private javax.swing.JComboBox jComboBox1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField2;
}
