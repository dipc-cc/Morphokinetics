/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.gaConvergence;

import geneticAlgorithm.Individual;
import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.border.BevelBorder.RAISED;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Nestor
 */
public class GaProgressPanel extends JPanel implements IgaProgressFrame {

  private ErrorPanel panel;
  private int totalIterations;
  
  // Interface objects:
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JProgressBar jProgressBar1;
  private JProgressBar jProgressBar2;
  private JProgressBar jProgressBar3;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JTable jTable1;
  private DecimalFormat formatter;
  
  public GaProgressPanel() {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
    }

    initComponents();

    panel = new ErrorPanel(Color.red);
    jScrollPane1.setViewportView(panel);
    totalIterations = 0;
    setVisible(true);
    
    formatter = new DecimalFormat("0.##E0");
  }

  /**
   * This method is called from within the constructor to initialise the form. 
   * 
   */ 
  private void initComponents() {

    jProgressBar1 = new JProgressBar();
    jProgressBar2 = new JProgressBar();
    jProgressBar3 = new JProgressBar();
    jLabel1 = new JLabel();
    jLabel2 = new JLabel();
    jLabel3 = new JLabel();
    jScrollPane1 = new JScrollPane();
    jScrollPane2 = new JScrollPane();
    jTable1 = new JTable();
    jLabel4 = new JLabel();

    jProgressBar1.setStringPainted(true);

    jProgressBar2.setStringPainted(true);

    jProgressBar3.setStringPainted(true);

    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setText("Total iterations");

    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setText("Current Iteration");

    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setText("Current Evaluator");

    jTable1.setModel(new DefaultTableModel(
      new Object [][] {
        {"Terrace", "E_d", "E_d", "E_d", "E_d", "E_d", "E_d", "E_d"},
        {"Corner ", null, "E_c", "E_c", "E_e", "Max", "E_e", "E_c"},
        {"Edge A ", null, null, "E_a", "E_f", "E_f", "E_f", "E_f"},
        {"Kink A ", null, null, null, null, null, null, null},
        {"Bulk   ", null, null, null, null, null, null, null},
        {"Edge B ", null, null, "E_f", "E_f", "E_f", "E_b", "E_f"},
        {"Kink B ", null, null, null, null, null, null, null}
      },
      new String [] {
        "", "Terrace", "Corner", "Edge A", "Kink A", "Bulk", "Edge B", "Kink B"
      }
    ) {
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false
      };

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    jTable1.setCellSelectionEnabled(true);
    jScrollPane2.setViewportView(jTable1);

    jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel4.setText("Genes of best individual");
    jLabel4.setBorder(BorderFactory.createBevelBorder(RAISED));

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(TRAILING)
          .addComponent(jLabel4, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane2, LEADING, DEFAULT_SIZE, 468, Short.MAX_VALUE)
          .addGroup(LEADING, layout.createSequentialGroup()
            .addComponent(jScrollPane1)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(LEADING)
              .addComponent(jProgressBar3, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
              .addGroup(TRAILING, layout.createParallelGroup(LEADING)
                .addComponent(jProgressBar1, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jLabel2))
              .addGroup(TRAILING, layout.createParallelGroup(LEADING)
                .addComponent(jLabel3)
                .addComponent(jProgressBar2, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(11, 11, 11)
        .addGroup(layout.createParallelGroup(TRAILING)
          .addComponent(jScrollPane1, PREFERRED_SIZE, 212, PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(RELATED)
            .addComponent(jProgressBar1, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
            .addPreferredGap(RELATED)
            .addComponent(jLabel2)
            .addPreferredGap(RELATED)
            .addComponent(jProgressBar2, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
            .addPreferredGap(RELATED)
            .addComponent(jLabel3)
            .addPreferredGap(RELATED)
            .addComponent(jProgressBar3, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)))
        .addPreferredGap(RELATED, DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabel4)
        .addPreferredGap(UNRELATED)
        .addComponent(jScrollPane2, PREFERRED_SIZE, 250, PREFERRED_SIZE)
        .addContainerGap())
    );
  }

  public void setProgress(float[] progress) {
    jProgressBar1.setValue((int) progress[0]);
    jProgressBar2.setValue((int) progress[1]);
    jProgressBar2.setString(formatPercent(progress[1]) + "" + "%");
    jProgressBar3.setValue((int) progress[2]);
    jProgressBar3.setString(formatPercent(progress[2]) + "%");
  }
  
  private String formatPercent(float number) {
    String out = String.valueOf(number);
    if (out.length() > 5) {
      out = out.substring(0, 5);
    }
    return out;
  }

  @Override
  public void addNewBestIndividual(Individual ind) {

    panel.addPoint(totalIterations, (float) ind.getTotalError());

    String value;
    for (int column = 1; column < 8; column++) {
      value = formatter.format((float) ind.getGene(0)); // E_d
      jTable1.setValueAt(value, 0, column);
    }
    value = formatter.format((float) ind.getGene(1)); // E_c
    jTable1.setValueAt(value, 1, 2);
    jTable1.setValueAt(value, 1, 3);
    jTable1.setValueAt(value, 1, 7);
    
    value = formatter.format((float) ind.getGene(2)); // E_e
    jTable1.setValueAt(value, 1, 4);
    jTable1.setValueAt(value, 1, 6);
    
    value = formatter.format((float) ind.getGene(3));
    jTable1.setValueAt(value, 2, 4);
    jTable1.setValueAt(value, 2, 5);
    jTable1.setValueAt(value, 2, 6);
    jTable1.setValueAt(value, 2, 7);
    jTable1.setValueAt(value, 5, 3);
    jTable1.setValueAt(value, 5, 4);
    jTable1.setValueAt(value, 5, 5);
    jTable1.setValueAt(value, 5, 7);
    
    value = formatter.format((float) ind.getGene(4));
    jTable1.setValueAt(value, 2, 3);
    value = formatter.format((float) ind.getGene(5));
    jTable1.setValueAt(value, 5, 6);
    totalIterations++;
  }

  @Override
  public void clear() {
    panel.clear();
    // jTable1. //TODO CLEAR JTABLE
    totalIterations = 0;
  }
}
