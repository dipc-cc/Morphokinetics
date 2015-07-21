/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class will read an input file with all the parameters for the execution
 *
 * @author J. Alberdi-Rodriguez
 */
public class Parser {

  final static Charset ENCODING = StandardCharsets.UTF_8;

  private enum ratesLibrary {

    basic, gosalvez
  };
  /**
   * Can be COX_PRB or synthetic
   */
  private String islandDensityType;
  /**
   * Can be linear or binned
   */
  private String listType;
  /**
   * Can be Ag
   */
  private String calculationMode;
  private int temperature;
  private int presure;
  private int flow;
  private int numberOfSimulations;
  private int sizeX;
  private int sizeY;
  private int binsLevels;
  private int extraLevels;
  private boolean multithreaded;
  private boolean visualize;
  private boolean justCentralFlake;
  private boolean printToImage;
  private boolean psd;

  private static final float constant_Y = (float) Math.sqrt(3) / 2.0f;

  /**
   * Constructor
   */
  public Parser() {
    this.islandDensityType = "COX_PRB";
    this.listType = "linear";
    this.temperature = 135;
    this.numberOfSimulations = 10;
    this.sizeX = 256;
    this.sizeY = (int) (sizeX / constant_Y);
    this.multithreaded = true;
    this.visualize = true;
    this.justCentralFlake = true;
  }

  /**
   * Read the execution parameters from the given file. If nothing found, default values are
   * assigned.
   *
   * @param filename
   * @return 0 if success, negative otherwise
   * @throws java.io.IOException
   */
  public int readFile(String filename) throws IOException {
    List<String> readList;
    try {
      //read the parameters file
      readList = readSmallTextFile(filename);
    } catch (IOException exception) {
      System.err.println("Could not read file " + filename);
      throw exception;
    }

    int lines = readList.size();
    String str = new String();
    for (int i = 0; i < lines; i++) {
      str += String.valueOf(readList.get(i));
    }

    System.out.println("Read " + lines + " lines");

    // Once the file is read, proceed to read the parameters
    JSONObject json = new JSONObject(str);
    try {
      islandDensityType = json.getString("islandDensityType");
    } catch (JSONException e) {
      islandDensityType = "COX_PRB";
    }
    try {
      listType = json.getString("listType");
    } catch (JSONException e) {
      islandDensityType = "linear";
    }
    try {
      temperature = json.getInt("temperature");
    } catch (JSONException e) {
      temperature = 135;
    }
    try {
      presure = json.getInt("presure");
    } catch (JSONException e) {
      presure = 135;
    }
    try {
      flow = json.getInt("flow");
    } catch (JSONException e) {
      flow = 135;
    }
    try {
      numberOfSimulations = json.getInt("numberOfSimulations");
    } catch (JSONException e) {
      numberOfSimulations = 10;
    }
    try {
      sizeX = json.getInt("sizeX");
    } catch (JSONException e) {
      sizeX = 256;
    }
    try {
      sizeY = json.getInt("sizeY");
    } catch (JSONException e) {
      sizeY = (int) (sizeX / constant_Y);
    }
    try {
      binsLevels = json.getInt("binsLevels");
    } catch (JSONException e) {
      binsLevels = 100;
    }
    try {
      extraLevels = json.getInt("extraLevels");
    } catch (JSONException e) {
      extraLevels = 0;
    }
    try {
      multithreaded = json.getBoolean("multithreaded");
    } catch (JSONException e) {
      multithreaded = true;
    }
    try {
      visualize = json.getBoolean("visualize");
    } catch (JSONException e) {
      visualize = true;
    }
    try {
      justCentralFlake = json.getBoolean("justCentralFlake");
    } catch (JSONException e) {
      justCentralFlake = true;
    }
    try {
      printToImage = json.getBoolean("printToImage");
    } catch (JSONException e) {
      printToImage = false;
    }
    try {
      calculationMode = json.getString("calculationMode");
    } catch (JSONException e) {
      calculationMode = "Ag";
    }
    try {
      psd = json.getBoolean("psd");
    } catch (JSONException e) {
      psd = false;
    }
    return 0;
  }

  private List<String> readSmallTextFile(String aFileName) throws IOException {
    Path path = Paths.get(aFileName);
    return Files.readAllLines(path, ENCODING);
  }

  /**
   * Prints all the parameters; either read from "parameter" file or the default value
   *
   * @return
   */
  public int print() {
    System.out.println("\tislandDensityType:\t" + islandDensityType);
    System.out.println("\tjustCentralFlake:\t" + justCentralFlake);
    System.out.println("\tlistType:\t\t" + listType);
    System.out.println("\tmultithreaded:\t\t" + multithreaded);
    System.out.println("\tnumberOfSimulations:\t" + numberOfSimulations);
    System.out.println("\tsizeX:\t\t\t" + sizeX);
    System.out.println("\tsizeY:\t\t\t" + sizeY);
    System.out.println("\tbinsLevels:\t\t" + binsLevels);
    System.out.println("\textraLevels:\t\t" + extraLevels);
    System.out.println("\tpresure:\t\t" + presure);
    System.out.println("\ttemperature:\t\t" + temperature);
    System.out.println("\tflow:\t\t\t" + flow);
    System.out.println("\tvisualize:\t\t" + visualize);
    System.out.println("\tprintToImage\t\t" + printToImage);
    System.out.println("\tcalculationMode:\t" + calculationMode);
    System.out.println("\tpsd:\t\t\t" + psd);

    return 0;
  }

  /**
   *
   * @return
   */
  public String getIslandDensityType() {
    return islandDensityType;
  }

  /**
   *
   * @return
   */
  public String getListType() {
    return listType;
  }

  /**
   *
   * @return
   */
  public int getTemperature() {
    return temperature;
  }

  /**
   *
   * @return
   */
  public int getPresure() {
    return presure;
  }

  /**
   *
   * @return
   */
  public int getFlow() {
    return flow;
  }

  /**
   *
   * @return
   */
  public int getNumberOfSimulations() {
    return numberOfSimulations;
  }

  /**
   *
   * @return
   */
  public int getSizeX() {
    return sizeX;
  }

  /**
   *
   * @return
   */
  public int getSizeY() {
    return sizeY;
  }

  int getBinsLevels() {
    return binsLevels;
  }

  int getExtraLevels() {
    return extraLevels;
  }

  /**
   *
   * @return
   */
  public boolean isMultithreaded() {
    return multithreaded;
  }

  /**
   *
   * @return
   */
  public boolean visualize() {
    return visualize;
  }

  /**
   *
   * @return
   */
  public boolean justCentralFlake() {
    return justCentralFlake;
  }

  public boolean isVisualize() {
    return visualize;
  }

  public boolean printToImage() {
    return printToImage;
  }

  public String getCalculationMode() {
    return calculationMode;
  }

  public boolean doPsd() {
    return psd;
  }
}
