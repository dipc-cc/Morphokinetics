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
package kineticMonteCarlo.atom;

import javafx.geometry.Point3D;

/**
 * This interface defines the minimum methods that an atom class have to have.
 * 
 * @author J. Alberdi-Rodriguez
 */
public interface ISite {
    
  public void setProbabilities(double[] probabilities);

  public double[] getProbabilities();

  public void setList(Boolean list);

  public boolean isOnList();

  public double getProbability();

  public boolean isEligible();

  public boolean isRemoved();
  
  public boolean isOccupied();

  public void unRemove();
  
  public void setRemoved();
  
  public double remove();
  
  public byte getType();
  
  public byte getRealType();

  public int getNumberOfNeighbours();

  public void setNumberOfNeighbours(int numberOfNeighbours);

  public void setNeighbour(AbstractSite atom, int i);
  
  public Point3D getPos();
  
}
