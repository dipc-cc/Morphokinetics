/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.kmcCore.catalysis;

import basic.Parser;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;

/**
 *
 * @author karmele
 */
public abstract class CatalysisKmc extends AbstractGrowthKmc {

  public CatalysisKmc(Parser parser) {
    super(parser);
  }
}