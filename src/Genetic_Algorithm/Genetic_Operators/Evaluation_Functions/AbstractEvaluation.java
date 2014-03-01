/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Evaluation_Functions;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEvaluation implements IEvaluation {
    
    protected double wheight;
    protected boolean showGraphics;
    
    
    public AbstractEvaluation(){
        this.wheight=1.0;
        this.showGraphics=false;}
    
    public double getWheight()                  {return wheight;}
          
    public AbstractEvaluation setShowGraphics(boolean showGraphics){
        this.showGraphics=showGraphics; 
        return this;}
    
    public AbstractEvaluation setWheight(float wheight) {
        this.wheight=wheight;
        return this;}
    
    public abstract void dispose();
     
}
