/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion;

import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;
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
    
  private int currentRadius;
  private Abstract2DDiffusionAtom[] currentPerimeter;
  private AbstractPerimeterStatistics perimeterStatistics;
  private short type;

  public RoundPerimeter(String statisticData) {
    this.perimeterStatistics = new PerimeterStatisticsFactory(statisticData).getStatistics();
    this.currentRadius = perimeterStatistics.getMinRadiusInSize();
    this.currentPerimeter = null;
  }
  
  public RoundPerimeter(String statisticData, short type){
   this(statisticData);
   this.type = type;
  }
  
  public short getType() {
    return type;
  }

  public int getCurrentRadius() {
    return this.currentRadius;
  }

  public void setMinRadius() {
    this.currentRadius = perimeterStatistics.getMinRadiusInSize();
  }
  
  public Abstract2DDiffusionAtom[] getCurrentPerimeter(){
    return this.currentPerimeter;
  }
  public int goToNextRadius() {
    this.currentRadius = perimeterStatistics.getNextRadiusInSize(currentRadius);
    return this.currentRadius;
  }

  public void setAtomPerimeter(Abstract2DDiffusionAtom[] perimeter) {
    this.currentPerimeter = perimeter;

  }

  public void setMaxPerimeter(){
    this.currentRadius = 125;
  }
  
  public Abstract2DDiffusionAtom getPerimeterReentrance(Abstract2DDiffusionAtom origin) {

    int angle = searchPerimeterOffsetReentrance();
    int neededSteps = perimeterStatistics.getHopsCount(currentRadius, angle);

    /* It randomly turns */
    if (utils.StaticRandom.raw() < 0.5) {
      angle = 360 - angle;
    }

    float destinationAngleDegree = (float) (angle + (origin.getAngle() * 180.0 / Math.PI));
    if (destinationAngleDegree >= 360) {
      destinationAngleDegree = destinationAngleDegree - 360;
    }

    int initialLocation = (int) (destinationAngleDegree * currentPerimeter.length / 360.0);
    float destinationAngleRad = (float) (destinationAngleDegree * Math.PI / 180.0f);

    Abstract2DDiffusionAtom chosen = null;
    int position = 0;
    float error = currentPerimeter[initialLocation].getAngle() - destinationAngleRad;

    if (error > 0) {

      for (int j = initialLocation - 1; j >= 0; j--) {
        float errorTemp = currentPerimeter[j].getAngle() - destinationAngleRad;
        if (Math.abs(errorTemp) < Math.abs(error)) {
          error = errorTemp;
        } else {
          chosen = currentPerimeter[j + 1];
          position = j + 1;
          break;
        }
      }
      if (chosen == null) {
        chosen = currentPerimeter[0];
        position = 0;
      }
    } else {

      for (int j = initialLocation + 1; j < currentPerimeter.length; j++) {
        float errorTemp = currentPerimeter[j].getAngle() - destinationAngleRad;
        if (Math.abs(errorTemp) < Math.abs(error)) {
          error = errorTemp;
        } else {
          chosen = currentPerimeter[j - 1];
          position = j - 1;
          break;
        }
      }

      if (chosen == null) {
        chosen = currentPerimeter[currentPerimeter.length - 1];
        position = currentPerimeter.length - 1;
      }
    }

    while (chosen.isOccupied() && chosen != origin) {
      position++;
      if (position == currentPerimeter.length) {
        position = 0;
      }
      chosen = currentPerimeter[position];
    }

    chosen.setMultiplier(neededSteps /*+((radius_por_paso[0]-1)*(radius_por_paso[0]-1))*/);
    return chosen;

  }

   protected int searchPerimeterOffsetReentrance() {
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

  public Abstract2DDiffusionAtom getRandomPerimeterAtom() {
    return this.currentPerimeter[(int) (utils.StaticRandom.raw() * currentPerimeter.length)];
  }

}
