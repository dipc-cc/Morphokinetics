/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion.Ag_Ag_growth;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.atom.diffusion.Modified_Buffer;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import utils.StaticRandom;

/**
 *
 * @author DONOSTIA INTERN
 */
public class AgAg_Atom extends Abstract_2D_diffusion_atom {

    private static AgAg_types_table types_table;
    private final AgAg_Atom[] neighbors = new AgAg_Atom[6];
    private byte n_immobile;
    private byte n_mobile;

    public AgAg_Atom(short X, short Y, Hops_per_step distance_per_step) {

        this.X = X;
        this.Y = Y;
        if (types_table == null) {
            types_table = new AgAg_types_table();
        }
        bonds_probability = new double[6];
        this.distance_per_step = distance_per_step;

    }

    public void setNeighbor(AgAg_Atom a, int pos) {
        neighbors[pos] = a;
    }

    public AgAg_Atom getNeighbor(int pos) {
        return neighbors[pos];
    }

    @Override
    public void initialize(Abstract_2D_diffusion_lattice lattice, double[][] probabilities, Modified_Buffer modified) {
        super.initialize(probabilities, modified);
    }

    @Override
    public boolean isEligible() {
        return occupied && (type < 3);
    }

    private boolean isPartOfInamovibleSubstrate() {
        return occupied && (type == 4);
    }

    public byte getN_immobile() {
        return n_immobile;
    }

    public byte getN_mobile() {
        return n_mobile;
    }

    @Override
    public void clear() {

        n_immobile = n_mobile = type = 0;
        occupied = false;
        outside = false;

        total_probability = 0;
        for (int i = 0; i < bonds_probability.length; i++) {
            bonds_probability[i] = 0;
        }

        setOnList(null);
    }

    @Override
    public int getOrientation() {

        int occupationCode = 0;
        for (int i = 0; i < 6; i++) {
            if (neighbors[i].isOccupied()) {
                occupationCode |= (1 << i);
            }
        }

        if (type == 2) {
            return aggr_calcula_edge_type(occupationCode);
        }
        if (type == 3) {
            return aggr_calcula_kink_type(occupationCode);
        }
        return -1;
    }

    private int aggr_calcula_edge_type(int code) {

        switch (code) {
            case 12:
                return 5;
            case 48:
                return 1;
            case 3:
                return 3;
            case 6:
                return 4;
            case 24:
                return 0;
            case 33:
                return 2;
            default:
                return -1;
        }
    }

    private int aggr_calcula_kink_type(int code) {

        switch (code) {
            case (1 + 2 + 4):
                return 0;
            case (2 + 4 + 8):
                return 1;
            case (4 + 8 + 16):
                return 2;
            case (8 + 16 + 32):
                return 3;
            case (16 + 32 + 1):
                return 4;
            case (32 + 1 + 2):
                return 5;
            default:
                return -1;
        }
    }

    @Override
    public Abstract_2D_diffusion_atom choose_random_hop() {

        double linearSearch = StaticRandom.raw() * total_probability;

        double sum = 0;
        int cont = 0;
        while (true) {
            sum += bonds_probability[cont++];
            if (sum >= linearSearch) {
                break;
            }
            if (cont == bonds_probability.length) {
                break;
            }
        }
        cont--;

        if (type == 2 && neighbors[cont].getType() == 1) {
            return ahead_corner_Atom(cont);
        }

        return neighbors[cont];
    }

    public AgAg_Atom ahead_corner_Atom(int corner_position) {
        if ((getOrientation() & 1) != 0) {

            switch (corner_position) {
                case 0:
                    return neighbors[5].getNeighbor(0);
                case 1:
                    return neighbors[2].getNeighbor(1);
                case 2:
                    return neighbors[1].getNeighbor(2);
                case 3:
                    return neighbors[4].getNeighbor(3);
                case 4:
                    return neighbors[3].getNeighbor(4);
                case 5:
                    return neighbors[0].getNeighbor(5);
            }
        } else {

            switch (corner_position) {
                case 0:
                    return neighbors[1].getNeighbor(0);
                case 1:
                    return neighbors[0].getNeighbor(1);
                case 2:
                    return neighbors[3].getNeighbor(2);
                case 3:
                    return neighbors[2].getNeighbor(3);
                case 4:
                    return neighbors[5].getNeighbor(4);
                case 5:
                    return neighbors[4].getNeighbor(5);
            }
        }
        return null;
    }

    @Override
    public boolean two_terrace_together() {

        if (n_mobile != 2 || n_immobile != 0) {
            return false;
        }

        int cont = 0;
        int i = 0;
        while (cont < 2 && i < 6) {
            if (neighbors[i].isOccupied()) {
                if (neighbors[i].getType() != 0) {
                    return false;
                }
                cont++;
            }
            i++;
        }
        return true;
    }

    @Override
    public int getNeighbourCount() {
        return 6;
    }

    //================================
    public void rem_Inmovil_add_Movil_procesa() {

        if (n_immobile == 0) {  //estado de transición
            n_mobile++;
            n_immobile--;
            return;
        }

        byte new_type = types_table.getType(--n_immobile, ++n_mobile);

        if (type != new_type) { // ha cambiado el tipo, hay que actualizar ligaduras

            boolean inmov_to_mov = (type >= 3 && new_type < 3);

            type = new_type;

            modified.addAtomPropio(this);
            if (n_mobile > 0 && !occupied) {
                modified.addAtomLigaduras(this);
            }

            if (inmov_to_mov && occupied) {
                for (int i = 0; i < 6; i++) {
                    if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                        neighbors[i].rem_Inmovil_add_Movil_procesa();
                    }
                }
            }
        }
    }

    public void rem_Movil_add_Inmovil_procesa(boolean forceNucleation) {

        if (n_mobile == 0) {
            n_mobile--;
            n_immobile++;
            return;
        }

        byte new_type = types_table.getType(++n_immobile, --n_mobile);

        if (forceNucleation && occupied) {
            new_type = 4;
        }

        if (type != new_type) { // ha cambiado el tipo, hay que actualizar ligaduras
            boolean mov_to_inmov = (type < 3 && new_type >= 3);
            type = new_type;
            modified.addAtomPropio(this);
            if (n_mobile > 0 && !occupied) {
                modified.addAtomLigaduras(this);
            }
            if (mov_to_inmov && occupied) {

                for (int i = 0; i < 6; i++) {
                    if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                        neighbors[i].rem_Movil_add_Inmovil_procesa(forceNucleation);
                    }
                }
            }
        }
    }

    public void add_Vecino_Ocupado_procesa(byte tipo_origen, boolean force_nucleation) { //este lo ejecutan los primeros vecinos

        byte new_type;

        if (tipo_origen < 3) {
            new_type = types_table.getType(n_immobile, ++n_mobile);
        } else {
            new_type = types_table.getType(++n_immobile, n_mobile);
        }

        if (force_nucleation) {
            new_type = 4;
        }

        if (type != new_type) {

            boolean mov_to_inmov = (type < 3 && new_type >= 3);

            type = new_type;

            modified.addAtomPropio(this);
            if (n_mobile > 0 && !occupied) {
                modified.addAtomLigaduras(this);
            }

            if (mov_to_inmov && occupied) {
                for (int i = 0; i < 6; i++) {
                    if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                        neighbors[i].rem_Movil_add_Inmovil_procesa(force_nucleation);
                    }
                }
            }
        }
    }

    public void remove_Movil_Ocupado() {

        byte new_type = types_table.getType(n_immobile, --n_mobile);

        if (type != new_type) {

            boolean inmov_to_mov = (type >= 3 && new_type < 3);

            type = new_type;

            modified.addAtomPropio(this);
            if (n_mobile > 0 && !occupied) {
                modified.addAtomLigaduras(this);
            }

            if (inmov_to_mov && occupied) {
                for (int i = 0; i < 6; i++) {
                    if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                        neighbors[i].rem_Inmovil_add_Movil_procesa();
                    }
                }
            }
        }
    }

    //=================================
    @Override
    public void deposit(boolean force_nucleation) {

        occupied = true;
        if (force_nucleation) {
            type = 4;
        }

        byte tipo_original = type;
        for (int i = 0; i < 6; i++) {
            if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                neighbors[i].add_Vecino_Ocupado_procesa(tipo_original, force_nucleation);
            }
        }

        modified.addAtomPropio(this);
        if (n_mobile > 0) {
            modified.addAtomLigaduras(this);
        }
        total_probability = 0;
    }

    @Override
    public void extract() {
        occupied = false;

        for (int i = 0; i < 6; i++) {
            if (!neighbors[i].isPartOfInamovibleSubstrate()) {
                neighbors[i].remove_Movil_Ocupado();
            }
        }

        if (n_mobile > 0) {
            modified.addAtomLigaduras(this);
        }

        list.addTotalProbability(-total_probability);
        this.setOnList(null);
    }

    @Override
    public void update_all_rates() {
        double temp = -total_probability;
        total_probability = 0;

        if (this.isEligible()) {
            obtainRatesFromNeighBors();
            temp += total_probability;
        }
        if (this.isOnList()) {
            list.addTotalProbability(temp);
        }
    }

    private void obtainRatesFromNeighBors() {

        for (int i = 0; i < 6; i++) {
            bonds_probability[i] = prob_jump_to_neighbor(i);
            total_probability += bonds_probability[i];
        }
    }

    @Override
    public void update_one_bound(int neighborpos) {

        double temp = 0;
        total_probability -= bonds_probability[neighborpos];
        temp -= bonds_probability[neighborpos];
        bonds_probability[neighborpos] = (float) prob_jump_to_neighbor(neighborpos);

        total_probability += bonds_probability[neighborpos];
        temp += bonds_probability[neighborpos];

        list.addTotalProbability(temp);
    }

    private double prob_jump_to_neighbor(int position) {

        if (neighbors[position].isOccupied()) {
            return 0;
        }

        byte origin_type = type;
        if (type == 2 && (getOrientation() & 1) == 0) {
            origin_type = 5;
        }
        if (type == 3 && (getOrientation() & 1) == 0) {
            origin_type = 6;
        }

        int my_position_for_neighbor = (position + 3) % 6;

        byte destination = neighbors[position].get_type_without_neighbor(my_position_for_neighbor);

        if (type == 2 && destination == 1) { //soy un edge y el vecino es un corner, eso significa que podemos girar, a ver a donde

            int othercorner = 0;
            if (origin_type == 2) {
                othercorner = 5;
            }
            if (origin_type == 5) {
                othercorner = 2;
            }
            return probabilities[origin_type][othercorner];

        } else {

            destination = (byte) Math.min(destination, 2);

            if (destination == 2 && (neighbors[position].getOrientation() & 1) == 0) {
                destination = 5;
            }

            return probabilities[origin_type][destination];
        }
    }

    @Override
    public byte get_type_without_neighbor(int neigh_pos) {

        if (!neighbors[neigh_pos].isOccupied()) {
            return type;
        }

        if (neighbors[neigh_pos].getType() < 3) {
            return types_table.getType(n_immobile, n_mobile - 1);
        } else {
            return types_table.getType(n_immobile - 1, n_mobile);
        }
    }
}
