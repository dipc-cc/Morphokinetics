/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.OutputType;
import basic.io.OutputType.formatFlag;
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
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
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
  /**
   * See {@link #getSurfaceType()}
   */
  private String surfaceType;
  private int temperature;
  private int presure;
  /**
   * See {@link #getDepositionFlux()}
   */
  private double depositionFlux;
  /**
   * See {@link #getEndTime()}
   */
  private double endTime;
  private double coverage;
  /**
   * See {@link #getPsdScale()}
   */
  private double psdScale;
  /**
   * See {@link #getPsdExtend()}
   */
  private double psdExtend;
  private int numberOfSimulations;
  private int cartSizeX;
  private int cartSizeY;
  private int binsLevels;
  private int extraLevels;
  private boolean multithreaded;
  private boolean visualise;
  private boolean withGui;
  private boolean justCentralFlake;
  private boolean printToImage;
  private boolean psd;
  private boolean outputData;
  private boolean randomSeed;
  private boolean useMaxPerimeter;
  private JSONArray outputDataFormat;
  /** To have the possibility to choose between different output formats. For the moment TXT, MKO,
   * PNG and EXTRA.
   */
  private final OutputType outputType;
  /** This numbers reflect the power of two and gives the chance to choose between inclusively among
   * TXT(0), MKO(1), PNG(2) and EXTRA(3). So a number between 0 (no evaluator) and 7 (all the
   * evaluators) has to be chosen.
   */
  private long numericFormatCode;
  
  // For evolutionary algorithm
  /** Can be original or dcma. */
  private String evolutionaryAlgorithm;
  /** Can be serial or threaded. */
  private boolean parallelEvaluator; 
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
  /** Chooses between exponential distribution of the random genes (true) or linear distribution
   * (false).
   */
  private boolean expDistribution;
  /** To have the possibility to choose between different evaluators. For the moment only PSD, TIME
   * and HIERARCHY.
   */
  private final EvaluatorType evaluatorType;
  /** This numbers reflect the power of two and gives the chance to choose between inclusively among
   * PSD(0), TIME(1) and HIERARCHY(2). So a number between 0 (no evaluator) and 7 (all the
   * evaluators) has to be chosen.
   */
  private long numericStatusCode;
  private JSONArray evaluator;
  /** If a hierarchy evaluator has been chosen, select the type of hierarchy evaluator. Options:
   * "basic", "step", "reference" and "Frobenius".
   */
  private String hierarchyEvaluator;
  /** Search for "rates" or "energies". */
  private String evolutionarySearchType;
  /** Decides if diffusion must be fixed. */
  private boolean fixDiffusion;
  
  /**
   * Constructor
   */
  public Parser() {
    calculationType = "batch";
    islandDensityType = "COX_PRB";
    listType = "linear";
    perimeterType = "circle";
    calculationMode = "Ag";
    surfaceType = "cartesian";
    temperature = 135;
    presure = 135;
    depositionFlux = 0.0035;
    coverage = 30.0;
    psdScale = 0.5;
    psdExtend = 1;
    endTime = -1;
    numberOfSimulations = 10;
    cartSizeX = 256;
    cartSizeY = 256;
    binsLevels = 100;
    extraLevels = 0;
    multithreaded = true;
    visualise = true;
    withGui = true;
    justCentralFlake = true;
    printToImage = false;
    psd = false;
    outputData = false;
    numericFormatCode = 2;
    outputType = new OutputType();
    randomSeed = true;
    useMaxPerimeter = false;

    evolutionaryAlgorithm = "original";
    parallelEvaluator = false;
    populationSize = 5;
    offspringSize = 32;
    populationReplacement = 5;
    totalIterations = 100;
    repetitions = 18;
    readReference = true;
    stopError = 0.022;
    minValueGene = 0.1;
    maxValueGene = 1e11;
    expDistribution = true;
    evaluatorType = new EvaluatorType();
    numericStatusCode = 3;
    hierarchyEvaluator = "basic";
    evolutionarySearchType = "rates";
    fixDiffusion = true;
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
      surfaceType = json.getString("surfaceType");
    } catch (JSONException e) {
      surfaceType = "cartesian";
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
      depositionFlux = json.getDouble("depositionFlux");
    } catch (JSONException e) {
      if (calculationMode.equals("Ag")) {
        depositionFlux = 0.0035;
      } else { // Graphene (or etching, where it does not matter the deposition
        depositionFlux = 0.000035;
      }
    }
    try {
      endTime = json.getDouble("endTime");
    } catch (JSONException e) {
      endTime = -1;
    }
    try {
      coverage = json.getDouble("coverage");
    } catch (JSONException e) {
      coverage = 30.0;
    }
    try {
      psdScale = json.getDouble("psdScale");
    } catch (JSONException e) {
      psdScale = 0.5;
    }
    try {
      psdExtend = json.getDouble("psdExtend");
    } catch (JSONException e) {
      psdExtend = 1;
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
      randomSeed = json.getBoolean("randomSeed");
    } catch (JSONException e) {
      randomSeed = true;
    }
    try {
      useMaxPerimeter = json.getBoolean("useMaxPerimeter");
    } catch (JSONException e) {
      useMaxPerimeter = false;
    }
    try {
      outputData = json.getBoolean("outputData");
    } catch (JSONException e) {
      outputData = false;
    }
    try {
      numericFormatCode = 0;
      outputDataFormat = json.getJSONArray("outputDataFormat");
      for (int i = 0; i < outputDataFormat.length(); i++) {
        JSONObject currentFormat = outputDataFormat.getJSONObject(i);
        String type = currentFormat.getString("type");
     	// This values must agree with those ones in outputFlag of file OutputType.java
        if (type.equals("txt")) {
          numericFormatCode += 1;
        }
        if (type.equals("mko")) {
          numericFormatCode += 2;
        }
        if (type.equals("png")){
          numericFormatCode += 4;
        }
        if (type.equals("extra")){
          numericFormatCode += 8;
        }
      }
    } catch (JSONException e) {
        numericFormatCode = 2; // Only mko (binary) output by default
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
      expDistribution = !evolutionarySearchType.equals("energies");
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
    System.out.printf("%32s: %s,\n", "\"presure\"", presure);
    System.out.printf("%32s: %s,\n", "\"temperature\"", temperature);
    System.out.printf("%32s: %s,\n", "\"depositionFlux\"", depositionFlux);
    System.out.printf("%32s: %s,\n", "\"endTime\"", endTime);
    System.out.printf("%32s: %s,\n", "\"coverage\"", coverage);
    System.out.printf("%32s: %s,\n", "\"psdScale\"", psdScale);
    System.out.printf("%32s: %s,\n", "\"psdExtend\"", psdExtend);
    System.out.printf("%32s: %s,\n", "\"visualise\"", visualise);
    System.out.printf("%32s: %s,\n", "\"withGui\"", withGui);
    System.out.printf("%32s: %s,\n", "\"printToImage\"", printToImage);
    System.out.printf("%32s: %s,\n", "\"calculationMode\"", calculationMode);
    System.out.printf("%32s: %s,\n", "\"surfaceType\"", surfaceType);
    System.out.printf("%32s: %s,\n", "\"psd\"", psd);
    System.out.printf("%32s: %s,\n", "\"randomSeed\"", randomSeed);
    System.out.printf("%32s: %s,\n", "\"useMaxPerimeter\"", useMaxPerimeter);
    System.out.printf("%32s: %s,\n", "\"outputData\"", outputData);
    if (outputDataFormat != null) {
      System.out.printf("%32s: [", "\"outputDataFormat\"");
      
      for (int i = 0; i < outputDataFormat.length(); i++) {
        JSONObject currentFormat = outputDataFormat.getJSONObject(i);
        System.out.printf(" {%s: \"%s\"},", "\"type\"", currentFormat.getString("type"));
      }
      System.out.printf("],\n");
    } else {
      System.out.printf("%32s: [ {\"type\": \"mko\"},],\n", "\"outputDataFormat\"");
    }
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

  /**
   * Number of atoms that is coming per second and per area. Synonym of deposition rate and
   * diffusionMl.
   *
   * @return deposition flux
   * @see ratesLibrary.RatesFromPrbCox#getDepositionRatePerSite()
   */
  public double getDepositionFlux() {
    return depositionFlux;
  }

  /**
   * Returns the maximum simulation time that each run has to do or -1 if there is no time limit.
   *
   * @return ending time of simulation or -1 (no time limit).
   */
  public double getEndTime() {
    return endTime;
  }
  
  /**
   * Returns the maximum coverage until a simulation is allowed to grow. Only valid for multi-flake
   * simulations
   *
   * @return coverage
   */
  double getCoverage() {
    return coverage;
  }
  
  /**
   * Factor with which has to be multiplied the size of the surface to calculate the PSD.
   * 
   * Converts this island:
         <pre> {@code                                                                 
                       'H                                   
       :H    ;HH;                                 
        HH.   #HH                                 
         HH`#HHH`     #HHHHH                      
         .HHHHHH      ,HHHHH                      
          #HHHH;H;`     #H'``              HHH`   
              HHHHH#H`   HHHH`       #HHH#HHHHH   
            :#HHHHHHH  ` :H;;#H   ;:`:#HHHHH;:    
              #HHHHH :HH; :HHH   :HHHHHHHHHHH;    
                HHHHHHHH` :#H;    HHHHHH;,HHHH;   
                  #HHHHHHHHHHH #HHHHHH#H   HHHH`  
                  :HHHHHHHHHHHHHHHHHH`      :H:`  
                  :HH`  HHHHHHH HHHHHH            
                  HH` :HHH::HHH:`#HHHH:`          
                    HHHHHHH`#HHHH  #HHH`          
                    HHH;#HH; HHH;                 
                     HHHHHH                       
                      #HHHH`                      
                :#'`  :HH'                        
                :HHH#HHHH;                        
                 ;HHHHHHH`                        
                  `.+HHHHH`                         
        }  </pre>                                                                               
   *
   * to this smaller one:
   <pre>
   {@code
               H  H.                
              `HHH   HHH           
               ;HHH:  H';      ;H: 
                ,HHH;;.H+; #`HHH,  
                  +HHH +H .HH';HH' 
                   +HHHHHHH#H   .. 
                   + ,H'+# HH+     
                    HHHH`+.        
                     H+#           
                  ;HHHH            
                    ;HH    
    } </pre>
   * @return psdScale
   */
  double getPsdScale() {
    return psdScale;
  }
  
  /**
   * Factor with which has to be added as empty area to the surface.
   * Used to decide the scale from this island:
   * <pre> {@code 
:::::::::::::::::::::::::
:::::::::::::::::::::::::
::::@::@.::::::::::::::::
::::`@@@:::@@@:::::::::::
:::::;@@@:::@';::::::;@::
::::::,@@@;;.@+;:#`@@@,::
::::::::+@@@ +@:.@@';@@':
:::::::::+@@@@@@@#@:::..:
:::::::::+ ,@'+# @@+:::::
::::::::::@@@@,+.::::::::
:::::::::::@+#:::::::::::
::::::::;@@@@::::::::::::
::::::::::;@@::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
} </pre>
    and this filled one:
    * <pre> {@code
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::@::::::::::::::::::::::::::::::
::::::::::::::::,@:;@:::::::::::::::::::::::::::::
:::::::::::::::::'@@.:::+@::::::::,.::::::::::::::
:::::::::::::::::::@@@@::@+@:::@@@@@::::::::::::::
::::::::::::::::::::'@@@+ @.:@@@@'@.::::::::::::::
::::::::::::::::::::::@@@@@;@@@:::#@::::::::::::::
:::::::::::::::::::::.@ @@@@ @@@::::::::::::::::::
::::::::::::::::::::::#@@@:@@::;::::::::::::::::::
::::::::::::::::::::::::@@::::::::::::::::::::::::
::::::::::::::::::::.@+@@:::::::::::::::::::::::::
:::::::::::::::::::::'@@@,::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
  }</pre>
   * @see kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc#increaseEmptyArea(float[][], double) 
   * @return psdExtend
   */
  double getPsdExtend() {
    return psdExtend;
  }
  
  public int getNumberOfSimulations() {
    return numberOfSimulations;
  }

  public int getCartSizeX() {
    return cartSizeX;
  }

  public void setCartSizeX(int sizeX) {
    cartSizeX = sizeX;
  }
  
  public int getCartSizeY() {
    return cartSizeY;
  }
  
  public void setCartSizeY(int sizeY) {
    cartSizeY = sizeY;
  }
  
  public int getHexaSizeI() {
    if (getCalculationMode().equals("Ag")) {
      return cartSizeX;
    } else {
      return (int) (cartSizeX / 1.5f);
    }
  }
  
  public int getHexaSizeJ() {
    return (int) (getCartSizeX() / AbstractGrowthLattice.Y_RATIO);
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
   *
   * @return calculation mode. Either: "Si", "Ag" or "graphene"
   */
  public String getCalculationMode() {
    return calculationMode;
  }

  /**
   * Can be "cartesian" or "periodic". If "cartesian" is chosen, the surface (islands) will have the
   * same shape as in the GUI, but the periodicity will not be correct in top-bottom (they is a
   * shift of 60º). If "periodic" is chosen, the shape will be shifted by 60º and periodicity will
   * be correct in 2D. This option will change the PSD; "cartesian" will have vertical and
   * horizontal symmetry and in "periodic" the symmetry will be shifted by 60º.
   *
   * @return surface type. Either: "cartesian" or "periodic"
   */
  public String getSurfaceType() {
    return surfaceType;
  }
  
  public boolean doPsd() {
    return psd;
  }

  public boolean outputData() {
    return outputData;
  }
  
  /**
   * To have the possibility to output to different file formats. For the moment only TXT, PNG, MKO
   * or EXTRA
   *
   * @return output format. Either: TXT, PNG, MKO or EXTRA
   */
  public EnumSet<formatFlag> getOutputFormats() {
    return outputType.getStatusFlags(numericFormatCode);
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
   *
   * @return by default 18
   */
  public int getRepetitions() {
    return repetitions;
  }
  
  /**
   * Chooses between to read a reference PSD or doing an initial run to be the objective for the
   * Evolutionary run
   *
   * @return read reference?
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
   * To have the possibility to choose between different evaluators in genetic algorithm runs. For
   * the moment only PSD, TIME and HIERARCHY.
   *
   * @return evaluator type. Either: PSD, TIME and HIERARCHY.
   */
  public EnumSet<evaluatorFlag> getEvaluatorTypes() {
    return evaluatorType.getStatusFlags(numericStatusCode);
  }
  
  /**
   * If a hierarchy evaluator has been chosen, select the type of hierarchy evaluator. Options:
   * "basic", "step", "reference" and "Frobenius".
   *
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
  
  public boolean isEnergySearch() {
    return evolutionarySearchType.equals("energies");
  }
  
  /**
   * Decides if diffusion must be fixed.
   *
   * @return true if fixed, false otherwise
   */
  public boolean isDiffusionFixed() {
    return fixDiffusion;
  }
}
