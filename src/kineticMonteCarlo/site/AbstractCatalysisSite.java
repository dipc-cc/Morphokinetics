/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.site;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public abstract class AbstractCatalysisSite extends AbstractSurfaceSite {

  public static final byte BR = 0;
  public static final byte CUS = 1;

  private final AbstractCatalysisSite[] neighbours = new AbstractCatalysisSite[4];
  /**
   * Bridge or CUS.
   */
  private final byte latticeSite;
  
  private CatalysisSiteAttributes attributes;
  
  public AbstractCatalysisSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 4);
    if (iHexa % 2 == 0) {
      latticeSite = BR;
    } else {
      latticeSite = CUS;
    }
  }
  
  @Override
  public GrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  @Override
  public void setAttributes(GrowthAtomAttributes attributes) {
    this.attributes = (CatalysisSiteAttributes) attributes;
  }
  
  public byte getLatticeSite() {
    return latticeSite;
  }
  
  /**
   * Tells when current atom is completely surrounded.
   * 
   * @return true if it has 4 occupied neighbours.
   */
  @Override
  public boolean isIsolated() {
    return getOccupiedNeighbours() == 4;
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite a, int pos) {
    neighbours[pos] = (AbstractCatalysisSite) a;
  }

  @Override
  public AbstractCatalysisSite getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  @Override
  public AbstractCatalysisSite getRandomNeighbour(byte process) {
    return (AbstractCatalysisSite) super.getRandomNeighbour(process);
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
  }
  
  @Override
  public void swapAttributes(AbstractSurfaceSite a) {
    AbstractCatalysisSite atom = (AbstractCatalysisSite) a;
    CatalysisSiteAttributes tmpAttributes = this.attributes;
    this.attributes = (CatalysisSiteAttributes) atom.getAttributes();
    this.attributes.addOneHop();
    atom.setAttributes(tmpAttributes);
  }
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId();
    return returnString;
  }

  @Override
  public double getProbability() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  public abstract void cleanCoCusNeighbours();
  
  public int getCoCusNeighbours() {
    return 0;
  }
  public abstract void addCoCusNeighbours(int value);
}
