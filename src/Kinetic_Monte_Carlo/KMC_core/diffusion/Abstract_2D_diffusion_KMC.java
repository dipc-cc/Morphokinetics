/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.diffusion;

import Kinetic_Monte_Carlo.KMC_core.*;
import Kinetic_Monte_Carlo.atom.diffusion.Abstract_2D_diffusion_atom;
import Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator.DevitaAccelerator;
import Kinetic_Monte_Carlo.atom.diffusion.Modified_Buffer;
import Kinetic_Monte_Carlo.lattice.Abstract_lattice;
import Kinetic_Monte_Carlo.lattice.diffusion.Abstract_2D_diffusion_lattice;
import Kinetic_Monte_Carlo.list.Linear_list;
import Kinetic_Monte_Carlo.list.List_configuration;
import java.awt.geom.Point2D;
import utils.MathUtils;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_2D_diffusion_KMC extends Abstract_KMC {

    protected Abstract_2D_diffusion_lattice lattice;
    protected Modified_Buffer modified_buffer;
    protected boolean justCentralFlake;
    protected Round_perimeter perimeter;
    protected DevitaAccelerator accelerator;

    public Abstract_2D_diffusion_KMC(List_configuration config, boolean justCentralFlake) {

        super(config);
        this.justCentralFlake = justCentralFlake;
        this.modified_buffer = new Modified_Buffer();
        this.list.autoCleanup(true);
    }

    public void setIslandDensityAndDepositionRate(double depositionRateML, double islandDensitySite) {

        if (justCentralFlake) {
            list.set_deposition_probability(depositionRateML / islandDensitySite);
        } else {
            list.set_deposition_probability(depositionRateML * lattice.getSizeX() * lattice.getSizeY());
        }
    }

    @Override
    public void initializeRates(double[] rates) {

        lattice.reset();
        list.reset();
        //we modify the 1D array into a 2D array;
        int length = (int) Math.sqrt(rates.length);
        double[][] processProbs2D = new double[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                processProbs2D[i][j] = rates[i * length + j];
            }
        }
        lattice.configure(processProbs2D);
        depositSeed();
    }

    @Override
    public Abstract_lattice getLattice() {
        return lattice;
    }

    @Override
    protected boolean perform_simulation_step() {

        Abstract_2D_diffusion_atom originAtom = ((Abstract_2D_diffusion_atom) list.next_event(RNG));
        Abstract_2D_diffusion_atom destinationAtom;
        
        if (originAtom == null) {
            destinationAtom = depositNewAtom();

        } else {
            destinationAtom = choose_random_hop(originAtom);
            if (destinationAtom.is_outside()) {
                destinationAtom = this.perimeter.getPerimeterReentrance(originAtom);
            }
            this.diffuseAtom(originAtom, destinationAtom);
        }

        if (PerimeterMustBeEnlarged(destinationAtom)) {
            int nextRadius = this.perimeter.goToNextRadius();
            if (nextRadius > 0) {
                this.perimeter.setAtomPerimeter(lattice.setInside(nextRadius));
            } else {
                return true;
            }
        }  
        return false;
    }
    
    public void simulate(int iterations) {
		int radius = perimeter.getCurrentRadius();
		int num_events = 0;// contador de eventos desde el ultimo cambio de radio
	
		iterations_for_last_simulation = 0;
	
		for (int i = 0; i < iterations; i++) {
			if (perform_simulation_step())
				break;
			
			iterations_for_last_simulation++;
			num_events++;
			
			if (radius == 20 && radius == perimeter.getCurrentRadius()) {//En la primera etapa no hay una referencia de eventos por lo que se pone un numero grande
				if (num_events == 4000000)
					break;
			} else if (radius != perimeter.getCurrentRadius()) {//Si cambia de radio se vuelve a empezar a contar el nuevo numero de eventos
				radius = perimeter.getCurrentRadius();
				num_events = 0;
			} else {
				if ((iterations_for_last_simulation - num_events) * 1.7 <= num_events) //Si los eventos durante la ultima etapa son 1.X veces mayores que los habidos hasta la etapa anterior Fin.
					break;
				
			}

		}

		list.cleanup();
	}
    private Abstract_2D_diffusion_atom choose_random_hop(Abstract_2D_diffusion_atom source) {
        if (accelerator != null) {
            return accelerator.choose_random_hop(source);
        }
        return source.choose_random_hop();
    }

    protected boolean depositAtom(int X, int Y) {
        return this.depositAtom(lattice.getAtom(X, Y));
    }

    protected boolean extractAtom(Abstract_2D_diffusion_atom origin) {
        if (!origin.isOccupied()) {
            return false;
        }

        origin.extract();

        modified_buffer.updateAtoms(list, lattice);
        return true;
    }

    protected boolean depositAtom(Abstract_2D_diffusion_atom origin) {
        if (origin.isOccupied()) {
            return false;
        }

        boolean force_nucleation = (!justCentralFlake && origin.two_terrace_together())  ; //indica si 2 terraces se van a chocar    
        origin.deposit(force_nucleation);
        modified_buffer.updateAtoms(list, lattice);
        return true;

    }

    protected boolean diffuseAtom(Abstract_2D_diffusion_atom origin, Abstract_2D_diffusion_atom destination) {

        if ((!origin.isEligible() || destination.isOccupied()) && (origin != destination)) {
            return false;
        }
        
        boolean force_nucleation = (!justCentralFlake && destination.two_terrace_together())  ; //indica si 2 terraces se van a chocar    
        origin.extract();
        destination.deposit(force_nucleation);
        modified_buffer.updateAtoms(list, lattice);

        return true;
    }

    protected Abstract_2D_diffusion_atom depositNewAtom() {
        Abstract_2D_diffusion_atom destinationAtom;
        if (!justCentralFlake) {
            do {
                int X = (int) (StaticRandom.raw() * lattice.getSizeX());
                int Y = (int) (StaticRandom.raw() * lattice.getSizeY());
                destinationAtom = lattice.getAtom(X, Y);
            } while (!this.depositAtom(destinationAtom));

        } else {
            do {
                destinationAtom = this.perimeter.getRandomPerimeterAtom();
            } while (!this.depositAtom(destinationAtom));
        }
        return destinationAtom;
    }

    protected boolean PerimeterMustBeEnlarged(Abstract_2D_diffusion_atom destinationAtom) {
        return destinationAtom.getType() > 0 && justCentralFlake && lattice.getDistanceToCenter(destinationAtom.getX(), destinationAtom.getY()) >= (this.perimeter.getCurrentRadius() - 2);
    }

    @Override
    public void getSampledSurface(float[][] surface) {
        int binY = surface.length;
        int binX = surface[0].length;

        Point2D corner1 = lattice.getSpatialLocation(0, 0);
        double scaleX = Math.abs(binX / (lattice.getSpatialSizeX()));
        double scaleY = Math.abs(binY / (lattice.getSpatialSizeY()));

        if (scaleX > 1 || scaleY > 1) {
            System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
            return;
        }

        for (int i = 0; i < surface.length; i++) {
            for (int j = 0; j < surface[0].length; j++) {
                surface[i][j] = -1;
            }
        }

        for (int i = 0; i < lattice.getSizeY(); i++) {
            for (int j = 0; j < lattice.getSizeX(); j++) {
                if (lattice.getAtom(j, i).isOccupied()) {
                    Point2D position = lattice.getSpatialLocation(j, i);
                    surface[(int) ((position.getY() - corner1.getY()) * scaleY)][(int) ((position.getX() - corner1.getX()) * scaleX)] = 0;
                }
            }
        }
        MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
    }

    protected abstract void depositSeed();
}
