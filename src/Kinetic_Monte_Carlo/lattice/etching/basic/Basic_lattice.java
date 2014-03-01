/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.etching.basic;

import Kinetic_Monte_Carlo.atom.Abstract_atom;
import Kinetic_Monte_Carlo.atom.etching.basic.Basic_atom;
import Kinetic_Monte_Carlo.lattice.etching.Abstract_etching_lattice;

/**
 *
 * @author Nestor
 */
public class Basic_lattice extends Abstract_etching_lattice {

    private Basic_atom[] lattice;

    public Basic_lattice(int sizeX, int sizeY) {

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.unit_cell_size=1;
        lattice = new Basic_atom[this.sizeX * this.sizeY];
        create_atoms(sizeX, sizeY);
        interconnect_atoms();
    }

    @Override
    public Abstract_atom getAtom(int X, int Y, int Z, int Unit_cell_pos) {
        return lattice[((Y) * sizeX + X) * unit_cell_size + Unit_cell_pos];
    }


    @Override
    public void setProbabilities(double[] probabilities) {
        for (int i = 0; i < lattice.length; i++) {
            lattice[i].initialize(probabilities);
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                lattice[i * sizeX + j].setOnList(null);
                lattice[i * sizeX + j].unRemove();
            }
        }

        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                lattice[i * sizeX + j].updateType_from_scratch();
            }
        }

        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (i < 4) {
                    lattice[i * sizeX + j].remove();
                }
            }
        }
    }

    @Override
    public int getSizeX() {
        return super.getSizeX();
    }

    @Override
    public int getSizeY() {
        return super.getSizeY();
    }

    @Override
    public int getSizeZ() {
        return 1;
    }

    @Override
    public int getSizeUC() {
        return 1;
    }

    private void create_atoms(int sizeX, int sizeY) {
        for (short i = 0; i < sizeY; i++) {
            for (short j = 0; j < sizeX; j++) {
                lattice[i * sizeX + j] = new Basic_atom(j, i);
            }
        }
    }

    private void interconnect_atoms() {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (i - 1 >= 0) {
                    lattice[i * sizeX + j].setNeighbor(lattice[(i - 1) * sizeX + j], 0);                    //up        
                }
                lattice[i * sizeX + j].setNeighbor(lattice[Math.min(i + 1, sizeY - 1) * sizeX + j], 1);      //down        
                int izq = j - 1;
                if (izq < 0) {
                    izq = sizeX - 1;
                }
                lattice[i * sizeX + j].setNeighbor(lattice[i * sizeX + izq], 2);                        //left        
                lattice[i * sizeX + j].setNeighbor(lattice[i * sizeX + ((j + 1) % sizeX)], 3);              //right       
            }
        }
    }
}
