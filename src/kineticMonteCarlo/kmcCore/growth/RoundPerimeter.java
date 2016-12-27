/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import java.util.List;
import java.util.TreeMap;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.lattice.perimeterStatistics.AbstractPerimeterStatistics;
import kineticMonteCarlo.lattice.perimeterStatistics.PerimeterStatisticsFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
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
  private List<AbstractGrowthAtom> currentPerimeter;
  private TreeMap<Integer, AbstractGrowthAtom> currentPerimeterTreeMap;
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
  public List<AbstractGrowthAtom> getCurrentPerimeter(){
    return currentPerimeter;
  }
  
  public void setCurrentPerimeter(List<AbstractGrowthAtom> perimeter) {
    currentPerimeter = perimeter;
    currentPerimeterTreeMap = new TreeMap<>();
    perimeter.stream().forEach(atom -> currentPerimeterTreeMap.put(atom.getId(), atom));
  }
  
  public void setMaxPerimeter(float sizeX, float sizeY) {
    currentRadius = (int) Math.max(sizeX, sizeY) / 2;
  }
  
  public boolean contains(AbstractGrowthAtom atom) {
    return currentPerimeterTreeMap.containsKey(atom.getId());
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

  public AbstractGrowthAtom getPerimeterReentrance(AbstractGrowthAtom originAtom) {

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

    AbstractGrowthAtom destinationAtom = null;
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

  public AbstractGrowthAtom getRandomPerimeterAtom() {
    return currentPerimeter.get(utils.StaticRandom.rawInteger(currentPerimeter.size()));
  }

  int searchPerimeterOffsetReentrance() {
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
