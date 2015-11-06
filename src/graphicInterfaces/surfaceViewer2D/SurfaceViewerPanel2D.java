package graphicInterfaces.surfaceViewer2D;

import java.awt.BorderLayout;
import static java.awt.BorderLayout.NORTH;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import utils.MathUtils;

public class SurfaceViewerPanel2D extends JPanel {

  private JCheckBox autoCheckBox;
  private JCheckBox shiftCheckBox;
  private JCheckBox logCheckBox;
  private JComboBox jComboBox1;
  private JLabel minLabel;
  private JLabel maxLabel;
  private JPanel mainPanel;
  private JPanel lowPanel;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JTextField minTextField;
  private JTextField maxTextField;
  private final int decimals;
  private String title;
  
  public SurfaceViewerPanel2D(String textInfo) {
    initComponents();
    ((Panel2D) mainPanel).setTextInfo(textInfo);
    decimals = 3;
    title = textInfo;
  }

  public SurfaceViewerPanel2D setMesh(float[][] mesh) {
    ((Panel2D) mainPanel).setPSD(mesh);
    minTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMin(), decimals) + "");
    maxTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMax(), decimals) + "");
    return this;
  }

  public void redrawPSD() {
    ((Panel2D) mainPanel).repaint();
  }

  public SurfaceViewerPanel2D setLogScale(boolean log) {
    logCheckBox.setSelected(log);
    jCheckBox3ActionPerformed();
    return this;
  }

  public SurfaceViewerPanel2D setShift(boolean shift) {
    shiftCheckBox.setSelected(shift);
    jCheckBox2ActionPerformed();
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
    jComboBox1ActionPerformed();
    return this;
  }

  public double getMax() {
    return ((Panel2D) mainPanel).getMax();
  }

  public double getMin() {
    return ((Panel2D) mainPanel).getMin();
  }

  public SurfaceViewerPanel2D setMin(double min) {
    minTextField.setText(min + "");
    jTextField1ActionPerformed();
    return this;
  }

  public SurfaceViewerPanel2D setMax(double max) {
    maxTextField.setText(max + "");
    jTextField2ActionPerformed();
    return this;
  }
  
  public void setError(double error) {
    String errorString = format("%s (%.3f)",title,error);
    ((Panel2D) mainPanel).setTextInfo(errorString);
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
   * This method is called from within the constructor to initialise the form.
   */
  @SuppressWarnings("unchecked")
  private void initComponents() {

    mainPanel = new Panel2D();
    lowPanel = new JPanel();
    jPanel3 = new JPanel();
    minLabel = new JLabel();
    minTextField = new JTextField();
    maxLabel = new JLabel();
    maxTextField = new JTextField();
    autoCheckBox = new JCheckBox();
    jComboBox1 = new JComboBox();
    jPanel4 = new JPanel();
    shiftCheckBox = new JCheckBox();
    logCheckBox = new JCheckBox();

    mainPanel.setBorder(BorderFactory.createEtchedBorder());
    mainPanel.setPreferredSize(new java.awt.Dimension(256, 256));

    GroupLayout jPanel1Layout = new GroupLayout(mainPanel);
    mainPanel.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(LEADING)
      .addGap(0, 508, Short.MAX_VALUE)
    );

    jPanel3.setBorder(BorderFactory.createTitledBorder(""));

    minLabel.setText("Min");

    minTextField.setText("jTextField1");
    minTextField.setEnabled(false);
    minTextField.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed();
      }
    });

    maxLabel.setText("Max");

    maxTextField.setText("jTextField2");
    maxTextField.setEnabled(false);
    maxTextField.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField2ActionPerformed();
      }
    });

    autoCheckBox.setSelected(true);
    autoCheckBox.setText("Auto");
    autoCheckBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox1ActionPerformed();
      }
    });

    jComboBox1.setModel(new DefaultComboBoxModel(new String[] { "HSV", "B/W" }));
    jComboBox1.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jComboBox1ActionPerformed();
      }
    });

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(minLabel)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(minTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(maxLabel)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(maxTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(autoCheckBox)
        .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(LEADING)
      .addGroup(jPanel3Layout.createParallelGroup(BASELINE)
        .addComponent(minTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
        .addComponent(minLabel)
        .addComponent(maxLabel)
        .addComponent(maxTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
        .addComponent(autoCheckBox)
      )
    );

    shiftCheckBox.setText("Shift PSD");
    shiftCheckBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox2ActionPerformed();
      }
    });

    logCheckBox.setText("Log scale");
    logCheckBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox3ActionPerformed();
      }
    });

    GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(shiftCheckBox)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(logCheckBox)
        .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(BASELINE)
          .addComponent(shiftCheckBox)
          .addComponent(logCheckBox))
        .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
    );

    lowPanel.setLayout(new BorderLayout());
    lowPanel.add(jPanel3, NORTH);
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(LEADING)
          .addComponent(mainPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
		  .addGroup(layout.createSequentialGroup()
            .addComponent(lowPanel, PREFERRED_SIZE, DEFAULT_SIZE, 10)
            .addGap(0, 14, Short.MAX_VALUE)))
	    )
		  );
    layout.setVerticalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(mainPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		.addComponent(lowPanel, 10, DEFAULT_SIZE, 25)
		)
		);

  }

    private void jCheckBox3ActionPerformed() {
      ((Panel2D) mainPanel).setLogScale(logCheckBox.isSelected());
      minTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMin(), decimals) + "");
      maxTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMax(), decimals) + "");
    }

    private void jComboBox1ActionPerformed() {
      if (jComboBox1.getSelectedIndex() == 0) {
        ((Panel2D) mainPanel).setColormap(Panel2D.COLOR_HSV);
      }
      if (jComboBox1.getSelectedIndex() == 1) {
        ((Panel2D) mainPanel).setColormap(Panel2D.COLOR_BW);
      }
    }

    private void jCheckBox2ActionPerformed() {
      ((Panel2D) mainPanel).setShift(shiftCheckBox.isSelected());
    }

    private void jCheckBox1ActionPerformed() {
      minTextField.setEnabled(!autoCheckBox.isSelected());
      maxTextField.setEnabled(!autoCheckBox.isSelected());

      ((Panel2D) mainPanel).setAuto(autoCheckBox.isSelected());
      minTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMin(), decimals) + "");
      maxTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMax(), decimals) + "");
    }

    private void jTextField1ActionPerformed() {
      try {
        ((Panel2D) mainPanel).setMin(Double.parseDouble(minTextField.getText()));
      } catch (Exception e) {
        minTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMin(), decimals) + "");
      }
    }

    private void jTextField2ActionPerformed() {
      try {
        ((Panel2D) mainPanel).setMax(Double.parseDouble(maxTextField.getText()));
      } catch (Exception e) {
        maxTextField.setText(MathUtils.truncate(((Panel2D) mainPanel).getMax(), decimals) + "");
      }
    }

}
