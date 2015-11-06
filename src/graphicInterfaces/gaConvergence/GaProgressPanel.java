/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.gaConvergence;

import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Nestor
 */
public class GaProgressPanel extends javax.swing.JPanel implements IgaProgressFrame {

  /**
   * Creates new form NewJFrame
   */
  private IGeneticAlgorithm geneticAlgorithm;
  private ErrorPanel panel;
  private int totalIterations;
  
  // Interface objects:
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JProgressBar jProgressBar1;
  private javax.swing.JProgressBar jProgressBar2;
  private javax.swing.JProgressBar jProgressBar3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTable jTable1;

  public GaProgressPanel() {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
    }

    initComponents();

    this.panel = new ErrorPanel(Color.red);
    this.jScrollPane1.setViewportView(panel);
    this.totalIterations = 0;
    this.setVisible(true);
  }

  /**
   * This method is called from within the constructor to initialise the form. 
   * 
   */ 
  private void initComponents() {

    jProgressBar1 = new javax.swing.JProgressBar();
    jProgressBar2 = new javax.swing.JProgressBar();
    jProgressBar3 = new javax.swing.JProgressBar();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jScrollPane2 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    jLabel4 = new javax.swing.JLabel();

    jProgressBar1.setStringPainted(true);

    jProgressBar2.setStringPainted(true);

    jProgressBar3.setStringPainted(true);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("Total iterations");

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Current Iteration");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Current Evaluator");

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null},
        {null, null, null, null, ""},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null},
        {null, null, null, null, null}
      },
      new String [] {
        "minus 4", "minus 3", "minus 2", "minus 1", "Last iteration"
      }
    ) {
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false
      };

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    jTable1.setCellSelectionEnabled(true);
    jScrollPane2.setViewportView(jTable1);

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel4.setText("Genes of best individual");
    jLabel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addComponent(jScrollPane1)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jLabel2))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel3)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(11, 11, 11)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
  }

  public void setProgress(float[] progress) {
    jProgressBar1.setValue((int) progress[0]);
    //jProgressBar1.setString(geneticAlgorithm.getCurrentIteration() + "/" + geneticAlgorithm.getTotalIterations());
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

    panel.addPoint(this.totalIterations, (float) ind.getTotalError());
    checkTableSize(ind);

    for (int column = 1; column < 5; column++) {
      for (int row = 0; row < ind.getGeneSize(); row++) {
        jTable1.setValueAt(jTable1.getValueAt(row, column), row, column - 1);
      }
    }
    for (int gene = 0; gene < ind.getGeneSize(); gene++) {
      jTable1.setValueAt((float) ind.getGene(gene), gene, 4);
    }
    this.totalIterations++;
  }

  @Override
  public void clear() {
    panel.clear();
    // jTable1. //TODO CLEAR JTABLE
    this.totalIterations = 0;
  }

  private void checkTableSize(Individual i) {

    if (jTable1.getModel().getRowCount() != i.getGeneSize()) {
      jTable1.setModel(createTableModel(i.getGeneSize(), 5));
    }

  }

  private DefaultTableModel createTableModel(int rows, int columns) {

    return new javax.swing.table.DefaultTableModel(
            new Object[rows][columns],
            new String[]{
              "minus 4", "minus 3", "minus 2", "minus 1", "Last iteration"
            }) {
              boolean[] canEdit = new boolean[]{
                false, false, false, false, false
              };

              public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
              }
            };
  }
  
}
