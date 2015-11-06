package graphicInterfaces.surfaceViewer2D;

import java.awt.BorderLayout;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import utils.MathUtils;

public class SurfaceViewerPanel2D extends JPanel {

  private JCheckBox jCheckBox1;
  private JCheckBox jCheckBox2;
  private JCheckBox jCheckBox3;
  private JComboBox jComboBox1;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JTextField jTextField1;
  private JTextField jTextField2;
  private int decimals;
  
  public SurfaceViewerPanel2D(String textInfo) {
    initComponents();
    ((Panel2D) jPanel1).setTextInfo(textInfo);
    decimals = 3;
  }

  public SurfaceViewerPanel2D setMesh(float[][] mesh) {
    ((Panel2D) jPanel1).setPSD(mesh);
    jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), decimals) + "");
    jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), decimals) + "");
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
  private void initComponents() {

    jPanel1 = new Panel2D();
    jPanel2 = new JPanel();
    jPanel3 = new JPanel();
    jLabel1 = new JLabel();
    jTextField1 = new JTextField();
    jLabel2 = new JLabel();
    jTextField2 = new JTextField();
    jCheckBox1 = new JCheckBox();
    jComboBox1 = new JComboBox();
    jPanel4 = new JPanel();
    jCheckBox2 = new JCheckBox();
    jCheckBox3 = new JCheckBox();

    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setPreferredSize(new java.awt.Dimension(256, 256));

    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGap(0, 508, Short.MAX_VALUE)
    );

    jPanel3.setBorder(BorderFactory.createTitledBorder(""));

    jLabel1.setText("Min");

    jTextField1.setText("jTextField1");
    jTextField1.setEnabled(false);
    jTextField1.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);
      }
    });

    jLabel2.setText("Max");

    jTextField2.setText("jTextField2");
    jTextField2.setEnabled(false);
    jTextField2.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField2ActionPerformed(evt);
      }
    });

    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Auto");
    jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox1ActionPerformed(evt);
      }
    });

    jComboBox1.setModel(new DefaultComboBoxModel(new String[] { "HSV", "B/W" }));
    jComboBox1.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jComboBox1ActionPerformed(evt);
      }
    });

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel2)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox1)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addComponent(jLabel1)
        .addComponent(jLabel2)
        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addComponent(jCheckBox1)
      )
    );

    jCheckBox2.setText("Shift PSD");
    jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox2ActionPerformed(evt);
      }
    });

    jCheckBox3.setText("Log scale");
    jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox3ActionPerformed(evt);
      }
    });

    GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox2)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jCheckBox3)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jCheckBox2)
          .addComponent(jCheckBox3))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setLayout(new BorderLayout());
    jPanel2.add(jPanel3, NORTH);
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		  .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, 10)
            .addGap(0, 14, Short.MAX_VALUE)))
	    )
		  );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		.addComponent(jPanel2, 10, GroupLayout.DEFAULT_SIZE, 25)
		)
		);

  }

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {
      ((Panel2D) jPanel1).setLogScale(jCheckBox3.isSelected());
      jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), decimals) + "");
      jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), decimals) + "");
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
      jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), decimals) + "");
      jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), decimals) + "");
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
      try {
        ((Panel2D) jPanel1).setMin(Double.parseDouble(jTextField1.getText()));
      } catch (Exception e) {
        jTextField1.setText(MathUtils.truncate(((Panel2D) jPanel1).getMin(), decimals) + "");
      }
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
      try {
        ((Panel2D) jPanel1).setMax(Double.parseDouble(jTextField2.getText()));
      } catch (Exception e) {
        jTextField2.setText(MathUtils.truncate(((Panel2D) jPanel1).getMax(), decimals) + "");
      }
    }

}
