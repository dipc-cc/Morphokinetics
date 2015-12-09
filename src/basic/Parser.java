/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import geneticAlgorithm.evaluationFunctions.EvaluatorType;
import geneticAlgorithm.evaluationFunctions.EvaluatorType.evaluatorFlag;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AgLattice;
import main.Morphokinetics;
import org.json.JSONArray;
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
   * Can be Si, Ag or graphene.
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
  private boolean visualise;
  private boolean withGui;
  private boolean justCentralFlake;
  private boolean printToImage;
  private boolean psd;
  private boolean outputData;
  private boolean randomSeed;
  private boolean useMaxPerimeter;
  
  // For evolutionary algorithm
  private String evolutionaryAlgorithm; /** Can be original or dcma */
  private boolean parallelEvaluator; /** Can be serial or threaded */
  private int populationSize;
  private int offspringSize;
  private int populationReplacement;
  private int totalIterations;
  private int repetitions;
  private boolean readReference;
  private double stopError;
  /** Minimum possible value that a gene can have. */
  private double minValueGene;
  /** Maximum possible value that a gene can have. */
  private double maxValueGene;
  /** Chooses between exponential distribution of the random genes (true) or linear distribution (false). */
  private boolean expDistribution;
  /** To have the possibility to choose between different evaluators. For the moment only PSD, TIME and HIERARCHY. */
  private EvaluatorType evaluatorType;
  /** This numbers reflect the power of two and gives the chance to choose between inclusively among PSD(0), TIME(1) and HIERARCHY(2). So a number between 0 (no evaluator) and 7 (all the evaluators) has to be chosen. */
  private long numericStatusCode;
  private JSONArray evaluator;
  /** If a hierarchy evaluator has been chosen, select the type of hierarchy evaluator. Options: "basic", "step", "reference" and "Frobenius". */
  private String hierarchyEvaluator;
  /** Search for "rates" or "energies". */
  private String evolutionarySearchType;
  /** Decides if diffusion must be fixed. */
  private boolean fixDiffusion;
  
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
    this.cartSizeY = 256;
    this.binsLevels = 100;
    this.extraLevels = 0;
    this.coverage = 30;
    this.multithreaded = true;
    this.visualise = true;
    this.withGui = true;
    this.justCentralFlake = true;
    this.printToImage = false;
    this.psd = false;
    this.outputData = false;
    this.randomSeed = true;
    this.useMaxPerimeter = false;
    
    this.evolutionaryAlgorithm = "original";
    this.parallelEvaluator = false;
    this.populationSize = 5;
    this.offspringSize = 32;
    this.populationReplacement = 5;
    this.totalIterations = 100;
    this.repetitions = 18;
    this.readReference = true;
    this.stopError = 0.022;
    this.minValueGene = 0.1;
    this.maxValueGene = 1e11;
    this.expDistribution = true;
    this.evaluatorType = new EvaluatorType();
    this.numericStatusCode = 3;
    this.hierarchyEvaluator = "basic";
    this.evolutionarySearchType = "rates";
    this.fixDiffusion = true;
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
      visualise = json.getBoolean("visualise");
    } catch (JSONException e) {
      visualise = true;
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
      parallelEvaluator = json.getBoolean("parallelEvaluator");
    } catch (JSONException e) {
      parallelEvaluator = false;
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
    try {
      repetitions = json.getInt("repetitions");
    } catch (JSONException e) {
      repetitions = 18;
    }
    try {
      readReference = json.getBoolean("readReference");
    } catch (JSONException e) {
      readReference = true;
    }
    try {
      stopError = json.getDouble("stopError");
    } catch (JSONException e) {
      stopError = 0.022;
    }
    try {
      evolutionarySearchType = json.getString("evolutionarySearchType");
    } catch (JSONException e) {
      evolutionarySearchType = "rates";
    }
    try {
      minValueGene = json.getDouble("minValueGene");
    } catch (JSONException e) {
      if (evolutionarySearchType.equals("energies")) minValueGene = 0.05;
      else minValueGene = 0.1;
    }
    try {
      maxValueGene = json.getDouble("maxValueGene");
    } catch (JSONException e) {
      if (evolutionarySearchType.equals("energies")) maxValueGene = 1;
      else maxValueGene = 1e11;
    }
    try {
      expDistribution = json.getBoolean("expDistribution");
    } catch (JSONException e) {
      if (evolutionarySearchType.equals("energies")) expDistribution = false;
      else expDistribution = true;
    }  
    try {
      hierarchyEvaluator = json.getString("hierarchyEvaluator");
    } catch (JSONException e) {
      hierarchyEvaluator = "basic";
    }
    try {
      numericStatusCode = 0;
      evaluator = json.getJSONArray("evaluator");
      for (int i = 0; i < evaluator.length(); i++) {
        JSONObject currentEvaluator = evaluator.getJSONObject(i);
        String type = currentEvaluator.getString("type");
     	// This values must agree with those ones in evaluatorFlag of file EvaluatorType.java
        if (type.equals("psd")) {
          numericStatusCode += 1;
        }
        if (type.equals("time")) {
          numericStatusCode += 2;
        }
        if (type.equals("hierarchy")){
          numericStatusCode +=4;
        }
      }
    } catch (JSONException e) {
        numericStatusCode = 1 + 2 + 4; // All the evaluators by default
    }
    try {
      fixDiffusion = json.getBoolean("fixDiffusion");
    } catch (JSONException e) {
      fixDiffusion = true;
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
    System.out.printf("%32s: %s,\n", "\"visualise\"", visualise);
    System.out.printf("%32s: %s,\n", "\"withGui\"", withGui);
    System.out.printf("%32s: %s,\n", "\"printToImage\"", printToImage);
    System.out.printf("%32s: %s,\n", "\"calculationMode\"", calculationMode);
    System.out.printf("%32s: %s,\n", "\"psd\"", psd);
    System.out.printf("%32s: %s,\n", "\"outputData\"", outputData);
    System.out.printf("%32s: %s,\n", "\"randomSeed\"", randomSeed);
    System.out.printf("%32s: %s,\n", "\"useMaxPerimeter\"", useMaxPerimeter);
    System.out.printf("%32s: %s,\n", "\"evolutionaryAlgorithm\"", evolutionaryAlgorithm);
    System.out.printf("%32s: %s,\n", "\"parallelEvaluator\"", parallelEvaluator);
    System.out.printf("%32s: %s,\n", "\"populationSize\"", populationSize);
    System.out.printf("%32s: %s,\n", "\"offspringSize\"", offspringSize);
    System.out.printf("%32s: %s,\n", "\"populationReplacement\"", populationReplacement);
    System.out.printf("%32s: %s,\n", "\"totalIterations\"", totalIterations);
    System.out.printf("%32s: %s,\n", "\"repetitions\"", repetitions);
    System.out.printf("%32s: %s,\n", "\"readReference\"", readReference);
    System.out.printf("%32s: %s,\n", "\"stopError\"", stopError);
    System.out.printf("%32s: %s,\n", "\"minValueGene\"", minValueGene);
    System.out.printf("%32s: %s,\n", "\"maxValueGene\"", maxValueGene);
    System.out.printf("%32s: %s,\n", "\"expDistribution\"", expDistribution);    
    if (evaluator != null) {
      System.out.printf("%32s: [", "\"evaluator\"");
      
      for (int i = 0; i < evaluator.length(); i++) {
        JSONObject currentEvaluator = evaluator.getJSONObject(i);
        System.out.printf(" {%s: \"%s\"},", "\"type\"", currentEvaluator.getString("type"));
      }
      System.out.printf("],\n");
    } else {
      System.out.printf("%32s: [ {\"type\": \"psd\"}, {\"type\": \"time\"}, {\"type\": \"hierarchy\"},],\n", "\"evaluator\"");
    }
    System.out.printf("%32s: %s,\n", "\"hierarchyEvaluator\"", hierarchyEvaluator);
    System.out.printf("%32s: %s,\n", "\"evolutionarySearchType\"", evolutionarySearchType);
    System.out.printf("%32s: %s,\n", "\"fixDiffusion\"", fixDiffusion);
    
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
    return (int) (this.getCartSizeX() / AgLattice.YRatio);
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

  public boolean visualise() {
    return visualise;
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
  
  /**
   * Can be Si, Ag or graphene.
   */
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
  
  public boolean isEvaluatorParallel() {
    return parallelEvaluator;
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
  
  /**
   * Number of repetitions or evaluations that a single Gene has to do.
   * @return by default 18 
   */
  public int getRepetitions() {
    return repetitions;
  }
  
  /**
   * Chooses between to read a reference PSD or doing an initial run to be the objective for the Evolutionary run
   * @return 
   */
  public boolean getReadReference() {
    return readReference;
  }
          
  public void setCalculationMode(String mode) {
    this.calculationMode = mode;
  }
  
  public void setPopulation(int populationSize) {
    this.populationSize = populationSize;
  }
  
  public void setEvolutionaryAlgorithm(String name) {
    this.evolutionaryAlgorithm = name;
  }
  
  /**
   * For evolutionary algorithm run mode target minimum error
   * @return minimum error
   */
  public double getStopError() {
    return stopError;
  }

  /**
   * For evolutionary algorithm, minimum possible value of a gene.
   *
   * @return minimum error
   */
  public double getMinValueGene() {
    return minValueGene;
  }  
  
  /**
   * For evolutionary algorithm, maximum possible value of a gene.
   *
   * @return minimum error
   */
  public double getMaxValueGene() {
    return maxValueGene;
  } 
  
  /**
   * Chooses between exponential distribution of the random genes (true) or linear distribution (false)
   * 
   * @return true exponential distribution; false linear
   */
  public boolean isExpDistribution() {
    return expDistribution;
  } 
  
  /**
   * To have the possibility to choose between different evaluators. For the moment only PSD, TIME and HIERARCHY.
   * @return 
   */
  public EnumSet<evaluatorFlag> getEvaluatorTypes() {
    return evaluatorType.getStatusFlags(numericStatusCode);
  }
  
  /**
   * If a hierarchy evaluator has been chosen, select the type of hierarchy evaluator. Options: "basic", "step", "reference" and "Frobenius".
   * @return "basic", "step", "reference" or "Frobenius".
   */
  public String getHierarchyEvaluator() {
    return hierarchyEvaluator;
  }
    
  public String getEvolutionarySearchType() {
    if (evolutionarySearchType.equals("rates") || evolutionarySearchType.equals("energies")) {
      return evolutionarySearchType;
    } else {
      System.out.println("Not valid search type. It must be \"rates\" of \"energies\".");
      return null;
    }
  }
  
  /**
   * Decides if diffusion must be fixed.
   *
   * @return
   */
  public boolean isDiffusionFixed() {
    return fixDiffusion;
  }
}
