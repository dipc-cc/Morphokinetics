/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
import geneticAlgorithm.Individual;
import graphicInterfaces.gaConvergence.GaProgressPanel;
import graphicInterfaces.growth.KmcCanvas;
import static graphicInterfaces.surfaceViewer2D.Panel2D.COLOR_BW;
import static graphicInterfaces.surfaceViewer2D.Panel2D.COLOR_HSV;
import graphicInterfaces.surfaceViewer2D.SurfaceViewerPanel2D;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.WEST;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;

public class MainInterface extends JFrame {

  private JLabel statusbar;
  private JPanel mainPanel;
  private SurfaceViewerPanel2D experimentalPanel2d;
  private SurfaceViewerPanel2D simulationPanel2d;
  private SurfaceViewerPanel2D diffPanel2d;
  private SurfaceViewerPanel2D surfacePanel2d;
  private KmcCanvas surfaceCanvas;
  private GaProgressPanel gaProgressPanel;
  private JLabel scaleLabel;
  private JPanel growPanel; 
  private JPanel mainGrowPanel; 
  private JScrollPane scrollPane;
  private JScrollPane mainScroll;
  private JSpinner jSpinner2;
  private BorderLayout growthLayout;
  
  public MainInterface(KmcCanvas canvas1) {
    this.growCanvas = canvas1;
    this.surfaceCanvas = canvas1;
    initGrowth();
    initUI();
    this.growPanel.add(canvas1);
    this.growCanvas.initialize();
    this.jSpinner2.setValue(((KmcCanvas) canvas1).getScale());
    this.setResizable(true);
   
  }

  /**
   * Must be called just after the constructor.
   */
  public void initialise() {
    paintLoop p = new paintLoop();
    p.start(); 
  }
  
  private void initUI() {
    createMenuBar();
    createMainJPanel();
    statusbar = new JLabel("Ready");
    statusbar.setBorder(BorderFactory.createEtchedBorder());

    createBorderLayout(mainScroll,statusbar);
    setTitle("Morphokinetics");
    setSize(1150, 1000);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }
  
  private void createMainJPanel() {
    JPanel interiorPanel = new JPanel();
    mainPanel = new JPanel();
    
    experimentalPanel2d = new SurfaceViewerPanel2D("Experimental");
    experimentalPanel2d.setLogScale(true)
            .setShift(true);
    simulationPanel2d = new SurfaceViewerPanel2D("Simulation");
    simulationPanel2d.setLogScale(true)
            .setShift(true);
    diffPanel2d = new SurfaceViewerPanel2D("Difference");
    diffPanel2d.setLogScale(true)
            .setShift(true)
            .setMin(-2)
            .setMax(2);
    surfacePanel2d = new SurfaceViewerPanel2D("Surface");
    surfacePanel2d.setMin(-1)
            .setMax(21);
    gaProgressPanel = new GaProgressPanel();
 
    JPanel psdPanel = new JPanel();
    JPanel psdNorth = new JPanel();
    JPanel psdSouth = new JPanel();
    psdNorth.add(experimentalPanel2d);
    psdNorth.add(simulationPanel2d);
    psdSouth.add(diffPanel2d);
    psdSouth.add(surfacePanel2d);
    BorderLayout psdLayout = new BorderLayout();
    psdPanel.setLayout(psdLayout);
    psdPanel.add(psdNorth, NORTH);
    psdPanel.add(psdSouth, SOUTH);
    BorderLayout mainLayout = new BorderLayout();
    mainPanel.setLayout(mainLayout);
    mainPanel.add(mainGrowPanel,NORTH);
    mainPanel.add(gaProgressPanel,WEST);
    mainPanel.add(psdPanel,EAST);
    
    mainScroll = new JScrollPane(mainPanel);
    
  }
  
  public void setExperimentalMesh(float[][] mesh){
    experimentalPanel2d.setMesh(mesh);
    simulationPanel2d.setMin(experimentalPanel2d.getMin())
            .setMax(experimentalPanel2d.getMax());
  }
  
  public void setSimulationMesh(float[][] mesh) {
    simulationPanel2d.setMesh(mesh);
  }
  
  public void setDifference(float[][] mesh) {
    diffPanel2d.setMesh(mesh);
  }
  
  public void setSurface(float[][] mesh) {
    surfacePanel2d.setMesh(mesh);
  }
  
  public void setError(double error) {
    diffPanel2d.setError(error);
    surfacePanel2d.setError(error);
    simulationPanel2d.setError(error);
  }
  
  private void createGroupLayout(JComponent... arg) {
    
    Container pane = getContentPane();
    GroupLayout gl = new GroupLayout(pane);
    
    pane.setLayout(gl);
    
    gl.setAutoCreateContainerGaps(true);
    gl.setHorizontalGroup(gl.createSequentialGroup()
            .addComponent(arg[0])
            .addComponent(arg[1]));
    gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(arg[0])
            .addComponent(arg[1]));
  }
  
  private void createBorderLayout(JComponent... arg) {
    
    Container pane = getContentPane();
    
    pane.setLayout(new BorderLayout());
    
    pane.add(arg[0],CENTER);
    pane.add(arg[1],SOUTH);
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
    JMenuItem saveMi = new JMenuItem("Save", iconSave);

    JMenuItem exitMi = new JMenuItem("Exit", iconExit);
    exitMi.setToolTipText("Exit application");
    exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

    exitMi.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    });

    fileMenu.add(newMi);
    fileMenu.add(openMi);
    fileMenu.add(saveMi);
    fileMenu.addSeparator();
    fileMenu.add(impMenu);
    fileMenu.addSeparator();
    fileMenu.add(exitMi);
    
    JCheckBoxMenuItem sbarMi = new JCheckBoxMenuItem("Show statubar");
    sbarMi.setMnemonic(KeyEvent.VK_S);
    sbarMi.setDisplayedMnemonicIndex(5);
    sbarMi.setSelected(true);
    
    JCheckBoxMenuItem colourMi = new JCheckBoxMenuItem("Colour PSD");
    colourMi.setMnemonic(KeyEvent.VK_C);
    colourMi.setDisplayedMnemonicIndex(0);
    colourMi.setSelected(true);
    
    JCheckBoxMenuItem shiftMi = new JCheckBoxMenuItem("Shift PSD");
    shiftMi.setMnemonic(KeyEvent.VK_S);
    shiftMi.setDisplayedMnemonicIndex(0);
    shiftMi.setSelected(true);
        
    JCheckBoxMenuItem logMi = new JCheckBoxMenuItem("Log scale PSD");
    logMi.setMnemonic(KeyEvent.VK_L);
    logMi.setDisplayedMnemonicIndex(0);
    logMi.setSelected(true);
    
    viewMenu.add(sbarMi);
    viewMenu.add(colourMi);
    viewMenu.add(shiftMi);
    viewMenu.add(logMi);
    menubar.add(fileMenu);
    menubar.add(viewMenu);

    setJMenuBar(menubar);
    
    sbarMi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          statusbar.setVisible(true);
        } else {
          statusbar.setVisible(false);
        }
      }
    });    
    
    colourMi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          simulationPanel2d.setColorMap(COLOR_HSV);
          experimentalPanel2d.setColorMap(COLOR_HSV);
          surfacePanel2d.setColorMap(COLOR_HSV);
          diffPanel2d.setColorMap(COLOR_HSV);
        } else {
          simulationPanel2d.setColorMap(COLOR_BW);
          experimentalPanel2d.setColorMap(COLOR_BW);
          surfacePanel2d.setColorMap(COLOR_BW);
          diffPanel2d.setColorMap(COLOR_BW);
        }
      }
    });

    shiftMi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          simulationPanel2d.setShift(true);
          experimentalPanel2d.setShift(true);
          surfacePanel2d.setShift(true);
          diffPanel2d.setShift(true);
        } else {
          simulationPanel2d.setShift(false);
          experimentalPanel2d.setShift(false);
          surfacePanel2d.setShift(false);
          diffPanel2d.setShift(false);
        }
      }
    });
    
    logMi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          simulationPanel2d.setLogScale(true);
          experimentalPanel2d.setLogScale(true);
          surfacePanel2d.setLogScale(true);
          diffPanel2d.setLogScale(true);
        } else {
          simulationPanel2d.setLogScale(false);
          experimentalPanel2d.setLogScale(false);
          surfacePanel2d.setLogScale(false);
          diffPanel2d.setLogScale(false);
        }
      }
    });
  }

  public static void main(String[] args) {

    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        MainInterface ex = new MainInterface(null);
        ex.setVisible(true);
      }
    });
  }
  
  /***********************************************************/
  /*******Code from GrowthKmcPanel ***************************/
  /***********************************************************/
  private boolean noStartDragData = false;
  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas growCanvas;

  public void repaintKmc() {
    growCanvas.performDraw();
  }

  public void printToImage(int i) {
    growCanvas.performDrawToImage(i);
  }

  /**
   * This method is called from within the constructor to initialise the form.
   */
  private void initGrowth() {
    scaleLabel = new JLabel();
    jSpinner2 = new JSpinner();
    growPanel = new JPanel();
    scrollPane = new JScrollPane(growPanel);
    mainGrowPanel = new JPanel();
    JPanel scalePanel = new JPanel();

    scaleLabel.setText("Scale");

    jSpinner2.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
    jSpinner2.setFocusCycleRoot(true);
    jSpinner2.setFocusable(false);

    scalePanel.add(scaleLabel);
    scalePanel.add(jSpinner2);
    growthLayout = new BorderLayout();
    mainGrowPanel.setLayout(growthLayout);
    mainGrowPanel.add(scalePanel,NORTH);
    mainGrowPanel.add(scrollPane,CENTER);
    
    // Listeners
    jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        jSpinner2StateChanged(evt);
      }
    });

    growPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });
    growPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        jPanel1MouseWheelMoved(evt);
      }
    });
    growPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }

      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }
    });

    growCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
    growCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
      }
    });

    pack();
  }

  private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {
    growCanvas.setScale((Integer) jSpinner2.getValue());
    growCanvas.setSize(growCanvas.getSizeX(), growCanvas.getSizeY());
  }

  private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();
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
    growCanvas.setScale(zoom);
    //canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    //this.setSize(canvas1.getSizeX() + 25, canvas1.getSizeY() + 50);
  }

  final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        repaintKmc();
        try {
          paintLoop.sleep(100);
          growCanvas.setBaseLocation(mouseX, mouseY);
          noStartDragData = true;
          mouseX = 0;
          mouseY = 0;
        } catch (Exception e) {
        }
      }
    }
  }

  public void setProgress(float[] progress) {
     gaProgressPanel.setProgress(progress);
  }
  
  public void addNewBestIndividual(Individual ind) {
    gaProgressPanel.addNewBestIndividual(ind);
  }
}
