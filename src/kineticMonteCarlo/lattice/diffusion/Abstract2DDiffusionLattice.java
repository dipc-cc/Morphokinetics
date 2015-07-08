/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.diffusion.Abstract2DDiffusionAtom;
import kineticMonteCarlo.lattice.AbstractLattice;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import utils.QuickSort;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionLattice extends AbstractLattice implements IDevitaLattice {

    protected Abstract2DDiffusionAtom[][] atoms;

    public abstract Abstract2DDiffusionAtom getNeighbor(int Xpos, int Ypos, int neighbor);

    public abstract void configure(double[][] probabilities);

    public abstract Abstract2DDiffusionAtom getAtom(int X, int Y);

    @Override
    public AbstractAtom getAtom(int X, int Y, int Z, int Unit_cell_pos) {
        if (Z != 0 || Unit_cell_pos != 0) {
            throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
        }
        return getAtom(X, Y);
    }

    //obtains the spatial location of certain atom, the distance between atoms is considered as 1
    public abstract Point2D getSpatialLocation(int X, int Y);

    public abstract Point2D getCentralLatticeLocation();
    
    public abstract float getSpatialSizeX();
    
    public abstract float getSpatialSizeY();

    @Override
    public void reset() {

        for (int i = 0; i < atoms[0].length; i++) { //X
            for (int j = 0; j < atoms.length; j++) { //Y   

                atoms[j][i].clear();
            }
        }
    }

    protected void setAngles() {

        Point2D middle = getCentralLatticeLocation();

        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++) {

                Point2D position = getSpatialLocation(X, Y);

                double Xdif = position.getX() - middle.getX();
                double Ydif = position.getY() - middle.getY();

                if (Xdif == 0) {
                    Xdif = 1e-8;
                }
                double angle = Math.atan(Ydif / Xdif);

                if (Xdif < 0) {
                    angle = Math.PI + angle;
                }
                if (Xdif >= 0 && Ydif < 0) {
                    angle = 2 * Math.PI + angle;
                }
                atoms[X][Y].setAngle((float) angle);
            }
        }
    }

    public double getDistanceToCenter(int X, int Y) {

        Point2D middle = getCentralLatticeLocation();
        Point2D position = getSpatialLocation(X, Y);

        return position.distance(middle);
    }

    //define como átomos inside a los átomos dentro de dicho rádio
    //devuelve un array de átomos que es el perimetro de dicha circunferencia.
    public Abstract2DDiffusionAtom[] setInside(int radius) {

        ArrayList<Abstract2DDiffusionAtom> perimeterList = new ArrayList();

        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++) {
                double distance = getDistanceToCenter(X, Y);
                if (radius <= distance) {
                    atoms[X][Y].setOutside(true);
                } else {
                    atoms[X][Y].setOutside(false);
                    if (distance > (radius - 1)) {
                        perimeterList.add(atoms[X][Y]);
                    }
                }
            }
        }

        Abstract2DDiffusionAtom[] perimeter = perimeterList.toArray(new Abstract2DDiffusionAtom[perimeterList.size()]);

        QuickSort.order_by_angle(perimeter, perimeter.length - 1);

        return perimeter;
    }

}
