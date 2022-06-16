package project.crewman.concrete;

import project.crewman.General;
import project.enums.Affiliation;

public class Jedi extends General {
	private int sanity;
	private final int intelligence;

	/**
	 *
	 * @return the sanity of the Jedi
	 */
	public int getSanity() {return sanity;}
	/**
	 *
	 * @return the intelligence of the Jedi
	 */
	public int getIntelligence() {return intelligence;}

	/**
	 *
	 * @param id the id of the Jedi
	 * @param name the name of the Jedi
	 * @param experience the experience of the Jedi
	 * @param midichlorian the midichlorian of the Jedi
	 * @param intelligence the intelligence of the Jedi
	 */
	public Jedi(int id, String name, int experience, int midichlorian, int intelligence) {
		super(id, name, experience, midichlorian, Affiliation.REPUBLIC);
		//sanity is initially 100
		this.sanity = 100;
		this.intelligence = intelligence;
	}

	/**
	 *  In a Republic Cruiser, the commander is the one with the highest experience
	 *  in case of equality the one with the lower id will become commander
	 * @return if this general has more priority to be the commander than the other
	 */
	@Override
	public int compareTo(General o) {
		int c1 = Integer.compare(this.experience, o.getExperience());
		if (c1 != 0) return -c1; // (-) more experience has higher priority
		else return Integer.compare(this.id,o.getId());// less id has higher priority
	}

	/**
	 *
	 * @return force power of the Jedi
	 */
	@Override
	public int getForcePower() {
		return 3*midichlorian;
	}

	/**
	 *
	 * @return the combat power of the Jedi
	 */
	@Override
	public int getCombatPower() {
		return getForcePower()+experience+(sanity-100)+intelligence;
	}

	/**
	 * resets the sanity of the Jedi back to 100, called on visit to Corousant
	 */
	public void resetSanity() {
		sanity = 100;
	}

	/**
	 * called when Sith persuade the Jedi
	 * @param i the amount of sanity to decrease
	 */
	public void reduceSanity(int i) {

		sanity -= i;
		if (sanity <=0 ){
			sanity = 0;
		}
	}
}
