/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package basic;

import basic.io.OutputType;
import basic.io.OutputType.formatFlag;
import basic.EvaluatorType.evaluatorFlag;
import basic.io.Restart;
import java.util.EnumSet;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class will read an input file called "parameters" with all the parameters for the execution.
 * The format of this file is JSON. If "parameters" file is missing in the launch directory, default
 * values are taken. Only desired options can be specified, default ones are used for the rest. In
 * all cases, the used options are printed to the main screen and they can be copied to a
 * "parameters" file.
 *
 * @author J. Alberdi-Rodriguez
 */
public class Parser {
  
  /**
   * Attribute to control the use of trees for catalysis.
   */
  private boolean[] catalysisTree;
  /**
   * Restart object to read "parameters" file.
   */
  private final Restart restart;
  /** 
   * See {@link #getCalculationType()}.
   */
  private String calculationType;
  /**
   * See {@link #getRatesLibrary()}.
   */
  private String ratesLibrary;
  /**
   * See {@link #getListType()}.
   */
  private String listType;
  /**
   * See {@link #getPerimeterType()}.
   */
  private String perimeterType;
  /**
   * See {@link #getCalculationMode()}.
   */
  private String calculationMode;
  /**
   * See {@link #getSurfaceType()}.
   */
  private String surfaceType;
  /**
   * See {@link #getTemperature()}.
   */
  private float temperature;
  /**
   * See {@link #getPressureO2()} and {@link #getPressureCO()}.
   */
  private double pressureCO;
  private double pressureO2;
  /**
   * See {@link #getDepositionFlux()}.
   */
  private double depositionFlux;
  /**
   * See {@link #getEndTime()}.
   */
  private double endTime;
  /**
   * See {@link #getCoverage()}.
   */
  private double coverage;
  /**
   * See {@link #getPsdScale()}.
   */
  private double psdScale;
  /**
   * See {@link #getPsdExtend()}.
   */
  private double psdExtend;
  /**
   * See {@link #getNumberOfSimulations() }.
   */
  private int numberOfSimulations;
  /**
   * See {@link #getNumberOfSteps() }.
   */
  private long numberOfSteps;
  /**
   * See {@link #getNumberOfCo2()}.
   */
  private int numberOfCo2;
  /**
   * See {@link #getCartSizeX()}.
   */
  private int cartSizeX;
  /**
   * See {@link #getCartSizeY()}.
   */
  private int cartSizeY;
  /**
   * See {@link #getCartSizeZ()}.
   */
  private int cartSizeZ;
  private int millerX;
  private int millerY;
  private int millerZ;
  /**
   * See {@link #getBinsLevels()}.
   */
  private int binsLevels;
  /**
   * See {@link #getExtraLevels()}.
   */
  private int extraLevels;
  private boolean multithreaded;
  /**
   * See {@link #visualise()}.
   */
  private boolean visualise;
  /**
   * See {@link #withGui()}.
   */
  private boolean withGui;
  /**
   * See {@link #justCentralFlake()}.
   */
  private boolean justCentralFlake;
  /**
   * See {@link #printToImage()}.
   */
  private boolean printToImage;
  /**
   * See {@link #doPsd()}.
   */
  private boolean psd;
  /**
   * See {@link #isPsdSymmetric()}.
   */
  private boolean psdSymmetry;
  /**
   * See {@link #isPeriodicSingleFlake()}.
   */
  private boolean periodicSingleFlake;
  /**
   * See {@link #outputData()} and {@link #getOutputFormats()}.
   */
  private boolean outputData;
  /**
   * See {@link #randomSeed()}.
   */
  private boolean randomSeed;
  /**
   * See {@link #useMaxPerimeter()}.
   */
  private boolean useMaxPerimeter;
  /**
   * See {@link #forceNucleation()}
   */
  private boolean forceNucleation;
  /**
   * See {@link #useDevita()}
   */
  private boolean devita;
  /**
   * See {@link #doCatalysisAdsorption()}
   */
  private String catalysisAdsorption;
  /**
   * See {@link #doCatalysisDesorption()}
   */
  private String catalysisDesorption;
  /**
   * See {@link #doCatalysisReaction()}
   */
  private String catalysisReaction;
  /**
   * See {@link #doCatalysisDiffusion()}
   */
  private String catalysisDiffusion;
  /**
   * See {@link #calalysisStart()}
   */
  private String catalysisStart;
  /**
   * See {@link #doPrintAllIterations()}
   */
  private boolean printAllIterations;
  /**
   * See {@link #doCatalysisO2Dissociation()}
   */
  private boolean catalysisO2Dissociation;
  /**
   * See {@link #areCollectionsAutomatic()}.
   */
  private boolean automaticCollections;
  /**
   * See {@link #doIslandDiffusion() ()}.
   */
  private boolean doIslandDiffusion;
  /**
   * See {@link #doMultiAtomDiffusion() ()}.
   */
  private boolean doMultiAtomDiffusion;
  private JSONArray outputDataFormat;
  /**
   * See {@link #getOutputFormats()}.
   */
  private final OutputType outputType;
  /**
   * This numbers reflect the power of two and gives the chance to choose between inclusively among
   * TXT(0), MKO(1), PNG(2), EXTRA(3), AE(4), XYZ(5), EXTRA2(6), CAT(7), AETOTAL(8) and SVG(9). So a number
   * between 0 and 2⁸ has to be chosen.
   */
  private long numericFormatCode;
  /**
   * See {@link #getOutputEvery()].
   */
  private int outputEvery;
  
  // For evolutionary algorithm
  /** Can be original or dcma. */
  private String evolutionaryAlgorithm;
  /** Can be serial or threaded. */
  private boolean parallelEvaluator; 
  private int populationSize;
  private int offspringSize;
  private int populationReplacement;
  /**
   *  See {@link #getTotalIterations()}.
   */
  private int totalIterations;
  private int repetitions;
  private boolean readReference;
  private double stopError;
  /** Minimum possible value that a gene can have. */
  private double minValueGene;
  /** Maximum possible value that a gene can have. */
  private double maxValueGene;  
  /** Gets O desorption rate multiplied by a factor for plotting resolution problems. */
  private double goMultiplier;

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
    ratesLibrary = "COX_PRB";
    listType = "linear";
    perimeterType = "circle";
    calculationMode = "Ag";
    surfaceType = "cartesian";
    temperature = 135.0f;
    pressureO2 = 1.0;
    pressureCO = 7.0;
    depositionFlux = 0.0035;
    coverage = 30.0;
    psdScale = 1;
    psdExtend = 1;
    endTime = -1;
    numberOfSimulations = 10;
    numberOfSteps = -1;
    numberOfCo2 = -1;
    cartSizeX = 256;
    cartSizeY = 256;
    cartSizeZ = 256;
    millerX = 0;
    millerY = 1;
    millerZ = 1;
    binsLevels = 100;
    extraLevels = 0;
    multithreaded = true;
    visualise = true;
    withGui = true;
    justCentralFlake = true;
    printToImage = false;
    psd = false;
    psdSymmetry = true;
    outputData = false;
    numericFormatCode = 2;
    outputType = new OutputType();
    outputEvery = 10;
    randomSeed = true;
    useMaxPerimeter = false;
    forceNucleation = true;
    devita = true;
    catalysisAdsorption = "true";
    catalysisDesorption = "true";
    catalysisReaction = "true";
    catalysisDiffusion = "true";
    printAllIterations = false;
    catalysisStart = "O";
    catalysisO2Dissociation = true;
    automaticCollections = false;
    doIslandDiffusion = true;
    doMultiAtomDiffusion = true;
    
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
    goMultiplier = 1.0;
    
    restart = new Restart(".");
  }

  /**
   * Read the execution parameters from the given file. If nothing found, default values are
   * assigned.
   *
   * @param fileName
   * @return 0 if success, negative otherwise
   */
  public int readFile(String fileName) throws JSONException {
    String str = restart.readFile(fileName);

    // Once the file is read, proceed to read the parameters
    JSONObject json = new JSONObject(str);
    try {
      calculationType = json.getString("calculationType");
    } catch (JSONException e) {
      calculationType = "batch";
    }    
    try {
      ratesLibrary = json.getString("ratesLibrary");
    } catch (JSONException e) {
      ratesLibrary = "COX_PRB";
    }
    try {
      listType = json.getString("listType");
    } catch (JSONException e) {
      listType = "linear";
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
      temperature = (float) json.getDouble("temperature");
    } catch (JSONException e) {
      temperature = 135.0f;
    }
    try {
      pressureO2 = json.getDouble("pressureO2");
    } catch (JSONException e) {
      pressureO2 = 1.0;
    }
    try {
      pressureCO = json.getDouble("pressureCO");
    } catch (JSONException e) {
      pressureCO = 7.0;
    }
    try {
      depositionFlux = json.getDouble("depositionFlux");
    } catch (JSONException e) {
      if (calculationMode.equals("Ag") || calculationMode.equals("AgUc")) {
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
      psdScale = 1;
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
      numberOfSteps = (long) json.getDouble("numberOfSteps");
    } catch (JSONException e) {
      numberOfSteps = -1;
    }
    try {
      numberOfCo2 = (int) json.getDouble("numberOfCo2");
    } catch (JSONException e) {
      numberOfCo2 = -1;
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
      cartSizeZ = json.getInt("cartSizeZ");
    } catch (JSONException e) {
      cartSizeZ = cartSizeX;
    }
   try {
      millerX = json.getInt("millerX");
    } catch (JSONException e) {
      millerX = 0;
    }  
    try {
      millerY = json.getInt("millerY");
    } catch (JSONException e) {
      millerY = 1;
    }
    try {
      millerZ = json.getInt("millerZ");
    } catch (JSONException e) {
      millerZ = 1;
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
      psdSymmetry = json.getBoolean("psdSymmetry");
    } catch (JSONException e) {
      psdSymmetry = true;
    }
    try {
      periodicSingleFlake = json.getBoolean("periodicSingleFlake");
    } catch (JSONException e) {
      periodicSingleFlake = false;
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
      forceNucleation = json.getBoolean("forceNucleation");
    } catch (JSONException e) {
      forceNucleation = true;
    }
    try {
      devita = json.getBoolean("devita");
    } catch (JSONException e) {
      devita = justCentralFlake; // By default Devita works with single-flake simulations only
    }
    try {
      catalysisAdsorption = json.getString("catalysisAdsorption");
    } catch (JSONException e) {
      try {
        catalysisAdsorption = Boolean.toString(json.getBoolean("catalysisAdsorption"));
      } catch (JSONException ex) {
        catalysisAdsorption = "true";
      }
    }
    try {
      printAllIterations = json.getBoolean("printAllIterations");
    } catch (JSONException e) {
      printAllIterations = false;
    }
    try {
      catalysisDesorption = json.getString("catalysisDesorption");
    } catch (JSONException e) {
      try {
        catalysisDesorption = Boolean.toString(json.getBoolean("catalysisDesorption"));
      } catch (JSONException ex) {
        catalysisDesorption = "true";
      }
    }
    try {
      catalysisReaction = json.getString("catalysisReaction");
      } catch (JSONException e) {
      try {
        catalysisReaction = Boolean.toString(json.getBoolean("catalysisReaction"));
      } catch (JSONException ex) {
        catalysisReaction = "true";
      }
    }
    try {
      catalysisDiffusion = json.getString("catalysisDiffusion");
    } catch (JSONException e) {
      try {
        catalysisDiffusion = Boolean.toString(json.getBoolean("catalysisDiffusion"));
      } catch (JSONException ex) {
        catalysisDiffusion = "true";
      }
    }
    try {
      catalysisStart = json.getString("catalysisStart");
    } catch (JSONException e) {
      catalysisStart = "O";
    }
    try {
      catalysisO2Dissociation = json.getBoolean("catalysisO2Dissociation");
    } catch (JSONException e) {
      catalysisO2Dissociation = true;
    }
    try {
      automaticCollections = json.getBoolean("automaticCollections");
    } catch (JSONException e) {
      automaticCollections = false;
    }
    try {
      doIslandDiffusion = json.getBoolean("doIslandDiffusion");
    } catch (JSONException e) {
      doIslandDiffusion = true;
    }
    try {
      doMultiAtomDiffusion = json.getBoolean("doMultiAtomDiffusion");
    } catch (JSONException e) {
      doMultiAtomDiffusion = true;
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
        if (type.equals("ae")){
          numericFormatCode += 16;
        }
        if (type.equals("xyz")){
          numericFormatCode += 32;
        }
        if (type.equals("extra2")){
          numericFormatCode += 64;
        }
        if (type.equals("catalysis")){
          numericFormatCode += 128;
        }
        if (type.equals("aetotal")){
          numericFormatCode += 256;
        }
        if (type.equals("svg")){
          numericFormatCode += 512;
        }
      }
    } catch (JSONException e) {
        numericFormatCode = 2; // Only mko (binary) output by default
    }
    try {
      outputEvery = json.getInt("outputEvery");
    } catch (JSONException e) {
      outputEvery = 10;
    }
    
    //  -------------- Evolutionary Algorithm ----------------------
    try {
      evolutionaryAlgorithm = json.getString("evolutionaryAlgorithm");
    } catch (JSONException e) {
      evolutionaryAlgorithm = "original";
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
    try {
      goMultiplier = json.getDouble("goMultiplier");
    } catch (JSONException e) {
      goMultiplier = 1.0;
    }
    return 0;
  }

  /**
   * Prints all the parameters; either read from "parameter" file or the default value.
   */
  public void print() throws JSONException {
    System.out.printf("%32s: %s,\n", "\"calculationType\"", calculationType);
    System.out.printf("%32s: %s,\n", "\"ratesLibrary\"", ratesLibrary);
    System.out.printf("%32s: %s,\n", "\"justCentralFlake\"", justCentralFlake);
    System.out.printf("%32s: %s,\n", "\"listType\"", listType);
    System.out.printf("%32s: %s,\n", "\"perimeterType\"", perimeterType);
    System.out.printf("%32s: %s,\n", "\"multithreaded\"", multithreaded);
    System.out.printf("%32s: %s,\n", "\"numberOfSimulations\"", numberOfSimulations);
    System.out.printf("%32s: %s,\n", "\"numberOfSteps\"", numberOfSteps);
    System.out.printf("%32s: %s,\n", "\"numberOfCo2\"", numberOfCo2);
    System.out.printf("%32s: %s,\n", "\"cartSizeX\"", cartSizeX);
    System.out.printf("%32s: %s,\n", "\"cartSizeY\"", cartSizeY);
    System.out.printf("%32s: %s,\n", "\"cartSizeZ\"", cartSizeZ);
    System.out.printf("%32s: %s,\n", "\"millerX\"", millerX);
    System.out.printf("%32s: %s,\n", "\"millerY\"", millerY);
    System.out.printf("%32s: %s,\n", "\"millerZ\"", millerZ);
    System.out.printf("%32s: %s,\n", "\"binsLevels\"", binsLevels);
    System.out.printf("%32s: %s,\n", "\"extraLevels\"", extraLevels);
    System.out.printf("%32s: %s,\n", "\"pressureO2\"", pressureO2);
    System.out.printf("%32s: %s,\n", "\"pressureCO\"", pressureCO);
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
    System.out.printf("%32s: %s,\n", "\"psdSymmetry\"", psdSymmetry);
    System.out.printf("%32s: %s,\n", "\"periodicSingleFlake\"", periodicSingleFlake);
    System.out.printf("%32s: %s,\n", "\"randomSeed\"", randomSeed);
    System.out.printf("%32s: %s,\n", "\"useMaxPerimeter\"", useMaxPerimeter);
    System.out.printf("%32s: %s,\n", "\"forceNucleation\"", forceNucleation);
    System.out.printf("%32s: %s,\n", "\"devita\"", devita);
    System.out.printf("%32s: %s,\n", "\"catalysisAdsorption\"", catalysisAdsorption);
    System.out.printf("%32s: %s,\n", "\"catalysisDesorption\"", catalysisDesorption);
    System.out.printf("%32s: %s,\n", "\"catalysisReaction\"", catalysisReaction);
    System.out.printf("%32s: %s,\n", "\"catalysisDiffusion\"", catalysisDiffusion);
    System.out.printf("%32s: %s,\n", "\"catalysisStart\"", catalysisStart);
    System.out.printf("%32s: %s,\n", "\"catalysisO2Dissociation\"", catalysisO2Dissociation);
    System.out.printf("%32s: %s,\n", "\"automaticCollections\"", automaticCollections);
    System.out.printf("%32s: %s,\n", "\"doIslandDiffusion\"", doIslandDiffusion);
    System.out.printf("%32s: %s,\n", "\"doMultiAtomDiffusion\"", doMultiAtomDiffusion);
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
    System.out.printf("%32s: %s,\n", "\"outputEvery\"", outputEvery);
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

  /**
   * Morphokinetics has two main calculation modes: one called batch, for arbitrarily chosen input
   * parameters, and the other called evolutionary, which tries to find the best input parameters
   * for a target system. Additionally, it has an utility which does PSD analysis from given
   * surfaces.
   *
   * Input "parameters" variable: {@code calculationType}.
   *
   * @return "batch", "evolutionary" or "psd".
   */
  public String getCalculationType() {
    return calculationType;
  }

  /**
   * Selects which rates are used for the simulation. Available options for graphene are:
   * "GaillardSimple", "Gaillard1Neighbour", "Gaillard2Neighbours", "Schoenhalz" or anything else
   * for synthetic rates. Available options for basic growth are: "simple", "version2", "version3"
   * or anything else for original synthetic rates. Available options for AgUc growth are: "simple"
   * or anything else for original Cox et al. rates.
   *
   * Input "parameters" variable: {@code ratesLibrary}.
   *
   * @return "GaillardSimple", "Gaillard1Neighbour", "Gaillard2Neighbours", "Schoenhalz" or anything
   * else for synthetic rates for graphene. "simple", "version2", "version3" or anything else for
   * basic growth. "simple" or anything else for AgUc.
   */
  public String getRatesLibrary() {
    return ratesLibrary;
  }

  /**
   * Selects the internal list type to be used between linear and binned.
   *
   * Input "parameters" variable: {@code listType}.
   *
   * @return "linear" or "binned".
   */
  public String getListType() {
    return listType;
  }

  /**
   * Sets the internal list type to be used. Should be linear or binned.
   *
   * @param listType linear or binned.
   */
  public void setListType(String listType) {
    this.listType = listType;
  }

  /**
   * Input "parameters" variable: {@code perimeterType}.
   *
   * @return properly formated "SQUARE" or "CIRCLE" (default option).
   */
  public short getPerimeterType() {
    switch (perimeterType) {
      case "square":
        return RoundPerimeter.SQUARE;
      case "circle":
      default:
        return RoundPerimeter.CIRCLE;
    }
  }

  public void setTemperature(float temperature) {
    this.temperature = temperature;
  }
  
  /**
   * Simulation temperature.
   * 
   * Input "parameters" variable: {@code temperature}.
   * 
   * @return temperature
   */
  public float getTemperature() {
    return temperature;
  }

  /**
   * Partial pressure for O2 in atm.
   * 
   * @return O2 pressure.
   */
  public double getPressureO2() {
    return pressureO2;
  }

  /**
   * GO multiplier for plotting resolution problem solving.
   * 
   * @return GO multiplier.
   */
  public double getGOMultiplier() {
    return goMultiplier;
  }
  
  /**
   * Partial pressure for CO in atm.
   * 
   * @return CO pressure.
   */
  public double getPressureCO() {
    return pressureCO;
  }

  /**
   * Number of atoms that is coming per second and per area. Synonym of deposition rate and
   * diffusionMl.
   *
   * Input "parameters" variable: {@code depositionFlux}.
   * 
   * @return deposition flux
   * @see ratesLibrary.IRates#getDepositionRatePerSite()
   */
  public double getDepositionFlux() {
    return depositionFlux;
  }

  /**
   * Returns the maximum simulation time that each run has to do or -1 if there is no time limit.
   *
   * Input "parameters" variable: {@code endTime}.
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
   * Input "parameters" variable: {@code coverage}.
   *
   * @return final coverage.
   */
  public double getCoverage() {
    return coverage;
  }
  
  /**
   * Factor with which has to be multiplied the size of the surface to calculate the PSD. I
   * recommend to be 1.
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
  public double getPsdScale() {
    return psdScale;
  }
  
  /**
   * Factor which has to be added as empty area to the surface.
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
  
  public void setNumberOfSimulations(int numberOfSimulations){
    this.numberOfSimulations = numberOfSimulations;
  }
  
  /**
   * Selects the number of simulations that has to be done with the same parameters.
   * 
   * Input "parameters" variable: {@code numberOfSimulations}.
   * 
   * @return number of simulations.
   */
  public int getNumberOfSimulations() {
    return numberOfSimulations;
  }
  
  /**
   * Selects the number of steps that each simulation will at most. -1 by default, which means that
   * it will run until another criteria is reached.
   *
   * Input "parameters" variable: {@code numberOfSteps}.
   *
   * @return number of simulations.
   */
  public long getNumberOfSteps() {
    return numberOfSteps;
  }
  
  /**
   * Selects the number of CO2 molecules that will be created at most. -1 by default, which means that
   * it will run until another criteria is reached.
   *
   * Input "parameters" variable: {@code numberOfCo2}.
   *
   * @return number of simulations.
   */
  public int getNumberOfCo2() {
    return numberOfCo2;
  }

  /**
   * Selects the Cartesian size in X direction. 
   * 
   * Input "parameters" variable: {@code cartSizeX}.
   * 
   * @return Cartesian size in X direction. 
   */
  public int getCartSizeX() {
    return cartSizeX;
  }

  public void setCartSizeX(int sizeX) {
    cartSizeX = sizeX;
  }
  
  /**
   * Selects the Cartesian size in Y direction. 
   * 
   * Input "parameters" variable: {@code cartSizeY}.
   * 
   * @return Cartesian size in Y direction. 
   */
  public int getCartSizeY() {
    return cartSizeY;
  }
    
  public void setCartSizeY(int sizeY) {
    cartSizeY = sizeY;
  }
  
  /**
   * Selects the Cartesian size in Z direction. Next methods are only meaningful in etching,
   * ignored in 2D surface growth.
   *
   * Input "parameters" variable: {@code cartSizeZ}.
   *
   * @return Cartesian size in Z direction.
   */
  public int getCartSizeZ() {
    return cartSizeZ;
  }
  
  public void setCartSizeZ(int sizeZ) {
    cartSizeZ = sizeZ;
  }
  
  public int getMillerX() {
    return millerX;
  }

  public void setMillerX(int sizeX) {
    millerX = sizeX;
  }
  
  public int getMillerY() {
    return millerY;
  }
    
  public void setMillerY(int sizeY) {
    millerY = sizeY;
  }
  
  public int getMillerZ() {
    return millerZ;
  }
  
  public void setMillerZ(int sizeZ) {
    millerZ = sizeZ;
  }  
  
  /**
   * Selects the lattice size in I direction. 
   * 
   * Can not be directly set from "parameters" file.
   * 
   * @return lattice size in I direction. 
   */
  public int getHexaSizeI() {
    if (calculationMode.equals("basic")|| getCalculationMode().equals("catalysis")) {
      return cartSizeX;
    }
    if (getCalculationMode().equals("Ag") || getCalculationMode().equals("AgUc") || getCalculationMode().equals("concerted")) {
      return cartSizeX;
    } else { // graphene, always even number
      int sizeI = (int) Math.ceil(cartSizeX / 1.5f);
      if (sizeI % 2 == 0) {
        return sizeI;
      }  else {
        return sizeI + 1;
      }
    }
  }
   
  /**
   * Selects the lattice size in J direction. 
   * 
   * Can not be directly set from "parameters" file.
   * 
   * @return lattice size in J direction. 
   */
  public int getHexaSizeJ() {
    if (calculationMode.equals("basic") || getCalculationMode().equals("catalysis")) {
      return cartSizeY;
    }
    if (getCalculationMode().equals("AgUc") || getCalculationMode().equals("concerted")) {
      return Math.round(getCartSizeY() / (2 * AbstractGrowthLattice.Y_RATIO));
    } else {
      return (int) (getCartSizeY() / AbstractGrowthLattice.Y_RATIO);
    }
  }

  /**
   * Selects the number of bin levels. Useful only if binned list is selected.
   *
   * Input "parameters" variable: {@code binsLevels}.
   *
   * @return number of levels of the bin.
   */
  public int getBinsLevels() {
    return binsLevels;
  }

  public void setBinsLevels(int binsLevels) {
    this.binsLevels = binsLevels;
  }

  public int getExtraLevels() {
    return extraLevels;
  }
  
  public void setExtraLevels(int extraLevels) {
    this.extraLevels = extraLevels;
  }

  public boolean isMultithreaded() {
    return multithreaded;
  }

  /**
   * If GUI is enabled (see {@link #withGui()}), selects to visualise the GUI.
   *
   * Input "parameters" variable: {@code visualise}.
   *
   * @return whether to show the GUI.
   */
  public boolean visualise() {
    if (withGui()) {
      return visualise;
    } else {
      return false;
    }
  }

  /**
   * Selects to run a single flake simulation (faster and usually with Devita acceleration) or not.
   * If not selected, usually a multi-flake simulation will happen in a periodic simulation box.
   *
   * Input "parameters" variable: {@code justCentralFlake}.
   * 
   * @return whether to simulate just a single flake.
   */
  public boolean justCentralFlake() {
    return justCentralFlake;
  }

  /**
   * Selects to enable all the GUI with its windows.
   *
   * Input "parameters" variable: {@code withGui}.
   *
   * See also {@link #visualise()} to also show the GUI.
   *
   * @return whether to enable GUI.
   */
  public boolean withGui() {
    return withGui;
  }

  /**
   * Selects to print the simulated system (the canvas of the frame) to a PNG file. Can be only be
   * used if GUI is enabled (see {@link #withGui()}).
   *
   * Input "parameters" variable: {@code printToImage}.
   *
   * @return whether to print to a PNG file.
   */
  public boolean printToImage() {
    if (!withGui) {
      return false;
    }
    return printToImage;
  }
  
  /**
   * Selects the type of system to be calculated. Can be Si (for silicon etching), Ag (2D Ag/Ag
   * growth in former mode), AgUc (same as previous Ag/Ag growth, with correct periodicity), basic
   * (synthetic simulation mode in a square lattice), graphene (graphene 2D growth simulation) or
   * Catalysis (CO2 catalysis on RuO2 (110)). For the graphene and catalysis pay attention to the
   * used rates library with {@link #getRatesLibrary()}.
   *
   * The temperature ({@link #getTemperature()}) has to be between 120 and 180 for Ag, between 120
   * and 220 for basic and 1273 for graphene.
   *
   * Input "parameters" variable: {@code calculationMode}.
   *
   * @return calculation mode. Either: "Si", "Ag", "AgUc", "basic", "graphene" or "catalysis"
   */
  public String getCalculationMode() {
    return calculationMode;
  }

  /**
   * This variable can be used for the PSD utility (see {@link #getCalculationType()}) to choose
   * between to do the tents for the surfaces or not.
   *
   * @return surface type. For PSD utility: "tent" or "plane".
   */
  public String getSurfaceType() {
    return surfaceType;
  }
  
  /**
   * Selects to do a PSD analysis of the simulated system. If many repetitions are used (see
   * {@link #getNumberOfSimulations()}), the PSD will be smoother and better defined. I recommend to
   * use {@link #getPsdScale()} == 1 and {@link #getPsdExtend()} == 1.
   *
   * Input "parameters" variable: {@code doPsd}.
   *
   * @return whether to do a PSD.
   */
  public boolean doPsd() {
    return psd;
  }

  /**
   * Returns if the PSD ({@link #doPsd()}) has to be symmetrised. If the periodicity is correctly
   * implemented, the PSD tents to be symmetric circularly. If this option is enabled, helps to that
   * symmetry. Or, the other way around, one can obtain properly defined PSD with less repetitions
   * ({@link #getNumberOfSimulations()})
   *
   * Input "parameters" variable: {@code isPsdSymmetric}.
   *
   * @return psdSymmetry
   */
  public boolean isPsdSymmetric() {
    return psdSymmetry;
  }

  /**
   * Returns if single-flake simulation is periodic. If yes, a simulation size of selected size
   * is created and it will be periodic. If not, a circular region will be created in the selected
   * region and when current atom exits the perimeter it will be inserted by a prefixed statistics.
   *
   * Input "parameters" variable: {@code periodicSingleFlake}.
   * 
   * @return whether periodic in single flake.
   */
  public boolean isPeriodicSingleFlake() {
    return periodicSingleFlake;
  }
  
  /**
   * Chooses to output data or not.
   *
   * Input "parameters" variable: {@code outputData}. It has to be used in combination with
   * {@link #getOutputFormats()} to chose the proper format for the output.
   *
   * @return whether to output data.
   */
  public boolean outputData() {
    return outputData;
  }
  
  /**
   * To have the possibility to output to different file formats. Available options are: TXT, PNG,
   * MKO, EXTRA, EXTRA2, AE (extra information for activation energy runs) or XYZ.
   *
   * Input "parameters" variable: {@code outputDataFormat}.
   * 
   * @return output format. Either: TXT, PNG, MKO, EXTRA, EXTRA2, AE, XYZ or AETOTAL
   */
  public EnumSet<formatFlag> getOutputFormats() {
    return outputType.getStatusFlags(numericFormatCode);
  }
  
  /**
   * How frequently should be printed extra file of catalysis.
   * 
   * @return output frequency.
   */
  public int getOutputEvery() {
    return outputEvery;
  }
  
  /**
   * Selects to use randomSeed, based on the current time in ms.
   *
   * Input "parameters" variable: {@code randomSeed}.
   *
   * @return whether to use random seed.
   */
  public boolean randomSeed() {
    return randomSeed;
  }

  /**
   * In single flake simulation mode ({@link #justCentralFlake()}) selects the initial size of the
   * simulation area. If this option is enabled, biggest possible area is used since the beginning.
   *
   * Input "parameters" variable: {@code useMaxPerimeter}.
   *
   * @return whether to use maximum possible perimeter.
   */
  public boolean useMaxPerimeter() {
    return useMaxPerimeter;
  }
  
  /**
   * If two terraces are together freeze them, in multi-flake
   * simulation mode. Should be only disabled for debugging purposes
   * because slows down significantly the execution time.
   * 
   * Input "parameters" variable: {@code forceNucleation}.
   *
   * @return whether to force nucleation.
   */
  public boolean forceNucleation() {
    return forceNucleation;
  }
  
  /**
   * Devita accelerator makes execution much faster, it may change results a bit. It is tested for
   * single-flake simulations and seems to work fine. Nothing tried yet for multi-flake simulations.
   *
   * Input "parameters" variable: {@code devita}.
   * 
   * @return whether to use Devita accelerator.
   */
  public boolean useDevita() {
    return devita;
  }
  
  /**
   * Adsorb atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisAdsorption}.
   * 
   * @return do adsorption.
   */
  public boolean doCatalysisAdsorption() {
    return !catalysisAdsorption.equals("false");
  }
  
  /**
   * Desorb atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisDesorption}.
   * 
   * @return do desorption.
   */
  public boolean doCatalysisDesorption() {
    return !catalysisDesorption.equals("false");
  }
  
  /**
   * Print all iterations during catalysis simulation.
   *
   * Input "parameters" variable: {@code printAllIterations}.
   * 
   * @return print all iterations.
   */
  public boolean doPrintAllIterations() {
    return printAllIterations;
  }
  
  /**
   * Allow to react atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisReaction}.
   * 
   * @return do reaction.
   */
  public boolean doCatalysisReaction() {
    return !catalysisReaction.equals("false");
  }
  
  /**
   * Diffuse atom during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisDiffusion}.
   * 
   * @return do diffusion.
   */
  public boolean doCatalysisDiffusion() {
    return !catalysisDiffusion.equals("false");
  }
  
  private void computeTree() {
    catalysisTree = new boolean[4];
    catalysisTree[ADSORPTION] = catalysisAdsorption.equals("tree");
    catalysisTree[DESORPTION] = catalysisDesorption.equals("tree");
    catalysisTree[REACTION] = catalysisReaction.equals("tree");
    catalysisTree[DIFFUSION] = catalysisDiffusion.equals("tree");
  }

  /**
   * Instead of writing "true", one can use the word "tree" to use a tree to store catalysis
 processes.
   * @param process
   * @return
   */
  public boolean useCatalysisTree(byte process) {
    if (catalysisTree == null) 
      computeTree();
    return catalysisTree[process];
  }
  
  /**
   * Starts a catalysis run given coverage. Can be "O" (Oxygen covered), "CO" (CO covered), "empty"
   * or "random" (randomly covered surface).
   *
   * Input "parameters" variable: {@code catalysisStart}.
   *
   * @return do diffusion.
   */
  public String catalysisStart() {
    return catalysisStart;
  }
  
  /**
   * Dissociates O2 atoms during catalysis simulation
   *
   * Input "parameters" variable: {@code catalysisO2Dissociation}.
   * 
   * @return do O2 dissociation.
   */
  public boolean doCatalysisO2Dissociation() {
    return catalysisO2Dissociation;
  }
  
  /**
   * Allow to change between tree and array collections, when the code decides to do so.
   *
   * Input "parameters" variable: {@code automaticCollections}.
   *
   * @return "automatically" change between tree/array.
   */
  public boolean areCollectionsAutomatic() {
    return automaticCollections;
  }
  
  /**
   * In concerted calculation mode, allow to island to move.
   * 
   * Input "parameter" variable: {@code doIslandDiffusion}.
   * 
   * @return island moving
   */
  public boolean doIslandDiffusion() {
    return doIslandDiffusion;
  }
  
  /**
   * In concerted calculation mode, allow to atoms on the edge to move. For example, two atoms can diffuse together.
   * 
   * Input "parameter" variable: {@code doMultiAtomDiffusion}.
   * 
   * @return multi-atom moving
   */
  public boolean doMultiAtomDiffusion() {
    return doMultiAtomDiffusion;
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
  
  /**
   * Total number of iterations that the evolutionary algorithm has to do.
   *
   * @return total number of iterations.
   */
  public int getTotalIterations() {
    return totalIterations;
  }
  
  /**
   * Number of repetitions or evaluations that a single Gene has to do.
   *
   * @return by default 18.
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
