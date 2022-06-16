package project.warships.concrete;

import java.util.ArrayList;

import project.sector.Sector;
import project.crewman.Crewman;

public class SeparatistFrigate extends SeparatistDestroyer {

	/**
	 * creates a new SeparatistFrigate
	 * @param id - id of the warship
	 * @param name - name of the warship
	 * @param currentSector - current sector of the warship
	 * @param coordinate - coordinate of the warship
	 * @param crew - crew of the warship
	 */
	public SeparatistFrigate(int id, String name, Sector currentSector, int coordinate, ArrayList<Crewman> crew) {
		super(id, name, currentSector,  coordinate,crew);
		this.armamentPower = 120;
		this.shieldPower = 100;
		this.crewCapacity = 12;
		this.escapePods = 2;
		
	}
	
}
