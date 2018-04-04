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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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
  
  private final Map<String, String> mapString;
  private final Map<String, Double> mapDouble;
  private final Map<String, Integer> mapInt;
  private final Map<String, Boolean> mapBoolean;

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
  private long numericStatusCode;
  private JSONArray evaluator;
  /** To have the possibility to choose between different evaluators. For the moment only PSD, TIME
   * and HIERARCHY.
   */
  private final EvaluatorType evaluatorType;
  
  /**
   * Constructor.
   */
  public Parser() {
    mapString = new LinkedHashMap<>(); // Insertion order is preserved
    mapString.put("calculationType", "batch");
    mapString.put("ratesLibrary", "COX_PRB");
    mapString.put("listType", "linear");
    mapString.put("perimeterType", "circle");
    mapString.put("calculationMode", "Ag");
    mapString.put("surfaceType", "cartesian");
    mapString.put("catalysisAdsorption", "true");
    mapString.put("catalysisDesorption", "true");
    mapString.put("catalysisReaction", "true");
    mapString.put("catalysisDiffusion", "true");
    mapString.put("catalysisStart", "O");
    mapDouble = new LinkedHashMap<>();
    mapDouble.put("temperature", 135.0);
    mapDouble.put("pressureO2", 1.0);
    mapDouble.put("pressureCO", 7.0);
    mapDouble.put("depositionFlux", 0.0035);
    mapDouble.put("coverage", 30.0);
    mapDouble.put("psdScale", 1.0);
    mapDouble.put("psdExtend", 1.0);
    mapDouble.put("endTime", -1.0);
    mapDouble.put("numberOfSteps", -1.0);
    mapInt = new LinkedHashMap<>();
    mapInt.put("numberOfSimulations", 10);
    mapInt.put("numberOfCo2", -1);
    mapInt.put("cartSizeX", 256);
    mapInt.put("cartSizeY", 256);
    mapInt.put("cartSizeZ", 256);
    mapInt.put("millerX", 0);
    mapInt.put("millerY", 1);
    mapInt.put("millerZ", 1);
    mapInt.put("binsLevels", 100);
    mapInt.put("extraLevels", 0);
    mapInt.put("outputEvery", 10);
    mapBoolean = new LinkedHashMap<>();
    mapBoolean.put("multithreaded", true);
    mapBoolean.put("visualise", true);
    mapBoolean.put("withGui", true);
    mapBoolean.put("justCentralFlake", true);
    mapBoolean.put("printToImage", false);
    mapBoolean.put("psd", false);
    mapBoolean.put("psdSymmetry", true);
    mapBoolean.put("outputData", false);
    mapBoolean.put("randomSeed", true);
    mapBoolean.put("useMaxPerimeter", false);
    mapBoolean.put("periodicSingleFlake", false);
    mapBoolean.put("forceNucleation", true);
    mapBoolean.put("devita", true);
    mapBoolean.put("printAllIterations", false);
    mapBoolean.put("catalysisO2Dissociation", true);
    mapBoolean.put("automaticCollections", false);
    mapBoolean.put("doIslandDiffusion", true);
    mapBoolean.put("doMultiAtomDiffusion", true);
    outputType = new OutputType();
    numericFormatCode = 2;
    
    mapString.put("evolutionaryAlgorithm", "original");
    mapString.put("hierarchyEvaluator", "basic");
    mapString.put("evolutionarySearchType", "rates");
    mapDouble.put("stopError", 0.022);
    mapDouble.put("minValueGene", 0.1);
    mapDouble.put("maxValueGene", 1e11);
    mapDouble.put("goMultiplier", 1.0);
    mapInt.put("populationSize", 5);
    mapInt.put("offspringSize", 32);
    mapInt.put("populationReplacement", 5);
    mapInt.put("totalIterations", 100);
    mapInt.put("repetitions", 18);
    mapInt.put("numericStatusCode", 3);
    mapBoolean.put("expDistribution", true);
    mapBoolean.put("parallelEvaluator", false);
    mapBoolean.put("readReference", true);
    mapBoolean.put("fixDiffusion", true);
    evaluatorType = new EvaluatorType();
    
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
    
    Set<String> keys = new HashSet<>(mapString.keySet()); // copy the keys
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        mapString.put(key, json.getString(key));
      } catch (JSONException e) {
      }
    }
    keys = new HashSet<>(mapDouble.keySet()); // copy the keys
    iter = keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        mapDouble.put(key, json.getDouble(key));
      } catch (JSONException e) {
      }
    }
    keys = new HashSet<>(mapInt.keySet()); // copy the keys
    iter = keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        mapInt.put(key, json.getInt(key));
      } catch (JSONException e) {
      }
    }
    keys = new HashSet<>(mapBoolean.keySet()); // copy the keys
    iter = keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        mapBoolean.put(key, json.getBoolean(key));
      } catch (JSONException e) {
      }
    }
    
    try {
      mapDouble.put("depositionFlux", json.getDouble("depositionFlux"));
    } catch (JSONException e) {
      if (getCalculationMode().equals("Ag") || getCalculationMode().equals("AgUc")) {
        mapDouble.put("depositionFlux", 0.0035);
      } else { // Graphene (or etching, where it does not matter the deposition
        mapDouble.put("depositionFlux", 0.000035);
      }
    }

    try {
      mapBoolean.put("devita", json.getBoolean("devita"));
    } catch (JSONException e) {
      mapBoolean.put("devita", mapBoolean.get("justCentralFlake")); // By default Devita works with single-flake simulations only
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
    return 0;
  }

  /**
   * Prints all the parameters; either read from "parameter" file or the default value.
   */
  public void print() throws JSONException {
    for (String key : mapString.keySet()) {
      System.out.printf("%32s: %s,\n", "\"" + key + "\"", "\"" + mapString.get(key) + "\"");
    }
    for (String key : mapDouble.keySet()) {
      System.out.printf("%32s: %s,\n", "\"" + key + "\"", mapDouble.get(key));
    }
    for (String key : mapInt.keySet()) {
      System.out.printf("%32s: %s,\n", "\"" + key + "\"", mapInt.get(key));
    }
    for (String key : mapBoolean.keySet()) {
      System.out.printf("%32s: %s,\n", "\"" + key + "\"", mapBoolean.get(key));
    }
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
    return mapString.get("calculationType");
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
    return mapString.get("ratesLibrary");
  }

  /**
   * Selects the internal list type to be used between linear and binned.
   *
   * Input "parameters" variable: {@code listType}.
   *
   * @return "linear" or "binned".
   */
  public String getListType() {
    return mapString.get("listType");
  }

  /**
   * Sets the internal list type to be used. Should be linear or binned.
   *
   * @param listType linear or binned.
   */
  public void setListType(String listType) {
    mapString.put("listType", listType);
  }

  /**
   * Input "parameters" variable: {@code perimeterType}.
   *
   * @return properly formated "SQUARE" or "CIRCLE" (default option).
   */
  public short getPerimeterType() {
    switch (mapString.get("perimeterType")) {
      case "square":
        return RoundPerimeter.SQUARE;
      case "circle":
      default:
        return RoundPerimeter.CIRCLE;
    }
  }

  public void setTemperature(float temperature) {
    mapDouble.put("temperature", (double) temperature);
  }
  
  /**
   * Simulation temperature.
   * 
   * Input "parameters" variable: {@code temperature}.
   * 
   * @return temperature
   */
  public float getTemperature() {
    return new Float(mapDouble.get("temperature"));
  }

  /**
   * Partial pressure for O2 in atm.
   * 
   * @return O2 pressure.
   */
  public double getPressureO2() {
    return mapDouble.get("pressureO2");
  }

  /**
   * GO multiplier for plotting resolution problem solving.
   * 
   * @return GO multiplier.
   */
  public double getGOMultiplier() {
    return mapDouble.get("goMultiplier");
  }
  
  /**
   * Partial pressure for CO in atm.
   * 
   * @return CO pressure.
   */
  public double getPressureCO() {
    return mapDouble.get("pressureCO");
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
    return mapDouble.get("depositionFlux");
  }

  /**
   * Returns the maximum simulation time that each run has to do or -1 if there is no time limit.
   *
   * Input "parameters" variable: {@code endTime}.
   *
   * @return ending time of simulation or -1 (no time limit).
   */
  public double getEndTime() {
    return mapDouble.get("endTime");
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
    return mapDouble.get("coverage");
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
    return mapDouble.get("psdScale");
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
  public double getPsdExtend() {
    return mapDouble.get("psdExtend");
  }
  
  public void setNumberOfSimulations(int numberOfSimulations){
    mapInt.put("numberOfSimulations", numberOfSimulations);
  }
  
  /**
   * Selects the number of simulations that has to be done with the same parameters.
   * 
   * Input "parameters" variable: {@code numberOfSimulations}.
   * 
   * @return number of simulations.
   */
  public int getNumberOfSimulations() {
    return mapInt.get("numberOfSimulations");
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
    double steps = mapDouble.get("numberOfSteps");
    return (long) steps;
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
    return mapInt.get("numberOfCo2");
  }

  /**
   * Selects the Cartesian size in X direction. 
   * 
   * Input "parameters" variable: {@code cartSizeX}.
   * 
   * @return Cartesian size in X direction. 
   */
  public int getCartSizeX() {
    return mapInt.get("cartSizeX");
  }

  public void setCartSizeX(int sizeX) {
    mapInt.put("cartSizeX", sizeX);
  }
  
  /**
   * Selects the Cartesian size in Y direction. 
   * 
   * Input "parameters" variable: {@code cartSizeY}.
   * 
   * @return Cartesian size in Y direction. 
   */
  public int getCartSizeY() {
    return mapInt.get("cartSizeY");
  }
    
  public void setCartSizeY(int sizeY) {
    mapInt.put("cartSizeY", sizeY);
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
    return mapInt.get("cartSizeZ");
  }
  
  public void setCartSizeZ(int sizeZ) {
    mapInt.put("cartSizeZ", sizeZ);
  }
  
  public int getMillerX() {
    return mapInt.get("millerX");
  }

  public void setMillerX(int sizeX) {
    mapInt.put("millerX", sizeX);
  }
  
  public int getMillerY() {
    return mapInt.get("millerY");
  }
    
  public void setMillerY(int sizeY) {
   mapInt.put("millerY", sizeY);
  }
  
  public int getMillerZ() {
    return mapInt.get("millerZ");
  }
  
  public void setMillerZ(int sizeZ) {
    mapInt.put("millerZ", sizeZ);
  }  
  
  /**
   * Selects the lattice size in I direction. 
   * 
   * Can not be directly set from "parameters" file.
   * 
   * @return lattice size in I direction. 
   */
  public int getHexaSizeI() {
    int cartSizeX = mapInt.get("cartSizeX");
    if (getCalculationMode().equals("basic") || getCalculationMode().equals("catalysis") || getCalculationMode().equals("ammonia")) {
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
    if (getCalculationMode().equals("basic") || getCalculationMode().equals("catalysis") || getCalculationMode().equals("ammonia")) {
      return mapInt.get("cartSizeY");
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
    return mapInt.get("binsLevels");
  }

  public void setBinsLevels(int binsLevels) {
    mapInt.put("binsLevels", binsLevels);
  }

  public int getExtraLevels() {
    return mapInt.get("extraLevels");
  }
  
  public void setExtraLevels(int extraLevels) {
    mapInt.put("extraLevels", extraLevels);
  }

  public boolean isMultithreaded() {
    return mapBoolean.get("multithreaded");
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
      return mapBoolean.get("visualise");
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
    return mapBoolean.get("justCentralFlake");
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
    return mapBoolean.get("withGui");
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
    if (!withGui()) {
      return false;
    }
    return mapBoolean.get("printToImage");
  }
  
  /**
   * Selects the type of system to be calculated. Can be Si (for silicon etching), Ag (2D Ag/Ag
   * growth in former mode), AgUc (same as previous Ag/Ag growth, with correct periodicity), basic
   * (synthetic simulation mode in a square lattice), graphene (graphene 2D growth simulation),
   * Catalysis (CO2 catalysis on RuO2 (110)) or Ammonia (Oxidation of ammonia on RuO2(110). For the
   * graphene and catalysis pay attention to the used rates library with {@link #getRatesLibrary()}.
   *
   * The temperature ({@link #getTemperature()}) has to be between 120 and 180 for Ag, between 120
   * and 220 for basic and 1273 for graphene.
   *
   * Input "parameters" variable: {@code calculationMode}.
   *
   * @return calculation mode. Either: "Si", "Ag", "AgUc", "basic", "graphene", "catalysis" or "ammonia".
   */
  public String getCalculationMode() {
    return mapString.get("calculationMode");
  }

  /**
   * This variable can be used for the PSD utility (see {@link #getCalculationType()}) to choose
   * between to do the tents for the surfaces or not.
   *
   * @return surface type. For PSD utility: "tent" or "plane".
   */
  public String getSurfaceType() {
    return mapString.get("surfaceType");
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
    return mapBoolean.get("psd");
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
    return mapBoolean.get("psdSymmetry");
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
    return mapBoolean.get("periodicSingleFlake");
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
    return mapBoolean.get("outputData");
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
    return mapInt.get("outputEvery");
  }
  
  /**
   * Selects to use randomSeed, based on the current time in ms.
   *
   * Input "parameters" variable: {@code randomSeed}.
   *
   * @return whether to use random seed.
   */
  public boolean randomSeed() {
    return mapBoolean.get("randomSeed");
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
    return mapBoolean.get("useMaxPerimeter");
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
    return mapBoolean.get("forceNucleation");
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
    return mapBoolean.get("devita");
  }
  
  /**
   * Print all iterations during catalysis simulation.
   *
   * Input "parameters" variable: {@code printAllIterations}.
   * 
   * @return print all iterations.
   */
  public boolean doPrintAllIterations() {
    return mapBoolean.get("printAllIterations");
  }
  
  /**
   * Adsorb atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisAdsorption}.
   * 
   * @return do adsorption.
   */
  public boolean doCatalysisAdsorption() {
    return !mapString.get("catalysisAdsorption").equals("false");
  }
  
  /**
   * Desorb atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisDesorption}.
   * 
   * @return do desorption.
   */
  public boolean doCatalysisDesorption() {
    return !mapString.get("catalysisDesorption").equals("false");
  }
  
  /**
   * Allow to react atoms during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisReaction}.
   * 
   * @return do reaction.
   */
  public boolean doCatalysisReaction() {
    return !mapString.get("catalysisReaction").equals("false");
  }
  
  /**
   * Diffuse atom during catalysis simulation.
   *
   * Input "parameters" variable: {@code catalysisDiffusion}.
   * 
   * @return do diffusion.
   */
  public boolean doCatalysisDiffusion() {
    return !mapString.get("catalysisDiffusion").equals("false");
  }
  
  private void computeTree() {
    catalysisTree = new boolean[4];
    catalysisTree[ADSORPTION] = mapString.get("catalysisAdsorption").equals("tree");
    catalysisTree[DESORPTION] = mapString.get("catalysisDesorption").equals("tree");
    catalysisTree[REACTION] = mapString.get("catalysisReaction").equals("tree");
    catalysisTree[DIFFUSION] = mapString.get("catalysisDiffusion").equals("tree");
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
    return mapString.get("catalysisStart");
  }
  
  /**
   * Dissociates O2 atoms during catalysis simulation
   *
   * Input "parameters" variable: {@code catalysisO2Dissociation}.
   * 
   * @return do O2 dissociation.
   */
  public boolean doCatalysisO2Dissociation() {
    return mapBoolean.get("catalysisO2Dissociation");
  }
  
  /**
   * Allow to change between tree and array collections, when the code decides to do so.
   *
   * Input "parameters" variable: {@code automaticCollections}.
   *
   * @return "automatically" change between tree/array.
   */
  public boolean areCollectionsAutomatic() {
    return mapBoolean.get("automaticCollections");
  }
  
  /**
   * In concerted calculation mode, allow to island to move.
   * 
   * Input "parameter" variable: {@code doIslandDiffusion}.
   * 
   * @return island moving
   */
  public boolean doIslandDiffusion() {
    return mapBoolean.get("doIslandDiffusion");
  }
  
  /**
   * In concerted calculation mode, allow to atoms on the edge to move. For example, two atoms can diffuse together.
   * 
   * Input "parameter" variable: {@code doMultiAtomDiffusion}.
   * 
   * @return multi-atom moving
   */
  public boolean doMultiAtomDiffusion() {
    return mapBoolean.get("doMultiAtomDiffusion");
  }
  
  public String getEvolutionaryAlgorithm() {
    return mapString.get("evolutionaryAlgorithm");
  }
  
  public boolean isEvaluatorParallel() {
    return mapBoolean.get("parallelEvaluator");
  }
  
  public int getPopulationSize() {
    return mapInt.get("populationSize");
  }
  
  public int getOffspringSize() {
    return mapInt.get("offspringSize");
  }
  
  public int getPopulationReplacement() {
    return mapInt.get("populationReplacement");
  }
  
  /**
   * Total number of iterations that the evolutionary algorithm has to do.
   *
   * @return total number of iterations.
   */
  public int getTotalIterations() {
    return mapInt.get("totalIterations");
  }
  
  /**
   * Number of repetitions or evaluations that a single Gene has to do.
   *
   * @return by default 18.
   */
  public int getRepetitions() {
    return mapInt.get("repetitions");
  }
  
  /**
   * Chooses between to read a reference PSD or doing an initial run to be the objective for the
   * Evolutionary run
   *
   * @return read reference?
   */
  public boolean getReadReference() {
    return mapBoolean.get("readReference");
  }
          
  public void setCalculationMode(String mode) {
    mapString.put("calculationMode", mode);
  }
  
  public void setPopulation(int populationSize) {
    mapInt.put("populationSize", populationSize);
  }
  
  public void setEvolutionaryAlgorithm(String name) {
    mapString.put("evolutionaryAlgorithm", name);
  }
  
  /**
   * For evolutionary algorithm run mode target minimum error
   * @return minimum error
   */
  public double getStopError() {
    return mapDouble.get("stopError");
  }

  /**
   * For evolutionary algorithm, minimum possible value of a gene.
   *
   * @return minimum error
   */
  public double getMinValueGene() {
    return mapDouble.get("minValueGene");
  }  
  
  /**
   * For evolutionary algorithm, maximum possible value of a gene.
   *
   * @return minimum error
   */
  public double getMaxValueGene() {
    return mapDouble.get("maxValueGene");
  } 
  
  /**
   * Chooses between exponential distribution of the random genes (true) or linear distribution (false)
   * 
   * @return true exponential distribution; false linear
   */
  public boolean isExpDistribution() {
    return mapBoolean.get("expDistribution");
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
    return mapString.get("hierarchyEvaluator");
  }
    
  public String getEvolutionarySearchType() {
    String type = mapString.get("evolutionarySearchType");
    if (type.equals("rates") || type.equals("energies")) {
      return type;
    } else {
      System.out.println("Not valid search type. It must be \"rates\" of \"energies\".");
      return null;
    }
  }
  
  public boolean isEnergySearch() {
    return getEvolutionarySearchType().equals("energies");
  }
  
  /**
   * Decides if diffusion must be fixed.
   *
   * @return true if fixed, false otherwise
   */
  public boolean isDiffusionFixed() {
    return mapBoolean.get("fixDiffusion");
  }
}
