package graphicInterfacesCommon.growth;

/**
 * Created by jalberdi004 on 10/3/16.
 */

public interface IGrowthKmcFrame {

  public void setVisible(boolean visible);

  public void printToImage(int i);

  public void printToImage(String folderName, int i);

  public void repaintKmc();

  public void updateProgressBar(int coverage);
}
