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
import static java.lang.String.format;
import javax.imageio.ImageIO;
import kineticMonteCarlo.atom.AbstractAtom;

/**
 *
 * @author Nestor
 */
public class KmcCanvas extends Canvas {

  protected int baseX = 0;
  protected int baseY = 0;
  protected BufferStrategy strategy;  //BufferStratrgy
  protected boolean initialized = false;
  protected AbstractGrowthLattice lattice;
  public int scale = 2;
  
  private final static Color gray = new Color (220,220,220);
  private final static Color whiteGray = new Color (230,230,230);
  // Colours from: https://github.com/Gnuplotting/gnuplot-palettes/blob/master/set3.pal
  private final static Color teal = new Color (141,211,199);
  private final static Color banana = new Color (255,255,179); 
  private final static Color lilac = new Color (190,186,218);
  private final static Color red = new Color (251,128,114);
  private final static Color blue = new Color (128,177,211);
   private final static Color orange = new Color (253,180,98);
  private final static Color green = new Color (179,222,105);
  private final static Color mauve = new Color (252,205,229);
  
  public KmcCanvas(AbstractGrowthLattice lattice) {
    this.lattice = lattice;
  }

  public void setBaseLocation(int baseX, int baseY) {
    this.baseX += baseX;
    this.baseY += baseY;
  }

  public int getScale() {
    return scale;
  }

  public int getSizeX() {
    return (int) (this.lattice.getCartSizeX() * scale);
  }

  public int getSizeY() {
    return (int) (this.lattice.getCartSizeY() * scale);
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public KmcCanvas() {   //constructor
    this.setIgnoreRepaint(true); //we repaint manually
    this.setFocusable(false);
  }

  public void dispose() {
    strategy.dispose();
  }

  /**
   * call this before starting game loop, it initializes the bufferStrategy
   */
  public void initialize() {
    createBufferStrategy(2);  //double buffering
    strategy = getBufferStrategy();
  }

  /**
   * public drawing method, call this from your game loop for update image
   */
  public void performDraw() {

    Graphics g;
    try {
      g = strategy.getDrawGraphics();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    this.paint((Graphics2D) g);
    g.dispose();

    if (!strategy.contentsLost()) {
      strategy.show();
    }

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
      String imageName = format("results/surface%03d.png",i);
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

    g.setColor(gray);
    g.fillRect(baseX, baseY, (int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale));

    for (int j = 0; j < lattice.getHexaSizeJ(); j++) {          //Y
      int Y = (int) (Math.round(lattice.getCartY(j) * scale) + baseY);
      for (int i = 0; i < lattice.getHexaSizeI(); i++) {
        int X = (int) Math.round(lattice.getCartX(i,j) * scale) + baseX;
        byte type = lattice.getAtom(i, j).getType();
        switch (type) {
          case AbstractAtom.TERRACE:
            g.setColor(whiteGray);
            break;
          case AbstractAtom.CORNER:
            g.setColor(red);
            break;
          case AbstractAtom.EDGE:
            g.setColor(lilac);
            break;
          case AbstractAtom.ARMCHAIR_EDGE:
            g.setColor(Color.WHITE);
            break;
          case AbstractAtom.ZIGZAG_WITH_EXTRA:
            g.setColor(Color.CYAN);
            break;
          case AbstractAtom.SICK:
            g.setColor(Color.BLUE);
            break;
          case AbstractAtom.KINK:
            g.setColor(banana);
            break;
          case AbstractAtom.BULK:
            g.setColor(green);
            break;
        }

        if (scale < 3) {
          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillRect(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside()) {
            g.drawRect(X, Y, scale, scale);
          }

        } else {

          if (lattice.getAtom(i, j).isOccupied()) {
            g.fillOval(X, Y, scale, scale);
          } else if (!lattice.getAtom(i, j).isOutside()) {
            g.drawOval(X, Y, scale, scale);
          }
        }
      }
    }
    g.dispose();
  }
}
