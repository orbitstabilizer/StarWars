package project.crewman;

import project.crewman.concrete.Officer;
import project.enums.Affiliation;
import project.interfaces.IForceUser;

/**
 * Generals can be commanders of the warships.
 */
public abstract class General extends Crewman implements IForceUser, Comparable<General> {
	protected int experience;
	protected int midichlorian;
	protected Affiliation affiliation;


	/**
	 * @return the experience of the general
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * @return the midichlorian of the general
	 */
	public int getMidichlorian() {
		return midichlorian;
	}

	/**
	 * @return the affiliation of the general
	 */
	public Affiliation getAffiliation() {
		return affiliation;
	}

	/**
	 * creates a new general
	 * @param id the id of the general
	 * @param name the name of the general
	 * @param experience the experience of the general
	 * @param midichlorian the midichlorian of the general
	 * @param affiliation the affiliation of the general
	 */
	public General(int id, String name, int experience, int midichlorian,Affiliation affiliation) {
		super(id, name);
		this.experience = experience;
		this.midichlorian = midichlorian;
		this.affiliation = affiliation;
	}


	/**
	 * only generals can kill
	 * side effects:
	 *  victim's state is set to dead
	 *  experience is increased by the amount of experience of the general
	 *  or intrinsic level of the officer
	 * @param victim the victim of the attack
	 */
	public void kill(Crewman victim){
		int xp = 0;
		if (victim instanceof General) {
			xp = ((General) victim).getExperience();
		}else if (victim instanceof Officer) {
			xp = ((Officer) victim).getIntrinsicLevel();
		}
		this.experience += xp;
		victim.setKilledBy(this);
	}


}
