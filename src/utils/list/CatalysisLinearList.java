/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list;

import basic.Parser;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisLinearList extends LinearList {
  
  public static final byte ADSORPTION = 0;
  public static final byte DESORPTION = 1;
  public static final byte REACTION = 2;
  public static final byte DIFFUSION = 3;
  
  public CatalysisLinearList(Parser parser) {
    super(parser);
  }

  /**
   * Equivalent to nextEvent.
   *
   * @return next reaction type that should be executed.
   */
  public byte nextReaction() {
    double position = StaticRandom.raw() * (getTotalProbability() + getDepositionProbability()
            + getDesorptionProbability() + getReactionProbability());

    addTime();

    if (position < getDepositionProbability()) {
      return ADSORPTION;
    }
    if (position < getDepositionProbability() + getDesorptionProbability()) {
      return DESORPTION;
    }
    if (position < getDepositionProbability() + getDesorptionProbability() + getReactionProbability()) {
      return REACTION;
    }
    return DIFFUSION;
  }
}
