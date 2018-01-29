/* 
 * Copyright (C) 2018  N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package graphicInterfaces.growth;

import basic.io.Restart;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import java.awt.Canvas;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import static java.lang.String.format;
import java.text.DecimalFormat;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class KmcCanvas extends Canvas {

  private int baseX;
  private int baseY;
  /**
   * Buffer Strategy. Makes the connection with the execution to be able to print it
   */
  private BufferStrategy strategy;
  private AbstractGrowthLattice lattice;
  private RoundPerimeter perimeter;
  private int scale;
  private String imageName;
  private boolean blackAndWhite; 
  private boolean printPerimeter; 
  private boolean paused;
  private boolean printId;
  private boolean printIslandNumber;
  private boolean printMultiAtom;
  private boolean printIslandCentres;
  private Restart restart;
  
  private final static Color GRAY = new Color (220,220,220);
  private final static Color WHITE_GRAY = new Color (230,230,230);
  // Colours from: https://github.com/Gnuplotting/gnuplot-palettes/blob/master/set3.pal
  private final static Color RED =  new Color (251,128,114);
  private final static Color BLUE = new Color (128,177,211);
  private final static Color ORANGE = new Color (253,180,98);
  private final static Color GREEN = new Color (179,222,105);
  private final static Color WHITE = new Color(255,255,255);
  private final static Color INDIANRED = new Color(205,92,92);
  private final static Color BLUEVIOLET = new Color(138,43,226);
  private final static Color CORNFLOWERBLUE = new Color(100,149,237);
  private final static Color DARKBLUE = new Color(0,0,139);
  private final static Color GOLD = new Color(255,215,0);
  private final Color[] colours = {WHITE, INDIANRED, BLUEVIOLET, GRAY, CORNFLOWERBLUE, DARKBLUE, GOLD, GREEN};
  
  public KmcCanvas(AbstractGrowthLattice lattice, RoundPerimeter perimeter) {
    this.lattice = lattice;
    this.perimeter = perimeter;
    baseX = 0;
    baseY = 0;
    scale = 2;
    blackAndWhite = false;
    printPerimeter = false;
    paused = false;
    printId = true;
    printIslandNumber = false;
    printIslandCentres = false;
    restart = new Restart();
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

  public void changeBlackAndWhite() {
    blackAndWhite = !blackAndWhite;
  }
  
  public void changePrintPerimeter() {
    printPerimeter = !printPerimeter;
  }
  
  public void setPaused(boolean pause) {
    this.paused = pause;
    lattice.setPaused(pause);
  }
  
  public void setPrintId(boolean printId) {
    this.printId = printId;
  }
  
  public void setPrintIslandNumber(boolean printIslandNumber) {
    this.printIslandNumber = printIslandNumber;
  }
  
  public void setPrintMultiAtom(boolean printMultiAtom) {
    this.printMultiAtom = printMultiAtom;
  }
  
  public void changePrintIslandCentres() {
    printIslandCentres = !printIslandCentres;
  }
  
  public boolean isPaused() {
    return paused;
  }
    
  public KmcCanvas() {   //constructor
  }

  public void dispose() {
    setIgnoreRepaint(true); //we repaint manually
    setFocusable(false);
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
   * @param fileName filename to which is going to be written.
   */
  public void performDrawToImage(String fileName) {
    if (!fileName.endsWith(".png")) {
      fileName = fileName + ".png";
    }
    imageName = fileName;
    performDrawToImage();
    writeXyz();
    writeSvg();
  }

  /**
   * This method prints the current canvas to a file.
   *
   * @param i simulation number.
   */
  public void performDrawToImage(int i) {
    imageName = format("results/surface%03d.png",i);
    performDrawToImage();
  }
  
  /**
   * This method prints the current canvas to a file.
   *
   * @param folder folder in which is going to be written.
   * @param i simulation number.
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
    g.setFont(new Font("Arial", Font.PLAIN, 10)); 
    g.setColor(GRAY);
    g.fillRect(baseX, baseY, (int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale));
    if (lattice instanceof CatalysisLattice) {
      paintCatalysis(g);
    } else {

    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * scale) + baseY;
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * scale) + baseX;
        
        if (blackAndWhite) {
          if (atom.isOccupied()) {
            g.setColor(BLUE);
            if (atom.isInnerPerimeter() && printPerimeter) {
              g.setColor(RED);
            }
          } else {
            g.setColor(WHITE_GRAY);
            if (atom.isOuterPerimeter() && printPerimeter) {
              g.setColor(BLACK);
            }
          }
        } else {
          g.setColor(colours[atom.getType()]);
          if (printPerimeter && perimeter != null) {
            if (perimeter.contains(atom))
              g.setColor(ORANGE);
          }
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
            g.setColor(getContrastColor(g.getColor()));
            if (printId) {
              g.drawString(Integer.toString(atom.getId()), X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
            if (printIslandNumber) {
              String text = Integer.toString(atom.getIslandNumber());
              g.drawString(text, X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
            if (printMultiAtom) {
              g.drawString(atom.getAttributes().getMultiAtomNumber().toString(), X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
          }
        } else if (!atom.isOutside()) {
          g.drawOval(X, Y, scale, scale);
        }
      }
    }
    }

    if (printIslandCentres) {
      try {
        for (int i = 0; i < lattice.getIslandCount(); i++) {
          Island island = lattice.getIsland(i);
          Point2D point = island.getCentreOfMass();
          int Y = (int) Math.round((point.getY()) * scale) + baseY;
          int X = (int) Math.round((point.getX()) * scale) + baseX;
          g.setColor(BLACK);
          g.drawLine(X - 5, Y - 5, X + 5, Y + 5);
          g.drawLine(X - 5, Y + 5, X + 5, Y - 5);
          g.drawOval(X - 5, Y - 5, 10, 10);
          g.setColor(RED);
          int diameter = (int) Math.round(2.0 * scale * island.getMaxDistance());
          int radius = (int) Math.round(scale * island.getMaxDistance());
          g.drawOval(X - radius, Y - radius, diameter, diameter);
          g.drawString(new DecimalFormat("##.##").format(island.getMaxDistance()), X, Y + 40);
          g.setColor(GREEN);
          diameter = (int) Math.round(2.0 * scale * island.getAvgDistance());
          radius = (int) Math.round(scale * island.getAvgDistance());
          g.drawOval(X - radius, Y - radius, diameter, diameter);
          g.setColor(BLACK);
          g.drawString(new DecimalFormat("##.##").format(island.getAvgDistance()), X, Y + 10);
        }
      } catch (NullPointerException e) {
        System.err.println("Some island centre or gyradius can not be printed. Ignoring and continuing... ");
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
  
  /**
   * Method taken from http://stackoverflow.com/questions/4672271/reverse-opposing-colors
   *
   * @param colour base colour.
   * @return black or white; the one with highest contrast.
   */
  private Color getContrastColor(Color colour) {
    double y = (299 * colour.getRed() + 587 * colour.getGreen() + 114 * colour.getBlue()) / 1000;
    return y >= 128 ? Color.black : Color.white;
  }
  
  private void paintCatalysis(Graphics g) {
    for (int i = 0; i < lattice.size(); i++) {
      if (i % 2 == 0) {
        g.setColor(GRAY);
      } else {
        g.setColor(WHITE);
      }

      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * scale) + baseY;
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * scale) + baseX;

        g.fillRect(X, Y, scale, scale);
        switch (atom.getType()) { // the cases are for graphene
          case CatalysisAtom.O:
            g.setColor(RED);
            break;
          case CatalysisAtom.CO:
            g.setColor(Color.BLUE);
            break;
        }

        if (scale < 10) {
          if (atom.isOccupied()) {
            g.fillRect(X, Y, scale, scale);
          }

        } else if (atom.isOccupied()) {
          g.fillOval(X, Y, scale, scale);
          /*if (scale > 8) {
            g.setColor(getContrastColor(g.getColor()));
            if (printId) {
              g.drawString(Integer.toString(atom.getId()), X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
            if (printIslandNumber) {
              String text = Integer.toString(atom.getIslandNumber());
              g.drawString(text, X + (scale / 2) - (scale / 4), Y + (scale / 2) + (scale / 4));
            }
          }//*/
        }
      }
    }
  }
  
  private void writeXyz() {
    restart.writeXyz(1, lattice);
  }
  
  private void writeSvg() {
    restart.writeSvg(1, lattice);
  }
}
