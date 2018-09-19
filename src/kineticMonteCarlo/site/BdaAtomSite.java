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
package kineticMonteCarlo.site;

import java.util.List;
import javafx.geometry.Point3D;

/**
 * This object is used to represent each of the atoms of the BDA molecule.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaAtomSite extends AbstractGrowthSite {
  /**
   * Cardinal position within unit cell.
   */
  private final int pos;
  private Point3D cartPos;
  /** Alpha, Beta (1 or 2), Gamma or Delta. */
  private byte type;


  public BdaAtomSite(int id, int pos, Point3D cartPos) {
    super(id, (short) -1, (short) -1, 0, -1);
    this.pos = pos;
    this.cartPos = cartPos;
  }

  /**
   * Cartesian X,Y,Z position within unit cell.
   *
   * @return X,Y,Z coordinates in unit cell
   */
  @Override
  public Point3D getPos() {
    return cartPos;
  }

  @Override
  public byte getTypeWithoutNeighbour(int posNeighbour) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean areTwoTerracesTogether() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthSite chooseRandomHop() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getOrientation() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void obtainRateFromNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double probJumpToNeighbour(int originType, int position) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List getAllNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double updateOneBound(int bond) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isPartOfImmobilSubstrate() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public BdaAtomSite getNeighbour(int pos) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite m, int pos) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
