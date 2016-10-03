package graphicInterfacesCommon.surfaceViewer2D;

/**
 * Created by jalberdi004 on 10/3/16.
 */

public interface IFrame2D {

  public IFrame2D setMesh(float[][] mesh);

  public void redrawPSD();

  public IFrame2D setLogScale(boolean log);

  public IFrame2D setShift(boolean shift);

  public IFrame2D showPSDControls(boolean enabled);

  public IFrame2D setColorMap(int colormap);

  public double getMax();

  public double getMin();

  public IFrame2D setMin(double min);

  public IFrame2D setMax(double max);

  /**
   * This method prints the current canvas to a file.
   *
   * @param i Simulation number
   */
  public void printToImage(int i);

  /**
   * This method prints the current canvas to a file.
   *
   * @param folder the folder to be written
   * @param i Simulation number
   */
  public void printToImage(String folder, int i);

  public void setVisible(boolean visible);

  public void toBack();
}
