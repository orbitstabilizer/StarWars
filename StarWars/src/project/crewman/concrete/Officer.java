package project.crewman.concrete;

import project.crewman.Crewman;
import project.enums.Intrinsic;

public class Officer extends Crewman implements Comparable<Officer> {
	private int intrinsicLevel;
	private final Intrinsic intrinsic;

	/**
	 *
	 * @return the intrinsic level of the Officer
	 */
	public int getIntrinsicLevel() {return intrinsicLevel;}
	/**
	 *
	 * @return the Intrinsic type of the Officer
	 */
	public Intrinsic getIntrinsic() {return intrinsic;}

	public Officer(int id, String name,Intrinsic intrinsic, int intrinsicLevel) {
		super(id, name);
		this.intrinsic = intrinsic;
		this.intrinsicLevel = intrinsicLevel;
	
	}

	/**
	 * trains the Officer, if level is 10 nothing happens
	 */
	 public void train() {
		 if(intrinsicLevel < 10) {
			 intrinsicLevel++;
		 }
	 }

	/**
	 * officer with higher intrinsic level has higher priority
	 * in case of equality the one with the lower id is the one with higher priority
	 * @param o the other Officer to compare
	 * @return if this Officer has more priority than the other
	 */
	@Override
	public int compareTo(Officer o) {
		if (this.intrinsicLevel == o.intrinsicLevel)
			return Integer.compare(this.id, o.id);
		return  -Integer.compare(this.intrinsicLevel, o.intrinsicLevel);// (-) more intrinsicLevel has higher priority
	}

}
