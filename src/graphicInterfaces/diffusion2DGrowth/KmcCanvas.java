/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicInterfaces.diffusion2DGrowth;

import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
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

/**
 *
 * @author Nestor
 */
public class KmcCanvas extends Canvas {

  protected int baseX = 0;
  protected int baseY = 0;
  protected BufferStrategy strategy;  //BufferStratrgy
  protected boolean initialized = false;
  protected Abstract2DDiffusionLattice lattice;
  public int scale = 2;

  public KmcCanvas(Abstract2DDiffusionLattice lattice) {
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
      String imageName = "results/surface" + i + ".png";
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
  
  @Override
  public void paint(Graphics g) { //real drawing method

    super.paint(g);

    g.setColor(Color.black);
    g.fillRect(baseX, baseY, (int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale));

    for (int j = 0; j < lattice.getHexaSizeJ(); j++) {          //Y
      int Y = (int) (Math.round(lattice.getCartY(j) * scale) + baseY);
      for (int i = 0; i < lattice.getHexaSizeI(); i++) {
        int X = (int) Math.round(lattice.getCartX(i,j) * scale) + baseX;
        byte type = lattice.getAtom(i, j).getType();
        switch (type) {
          case AbstractAtom.TERRACE:
            g.setColor(Color.RED);
            break;
          case AbstractAtom.CORNER:
            g.setColor(Color.MAGENTA);
            break;
          case AbstractAtom.EDGE:
            g.setColor(Color.ORANGE);
            break;
          case AbstractAtom.KINK:
            g.setColor(Color.YELLOW);
            break;
          case AbstractAtom.BULK:
            g.setColor(Color.GREEN);
            break;
          case 5: // imposible
            g.setColor(Color.WHITE);
            break;
          case 6: // imposible
            g.setColor(Color.CYAN);
            break;
          case 7: // imposible
            g.setColor(Color.BLUE);
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
