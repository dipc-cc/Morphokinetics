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

import basic.io.Restart;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

public class GrowthKmcFrame extends JFrame {

  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private boolean paused;
  private KmcCanvas canvas1;
  private JButton pauseButton;
  private JToggleButton bwButton;
  private JButton pngSaveButton;
  private JLabel jLabelScale;
  private JPanel jPanel1;
  private JSpinner jSpinnerScale;
  private JLabel statusbar;
  private JCheckBoxMenuItem bwMi;
  private JProgressBar progressBar;
  private final int maxCoverage;

  private ImageIcon pauseIcon;
  private ImageIcon resumeIcon;
  /**
   * Creates new form frame for growth.
   *
   * @param canvas1
   * @param max maximum value for the progress bar
   */
  public GrowthKmcFrame(KmcCanvas canvas1, int max) {
    createMenuBar();
    maxCoverage = max;
    initComponents();
    this.canvas1 = canvas1;
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    jPanel1.add(canvas1);
    canvas1.initialise();
    jSpinnerScale.setValue(((KmcCanvas) canvas1).getScale());
    setSize(canvas1.getSizeX() + 70, canvas1.getSizeY() + 120);

    canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      @Override
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
    paused = false;
    jLabelScale.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pause");
    jLabelScale.getActionMap().put("pause", new Pause());
  }

  public void repaintKmc() {
    canvas1.performDraw();
    canvas1.setBaseLocation(mouseX, mouseY);
    mouseX = 0;
    mouseY = 0;
    
    validate();
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/results
   *
   * @param i simulation number
   */
  public void printToImage(int i) {
    canvas1.performDrawToImage(i);
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/folder/results
   *
   * @param folder folder to save the current image
   * @param i simulation number
   */
  public void printToImage(String folder, int i) {
    canvas1.performDrawToImage(folder, i);
  }
  
  /**
   * Updated the progress bar of the bottom of the frame.
   * 
   * @param coverage 
   */
  public void updateProgressBar(int coverage) {
    progressBar.setValue(coverage);
    statusbar.setText(Integer.toString(coverage) + "/" + maxCoverage);
  }

  /**
   * This method is called from within the constructor to initialise the form. 
   */ 
  private void initComponents() {
    jLabelScale = new JLabel();
    jSpinnerScale = new JSpinner();
    jPanel1 = new JPanel();
    pauseButton = new JButton();
    pauseIcon = new ImageIcon(Restart.getJarBaseDir() + "/resources/png/pause.png");
    resumeIcon = new ImageIcon(Restart.getJarBaseDir() + "/resources/png/resume.png");
    pauseButton.setIcon(pauseIcon);
    bwButton = new JToggleButton();
    bwButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/bw.png"));
    pngSaveButton = new JButton();
    pngSaveButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/save.png"));
    statusbar = new JLabel("Running");
    progressBar = new JProgressBar(0, maxCoverage);

    JScrollPane scrollPane = new JScrollPane(jPanel1);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle("Morphoniketics");
    setResizable(true);
    setMinimumSize(new Dimension(300, 110));

    jLabelScale.setText("Scale");

    jSpinnerScale.setModel(new SpinnerNumberModel(1, 1, null, 1));
    jSpinnerScale.setFocusCycleRoot(true);
    jSpinnerScale.setFocusable(false);
    jSpinnerScale.addChangeListener((ChangeEvent evt) -> {
      jSpinnerScaleStateChanged(evt);
    });

    jPanel1.addMouseWheelListener((MouseWheelEvent evt) -> {
      jPanel1MouseWheelMoved(evt);
    });
    
    pauseButton.addActionListener((ActionEvent evt) -> {
      pause();
    });
    bwButton.addActionListener((ActionEvent evt) -> {
      bwMi.setSelected(!bwMi.isSelected());
    });
    pngSaveButton.addActionListener((ActionEvent evt) -> {
      pngSave();
    });

    pauseButton.setToolTipText("Pauses the execution");
    bwButton.setToolTipText("Changes between mono colour and all colours");
    pngSaveButton.setToolTipText("Saves current view to PNG image");
    
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(statusbar)
                                    .addPreferredGap(RELATED)
                                    .addComponent(progressBar))
                            .addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabelScale)
                                    .addPreferredGap(RELATED)
                                    .addComponent(jSpinnerScale, PREFERRED_SIZE, 61, PREFERRED_SIZE)
                                    .addPreferredGap(RELATED)
                                    .addComponent(pauseButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(bwButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(pngSaveButton, PREFERRED_SIZE, 20, 20)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(TRAILING, layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(jLabelScale)
                            .addComponent(pauseButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(bwButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(pngSaveButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(jSpinnerScale, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                    .addPreferredGap(RELATED)
                    .addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(LEADING)
                              .addComponent(statusbar)
                    .addComponent(progressBar))
            ));

    pack();
  }

  private void jSpinnerScaleStateChanged(ChangeEvent evt) {
    canvas1.setScale((Integer) jSpinnerScale.getValue());
    increaseSize();
  }

  private void jPanel1MousePressed(MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();
  }

  private void jPanel1MouseReleased(MouseEvent evt) {
    if (evt.getX() == startMouseX && evt.getY() == startMouseY) {
      canvas1.changeOccupationByHand(startMouseX, startMouseY);
    }
  }

  private void jPanel1MouseWheelMoved(MouseWheelEvent evt) {
    int zoom = (Integer) jSpinnerScale.getValue();
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
    jSpinnerScale.setValue(zoom);
    canvas1.setScale(zoom);
    increaseSize();
  }
  
  private void increaseSize() {
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    setSize(canvas1.getSizeX() + 70, canvas1.getSizeY() + 120);
  }
    
  private void pause() {
    paused = !paused;
    canvas1.setPaused(paused);
    if (paused) {
      pauseButton.setIcon(resumeIcon);
      statusbar.setText("Paused");
    } else {
      pauseButton.setIcon(pauseIcon);
      statusbar.setText("Running");
    }
  }

  private class Pause extends AbstractAction {

    public Pause() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      pause();
    }
  }
  
  private void pngSave() {
    paused = true;
    canvas1.setPaused(true);
    canvas1.performDrawToImage(-111);
  }
  
  private void createMenuBar() {
    JMenuBar menubar = new JMenuBar();

    ImageIcon iconNew = new ImageIcon("new.png");
    ImageIcon iconOpen = new ImageIcon("open.png");
    ImageIcon iconSave = new ImageIcon("save.png");
    ImageIcon iconExit = new ImageIcon("exit.png");

    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");

    JMenu impMenu = new JMenu("Import");

    JMenuItem newsfMi = new JMenuItem("Import newsfeed list...");
    JMenuItem bookmMi = new JMenuItem("Import bookmarks...");
    JMenuItem mailMi = new JMenuItem("Import mail...");

    impMenu.add(newsfMi);
    impMenu.add(bookmMi);
    impMenu.add(mailMi);

    JMenuItem newMi = new JMenuItem("New", iconNew);
    JMenuItem openMi = new JMenuItem("Open", iconOpen);
    JMenuItem saveMi = new JMenuItem("Save PNG image...", iconSave);

    JMenuItem exitMi = new JMenuItem("Exit", iconExit);
    exitMi.setToolTipText("Exit application");
    exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

    exitMi.addActionListener((ActionEvent event) -> {
      System.exit(0);
    });

    fileMenu.add(newMi);
    fileMenu.add(openMi);
    fileMenu.add(saveMi);
    fileMenu.addSeparator();
    fileMenu.add(impMenu);
    fileMenu.addSeparator();
    fileMenu.add(exitMi);
    
    JCheckBoxMenuItem sbarMi = new JCheckBoxMenuItem("Show statubar");
    sbarMi.setMnemonic(KeyEvent.VK_T);
    sbarMi.setDisplayedMnemonicIndex(6);
    sbarMi.setSelected(true);
    
    bwMi = new JCheckBoxMenuItem("B/W output");
    bwMi.setMnemonic(KeyEvent.VK_B);
    bwMi.setDisplayedMnemonicIndex(0);
    bwMi.setSelected(false);
    
    JCheckBoxMenuItem idMi = new JCheckBoxMenuItem("Show atom identifiers");
    idMi.setMnemonic(KeyEvent.VK_I);
    idMi.setDisplayedMnemonicIndex(10);
    idMi.setSelected(true);
        
    JCheckBoxMenuItem islandsMi = new JCheckBoxMenuItem("Show island numbers");
    islandsMi.setMnemonic(KeyEvent.VK_N);
    islandsMi.setDisplayedMnemonicIndex(12);
    islandsMi.setSelected(false);
    
    JCheckBoxMenuItem centresMi = new JCheckBoxMenuItem("Show island centres");
    centresMi.setMnemonic(KeyEvent.VK_C);
    centresMi.setDisplayedMnemonicIndex(12);
    centresMi.setSelected(false);
    
    viewMenu.add(sbarMi);
    viewMenu.add(bwMi);
    viewMenu.add(idMi);
    viewMenu.add(islandsMi);
    viewMenu.add(centresMi);
    viewMenu.setMnemonic(KeyEvent.VK_V);
    viewMenu.setDisplayedMnemonicIndex(0);
    menubar.add(fileMenu);
    menubar.add(viewMenu);

    setJMenuBar(menubar);
    
    saveMi.addActionListener((ActionEvent e)-> {
      pngSave();
    });
    
    sbarMi.addItemListener((ItemEvent e) -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        statusbar.setVisible(true);
        progressBar.setVisible(true);
      } else {
        statusbar.setVisible(false);
        progressBar.setVisible(false);
      }
    });    
    
    bwMi.addItemListener((ItemEvent e) -> {
      canvas1.changeBlackAndWhite();
      bwButton.setSelected(!bwButton.isSelected());
    });

    idMi.addItemListener((ItemEvent e) -> {
      canvas1.changePrintId();
    });
    
    islandsMi.addItemListener((ItemEvent e) -> {
      canvas1.changePrintIslandNumber();
    });
    
    centresMi.addItemListener((ItemEvent e) -> {
      canvas1.changePrintIslandCentres();
    });
  }
}
