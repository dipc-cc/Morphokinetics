/*
 * 
 * Clase que modela un átomo de la retícula
 *  La clase alberga todas los parámetros necesarios
 * 
 */
package Kinetic_Monte_Carlo.atom.diffusion.Graphene_CVD_growth;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.atom.diffusion.Array_stack;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.Hops_per_step;
import Kinetic_Monte_Carlo.atom.diffusion.Modified_Buffer;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.lattice.diffusion.Graphene_CVD_Growth.Graphene_CVD_Growth_lattice;
import utils.StaticRandom;

public class Graphene_atom extends Abstract_2D_diffusion_atom {

    private Graphene_CVD_Growth_lattice lattice;
    private static final Array_stack PStack = new Array_stack(12);
    private static Graphene_types_table types_table;
    private byte n1, n2, n3;

    public Graphene_atom(short X, short Y,Hops_per_step distance_per_step) {

        this.X = X;
        this.Y = Y;
        if (types_table == null) {
            types_table = new Graphene_types_table();
        }

        this.distance_per_step=distance_per_step;
    }

    @Override
    public void initialize(Abstract_2D_diffusion_lattice lattice, double[][] probabilities, Modified_Buffer modified) {
        super.initialize(probabilities, modified);
        this.lattice = (Graphene_CVD_Growth_lattice) lattice;
    }

    @Override
    public boolean isEligible() {
        return occupied && (type < 6);
    } // tipos 6 y 7 son considerados atomos inmoviles


    public byte getn1() {
        return n1;
    }

    public byte getn2() {
        return n2;
    }

    public byte getn3() {
        return n3;
    }

    public int getn1n2n3() {
        return (n1 + n2 + n3);
    }

    @Override
    public void clear() {

        n1 = n2 = n3 = type = 0;
        occupied = false;
        outside = false;

        total_probability = 0;
        setOnList(null);

        if (bonds_probability != null) {
            PStack.returnProbArray(bonds_probability);
            bonds_probability = null;
        }
    }


    @Override
    public int getOrientation() {

        if (n1 == 1) {
            for (int i = 0; i < 3; i++) {
                if (lattice.getNeighbor(X, Y, i).isOccupied()) {
                    return i;
                }
            }
        }
        return -1;
    }

//escogemos a que posición vamos a saltar
    @Override
    public Abstract_2D_diffusion_atom choose_random_hop() {
      
        double raw=StaticRandom.raw();
        
        
        if (bonds_probability == null) {
            return lattice.getNeighbor(X, Y, (int) (raw * 12));
        }
        
        double linearSearch = raw * total_probability;

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
        //System.out.println(bonds_probability.length);
        return lattice.getNeighbor(X, Y, cont - 1);
    }

    private void add_1st_Vecino(boolean force_nucleation) {

        byte tipo_new = types_table.getType(++n1, n2, n3);
        if (force_nucleation && occupied) {
            tipo_new = 7;
        }
        evaluate_modified_when_add_neigh(tipo_new);
    }

    private void add_2nd_Vecino() {

        byte tipo_new = types_table.getType(n1, ++n2, n3);
        evaluate_modified_when_add_neigh(tipo_new);
    }

    private void add_3rd_Vecino() {

        byte tipo_new = types_table.getType(n1, n2, ++n3);
        evaluate_modified_when_add_neigh(tipo_new);
    }

    private void rem_1st_Vecino() {

        byte tipo_new = types_table.getType(--n1, n2, n3);
        evaluate_modified_when_rem_neigh(tipo_new);
    }

    private void rem_2nd_Vecino() {

        byte tipo_new = types_table.getType(n1, --n2, n3);
        evaluate_modified_when_rem_neigh(tipo_new);
    }

    private void rem_3rd_Vecino() {

        byte tipo_new = types_table.getType(n1, n2, --n3);
        evaluate_modified_when_rem_neigh(tipo_new);
    }

    @Override
    public boolean two_terrace_together() {
        return n1 == 1 && n2 == 0 && n3 == 0;
    }

    @Override
    public void deposit(boolean force_nucleation) {

        occupied = true;
        if (force_nucleation) {
            type = 7;
        }
        int i = 0;

        for (; i < 3; i++) {
            lattice.getNeighbor(X, Y, i).add_1st_Vecino(force_nucleation);
        }
        for (; i < 9; i++) {
            lattice.getNeighbor(X, Y, i).add_2nd_Vecino();
        }
        for (; i < 12; i++) {
            lattice.getNeighbor(X, Y, i).add_3rd_Vecino();
        }

        modified.addAtomPropio(this);
        if (getn1n2n3() > 0) {
            modified.addAtomLigaduras(this);
        }
        total_probability = 0;
    }

//extrae el átomo de este lugar (pásalo a no occupied y reduce la vecindad de los átomos vecinos, si cambia algún tipo, recalcula probabilidades)
    @Override
    public void extract() {

        occupied = false;

        int i = 0;
        for (; i < 3; i++) {
            lattice.getNeighbor(X, Y, i).rem_1st_Vecino();
        }
        for (; i < 9; i++) {
            lattice.getNeighbor(X, Y, i).rem_2nd_Vecino();
        }
        for (; i < 12; i++) {
            lattice.getNeighbor(X, Y, i).rem_3rd_Vecino();
        }

        if (getn1n2n3() > 0) {
            modified.addAtomLigaduras(this);
        }

        list.addTotalProbability(-total_probability);
        this.setOnList(null);
        if (bonds_probability != null) {
            PStack.returnProbArray(bonds_probability);
            bonds_probability = null;
        }
    }

    @Override
    public void update_all_rates() {

        double temp = -total_probability;
        total_probability = 0;

        if (this.isEligible()) {
            obtainRatesFromNeighBors(areAllRatesTheSame());
            temp += total_probability;
        }
        if (this.isOnList()) {
            list.addTotalProbability(temp);
        }
    }

    private void obtainRatesFromNeighBors(boolean equalRates) {
        if (equalRates) {
            total_probability += prob_jump_to_neighbor(type, 0) * 12.0;
        } else {
            if (bonds_probability == null) {
                bonds_probability = PStack.getProbArray();
            }
            for (int i = 0; i < 12; i++) {
               
                bonds_probability[i] = prob_jump_to_neighbor(type, i);
                total_probability += bonds_probability[i];
            }
        }
    }

    private boolean areAllRatesTheSame() {
        if ((n1 + n2 + n3) != 0) {
            return false;
        }
        for (int i = 11; i >= 3; i--) {
            if (lattice.getNeighbor(X, Y, i).getType() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update_one_bound(int neighborpos) {

        double temp = 0;

        int i = neighborpos; //eres el vecino 1 de tu 1º vecino. Eres el vecino 2 de tu 2º vecino. eres el 3º Vecino de tu vecino 3.
        switch (neighborpos) {
            case 3:
            case 4:
            case 5:
                i += 3;
                break;
            case 6:
            case 7:
            case 8:
                i -= 3;
                break;
            default:
                break;
        }

        if (bonds_probability == null) {
            double rateNuevo = prob_jump_to_neighbor(type, i);
            if (rateNuevo * 12 != total_probability) {
                double prob_independiente = total_probability / 12.0;
                bonds_probability = PStack.getProbArray();
                for (int a = 0; a < 12; a++) {
                    bonds_probability[a] = prob_independiente;
                }
                bonds_probability[i] = rateNuevo;
                total_probability += (rateNuevo - prob_independiente);
                temp += (rateNuevo - prob_independiente);
            }
        } else {
            total_probability -= bonds_probability[i];
            temp -= bonds_probability[i];
            bonds_probability[i] = prob_jump_to_neighbor(type, i);
            total_probability += bonds_probability[i];
            temp += bonds_probability[i];
        }
        list.addTotalProbability(temp);

    }

    
//probabilidad de saltar a cierta posicion vecina
    private double prob_jump_to_neighbor(int origin_type, int pos) {

        Abstract_2D_diffusion_atom atom = lattice.getNeighbor(X, Y, pos);

        if (atom.isOccupied()) {

            return 0;
        }
 
        byte lastTemp = atom.get_type_without_neighbor(pos);

        double rate;
        if (multiplier != 1) {
            rate = probabilities[origin_type][lastTemp] / multiplier;
            multiplier=1;
        } else {
            int hops = distance_per_step.getDistancePerStep(origin_type, origin_type);
            
           //if (origen_tipo==2) System.out.println(origen_tipo+" "+lastTemp+" "+hops);
            
            switch (origin_type) {
                case 0:
                    rate = probabilities[origin_type][lastTemp] / (hops * hops);
                    break;
                case 2:
                    rate = probabilities[origin_type][lastTemp] / (hops * hops);
                    break;
                case 3:
                    rate = probabilities[origin_type][lastTemp] / (hops * hops);
                    break;
                default:
                    rate = probabilities[origin_type][lastTemp];
                    break;
            }
        }
        return rate;
    }

//saber que tipo de átomo sería si el vecino pos_origen estuviera vacío
//útil para saber el tipo de átomos que serás al saltar.
//sabiendo el tipo de átomo origen y destino puedes sacar la probabilidad de éxito   origen ====> destino
    @Override
    public byte get_type_without_neighbor(int pos_origen) {

        if (pos_origen < 3) {
            return types_table.getType(n1 - 1, n2, n3);
        }
        if (pos_origen < 9) {
            return types_table.getType(n1, n2 - 1, n3);
        }
        return types_table.getType(n1, n2, n3 - 1);
    }

    @Override
    public int getNeighbourCount() {
        return 12;
    }

    private void evaluate_modified_when_rem_neigh(byte tipo_new) {
        if (type != tipo_new) {
            type = tipo_new;
            if (occupied) {
                modified.addAtomPropio(this);
            }
            if (getn1n2n3() > 0 && !occupied) {
                modified.addAtomLigaduras(this);
            }
        }
    }

    private void evaluate_modified_when_add_neigh(byte tipo_new) {
        if (type != tipo_new) {
            type = tipo_new;
            if (occupied) {
                modified.addAtomPropio(this);
            }
            if (getn1n2n3() > 1 && !occupied) {
                modified.addAtomLigaduras(this);
            }
        }
    }
}
