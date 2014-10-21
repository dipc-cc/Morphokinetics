/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator;

import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.lattice.diffusion.IDevitaLattice;
import java.util.HashMap;
import java.util.Map;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class DevitaAccelerator {

    private final IDevitaLattice lattice;
    private final Map<Integer, Integer> remaining_hops_map;
    private final Map<Integer, DevitaHopsConfig> devitaConfig;
    private Hops_per_step hops_per_step;
    private static final int MAX_ACCUMULATED_STEPS = 100;
    private static final int MIN_ACCUMULATED_STEPS = 30;
    private static final int MAX_DISTANCE_HOPS = 5;
    private static final int MIN_DISTANCE_HOPS = 1;

    public DevitaAccelerator(IDevitaLattice lattice, Hops_per_step hops_per_steps) {
        this.lattice = lattice;
        this.hops_per_step = hops_per_steps;
        this.remaining_hops_map = new HashMap();
        this.devitaConfig = new HashMap();
    }

    public void tryToSpeedUp(int type, DevitaHopsConfig config) {
        this.hops_per_step.setDistancePerStep(type, 1);
        this.devitaConfig.put(type, config);
        update_remaining_hops(type, 0);
    }

    public Abstract_2D_diffusion_atom choose_random_hop(Abstract_2D_diffusion_atom source) {
        int sourceAtomType = (int) source.getType();

        if (!hops_per_step.isAccelerationEnabled(sourceAtomType)) {
            return source.choose_random_hop();
        }

        int desired_hop_distance = hops_per_step.getDistancePerStep(sourceAtomType, sourceAtomType);
        int remaining_hops = desired_hop_distance * desired_hop_distance + remaining_hops_map.get(sourceAtomType);

        Abstract_2D_diffusion_atom destination;

        int remaining_distance = (int) Math.sqrt(remaining_hops);
        int possible_distance = lattice.getAvailableDistance(sourceAtomType, source.getX(), source.getY(), remaining_distance);


        if (possible_distance <= 0) {
            destination = source.choose_random_hop();
            possible_distance = 1;
        } else {

            destination = lattice.getFarSite(sourceAtomType, source.getX(), source.getY(), possible_distance);
        }

        remaining_hops -= possible_distance * possible_distance;


        //System.out.println(desired_hop_distance);

        update_remaining_hops(sourceAtomType, remaining_hops);
        update_desired_hop_distances(remaining_hops, desired_hop_distance, sourceAtomType);

        return destination;
    }

    private void update_remaining_hops(int sourceAtomType, int pending_jumps) {
        remaining_hops_map.put(sourceAtomType, pending_jumps);
    }

    private void update_desired_hop_distances(int pending_jumps, int desired_hop_distance, int sourceAtomType) {

        DevitaHopsConfig config = devitaConfig.get(sourceAtomType);

        if (pending_jumps < config.getMin_accumulated_steps() && desired_hop_distance < config.getMax_distance_hops()) {
            hops_per_step.setDistancePerStep(sourceAtomType, desired_hop_distance + 1);
        }
        if (pending_jumps > config.getMax_accumulated_steps() && desired_hop_distance > config.getMin_distance_hops()) {
            hops_per_step.setDistancePerStep(sourceAtomType, desired_hop_distance >> 1);
        }
    }
}
