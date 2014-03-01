/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.lattice.diffusion.perimeter_statistics;

import Kinetic_Monte_Carlo.lattice.diffusion.perimeter_statistics.Ag_Ag.Ag_Ag_perimeter_statistics;
import Kinetic_Monte_Carlo.lattice.diffusion.perimeter_statistics.Graphene_CVD_growth.Graphene_perimeter_statistics;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class Perimeter_statistics_factory {

    private static Map<String, Abstract_perimeter_statistics> perimeter_statistics;

    public Perimeter_statistics_factory() {

        perimeter_statistics = new HashMap();
        perimeter_statistics.put("Graphene_CVD_growth", new Graphene_perimeter_statistics());
        perimeter_statistics.put("Ag_Ag_growth", new Ag_Ag_perimeter_statistics());
    }

    public Abstract_perimeter_statistics getStatistics(String statisticsName) {

        return perimeter_statistics.get(statisticsName);
    }
}
