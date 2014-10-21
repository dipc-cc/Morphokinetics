/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator;

import Kinetic_Monte_Carlo.KMC_core.worker.IFinish_listener;
import Kinetic_Monte_Carlo.KMC_core.worker.IInterval_listener;
import Kinetic_Monte_Carlo.KMC_core.worker.KMC_worker;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nestor
 */
public abstract class Multithreaded_PSD_Evaluation extends AbstractPSDEvaluation implements IFinish_listener, IInterval_listener {

    protected KMC_worker[] workers;
    protected int num_threads;
    protected int finishedSimulation;
    protected Semaphore evalation_complete;

    
    public Multithreaded_PSD_Evaluation(int repeats, int measureInterval,int num_threads) {
        super(repeats, measureInterval);
        
        this.workers=new KMC_worker[num_threads];
        this.num_threads=num_threads;
        evalation_complete = new Semaphore(0);
    }
    
    @Override
    public synchronized void handleSimulationFinish(int workerID, int workID) {
        
        finishedSimulation++;
        if (currentSimulation < currentPopulation.size() * repeats) {
            assignNewWork(workerID);
        }

        if (finishedSimulation == currentPopulation.size() * repeats) {
            evalation_complete.release();
        }
    }
    
    
    protected void assignNewWork(int workerID) {
               
        int individual = currentSimulation / repeats;
        
        workers[workerID].initialize(currentPopulation.getIndividual(individual).getGenes());
        workers[workerID].simulate(this, this, measureInterval, individual);
        currentSimulation++;  
    }
    
    public void dispose(){
    
    for (int i=0;i<workers.length;i++) workers[i].destroy();
    
    }
    
    

}
