/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.kmcCore.growth;

import java.util.List;
import java.util.TreeMap;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.lattice.perimeterStatistics.AbstractPerimeterStatistics;
import kineticMonteCarlo.lattice.perimeterStatistics.PerimeterStatisticsFactory;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class RoundPerimeter {

  public final static short CIRCLE = 0;
  public final static short SQUARE = 1;

  /**
   * Current size of the radius (in units).
   */
  private int currentRadius;
  /**
   * Atoms vector belonging to the perimeter.
   */
  private List<AbstractGrowthSite> currentPerimeter;
  private TreeMap<Integer, AbstractGrowthSite> currentPerimeterTreeMap;
  private AbstractPerimeterStatistics perimeterStatistics;
  private short type;
  private int neededSteps;

  public RoundPerimeter(String statisticData) {
    perimeterStatistics = new PerimeterStatisticsFactory(statisticData).getStatistics();
    currentRadius = perimeterStatistics.getMinRadiusInSize();
    currentPerimeter = null;
  }
  
  public RoundPerimeter(String statisticData, short type){
   this(statisticData);
   this.type = type;
  }
  
  public int getNeededSteps() {
    return neededSteps;
  }
  
  public short getType() {
    return type;
  }

  /**
   * 
   * @return current size of the radius (in units).
   */
  public int getCurrentRadius() {
    return currentRadius;
  }

  public void setMinRadius() {
    currentRadius = perimeterStatistics.getMinRadiusInSize();
  }
  
  /**
   * 
   * @return atoms vector belonging to the perimeter.
   */
  public List<AbstractGrowthSite> getCurrentPerimeter(){
    return currentPerimeter;
  }
  
  public void setCurrentPerimeter(List<AbstractGrowthSite> perimeter) {
    currentPerimeter = perimeter;
    currentPerimeterTreeMap = new TreeMap<>();
    perimeter.stream().forEach(atom -> currentPerimeterTreeMap.put(atom.getId(), atom));
  }
  
  public void setMaxPerimeter(float sizeX, float sizeY) {
    currentRadius = (int) Math.max(sizeX, sizeY) / 2;
  }
  
  public boolean contains(AbstractGrowthSite atom) {
    if (currentPerimeterTreeMap != null) {
      return currentPerimeterTreeMap.containsKey(atom.getId());
    } else {
      return false;
    }
  }

  /**
   * Enlarge the perimeter; i.e. go to the next radius in the perimeter.
   * 
   * @return the new value of the perimeter.
   */
  public int goToNextRadius() {
    currentRadius = perimeterStatistics.getNextRadiusInSize(currentRadius);
    return currentRadius;
  }

  public AbstractGrowthSite getPerimeterReentrance(AbstractGrowthSite originAtom) {

    int angle = searchPerimeterOffsetReentrance();
    neededSteps = perimeterStatistics.getHopsCount(currentRadius, angle);
    
    /* It randomly turns */
    if (utils.StaticRandom.raw() < 0.5) {
      angle = 360 - angle;
    }

    float destinationAngleDegree = (float) (angle + (originAtom.getAngle() * 180.0 / Math.PI));
    while (destinationAngleDegree >= 360) {
      destinationAngleDegree = destinationAngleDegree - 360;
    }

    int initialLocation = (int) (destinationAngleDegree * currentPerimeter.size() / 360.0);
    double destinationAngleRad = destinationAngleDegree * Math.PI / 180.0f;

    AbstractGrowthSite destinationAtom = null;
    int position = 0;
    double error = currentPerimeter.get(initialLocation).getAngle() - destinationAngleRad;

    if (error > 0) {

      for (int j = initialLocation - 1; j >= 0; j--) {
        double errorTemp = currentPerimeter.get(j).getAngle() - destinationAngleRad;
        if (Math.abs(errorTemp) < Math.abs(error)) {
          error = errorTemp;
        } else {
          destinationAtom = currentPerimeter.get(j + 1);
          position = j + 1;
          break;
        }
      }
      if (destinationAtom == null) {
        destinationAtom = currentPerimeter.get(0);
        position = 0;
      }
    } else {

      for (int j = initialLocation + 1; j < currentPerimeter.size(); j++) {
        double errorTemp = currentPerimeter.get(j).getAngle() - destinationAngleRad;
        if (Math.abs(errorTemp) < Math.abs(error)) {
          error = errorTemp;
        } else {
          destinationAtom = currentPerimeter.get(j - 1);
          position = j - 1;
          break;
        }
      }

      if (destinationAtom == null) {
        destinationAtom = currentPerimeter.get(currentPerimeter.size() - 1);
        position = currentPerimeter.size() - 1;
      }
    }

    while (destinationAtom.isOccupied() || destinationAtom.equals(originAtom)) {
      position++;
      if (position == currentPerimeter.size()) {
        position = 0;
      }
      destinationAtom = currentPerimeter.get(position);
    }

    destinationAtom.setMultiplier(neededSteps /*+((radius_por_paso[0]-1)*(radius_por_paso[0]-1))*/);
    return destinationAtom;

  }

  public AbstractGrowthSite getRandomPerimeterAtom() {
    return currentPerimeter.get(utils.StaticRandom.rawInteger(currentPerimeter.size()));
  }

  private int searchPerimeterOffsetReentrance() {
    int linearSearch = (int) (perimeterStatistics.getTotalCount(currentRadius) * StaticRandom.raw());
    int actualCount = 0;
    int angle = 0;

    for (; angle < 179; angle++) {
      actualCount += perimeterStatistics.getReentranceCount(currentRadius, angle);
      if (linearSearch <= actualCount) {
        break;
      }
    }

    return angle;
  }
}
