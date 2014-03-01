/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.etching.Si_etching;

import Kinetic_Monte_Carlo.atom.etching.Si_etching.Si_atom;
import Kinetic_Monte_Carlo.lattice.etching.Abstract_etching_lattice;
import Kinetic_Monte_Carlo.lattice.etching.Si_etching.unit_cell.Unit_Cell;

/**
 *
 * @author Nestor
 */
public class Si_lattice extends Abstract_etching_lattice {
    
    private Si_atom[] lattice;
    private  Unit_Cell unitCell;
    
    public Si_lattice(int millerX, int millerY, int millerZ, int sizeX, int sizeY, int sizeZ) {
        
        
        if (millerX == 0 && millerY == 0 && millerZ == 0) {
            System.exit(-1);
        }
        
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        
        unitCell = new Unit_Cell();
        unit_cell_size = unitCell.create_Unit_Cell(millerX, millerY, millerZ);
        if (unit_cell_size < 4 || unit_cell_size > 127) {
            System.out.println("UC size inappropiate: " + unit_cell_size);
            System.exit(-1);
        }
        short[] UC_neig = unitCell.getNeighs();
        byte[] block = unitCell.getN_block();
        
        
        float[] coords = new float[unit_cell_size * 3 + 3];
        for (int i = 0; i < unit_cell_size; i++) {
            coords[i * 3] = unitCell.getCellsP()[i].getPos_x(0, 0, 0);
            coords[i * 3 + 1] = unitCell.getCellsP()[i].getPos_y(0, 0, 0);
            coords[i * 3 + 2] = unitCell.getCellsP()[i].getPos_z(0, 0, 0);
        }
        
        coords[unit_cell_size * 3] = (float) unitCell.getLimitX();
        coords[unit_cell_size * 3 + 1] = (float) unitCell.getLimitY();
        coords[unit_cell_size * 3 + 2] = (float) unitCell.getLimitZ();
        
        
        double tamX = unitCell.getCellsP()[0].getLimitX() * sizeX;
        double tamY = unitCell.getCellsP()[0].getLimitY() * sizeY;
        
        
        if (tamX > tamY) {
            this.sizeX = (int) Math.round((int) sizeX * tamY / tamX);
        } 
        else {
            this.sizeY = (int) Math.round((int) sizeY * tamX / tamY);
        }
        
        
        lattice = new Si_atom[this.sizeX * this.sizeY * this.sizeZ * unit_cell_size];
        
        this.create_atoms(coords, unitCell);
        this.interconnect_atoms(UC_neig, block);
    }
    
    @Override
    public void setProbabilities(double[] probabilities) {
        for (int i = 0; i < lattice.length; i++) {
            lattice[i].initialize(probabilities);
        }
    }
    
    public Unit_Cell getUnit_Cell(){
    
        return unitCell;
    }
    
    
    public Si_atom getAtom(int Unit_cell_X, int Unit_cell_Y, int Unit_cell_Z, int Unit_cell_pos) {
        
        return lattice[((Unit_cell_Z * sizeY + Unit_cell_Y) * sizeX + Unit_cell_X) * unit_cell_size + Unit_cell_pos];
    }
    
    @Override
    public void reset() {

        //Unremove atoms and set to bulk mode (4,12)
        //----------------------------------------------------------

        for (int i = 0; i < this.sizeY; i++) {
            for (int j = 0; j < this.sizeX; j++) {
                for (int a = 0; a < this.unit_cell_size; a++) {
                    lattice[(((sizeZ - 1) * sizeY + i) * sizeX + j) * unit_cell_size + a].setOnList(null);
                    lattice[(((sizeZ - 1) * sizeY + i) * sizeX + j) * unit_cell_size + a].unRemove();
                }
            }
        }
        
        for (int k = 0; k < (sizeZ - 1) * sizeY * sizeX * unit_cell_size; k++) {
            lattice[k].setOnList(null);
            lattice[k].unRemove();
            lattice[k].set_as_bulk();  
        }

        // Update neighborhood of top atoms
        //----------------------------------------------------------  

        for (int k = (sizeZ - 1) * sizeY * sizeX * unit_cell_size; k < sizeZ * sizeY * sizeX * unit_cell_size; k++) {
            lattice[k].updaten1_from_scratch();
        }
        
        for (int k = (sizeZ - 1) * sizeY * sizeX * unit_cell_size; k < sizeZ * sizeY * sizeX * unit_cell_size; k++) {
            lattice[k].updaten2_from_scratch();
        }

        // Remove top layer atoms
        //----------------------------------------------------------
        for (int i = 0; i < this.sizeY; i++) {
            for (int j = 0; j < this.sizeX; j++) {
                for (int a = 0; a < this.unit_cell_size; a++) {
                    
                    lattice[(((this.sizeZ - 1) * this.sizeY + i) * this.sizeX + j) * this.unit_cell_size + a].remove(); 
                }
            }
        } 
    }
    
    
    

    private void create_atoms(float[] coords, Unit_Cell UC) {
        //atoms creation
                int cont = 0;
                for (int a = 0; a < this.sizeZ; a++) {
                    for (int b = 0; b < this.sizeY; b++) {
                        for (int c = 0; c < this.sizeX; c++) {
                            for (int j = 0; j < unit_cell_size; j++) {
                                
                                
                                float X = coords[j * 3] + c * (float) UC.getLimitX();
                                float Y = coords[j * 3 + 1] + b * (float) UC.getLimitY();
                                float Z = -coords[j * 3 + 2] + (a + 1) * (float) UC.getLimitZ();
                                
                                lattice[cont] = new Si_atom(X, Y, Z);
                                cont++;
                                
                            }
                        }
                    }
                }
    }

    private void interconnect_atoms(short[] UC_neig, byte[] block) {
        //atoms inter-connection

                for (int Z = 0; Z < this.sizeZ; Z++) {
                    for (int Y = 0; Y < this.sizeY; Y++) {
                        for (int X = 0; X < this.sizeX; X++) {
                            for (int j = 0; j < unit_cell_size; j++) {
                                
                                for (int i = 0; i < 4; i++) {
                                    
                                    int vecino_X = X;
                                    int vecino_Y = Y;
                                    int vecino_Z = Z;
                                    int pos_vecino = UC_neig[j * 4 + i];
                                    if ((block[j * 4 + i] & 3) == 1) {
                                        vecino_Y++;
                                    }
                                    if ((block[j * 4 + i] & 3) == 3) {
                                        vecino_Y--;
                                    }
                                    if (vecino_Y < 0) {
                                        vecino_Y = sizeY - 1;
                                    }
                                    if (vecino_Y > sizeY - 1) {
                                        vecino_Y = 0;
                                    }
                                    
                                    if (((block[j * 4 + i] >> 2) & 3) == 1) {
                                        vecino_X++;
                                    }
                                    if (((block[j * 4 + i] >> 2) & 3) == 3) {
                                        vecino_X--;
                                    }
                                    if (vecino_X < 0) {
                                        vecino_X = sizeX - 1;
                                    }
                                    if (vecino_X > sizeX - 1) {
                                        vecino_X = 0;
                                    }
                                    
                                    if (((block[j * 4 + i] >> 4) & 3) == 1) {
                                        vecino_Z++;
                                    }
                                    if (((block[j * 4 + i] >> 4) & 3) == 3) {
                                        vecino_Z--;
                                    }
                                    
                                    if (vecino_Z < 0) {
                                        vecino_X = 0;
                                        vecino_Y = 0;
                                        vecino_Z = 0;
                                        pos_vecino = j;
                                    }
                                    if (vecino_Z < sizeZ) {
                                        
                                        lattice[((Z * sizeY + Y) * sizeX + X) * unit_cell_size + j].setNeighbor(lattice[((vecino_Z * sizeY + vecino_Y) * sizeX + vecino_X) * unit_cell_size + pos_vecino], i);
                                        
                                    } else {
                                        lattice[((Z * sizeY + Y) * sizeX + X) * unit_cell_size + j].setNeighbor(null, i);
                                    }
                                }
                            }
                        }
                    }
                }
    }
}
