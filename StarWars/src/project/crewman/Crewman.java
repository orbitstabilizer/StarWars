package project.crewman;

import project.enums.CrewmanState;
import project.warships.Warship;


public abstract class Crewman {
	protected final int id;
	protected final String name;
	private Warship currentWarship;
	private Crewman killedBy;
	protected CrewmanState state;

	/**
	 *
	 * @return current state of the crewman
	 */
	public CrewmanState getState() {
		return state;
	}

	/**
	 *
	 * @return id of the crewman
	 */
	public int getId() {
		return id;
	}

	/**
	 *
	 * @return name of the crewman
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return crewman who killed this crewman, if the crewman is dead
	 * else returns null
	 */
	public Crewman getKilledBy() {
		return killedBy;
	}

	public Warship getCurrentWarship() {
		return currentWarship;
	}
	/**
	 *
	 * @param general sets the killedBy field of this crewman to the given general
	 */
	public void setKilledBy(General general) {
		this.killedBy = general;
		this.state = CrewmanState.DEAD;
	}
	/**
	 *
	 * @param state new state of the crewman
	 */
	public void setState(CrewmanState state) {
		this.state = state;
	}

	/**
	 *
	 * @param warship sets the currentWarship field of this crewman to the given warship
	 */
	public void setCurrentWarship(Warship warship) {
		this.currentWarship = warship;
	}


	/**
	 *
	 * @param id id of the crewman
	 * @param name name of the crewman
	 */
	public Crewman(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.state = CrewmanState.FREE;
		this.killedBy = null;
	}


	/**
	 *
	 * @param o object to compare with
	 * @return true if the given object is a crewman and has the same id
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Crewman crewman = (Crewman) o;
		return id == crewman.id;
	}

	@Override
	public String toString() {
		return "Crewman{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
