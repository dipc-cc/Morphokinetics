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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import static java.lang.String.format;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractSurfaceLattice;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class KmcCanvas extends Canvas {

  private int baseX;
  private int baseY;
  /**
   * Buffer Strategy. Makes the connection with the execution to be able to print it
   */
  private BufferStrategy strategy;
  private AbstractSurfaceLattice lattice;
  private int scale;
  private String imageName;
  private boolean paused;
  private boolean printIslandCentres;
  private Restart restart;
  
  static Color GRAY = new Color (220,220,220);
  static Color WHITE_GRAY = new Color (230,230,230);
  // Colours from: https://github.com/Gnuplotting/gnuplot-palettes/blob/master/set3.pal
  final static Color RED =  new Color (251,128,114);
  final static Color BLUE = new Color (128,177,211);
  final static Color ORANGE = new Color (253,180,98);
  final static Color GREEN = new Color (179,222,105);
  final static Color WHITE = new Color(255,255,255);
  final static Color INDIANRED = new Color(205,92,92);
  final static Color BLUEVIOLET = new Color(138,43,226);
  final static Color CORNFLOWERBLUE = new Color(100,149,237);
  final static Color DARKBLUE = new Color(0,0,139);
  final static Color GOLD = new Color(255,215,0);
  final Color[] colours = {WHITE, INDIANRED, BLUEVIOLET, GRAY, CORNFLOWERBLUE, DARKBLUE, GOLD, GREEN};
  //private final Color[] colours = {GREEN, INDIANRED, BLUEVIOLET, BLACK, CORNFLOWERBLUE, DARKBLUE, GOLD, GREEN};
  
  private boolean printId;
  private boolean printIslandNumber;
  private boolean printMultiAtom;
  
  public KmcCanvas() {   //constructor
  }
  
  public KmcCanvas(AbstractSurfaceLattice lattice, RoundPerimeter perimeter) {
    this(lattice);
  }
  
  public KmcCanvas(AbstractSurfaceLattice lattice) {
    this.lattice = lattice;
    baseX = 0;
    baseY = 0;
    scale = 2;
    paused = false;
    restart = new Restart();
    printId = true;
  }
  
  AbstractSurfaceLattice getLattice() {
    return lattice;
  }

  public void setBaseLocation(int baseX, int baseY) {
    this.baseX += baseX;
    this.baseY += baseY;
  }
  
  public int getBaseX() {
    return baseX;
  }
  
  public int getBaseY() {
    return baseY;
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
  
  public void setPaused(boolean pause) {
    this.paused = pause;
    lattice.setPaused(pause);
  }
  
  public void changePrintIslandCentres() {
    printIslandCentres = !printIslandCentres;
  }
  
  public boolean isPaused() {
    return paused;
  }

  public void setPrintId(boolean printId) {
    this.printId = printId;
  }
  
  public boolean printId() {
    return printId;
  }
  
  public void setPrintIslandNumber(boolean printIslandNumber) {
    this.printIslandNumber = printIslandNumber;
  }
  
  public boolean printIslandNumber() {
    return printIslandNumber;
  }
  
  public void setPrintMultiAtom(boolean printMultiAtom) {
    this.printMultiAtom = printMultiAtom;
  }
  
  public boolean printMultiAtom() {
    return printMultiAtom;
  }
  
  public void dispose() {
    setIgnoreRepaint(true); //we repaint manually
    setFocusable(false);
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
    g.fillRect(baseX, baseY, getSizeX(), getSizeY());
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
  
  private void writeXyz() {
    restart.writeXyz(1, lattice);
  }
  
  void writeSvg() {
    restart.writeSvg(1, lattice, true);
  }

  abstract void changeBlackAndWhite();
  abstract void changePrintPerimeter();
}
