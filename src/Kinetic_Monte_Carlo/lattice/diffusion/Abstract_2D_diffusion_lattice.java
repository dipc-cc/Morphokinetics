/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.diffusion;

import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.lattice.Abstract_lattice;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import utils.QuickSort;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_2D_diffusion_lattice extends Abstract_lattice implements IDevitaLattice {

    protected Abstract_2D_diffusion_atom[][] atoms;

    public abstract Abstract_2D_diffusion_atom getNeighbor(int Xpos, int Ypos, int neighbor);

    public abstract void configure(double[][] probabilities);

    public abstract Abstract_2D_diffusion_atom getAtom(int X, int Y);

    @Override
    public Abstract_atom getAtom(int X, int Y, int Z, int Unit_cell_pos) {
        if (Z != 0 || Unit_cell_pos != 0) {
            throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
        }
        return getAtom(X, Y);
    }

    //obtains the spatial location of certain atom, the distance between atoms is considered as 1
    public abstract Point2D getSpatialLocation(int X, int Y);

    public abstract Point2D getCentralLatticeLocation();

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
    public Abstract_2D_diffusion_atom[] setInside(int radius) {

        ArrayList<Abstract_2D_diffusion_atom> perimeterList = new ArrayList();

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

        Abstract_2D_diffusion_atom[] perimeter = perimeterList.toArray(new Abstract_2D_diffusion_atom[perimeterList.size()]);

        QuickSort.order_by_angle(perimeter, perimeter.length - 1);

        return perimeter;
    }

}
