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
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.lattice.AgAgLattice;
import main.Morphokinetics;
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
   * Can be batch or evolutionary.
   */
  private String calculationType;
  /**
   * Can be COX_PRB or synthetic. Currently not used.
   */
  private String islandDensityType;
  /**
   * Can be linear or binned.
   */
  private String listType;
  private String perimeterType;
  /**
   * Can be Ag or graphene.
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
  private int coverage;
  private boolean multithreaded;
  private boolean visualize;
  private boolean withGui;
  private boolean justCentralFlake;
  private boolean printToImage;
  private boolean psd;
  private boolean outputData;
  private boolean randomSeed;
  private boolean useMaxPerimeter;
  
  // For evolutionary algorithm
  private String evolutionaryAlgorithm; /** Can be original or dcma */
  private String evaluator; /** Can be serial or threaded */
  private int populationSize;
  private int offspringSize;
  private int populationReplacement;
  private int totalIterations;
  /**
   * Constructor
   */
  public Parser() {
    this.calculationType = "batch";
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
    this.coverage = 30;
    this.multithreaded = true;
    this.visualize = true;
    this.withGui = true;
    this.justCentralFlake = true;
    this.printToImage = false;
    this.psd = false;
    this.outputData = false;
    this.randomSeed = true;
    this.useMaxPerimeter = false;
    
    this.evolutionaryAlgorithm = "original";
    this.evaluator = "serial";
    this.populationSize = 5;
    this.offspringSize = 32;
    this.populationReplacement = 5;
    this.totalIterations = 100;
  }

  /**
   * Read the execution parameters from the given file. If nothing found, default values are
   * assigned.
   *
   * @param filename
   * @return 0 if success, negative otherwise
   */
  public int readFile(String filename) {
    List<String> readList = null;
    try {
      //read the parameters file
      readList = readSmallTextFile(filename);
    } catch (IOException exception) {
      System.err.println("Could not read file " + filename);
      Logger.getLogger(Morphokinetics.class.getName()).log(Level.SEVERE, null, exception);
      return -1;
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
      calculationType = json.getString("calculationType");
    } catch (JSONException e) {
      calculationType = "batch";
    }    
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
      coverage = json.getInt("coverage");
    } catch (JSONException e) {
      coverage = 30;
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
    
    //  -------------- Evolutionary Algorithm ----------------------
    try {
      evolutionaryAlgorithm = json.getString("evolutionaryAlgorithm");
    } catch (JSONException e) {
      islandDensityType = "original";
    }    
    try {
      evaluator = json.getString("evaluator");
    } catch (JSONException e) {
      evaluator = "serial";
    }
    try {
      populationSize = json.getInt("populationSize");
    } catch (JSONException e) {
      populationSize = 5;
    }  
    try {
      offspringSize = json.getInt("offspringSize");
    } catch (JSONException e) {
      offspringSize = 32;
    }    
    try {
      populationReplacement = json.getInt("populationReplacement");
    } catch (JSONException e) {
      populationReplacement = 5;
    } 
    try {
      totalIterations = json.getInt("totalIterations");
    } catch (JSONException e) {
      totalIterations = 100;
    }
    return 0;
  }

  private List<String> readSmallTextFile(String aFileName) throws IOException {
    Path path = Paths.get(aFileName);
    return Files.readAllLines(path, ENCODING);
  }

  /**
   * Prints all the parameters; either read from "parameter" file or the default value.
   */
  public void print() {
    System.out.printf("%32s: %s,\n", "\"calculationType\"", calculationType);
    System.out.printf("%32s: %s,\n", "\"islandDensityType\"", islandDensityType);
    System.out.printf("%32s: %s,\n", "\"justCentralFlake\"", justCentralFlake);
    System.out.printf("%32s: %s,\n", "\"listType\"", listType);
    System.out.printf("%32s: %s,\n", "\"perimeterType\"", perimeterType);
    System.out.printf("%32s: %s,\n", "\"multithreaded\"", multithreaded);
    System.out.printf("%32s: %s,\n", "\"numberOfSimulations\"", numberOfSimulations);
    System.out.printf("%32s: %s,\n", "\"cartSizeX\"", cartSizeX);
    System.out.printf("%32s: %s,\n", "\"cartSizeY\"", cartSizeY);
    System.out.printf("%32s: %s,\n", "\"binsLevels\"", binsLevels);
    System.out.printf("%32s: %s,\n", "\"extraLevels\"", extraLevels);
    System.out.printf("%32s: %s,\n", "\"coverage\"", coverage);
    System.out.printf("%32s: %s,\n", "\"presure\"", presure);
    System.out.printf("%32s: %s,\n", "\"temperature\"", temperature);
    System.out.printf("%32s: %s,\n", "\"flow\"", flow);
    System.out.printf("%32s: %s,\n", "\"visualize\"", visualize);
    System.out.printf("%32s: %s,\n", "\"withGui\"", withGui);
    System.out.printf("%32s: %s,\n", "\"printToImage\"", printToImage);
    System.out.printf("%32s: %s,\n", "\"calculationMode\"", calculationMode);
    System.out.printf("%32s: %s,\n", "\"psd\"", psd);
    System.out.printf("%32s: %s,\n", "\"outputData\"", outputData);
    System.out.printf("%32s: %s,\n", "\"randomSeed\"", randomSeed);
    System.out.printf("%32s: %s,\n", "\"useMaxPerimeter\"", useMaxPerimeter);
    System.out.printf("%32s: %s,\n", "\"evolutionaryAlgorithm\"", evolutionaryAlgorithm);
    System.out.printf("%32s: %s,\n", "\"evaluator\"", evaluator);
    System.out.printf("%32s: %s,\n", "\"populationSize\"", populationSize);
    System.out.printf("%32s: %s,\n", "\"offspringSize\"", offspringSize);
    System.out.printf("%32s: %s,\n", "\"populationReplacement\"", populationReplacement);
    System.out.printf("%32s: %s\n", "\"totalIterations\"", totalIterations);
  }

  public String getCalculationType() {
    return calculationType;
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
    if (this.getCalculationMode().equals("Ag")) {
      return cartSizeX;
    } else {
      return (int) (cartSizeX/1.5f);
    }
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
  
  int getCoverage() {
    return coverage;
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
    if (!withGui) {
      return false;
    }
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
  
  public String getEvolutionaryAlgorithm() {
    return evolutionaryAlgorithm;
  }
  
  public String getEvaluator() {
    return evaluator;
  }
  
  public int getPopulationSize() {
    return populationSize;
  }
  
  public int getOffspringSize() {
    return offspringSize;
  }
  
  public int getPopulationReplacement() {
    return populationReplacement;
  }
  
  public int getTotalIterations() {
    return totalIterations;
  }
}
