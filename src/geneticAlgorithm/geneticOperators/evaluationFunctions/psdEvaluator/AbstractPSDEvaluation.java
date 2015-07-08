/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.PSD_analysis.PSD_signature_2D;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPSDEvaluation extends AbstractEvaluation {

    protected int repeats;
    protected float[][] experimentalPSD;
    protected int measureInterval;
    protected Population currentPopulation;
    protected int currentSimulation;

    public AbstractPSDEvaluation(int repeats, int measureInterval) {
        super();
        this.repeats = repeats;
        this.measureInterval = measureInterval;

    }

    public AbstractPSDEvaluation setPSD(float[][] ExperimentalPSD) {
        this.experimentalPSD = ExperimentalPSD;
        return this;
    }

    public abstract float[][] calculate_PSD_from_individual(Individual i);

    protected void calculateRelativeDifference(float[][] difference, PSD_signature_2D PSD) {


        for (int a = 0; a < difference.length; a++) {
            for (int b = 0; b < difference[0].length; b++) {
                
                difference[a][b] = (PSD.getPSD()[a][b] - experimentalPSD[a][b]) / Math.min(experimentalPSD[a][b], PSD.getPSD()[a][b]);
            }
        }


    }

    @Override
    public float getProgressPercent() {
        if (currentPopulation != null) {
            return currentSimulation * 100.0f / (repeats * currentPopulation.size());
        } else {
            return 0;
        }
    }

    
    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }
    
    
    
    
}
