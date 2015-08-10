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
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.lattice.AgAgLattice;
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
   * Can be COX_PRB or synthetic. Currently not used
   */
  private String islandDensityType;
  /**
   * Can be linear or binned
   */
  private String listType;
  private String perimeterType;
  /**
   * Can be Ag
   */
  private String calculationMode;
  private int temperature;
  private int presure;
  private int flow;
  private int numberOfSimulations;
  private int cartSizeX;
  private int cartSizeY;
  private int binsLevels;
  private int extraLevels;
  private boolean multithreaded;
  private boolean visualize;
  private boolean withGui;
  private boolean justCentralFlake;
  private boolean printToImage;
  private boolean psd;
  private boolean outputData;
  private boolean randomSeed;
  private boolean useMaxPerimeter;

  /**
   * Constructor
   */
  public Parser() {
    this.islandDensityType = "COX_PRB";
    this.listType = "linear";
    this.perimeterType = "circle";
    this.calculationMode = "Ag";
    this.temperature = 135;
    this.presure = 135;
    this.flow = 135;
    this.numberOfSimulations = 10;
    this.cartSizeX = 256;
    this.cartSizeY = (int) (cartSizeX / AgAgLattice.YRatio);
    this.binsLevels = 100;
    this.extraLevels = 0;
    this.multithreaded = true;
    this.visualize = true;
    this.withGui = true;
    this.justCentralFlake = true;
    this.printToImage = false;
    this.psd = false;
    this.outputData = false;
    this.randomSeed = true;
    this.useMaxPerimeter = false;
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

    System.out.println("Parser read " + lines + " lines from " + filename);

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
      perimeterType = json.getString("perimeterType");
    } catch (JSONException e) {
      perimeterType = "circle";
    }
    try {
      calculationMode = json.getString("calculationMode");
    } catch (JSONException e) {
      calculationMode = "Ag";
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
      cartSizeX = json.getInt("cartSizeX");
    } catch (JSONException e) {
      cartSizeX = 256;
    }  
    try {
      cartSizeY = json.getInt("cartSizeY");
    } catch (JSONException e) {
      cartSizeY = cartSizeX;
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
      withGui = json.getBoolean("withGui");
    } catch (JSONException e) {
      withGui = true;
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
      psd = json.getBoolean("psd");
    } catch (JSONException e) {
      psd = false;
    }
    try {
      outputData = json.getBoolean("outputData");
    } catch (JSONException e) {
      outputData = false;
    }
    try {
      randomSeed = json.getBoolean("randomSeed");
    } catch (JSONException e) {
      randomSeed = true;
    }
    try {
      useMaxPerimeter = json.getBoolean("useMaxPerimeter");
    } catch (JSONException e) {
      useMaxPerimeter = false;
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
  public void print() {
    System.out.println("\tislandDensityType:\t" + islandDensityType);
    System.out.println("\tjustCentralFlake:\t" + justCentralFlake);
    System.out.println("\tlistType:\t\t" + listType);
    System.out.println("\tperimeterType:\t\t" + perimeterType);
    System.out.println("\tmultithreaded:\t\t" + multithreaded);
    System.out.println("\tnumberOfSimulations:\t" + numberOfSimulations);
    System.out.println("\tcartSizeX:\t\t" + cartSizeX);
    System.out.println("\tcartSizeY:\t\t" + cartSizeY);
    System.out.println("\tbinsLevels:\t\t" + binsLevels);
    System.out.println("\textraLevels:\t\t" + extraLevels);
    System.out.println("\tpresure:\t\t" + presure);
    System.out.println("\ttemperature:\t\t" + temperature);
    System.out.println("\tflow:\t\t\t" + flow);
    System.out.println("\tvisualize:\t\t" + visualize);
    System.out.println("\twithGui:\t\t" + withGui);
    System.out.println("\tprintToImage\t\t" + printToImage);
    System.out.println("\tcalculationMode:\t" + calculationMode);
    System.out.println("\tpsd:\t\t\t" + psd);
    System.out.println("\toutputData:\t\t" + outputData);
    System.out.println("\trandomSeed:\t\t" + randomSeed);
    System.out.println("\tuseMaxPerimeter:\t" + useMaxPerimeter);
  }

  public String getIslandDensityType() {
    return islandDensityType;
  }

  public String getListType() {
    return listType;
  }

  public short getPerimeterType() {
    switch (perimeterType) {
      case "square":
        return RoundPerimeter.SQUARE;
      case "circle":
      default:
        return RoundPerimeter.CIRCLE;
    }
  }

  public int getTemperature() {
    return temperature;
  }

  public int getPresure() {
    return presure;
  }

  public int getFlow() {
    return flow;
  }

  public int getNumberOfSimulations() {
    return numberOfSimulations;
  }

  public int getCartSizeX() {
    return cartSizeX;
  }

  public int getCartSizeY() {
    return cartSizeY;
  }
  
  public int getHexaSizeI() {
    return cartSizeX;
  }
  
  public int getHexaSizeJ() {
    return (int) (this.getCartSizeX() / AgAgLattice.YRatio);
  }

  int getBinsLevels() {
    return binsLevels;
  }

  int getExtraLevels() {
    return extraLevels;
  }

  public boolean isMultithreaded() {
    return multithreaded;
  }

  public boolean visualize() {
    return visualize;
  }

  public boolean justCentralFlake() {
    return justCentralFlake;
  }

  public boolean withGui() {
    return withGui;
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

  public boolean outputData() {
    return outputData;
  }

  public boolean randomSeed() {
    return randomSeed;
  }

  public boolean useMaxPerimeter() {
    return useMaxPerimeter;
  }
}
