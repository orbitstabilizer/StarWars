package project.warships;

import java.util.*;

import project.enums.Intrinsic;
import project.enums.CrewmanState;
import project.enums.WarshipState;
import project.sector.Sector;
import project.crewman.Crewman;
import project.crewman.General;
import project.crewman.concrete.Officer;
import project.enums.Affiliation;
import project.interfaces.IWarship;


public abstract class Warship implements IWarship , Comparable<Warship> {

	protected final int Id; //(will be unique for each warship)
	protected final String name;
	protected Sector currentSector;
	private int coordinate;
	protected final Affiliation aﬃliation;
	protected int armamentPower;
	protected int shieldPower;
	protected int crewCapacity;
	protected WarshipState state;
	protected Warship destroyedBy;
	protected ArrayList<Crewman> crew;
	// TODO: Ids are strictly increasing, can be useful
	//TODO: don't forget that you have to update TreeSet bu first removing then adding
	// generals.add(generals.pollFirst());


	/**
	 *
	 * @param id  id of the warship (will be unique for each warship)
	 * @param name name of the warship
	 * @param currentSector current sector of the warship
	 * @param coordinate coordinate of the warship
	 * @param armamentPower armament power of the warship
	 * @param shieldPower shield power of the warship
	 * @param crewCapacity crew capacity of the warship
	 * @param crew crew of the warship
	 * @param aﬃliation affiliation of the warship
	 */
	public Warship(int id, String name, Sector currentSector, int coordinate, int armamentPower, int shieldPower,
				   int crewCapacity, ArrayList<Crewman> crew, Affiliation aﬃliation) {
		Id = id;
		this.name = name;
		this.currentSector = currentSector;
		this.coordinate = coordinate;
		this.armamentPower = armamentPower;
		this.shieldPower = shieldPower;
		this.crewCapacity = crewCapacity;
		this.aﬃliation = aﬃliation;
		this.state = WarshipState.INTACT;
		this.destroyedBy = null;
		this.crew = crew;
		crew.forEach(c -> {
			c.setCurrentWarship(this);
			c.setState(CrewmanState.ONBOARD);
			c.setCurrentWarship(this);
		});
	}

	/**
	 *
	 * @param crewman crewman to be removed from the warship
	 */
	@Override
	public void removeCrewman(Crewman crewman) {
		if (crewman.getState() != CrewmanState.ONBOARD) return;
		if (crewman.getCurrentWarship() != this) return;
		long generalCount = crew.stream().filter(c -> c instanceof General).count();
		if (crewman.getState() == CrewmanState.ONBOARD) {
			if (crewman instanceof General) {
				if (generalCount> 1) {
					crew.remove(crewman);
					crewman.setState(CrewmanState.FREE);
					crewman.setCurrentWarship(null);
				}

			} else if (crewman instanceof Officer) {
				crew.remove(crewman);
				crewman.setState(CrewmanState.FREE);
				crewman.setCurrentWarship(null);
			}
		}

	}

	private static HashMap<Intrinsic,Integer> oc = new HashMap<>();

	/**
	 * A warship’s power output depends on many factors:
	 * 	Armament Power,
	 * 	Shield Power,
	 * 	Generals’ Contribution: the sum of all generals’ combat power who is in the crew
	 * 	Officers’ Contribution: (PILOTING MAX + 1) *
	 * 							(GUNNERY MAX + 1) *
	 * 							(ENGINEERING MAX + 1)*
	 * 							(TACTICAL MAX + 1) *
	 * 							(COMMAND MAX + 1)
	 * 	Sector Buff: If a warship and a sector are affiliated to the same organization,
	 * 					Sector Buff is 3.
	 * 				 If they aren’t, Sector Buff is 2.
	 * @return the power of the warship: Sector Buff*   ( Armament Power+
	 * 													  Shield Power+
	 * 													  General’s Contribution+
	 * 													  Officer’s Contribution )
	 *
	 * @return the power of the warship
	 */
	@Override
	public int getPowerOutput() {
		if (state == WarshipState.DESTROYED)
			return Integer.MIN_VALUE;
		int gc =0;
		Arrays.stream(Intrinsic.values()).forEach(intrinsic -> oc.put(intrinsic,0));
		for (Crewman crewman : crew) {
			if (crewman instanceof General) {
				gc += ((General) crewman).getCombatPower();
			}else{
				var officer = (Officer) crewman;
				if (oc.get(officer.getIntrinsic()) < officer.getIntrinsicLevel())
					oc.put(officer.getIntrinsic(),officer.getIntrinsicLevel());
			}
		}
		int occ = 1;
		for (var o : oc.values()) {
			occ *= (o + 1);
		}
		int sectorBuff = this.currentSector.getAffiliation() == this.aﬃliation ? 3 : 2;

		return sectorBuff * (armamentPower + shieldPower + gc + occ);
	}

	/**
	 *
	 * @return the commander of the warship
	 */
	@Override
	public General getCommander() {
		var ans = crew.stream()
				.filter(crewman -> crewman instanceof General)
				.min(Comparator.comparing(g -> ((General) g)));
		try{
			return (General) ans.get();
		}catch (NoSuchElementException e){
			return null; // should never happen
		}
	}

	/**
	 *
	 * @param amount amount of armament power to be added to the warship
	 */
	@Override
	public void upgradeArmament(int amount) {
		armamentPower += amount;

	}

	/**
	 *
	 * @param amount amount of shield power to be added to the warship
	 */
	@Override
	public void upgradeShield(int amount) {
		shieldPower += amount;

	}

	/**
	 * jumps to the given sector
	 * @param sector sector to jump to
	 * @param coordinate coordinate to jump to
	 */
	@Override
	public void jumpToSector(Sector sector, int coordinate) {
		if (state == WarshipState.DESTROYED) return;
		currentSector.removeWarship(this);
		this.currentSector = sector;
		this.coordinate = coordinate;
		sector.addWarship(this);

	}

	/**
	 *
	 * @return the current sector of the warship
	 */
	public Sector getCurrentSector() {
		return currentSector;
	}

	/**
	 *
	 * @param warship to be attacked
	 */
	abstract public void attack(Warship warship);

	/**
	 * destroys this warship
	 * @param general commander of the warship destroying this warship
	 * @param warship warship destroying this warship
	 */
	abstract public void getDestroyedByCombat(General general, Warship warship);

	/**
	 * @param warship sets warship that destroyed this warship
	 */
	protected void setDestroyedBy(Warship warship) {
		this.crew.clear();
		this.state = WarshipState.DESTROYED;
		this.currentSector.removeWarship(this);
		this.destroyedBy = warship;
	}

	/**
	 *
	 * @return hashcode of the warship [id]
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Id);
	}

	/**
	 *
	 * @return true if warship has space for more crewmen
	 */
	protected boolean hasSpace() {
		return crew.size() < crewCapacity;
	}

	/**
	 *
	 * @return state of the warship
	 */
	public WarshipState getState() {
		return state;
	}

	/**
	 *
	 * @return the warship's name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return the warship that destroyed this warship/ null if this warship is not destroyed
	 */
	public Warship getDestroyedBy() {
		return destroyedBy;
	}

	/**
	 *
	 * @return the warship's coordinate
	 */
	public int getCoordinate() {
		return coordinate;
	}

	/**
	 *
	 * @return the warship's id
	 */
	public int getId() {
		return Id;
	}

	/**
	 *
	 * @return the warship's affiliation
	 */
	public Affiliation getAffiliation() {
		return aﬃliation;
	}

	/**
	 * @param o object to be compared
	 * @return the warship with the higher power/ if the power is equal, the warship with the lower id is returned
	 */
	@Override
	public int compareTo(Warship o) {
		var tmp = Integer.compare(o.getPowerOutput(),getPowerOutput());
		if(tmp != 0) return tmp;
		return Integer.compare(getId(),o.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Warship warship = (Warship) o;
		return Id == warship.Id;
	}
	@Override
	public String toString() {
		return "Warship{" +
				"Id=" + Id +
				", name='" + name + '\'' +
				", coordinate=" + getCoordinate() +
				", aﬃliation=" + aﬃliation +
				'}';
	}
}
