package project.crewman.concrete;

import project.crewman.General;
import project.enums.Affiliation;

public class Sith extends General {
	private final int persuasion;

	/**
	 *
	 * @param id the id of the Sith
	 * @param name the name of the Sith
	 * @param experience the experience of the Sith
	 * @param midichlorian the midichlorian of the Sith
	 * @param persuasion the persuasion of the Sith
	 */
	public Sith(int id, String name, int experience, int midichlorian, int persuasion) {
		super(id, name, experience, midichlorian, Affiliation.SEPARATISTS);
		this.persuasion = persuasion;
	}

	/**
	 *  In a Separatist ship since the dark side only cares about power,
	 *  the commander is the one who has the highest combat power
	 *  (in case of equality the one with the lower id will become the commander)
	 * @return if this general has more priority to be the commander than the other
	 */
	@Override
	public int compareTo(General o) {
		int c1 = Integer.compare(this.getCombatPower(), o.getCombatPower());
		if (c1 != 0) return -c1; // (-) more combat power has higher priority
		else return Integer.compare(this.id, o.getId());// less id has higher priority
	}

	/**
	 *
	 * @return the force power of the Sith
	 */
	@Override
	public int getForcePower() {
		return 4*midichlorian;
	}

	/**
	 *
	 * @return the combat power of the Sith
	 */
	@Override
	public int getCombatPower() {
		return getForcePower() + experience + persuasion;
	}

	/**
	 * if jedi has lower intelligence than this persuasion, he will reduce jedi's sanity
	 * @param jedi the Jedi to be persuaded
	 * @return if the Sith persuaded the Jedi
	 */
    public boolean persuade(Jedi jedi) {
		if (jedi.getIntelligence() <= this.persuasion) {
			jedi.reduceSanity(this.persuasion - jedi.getIntelligence());
		}
		return jedi.getSanity()<= 0;
	}
}
