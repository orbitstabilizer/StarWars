package project.warships.concrete;

import project.crewman.Crewman;
import project.crewman.General;
import project.crewman.concrete.Jedi;
import project.crewman.concrete.Officer;
import project.crewman.concrete.Sith;
import project.enums.Affiliation;
import project.enums.CrewmanState;
import project.sector.Sector;
import project.warships.Warship;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class SeparatistDestroyer extends Warship {
	protected int escapePods;

	/**
	 *
	 * @param id - unique id of the warship
	 * @param name - name of the warship
	 * @param currentSector - current sector of the warship
	 * @param coordinate - coordinate of the warship
	 * @param crew - crew of the warship
	 */
	public SeparatistDestroyer(int id, String name, Sector currentSector, int coordinate, ArrayList<Crewman> crew) {
		super(id, name, currentSector, coordinate, 80, 60, 7, crew, Affiliation.SEPARATISTS);
		this.escapePods = 1;
	}

	/**
	 * adds a crewman to this warship according to the sith policy
	 * @param crewman to be added to the warship
	 */
	@Override
	public void addCrewman(Crewman crewman) {
		if (!hasSpace() || crewman.getState()!= CrewmanState.FREE) return;
		if (crewman instanceof Sith) {
			crew.add(crewman);
			crewman.setState(CrewmanState.ONBOARD);
			crewman.setCurrentWarship(this);
		}else if (crewman instanceof Officer o){
			int maxIntrinsicLevel = crew.stream()
					.filter(c -> c instanceof Officer)
					.filter(c -> ((Officer) c).getIntrinsic() == o.getIntrinsic())
					.map(c -> ((Officer) c).getIntrinsicLevel())
					.max(Integer::compareTo).orElse(0);
			if ( maxIntrinsicLevel < o.getIntrinsicLevel() ) {
				crew.add(o);
				o.setState(CrewmanState.ONBOARD);
				o.setCurrentWarship(this);

			}

		}
	}


	/**
	 * carries out attack on the other warship
	 * @param warship to be attacked
	 */
	@Override
	public void attack(Warship warship) {
		if (warship instanceof RepublicCruiser repCruiser) {
			this.jumpToSector(warship.getCurrentSector(),warship.getCoordinate());
			Jedi repCom = (Jedi) repCruiser.getCommander();
			Sith sepCom = (Sith) getCommander();
			boolean isPersuaded = sepCom.persuade(repCom);
			if (isPersuaded) {
				repCruiser.getDestroyedWithBetrayal(sepCom, this);
			} else if (this.getPowerOutput() > repCruiser.getPowerOutput()) {
				repCruiser.getDestroyedByCombat(sepCom, this);
			} else {
				this.getDestroyedByCombat(repCom, warship);
			}
		}
	}

	/**
	 * carries events that occur when the warship is destroyed by combat
	 * @param general commander of the warship destroying this warship
	 * @param warship warship destroying this warship
	 */
	@Override
	public void getDestroyedByCombat(General general, Warship warship) {
		var officers = crew.stream().filter(c -> c instanceof Officer).collect(Collectors.toList());
		var generals = crew.stream().filter(c -> c instanceof General).map(c -> (General) c).toList();
		((RepublicCruiser) warship).imprison(officers);
		ArrayList<General> toBeKilled = new ArrayList<>();
		PriorityQueue<General> toEscape = new PriorityQueue<>();
		for (General g : generals) {
			if (g.getCombatPower() <= general.getCombatPower()) {
				toBeKilled.add(g);
			}else toEscape.add(g);
		}
		for (int i =0;i< escapePods; i++) {
			var g = toEscape.poll();
			if (g!=null) {
				g.setState(CrewmanState.FREE); // TODO: should change current warship
				g.setCurrentWarship(null);
			}
		}
		for (General g : toBeKilled) {
			general.kill(g);
		}
		for (General g: toEscape){
			general.kill(g);
		}
		this.setDestroyedBy(warship);
	}

}
