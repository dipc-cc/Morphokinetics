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
import graphicInterfacesCommon.growth.IGrowthKmcFrame;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;

public class GrowthKmcFrame extends JFrame implements IGrowthKmcFrame{

  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private boolean paused;
  private final KmcCanvas canvas;
  private JButton pauseButton;
  private JToggleButton bwButton;
  private JButton pngSaveButton;
  private JButton centreButton;
  private JButton idButton;
  private JLabel labelScale;
  private JPanel panel;
  private JSpinner spinnerScale;
  private JLabel statusbar;
  private JCheckBoxMenuItem bwMi;
  private JCheckBoxMenuItem centresMi;
  private JCheckBoxMenuItem perimeterMi;
  private JProgressBar progressBar;
  private final int maxCoverage;
  private JCheckBoxMenuItem idMi;
  private JCheckBoxMenuItem islandsMi;
  private ImageIcon pauseIcon;
  private ImageIcon resumeIcon;
  /**
   * Creates new form frame for growth.
   *
   * @param lattice
   * @param max maximum value for the progress bar
   */
  public GrowthKmcFrame(AbstractGrowthLattice lattice, int max) {
    createMenuBar();
    maxCoverage = max;
    initComponents();
    canvas = new KmcCanvas(lattice);
    canvas.setSize(canvas.getSizeX(), canvas.getSizeY());
    panel.add(canvas);
    canvas.initialise();
    spinnerScale.setValue(((KmcCanvas) canvas).getScale());
    setSize(canvas.getSizeX() + 70, canvas.getSizeY() + 120);

    canvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent evt) {
        panelMouseReleased(evt);
      }

      @Override
      public void mousePressed(MouseEvent evt) {
        panelMousePressed(evt);
      }
    });
    paused = false;
    labelScale.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pause");
    labelScale.getActionMap().put("pause", new Pause());
  }

  @Override
  public void repaintKmc() {
    canvas.performDraw();
    canvas.setBaseLocation(mouseX, mouseY);
    mouseX = 0;
    mouseY = 0;
    
    validate();
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/results
   *
   * @param i simulation number
   */
  @Override
  public void printToImage(int i) {
    canvas.performDrawToImage(i);
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/folder/results
   *
   * @param folder folder to save the current image
   * @param i simulation number
   */
  @Override
  public void printToImage(String folder, int i) {
    canvas.performDrawToImage(folder, i);
  }
  
  /**
   * Updates the progress bar of the bottom of the frame.
   * 
   * @param coverage 
   */
  @Override
  public void updateProgressBar(int coverage) {
    progressBar.setValue(coverage);
    statusbar.setText(Integer.toString(coverage) + "/" + maxCoverage);
  }

  /**
   * This method is called from within the constructor to initialise the form. 
   */ 
  private void initComponents() {
    labelScale = new JLabel();
    spinnerScale = new JSpinner();
    panel = new JPanel();
    pauseButton = new JButton();
    pauseIcon = new ImageIcon(Restart.getJarBaseDir() + "/resources/png/pause.png");
    resumeIcon = new ImageIcon(Restart.getJarBaseDir() + "/resources/png/resume.png");
    pauseButton.setIcon(pauseIcon);
    bwButton = new JToggleButton();
    bwButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/bw.png"));
    pngSaveButton = new JButton();
    pngSaveButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/save.png"));
    centreButton = new JButton();
    centreButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/centre.png"));
    idButton = new JButton();
    idButton.setIcon(new ImageIcon(Restart.getJarBaseDir() + "/resources/png/id.png"));
    statusbar = new JLabel("Running");
    progressBar = new JProgressBar(0, maxCoverage);

    JScrollPane scrollPane = new JScrollPane(panel);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle("Morphoniketics");
    setResizable(true);
    setMinimumSize(new Dimension(300, 110));

    labelScale.setText("Scale");

    spinnerScale.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spinnerScale.setFocusCycleRoot(true);
    spinnerScale.setFocusable(false);
    spinnerScale.addChangeListener((ChangeEvent evt) -> {
      spinnerScaleStateChanged(evt);
    });

    panel.addMouseWheelListener((MouseWheelEvent evt) -> {
      panelMouseWheelMoved(evt);
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
    centreButton.addActionListener((ActionEvent e) -> {
      centresMi.setSelected(!centresMi.isSelected());
    });
    idButton.addActionListener((ActionEvent e) -> {
      idMi.setSelected(!idMi.isSelected());
    });

    pauseButton.setToolTipText("Pauses the execution");
    bwButton.setToolTipText("Changes between mono colour and all colours");
    pngSaveButton.setToolTipText("Saves current view to PNG image");
    centreButton.setToolTipText("Shows centre of mass, gyradius and diameter");
    idButton.setToolTipText("Shows id of atom, island number or nothing");
    
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
                                    .addComponent(labelScale)
                                    .addPreferredGap(RELATED)
                                    .addComponent(spinnerScale, PREFERRED_SIZE, 61, PREFERRED_SIZE)
                                    .addPreferredGap(RELATED)
                                    .addComponent(pauseButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(pngSaveButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(bwButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(centreButton, PREFERRED_SIZE, 20, 20)
                                    .addPreferredGap(RELATED)
                                    .addComponent(idButton, PREFERRED_SIZE, 20, 20)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(TRAILING, layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(labelScale)
                            .addComponent(pauseButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(bwButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(pngSaveButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(centreButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(idButton, PREFERRED_SIZE, 20, 20)
                            .addComponent(spinnerScale, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                    .addPreferredGap(RELATED)
                    .addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(LEADING)
                              .addComponent(statusbar)
                    .addComponent(progressBar))
            ));

    pack();
  }

  private void spinnerScaleStateChanged(ChangeEvent evt) {
    canvas.setScale((Integer) spinnerScale.getValue());
    increaseSize();
  }

  private void panelMousePressed(MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();
  }

  private void panelMouseReleased(MouseEvent evt) {
    if (evt.getX() == startMouseX && evt.getY() == startMouseY) {
      canvas.changeOccupationByHand(startMouseX, startMouseY);
    }
  }

  private void panelMouseWheelMoved(MouseWheelEvent evt) {
    int zoom = (Integer) spinnerScale.getValue();
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
    spinnerScale.setValue(zoom);
    canvas.setScale(zoom);
    increaseSize();
  }
  
  private void increaseSize() {
    canvas.setSize(canvas.getSizeX(), canvas.getSizeY());
    setSize(canvas.getSizeX() + 70, canvas.getSizeY() + 120);
  }
    
  private void pause() {
    paused = !paused;
    canvas.setPaused(paused);
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
    pngSave("results/tmp-surface-111.png");
  }
  
  private void pngSave(String filename) {
    paused = false; // pauses the execution
    pause();
    canvas.setPaused(true);
    canvas.performDrawToImage(filename);
  }    
  
  private void createMenuBar() {
    JMenuBar menubar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");

    JMenuItem saveMi = new JMenuItem("Save PNG image");
    JMenuItem saveAsMi = new JMenuItem("Save PNG image as...");

    JMenuItem exitMi = new JMenuItem("Exit");
    exitMi.setToolTipText("Exit application");
    exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

    exitMi.addActionListener((ActionEvent event) -> {
      System.exit(0);
    });

    fileMenu.add(saveMi);
    fileMenu.add(saveAsMi);
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
    
    idMi = new JCheckBoxMenuItem("Show atom identifiers");
    idMi.setMnemonic(KeyEvent.VK_I);
    idMi.setDisplayedMnemonicIndex(10);
    idMi.setSelected(true);
        
    islandsMi = new JCheckBoxMenuItem("Show island numbers");
    islandsMi.setMnemonic(KeyEvent.VK_N);
    islandsMi.setDisplayedMnemonicIndex(12);
    islandsMi.setSelected(false);
    
    perimeterMi = new JCheckBoxMenuItem("Print perimeter");
    perimeterMi.setMnemonic(KeyEvent.VK_P);
    perimeterMi.setDisplayedMnemonicIndex(0);
    perimeterMi.setSelected(false);
    
    centresMi = new JCheckBoxMenuItem("Show island centres");
    centresMi.setMnemonic(KeyEvent.VK_C);
    centresMi.setDisplayedMnemonicIndex(12);
    centresMi.setSelected(false);
    
    viewMenu.add(sbarMi);
    viewMenu.add(bwMi);
    viewMenu.add(idMi);
    viewMenu.add(islandsMi);
    viewMenu.add(centresMi);
    viewMenu.add(perimeterMi);
    viewMenu.setMnemonic(KeyEvent.VK_V);
    viewMenu.setDisplayedMnemonicIndex(0);
    menubar.add(fileMenu);
    menubar.add(viewMenu);

    setJMenuBar(menubar);
    
    saveMi.addActionListener((ActionEvent e) -> {
      pngSave();
    });

    saveAsMi.addActionListener((ActionEvent e) -> {
      FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
      JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
      fileChooser.addChoosableFileFilter(imageFilter);
      int returnVal = fileChooser.showOpenDialog(saveAsMi);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        pngSave(fileChooser.getSelectedFile().getAbsolutePath());
      }
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
      canvas.changeBlackAndWhite();
      bwButton.setSelected(!bwButton.isSelected());
    });

    idMi.addItemListener(new MenuItemHandler());
    islandsMi.addItemListener(new MenuItemHandler());
    
    centresMi.addItemListener((ItemEvent e) -> {
      canvas.changePrintIslandCentres();
    });
    
    perimeterMi.addItemListener((ItemEvent e) -> {
      canvas.changePrintPerimeter();
    });
  }

  public class MenuItemHandler implements ItemListener {

    /**
     * Do not allow to have both id and island number at the same time selected.
     * 
     * @param e 
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
      AbstractButton button = (AbstractButton) e.getItem();
      
      if (idMi.equals(button)){
        if (idMi.isSelected() && islandsMi.isSelected()) {
          islandsMi.setSelected(false);
        }
      }
      if (islandsMi.equals(button)){
        if (idMi.isSelected() && islandsMi.isSelected()) {
          idMi.setSelected(false);
        }
      }
      canvas.setPrintId(idMi.isSelected());
      canvas.setPrintIslandNumber(islandsMi.isSelected());
    }
  }
}
