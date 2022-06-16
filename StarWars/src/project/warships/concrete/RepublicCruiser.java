package project.warships.concrete;



import java.util.ArrayList;
import java.util.List;

import project.crewman.General;
import project.crewman.concrete.Jedi;
import project.crewman.concrete.Officer;
import project.crewman.concrete.Sith;
import project.enums.CrewmanState;
import project.sector.Sector;
import project.crewman.Crewman;
import project.enums.Affiliation;
import project.warships.Warship;

public class RepublicCruiser extends Warship {
	private final ArrayList<Crewman> captives;

	/**
	 * creates a new RepublicCruiser
	 * @param id the id of the warship
	 * @param name the name of the warship
	 * @param currentSector the sector the warship is in
	 * @param coordinate the coordinate of the warship
	 * @param crew the crew of the warship
	 */
	public RepublicCruiser(int id, String name, Sector currentSector, int coordinate, ArrayList<Crewman> crew) {
		super(id, name, currentSector, coordinate, 100, 100, 10, crew, Affiliation.REPUBLIC);
		this.captives = new ArrayList<>();
	}

	/**
	 * visits Corousant
	 */
	public void visitCorousant() {
		for (Crewman crewman : captives) {
			crewman.setState(CrewmanState.IMPRISONED);
			crewman.setCurrentWarship(null);
		}
		captives.clear();
		crew.stream()
				.filter(c -> c instanceof General)
				.forEach(c-> ((Jedi)c).resetSanity());
	}

	/**
	 * attack protocol specific to RepublicCruiser
	 * @param warship to be attacked
	 */
	@Override
	public void attack(Warship warship) {
		if (this.getAffiliation() == warship.getAffiliation()) return;
		if (warship instanceof SeparatistDestroyer sd) {
			this.jumpToSector(warship.getCurrentSector(),warship.getCoordinate());
			Jedi repCom = (Jedi) this.getCommander();
			Sith sepCom = (Sith) sd.getCommander();
			boolean isPersuaded = sepCom.persuade(repCom);
			if (isPersuaded) {
				this.getDestroyedWithBetrayal(sepCom, warship);
			} else if (this.getPowerOutput() > sd.getPowerOutput()) {
				sd.getDestroyedByCombat(repCom, this);
			} else {
				this.getDestroyedByCombat(sepCom, warship);
			}
		}

	}

	/**
	 * adds a crewman according to jedi policy
	 * @param crewman the crewman to be added
	 */
	@Override
	public void addCrewman(Crewman crewman) {
		if (!hasSpace() || crewman.getState()!= CrewmanState.FREE) return;
		if (crewman instanceof Jedi) {
			crew.add(crewman);
			crewman.setState(CrewmanState.ONBOARD);
			crewman.setCurrentWarship(this);
		}else if (crewman instanceof Officer) {
			crew.add(crewman);
			crewman.setState(CrewmanState.ONBOARD);
			crewman.setCurrentWarship(this);
		}

	}

	/**
	 * when jedi looses all its sanity, the warship is destroyed
	 * @param sepCom the sith commander of the attacking warship
	 * @param warship  the warship destroying this one
	 */
	public void getDestroyedWithBetrayal(Sith sepCom, Warship warship) {
		General commander = getCommander();
		crew.forEach(crewman -> {
			if (!crewman.equals(commander)){
				commander.kill(crewman);
			}
		});
		captives.forEach(commander::kill);
		sepCom.kill(commander);
		this.setDestroyedBy(warship);
	}

	/**
	 * if power output of attacking warship is greater than this one, this one is destroyed
	 * @param sepCom the sith commander of the attacking warship
	 * @param warship warship destroying this warship
	 */
	@Override
	public void getDestroyedByCombat(General sepCom, Warship warship) {
		crew.forEach(sepCom::kill);
		captives.forEach(sepCom::kill);
		this.setDestroyedBy(warship);
	}

	/**
	 * adds a crewman to the captive list
	 * @param officers the officers to be imprisoned
	 */
	public void imprison(List<Crewman> officers) {
		officers.forEach(officer -> {
			officer.setCurrentWarship(this);
			captives.add(officer);

		});

	}
}
