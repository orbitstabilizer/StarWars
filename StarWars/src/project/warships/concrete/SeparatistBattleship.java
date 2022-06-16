package project.warships.concrete;

import java.util.ArrayList;

import project.sector.Sector;
import project.crewman.Crewman;

public class SeparatistBattleship extends SeparatistDestroyer {
	/**
	 * creates a new SeparatistBattleship
	 * @param id - id of the warship
	 * @param name - name of the warship
	 * @param currentSector - current sector of the warship
	 * @param coordinate - coordinate of the warship
	 * @param crew - crew of the warship
	 */
	public SeparatistBattleship(int id, String name, Sector currentSector, int coordinate, ArrayList<Crewman> crew) {
		super(id, name, currentSector,  coordinate,crew);
		this.armamentPower = 400;
		this.shieldPower = 200;
		this.crewCapacity = 20;
		this.escapePods = 3;
	}
	

}
