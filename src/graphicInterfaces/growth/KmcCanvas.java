/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.growth;

import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import static kineticMonteCarlo.lattice.AbstractGrowthLattice.Y_RATIO;
import kineticMonteCarlo.lattice.AgUcLattice;
import kineticMonteCarlo.unitCell.IUc;
import static java.lang.String.format;

/**
 *
 * @author Nestor
 */
public class KmcCanvas extends Canvas {

  private int baseX;
  private int baseY;
  /**
   * Buffer Strategy. Makes the connection with the execution to be able to print it
   */
  private BufferStrategy strategy;
  private AbstractGrowthLattice lattice;
  private int scale;
  private String imageName;
  
  private final static Color GRAY = new Color (220,220,220);
  private final static Color WHITE_GRAY = new Color (230,230,230);
  // Colours from: https://github.com/Gnuplotting/gnuplot-palettes/blob/master/set3.pal
  private final static Color TEAL = new Color (141,211,199);
  private final static Color BANANA = new Color (255,255,179); 
  private final static Color LILAC = new Color (190,186,218);
  private final static Color RED=  new Color (251,128,114);
  private final static Color BLUE = new Color (128,177,211);
  private final static Color ORANGE = new Color (253,180,98);
  private final static Color GREEN = new Color (179,222,105);
  private final static Color MAUVE = new Color (252,205,229);
  
  public KmcCanvas(AbstractGrowthLattice lattice) {
    this.lattice = lattice;
    baseX = 0;
    baseY = 0;
    scale = 2;
  }

  public void setBaseLocation(int baseX, int baseY) {
    this.baseX += baseX;
    this.baseY += baseY;
  }

  public int getScale() {
    return scale;
  }

  public int getSizeX() {
    return (int) (lattice.getCartSizeX() * scale);
  }

  public int getSizeY() {
    return (int) (lattice.getCartSizeY() * scale);
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public KmcCanvas() {   //constructor
    setIgnoreRepaint(true); //we repaint manually
    setFocusable(false);
  }

  public void dispose() {
    strategy.dispose();
  }

  /**
   * Call this before starting game loop, it initialises the bufferStrategy.
   */
  public void initialise() {
    createBufferStrategy(2);  //double buffering
    strategy = getBufferStrategy();
  }

  /**
   * Public drawing method, call this from your game loop for update image.
   */
  public void performDraw() {

    Graphics g;
    try {
      g = strategy.getDrawGraphics();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    paint((Graphics2D) g);
    g.dispose();

    if (!strategy.contentsLost()) {
      strategy.show();
    }

  }

  /**
   * This method prints the current canvas to a file.
   *
   * @param i simulation number
   */
  public void performDrawToImage(int i) {
    imageName = format("results/surface%03d.png",i);
    performDrawToImage();
  }
  
  /**
   * This method prints the current canvas to a file.
   *
   * @param folder folder in which is going to be written
   * @param i simulation number
   */
  public void performDrawToImage(String folder, int i) {
    imageName = folder + format("/surface%03d.png", i);
    performDrawToImage();
  }
  
  /**
   * Does the actual writing.
   */
  private void performDrawToImage() {
    BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    paint(graphics);
    graphics.dispose();
    try {
      FileOutputStream out = new FileOutputStream(imageName);
      ImageIO.write(image, "png", out);
      out.close();    
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void paint(Graphics g) { //real drawing method
    super.paint(g);

    g.setColor(GRAY);
    g.fillRect(baseX, baseY, (int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale));

    for (int i = 0; i < lattice.size(); i++) {
      IUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * scale) + baseY;
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * scale) + baseX;

        switch (atom.getType()) { // the cases are for graphene
          case AbstractAtom.TERRACE:
            g.setColor(WHITE_GRAY);
            break;
          case AbstractAtom.CORNER:
            g.setColor(RED);
            break;
          case AbstractAtom.EDGE:
            g.setColor(LILAC);
            break;
          case AbstractAtom.ARMCHAIR_EDGE: // == Ag KINK
            g.setColor(Color.WHITE);
            break;
          case AbstractAtom.ZIGZAG_WITH_EXTRA: // == Ag ISLAND
            g.setColor(Color.CYAN);
            break;
          case AbstractAtom.SICK:
            g.setColor(Color.BLUE);
            break;
          case AbstractAtom.KINK:
            g.setColor(BANANA);
            break;
          case AbstractAtom.BULK:
            g.setColor(GREEN);
            break;
        }

        if (scale < 3) {
          if (atom.isOccupied()) {
            g.fillRect(X, Y, scale, scale);
          } else if (!atom.isOutside()) {
            g.drawRect(X, Y, scale, scale);
          }

        } else if (atom.isOccupied()) {
          g.fillOval(X, Y, scale, scale);
          if (scale > 8) {
            g.drawString(Integer.toString(atom.getId()), X, Y);
          }
        } else if (!atom.isOutside()) {
          g.drawOval(X, Y, scale, scale);
        }
      }
    }
    g.dispose();
  }
  
  /**
   * Changes the occupation of the clicked atom from unoccupied to occupied, or vice versa. It is
   * experimental and only works with AgUc simulation mode. If fails, the execution continues
   * normally.
   *
   * @param xMouse absolute X location of the pressed point
   * @param yMouse absolute Y location of the pressed point
   */
  public void changeOccupationByHand(double xMouse, double yMouse) {
    lattice.changeOccupationByHand(xMouse, yMouse, scale);
  }
}
