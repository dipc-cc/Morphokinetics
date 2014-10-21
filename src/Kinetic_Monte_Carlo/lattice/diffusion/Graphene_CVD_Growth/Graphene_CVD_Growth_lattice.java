/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.diffusion.Graphene_CVD_Growth;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.atom.diffusion.Graphene_CVD_growth.Graphene_atom;
import Kinetic_Monte_Carlo.atom.diffusion.Modified_Buffer;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import java.awt.geom.Point2D;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class Graphene_CVD_Growth_lattice extends Abstract_2D_diffusion_lattice {

    private static int[] latticeNeighborhoodData;
    private static final double cos60 = Math.cos(60 * Math.PI / 180);
    private static final double cos30 = Math.cos(30 * Math.PI / 180);
    private final Modified_Buffer modified;

    public Graphene_CVD_Growth_lattice(int sizeX, int sizeY, Modified_Buffer modified, Hops_per_step distance_per_step) {

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        sizeZ = 1;
        unit_cell_size = 4;
        atoms = new Graphene_atom[sizeX][sizeY];
        this.modified = modified;

        if (latticeNeighborhoodData == null) {
            initializeNeighborHoodCache();
        }
        create_atoms(distance_per_step);
        setAngles();
    }

    private static void initializeNeighborHoodCache() {

        latticeNeighborhoodData = new int[12];
        latticeNeighborhoodData[0] = (1 & 0xFFFF) + (0 << 16);
        latticeNeighborhoodData[1] = (-1 << 16);
        latticeNeighborhoodData[2] = (1 << 16);

        latticeNeighborhoodData[3] = (1 & 0xFFFF) + (1 << 16);
        latticeNeighborhoodData[4] = (1 & 0xFFFF) + (-1 << 16);
        latticeNeighborhoodData[5] = (-2 << 16);
        latticeNeighborhoodData[6] = (-1 & 0xFFFF) + (-1 << 16);
        latticeNeighborhoodData[7] = (-1 & 0xFFFF) + (1 << 16);
        latticeNeighborhoodData[8] = (2 << 16);

        latticeNeighborhoodData[9] = (1 & 0xFFFF) + (2 << 16);
        latticeNeighborhoodData[10] = (1 & 0xFFFF) + (-2 << 16);
        latticeNeighborhoodData[11] = (-1 & 0xFFFF) + (0 << 16);
    }

    private void create_atoms(Hops_per_step distance_per_step) {

        for (int i = 0; i < sizeX; i += 2) { //X
            for (int j = 0; j < sizeY; j += 2) { //Y
                //para cada unit cell

                //atomo 0 de la unit cell, tipo 0
                atoms[i][j] = new Graphene_atom((short) i, (short) j, distance_per_step);

                i++;
                //atomo 1 de la unit cell, tipo 1
                atoms[i][j] = new Graphene_atom((short) i, (short) j, distance_per_step);

                i--;
                j++;
                //atomo 2 de la unit cell, tipo 1   
                atoms[i][j] = new Graphene_atom((short) i, (short) j, distance_per_step);

                i++;
                //atomo 3 de la unit cell, tipo 0
                atoms[i][j] = new Graphene_atom((short) i, (short) j, distance_per_step);

                i--;
                j--;
            }
        }
    }


    @Override
    public void configure(double[][] probabilities) {

        for (int i = 0; i < atoms[0].length; i++) { //X
            for (int j = 0; j < atoms.length; j++) { //Y   

                atoms[j][i].initialize(this, probabilities, modified);
            }
        }
    }

    @Override
    public Graphene_atom getNeighbor(int Xpos, int Ypos, int neighbor) {

        int vec = latticeNeighborhoodData[neighbor];                      //esto define el tipo de atomo
        int vec_X = (short) (vec & 0xFFFF);
        int vec_Y = ((vec >> 16));
        if (((Xpos + Ypos) & 1) != 0) {
            vec_X = -vec_X;
            vec_Y = -vec_Y;
        }
        int posXV = Xpos + vec_X;
        if (posXV < 0) {
            posXV += atoms.length;
        } else if (posXV >= atoms.length) {
            posXV -= atoms.length;
        }
        int posYV = Ypos + vec_Y;
        if (posYV < 0) {
            posYV += atoms[0].length;
        } else if (posYV >= atoms[0].length) {
            posYV -= atoms[0].length;
        }
        return (Graphene_atom) atoms[posXV][posYV];
    }

    public int getPosX(int X, int Y, int pos, boolean tipo0) {
        int vec_X = (short) (latticeNeighborhoodData[pos] & 0xFFFF);
        if (!tipo0) {
            vec_X = -vec_X;
        }
        int posXV = X + vec_X;
        if (posXV < 0) {
            posXV += atoms.length;
        } else if (posXV >= atoms.length) {
            posXV -= atoms.length;
        }
        return posXV;
    }

    public int getPosY(int X, int Y, int pos, boolean tipo0) {
        int vec_Y = ((latticeNeighborhoodData[pos] >> 16));
        if (!tipo0) {
            vec_Y = -vec_Y;
        }
        int posYV = Y + vec_Y;
        if (posYV < 0) {
            posYV += atoms[0].length;
        } else if (posYV >= atoms[0].length) {
            posYV -= atoms[0].length;
        }
        return posYV;
    }

    @Override
    public int getAvailableDistance(int atomType, short Xpos, short Ypos, int thresholdDistance) {

        int[] point = new int[2];
        switch (atomType) {
            case 0:
                return getClearArea_terrace(Xpos, Ypos, thresholdDistance);
            case 2:
                return getClearArea_zigzag(Xpos, Ypos, thresholdDistance, point, StaticRandom.raw());
            case 3:
                return getClearArea_armchair(Xpos, Ypos, thresholdDistance, point, StaticRandom.raw());
            default:
                return 0;
        }
    }

    @Override
    public Abstract_2D_diffusion_atom getFarAtom(int originType, short Xpos, short Ypos, int distance) {

        int[] point = new int[2];
        switch (originType) {
            case 0:
                return chooseClearArea_terrace(Xpos, Ypos, distance, StaticRandom.raw());
            case 2:
                getClearArea_zigzag(Xpos, Ypos, distance, point, StaticRandom.raw());
                return this.getAtom(point[0], point[1]);
            case 3:
                getClearArea_armchair(Xpos, Ypos, distance, point, StaticRandom.raw());
                return this.getAtom(point[0], point[1]);
            default:
                return null;
        }
    }

    private Abstract_2D_diffusion_atom chooseClearArea_terrace(short X, short Y, int s, double raw) {

        int temp = (int) (raw * (s * 2 * 6));

        boolean tipo_0 = (((X + Y) & 1) == 0);
        int X_v, Y_v;
        X_v = X;
        Y_v = (Y - s * 2);
        if (Y_v < 0) {
            Y_v += atoms[0].length;
        }

        int counter = 0;

        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 0, true);
                Y_v = getPosY(X_v, Y_v, 0, true);
            } else {
                X_v = getPosX(X_v, Y_v, 1, false);
                Y_v = getPosY(X_v, Y_v, 1, false);
            }
            tipo_0 = !tipo_0;
        }
        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 2, true);
                Y_v = getPosY(X_v, Y_v, 2, true);
            } else {
                X_v = getPosX(X_v, Y_v, 1, false);
                Y_v = getPosY(X_v, Y_v, 1, false);
            }
            tipo_0 = !tipo_0;
        }
        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 2, true);
                Y_v = getPosY(X_v, Y_v, 2, true);
            } else {
                X_v = getPosX(X_v, Y_v, 0, false);
                Y_v = getPosY(X_v, Y_v, 0, false);
            }
            tipo_0 = !tipo_0;
        }
        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 1, true);
                Y_v = getPosY(X_v, Y_v, 1, true);
            } else {
                X_v = getPosX(X_v, Y_v, 0, false);
                Y_v = getPosY(X_v, Y_v, 0, false);
            }
            tipo_0 = !tipo_0;
        }
        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 1, true);
                Y_v = getPosY(X_v, Y_v, 1, true);
            } else {
                X_v = getPosX(X_v, Y_v, 2, false);
                Y_v = getPosY(X_v, Y_v, 2, false);
            }
            tipo_0 = !tipo_0;
        }
        for (int i = 0; i < s * 2; i++) {
            counter++;
            if (counter > temp) {
                return atoms[X_v][Y_v];
            }
            if (tipo_0) {
                X_v = getPosX(X_v, Y_v, 0, true);
                Y_v = getPosY(X_v, Y_v, 0, true);
            } else {
                X_v = getPosX(X_v, Y_v, 2, false);
                Y_v = getPosY(X_v, Y_v, 2, false);
            }
            tipo_0 = !tipo_0;
        }
        return null;
    }

    private int getClearArea_terrace(short X, short Y, int m) {
      
        int s = 1;

        boolean type_0 = (((X + Y) & 1) == 0);

        short X_v, Y_v;
        X_v = X;

        Y_v = (short) (Y - 2);
        if (Y_v < 0) {
            Y_v += atoms[0].length;
        }
        byte error_code = 0;


        out:
        while (true) {

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 0);
                } else {
                    a = getNeighbor(X_v, Y_v, 1);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 2);
                } else {
                    a = getNeighbor(X_v, Y_v, 1);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 2);
                } else {
                    a = getNeighbor(X_v, Y_v, 0);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 1);
                } else {
                    a = getNeighbor(X_v, Y_v, 0);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 1);
                } else {
                    a = getNeighbor(X_v, Y_v, 2);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }

            for (int i = 0; i < s * 2; i++) {
                Graphene_atom a;
                if (type_0) {
                    a = getNeighbor(X_v, Y_v, 0);
                } else {
                    a = getNeighbor(X_v, Y_v, 2);
                }
                if (a.is_outside()) {
                    error_code |= 1;
                }
                if (a.isOccupied()) {
                    error_code |= 2;
                    break out;
                }
                X_v = a.getX();
                Y_v = a.getY();
                type_0 = !type_0;
            }


            if (error_code != 0) {
                break;
            }
            if (s >= m) {
                return s;
            }
            s++;
            Y_v -= 2;
            if (Y_v < 0) {
                Y_v = (short) (sizeY - 1);
            }
        }


        if ((error_code & 2) != 0) {
            return s - 1;
        }
        if ((error_code & 1) != 0) {
            return s;
        }
        return -1;
    }

    private int getClearArea_zigzag(short X, short Y, int m, int[] XY_destino, double raw) {


        int s = 1;
        int X_v1, Y_v1, X_v2, Y_v2;
        int orientation = atoms[X][Y].getOrientation();

        X_v1 = X;
        Y_v1 = Y;
        X_v2 = X;
        Y_v2 = Y;

        int vecino1, vecino2;

        switch (orientation) {

            case 0:
                vecino1 = 5;
                vecino2 = 8;
                break;
            case 1:
                vecino1 = 4;
                vecino2 = 7;
                break;
            case 2:
                vecino1 = 3;
                vecino2 = 6;
                break;
            default: {
                return -1;
            }
        }

        while (true) {
            int tipo1, tipo2;
            int X_v1_temp, X_v2_temp, Y_v1_temp, Y_v2_temp;

            X_v1_temp = getPosX(X_v1, Y_v1, vecino1, true);
            Y_v1_temp = getPosY(X_v1, Y_v1, vecino1, true);
            X_v2_temp = getPosX(X_v2, Y_v2, vecino2, true);
            Y_v2_temp = getPosY(X_v2, Y_v2, vecino2, true);
            X_v1 = X_v1_temp;
            X_v2 = X_v2_temp;
            Y_v1 = Y_v1_temp;
            Y_v2 = Y_v2_temp;
            if (s == 1 && atoms[X][Y].isOccupied()) {
                tipo1 = atoms[X_v1][Y_v1].get_type_without_neighbor(vecino1);
                tipo2 = atoms[X_v2][Y_v2].get_type_without_neighbor(vecino2);
            } else {
                tipo1 = atoms[X_v1][Y_v1].getType();
                tipo2 = atoms[X_v2][Y_v2].getType();
            }



            if (atoms[X_v1][Y_v1].isOccupied() || atoms[X_v2][Y_v2].isOccupied() || tipo2 != 2 || tipo1 != 2) {
                return s - 1;
            }

            if (raw < 0.5) {
                XY_destino[0] = X_v1;
                XY_destino[1] = Y_v1;
            } else {
                XY_destino[0] = X_v2;
                XY_destino[1] = Y_v2;
            }

            if (s == m) {
                return s;
            }
            //System.out.println("holaaa!! "+ s);
            s++;
        }
    }

    private int getClearArea_armchair(short X, short Y, int m, int[] XY_destination, double raw) {


        int s = 1;
        int X_v1, Y_v1, X_v2, Y_v2;
        boolean tipo_0 = (((X + Y) & 1) == 0);
        int orientacion = atoms[X][Y].getOrientation();

        X_v1 = X;
        Y_v1 = Y;
        X_v2 = X;
        Y_v2 = Y;

        int neighbor1, neighbor2;

        switch (orientacion) {
            case 0:
                neighbor1 = 10;
                neighbor2 = 2;
                if (getNeighbor(X, Y, neighbor1).isOccupied()) {
                    neighbor1 = 9;
                    neighbor2 = 1;
                }
                break;
            case 1:
                neighbor1 = 11;
                neighbor2 = 0;
                if (getNeighbor(X, Y, neighbor1).isOccupied()) {
                    neighbor1 = 10;
                    neighbor2 = 2;
                }
                break;

            case 2:
                neighbor1 = 11;
                neighbor2 = 0;
                if (getNeighbor(X, Y, neighbor1).isOccupied()) {
                    neighbor1 = 9;
                    neighbor2 = 1;
                }
                break;

            default:
                return -1;
        }

//System.out.println(vecino1+" "+vecino2);


        while (true) {
            int tipo1, tipo2;
            int X_v1_temp, X_v2_temp, Y_v1_temp, Y_v2_temp;

            if (tipo_0) {
                X_v1_temp = getPosX(X_v1, Y_v1, neighbor1, true);
                Y_v1_temp = getPosY(X_v1, Y_v1, neighbor1, true);
                X_v2_temp = getPosX(X_v2, Y_v2, neighbor2, true);
                Y_v2_temp = getPosY(X_v2, Y_v2, neighbor2, true);
                X_v1 = X_v1_temp;
                X_v2 = X_v2_temp;
                Y_v1 = Y_v1_temp;
                Y_v2 = Y_v2_temp;
                if (s == 1 && atoms[X][Y].isOccupied()) {
                    tipo1 = atoms[X_v1][Y_v1].get_type_without_neighbor(neighbor1);
                    tipo2 = atoms[X_v2][Y_v2].get_type_without_neighbor(neighbor2);
                } else {
                    tipo1 = atoms[X_v1][Y_v1].getType();
                    tipo2 = atoms[X_v2][Y_v2].getType();
                }
            } else {
                X_v1_temp = getPosX(X_v1, Y_v1, neighbor2, false);
                Y_v1_temp = getPosY(X_v1, Y_v1, neighbor2, false);
                X_v2_temp = getPosX(X_v2, Y_v2, neighbor1, false);
                Y_v2_temp = getPosY(X_v2, Y_v2, neighbor1, false);
                X_v1 = X_v1_temp;
                X_v2 = X_v2_temp;
                Y_v1 = Y_v1_temp;
                Y_v2 = Y_v2_temp;
                if (s == 1 && atoms[X][Y].isOccupied()) {
                    tipo1 = atoms[X_v1][Y_v1].get_type_without_neighbor(neighbor2);
                    tipo2 = atoms[X_v2][Y_v2].get_type_without_neighbor(neighbor1);
                } else {
                    tipo1 = atoms[X_v1][Y_v1].getType();
                    tipo2 = atoms[X_v2][Y_v2].getType();
                }
            }


            if (atoms[X_v1][Y_v1].isOccupied() || atoms[X_v2][Y_v2].isOccupied() || tipo2 != 3 || tipo1 != 3) {
                return s - 1;
            }


            if (raw < 0.5) {
                XY_destination[0] = X_v1;
                XY_destination[1] = Y_v1;
            } else {
                XY_destination[0] = X_v2;
                XY_destination[1] = Y_v2;
            }

            if (s == m) {
                return s;
            }
            tipo_0 = !tipo_0;
            s++;
        }
    }

    //we ignore the unit_cell_pos by now, we get directly the atom of X,Y location
    @Override
    public Abstract_2D_diffusion_atom getAtom(int X, int Y) {
        return atoms[X][Y];

    }

    @Override
    public float getSpatialSizeX() {
        return sizeX;
    }

    @Override
    public float getSpatialSizeY() {
        return sizeY;
    }
    
    @Override
    public Point2D getCentralLatticeLocation() {
        return getSpatialLocation(sizeX/2,sizeY/2);
    }


    
    @Override
    public Point2D getSpatialLocation(int Xpos, int Ypos) {

        double XLocation;
        if ((Xpos & 1) == 0) {
            XLocation = (Xpos >> 1) * (2 + 2 * cos60) + 0.5 + (1 & Xpos) + cos60;
        } else {
            XLocation = (Xpos >> 1) * (2 + 2 * cos60) + 0.5 + (1 & Xpos) * (1 + 2 * cos60);
        }
        double YLocation = (Ypos >> 1) * (2 * cos30) + (1 & Ypos) * cos30;
        return new Point2D.Double(XLocation, YLocation);

    }
}