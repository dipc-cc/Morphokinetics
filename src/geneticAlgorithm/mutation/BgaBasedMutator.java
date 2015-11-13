/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.mutation;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class BgaBasedMutator implements IMutation {

  /** mutation range */
  private final float r;
  /** mutation precision */
  private final float k;
  /** mutation rate as percentage */
  private final float mutRate;
  
  public BgaBasedMutator() {
    this.r = /*0.1f*/ 1f;
    this.k = 7;
    this.mutRate = 0.5f;
  }

  @Override
  public void mutate(Population p, List nonFixedGenes) {

    for (int ind = 0; ind < p.size(); ind++) {

      Individual child = p.getIndividual(ind);

      int mutations = (int) Math.round(utils.StaticRandom.raw() * nonFixedGenes.size() * mutRate);

      for (int m = 0; m < mutations; m++) {

        int posList = (int) (utils.StaticRandom.raw() * nonFixedGenes.size());
        int pos = (Integer) nonFixedGenes.get(posList);

        double a = (Math.pow(2, utils.StaticRandom.raw() * k) / Math.pow(2, k));
        double s = utils.StaticRandom.raw() * 2 - 1;
        double oldValue = child.getGene(pos);
        double newValue = oldValue + s * r * a * oldValue;

        //System.out.println(pos+","+oldValue+" "+(newValue));
        child.setGene(pos, newValue);

      }

    }

  }
}
