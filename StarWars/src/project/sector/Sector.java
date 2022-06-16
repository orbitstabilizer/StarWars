package project.sector;

import project.enums.Affiliation;
import project.enums.WarshipState;
import project.warships.Warship;
import project.warships.concrete.SeparatistDestroyer;

import java.util.*;

public class Sector {
	private final int id;
	private final String name;
	private final Affiliation affiliation;
	public HashMap<Integer,Warship> warships;

	/**
	 *
	 * @return the sector's id
	 */
	public int getId() {
		return id;
	}

	/**
	 *
	 * @return the sector's name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return the sector's affiliation
	 */
	public Affiliation getAffiliation() {
		return affiliation;
	}

	/**
	 *
	 * @param id the sector's id
	 * @param name the sector's name
	 * @param affiliation the sector's affiliation
	 */
	public Sector(int id, String name, Affiliation affiliation) {
		this.id = id;
		this.name = name;
		this.affiliation = affiliation;
		warships = new HashMap<>();
	}

	/**
	 * adds the warship to the sector
	 * @param warship the warship to add to the sector
	 */
	public void addWarship(Warship warship){
		warships.put(warship.getId(), warship);
	}

	/**
	 * removes the warship from the sector
	 * @param warship the warship to remove from the sector
	 */
	public void removeWarship(Warship warship){
		warships.remove(warship.getId());
	}
	
	
	
	/*
	 * brute-force:
	 * 0 N := Warship-Count
	 * 1 initialize warships ArrayList                     : O(N)
	 * 2 sort warships according to x                      : O(N*log(N))
	 * 3 initialize hasTargeted ArrayList                  : O(N)
	 * 4 initialize powers ArrayList      			       : O(N) // calculating power output is O(1)
	 * 5 For-each i in range(0, N):                        : O(N)*O(N) = O(N^2)
	 * 7	if warship[i] is attackable:               	   : O(1)
	 * 8 		For-each j in range(i-1,0,-1):             : O(N)*O(1) = O(N)
	 * 9    		if warship[j] can target warship[i]:   : O(1)
	 * 10				warship[j].attack(warship[i])      : O(1)
	 * 11				hasTargeted[i] = True              : O(1)
	 * 12			hasTargeted[j] = True	          	   : O(1)
	 * 
	 * 												   : Worst-case[ all ships are Separatist ] : O(N^2) 
	 * 												   : Best-case [ all ships are Republican ] : O(N*log(N)
	 * 												   
	 * 
	 * 					
	 * 
	 */
	/*
	public void assault() {
		ArrayList<Warship> warships = new ArrayList<>(this.warships.values());
		warships.sort(Comparator.comparingInt(Warship::getCoordinate));
		boolean[] hasTargeted = new boolean[warships.size()];
		Arrays.fill(hasTargeted, false);
		ArrayList<Integer> powers = new ArrayList<>();
		for (Warship warship : warships) {
			powers.add(warship.getPowerOutput());
		}

		for (int s = 0 ;s< warships.size();s++){
			if (warships.get(s).getAffiliation() != Affiliation.SEPARATISTS) continue;
			if (warships.get(s).getState() == WarshipState.DESTROYED) continue;
			for (int r = s-1; r>=0; r--){
				if (warships.get(r).getAffiliation() != Affiliation.REPUBLIC) continue;
				if (warships.get(r).getState() == WarshipState.DESTROYED) continue;
				if (hasTargeted[r]) continue;
				if ( powers.get(r)> powers.get(s)){
					if (!hasTargeted[s]){
						warships.get(r).attack(warships.get(s));
						hasTargeted[s] = true;
					}
					hasTargeted[r] = true;
				}
			}
			hasTargeted[s] = true;
		}

	}
	*/
	
	/**
	 * this method uses custom AssaultHandler data structure for handling the assault event
	 * ADT: AssaultHandler -> building the data structure : O(n)
	 *  - setTarget(Warship target)          -> set the target of the assault : O(1)
	 *  - getTarget(Warship republicCruiser) -> get the target of the republicCruiser : O(log n)
	 *  - setVisited(Warship separatistDestroyer) -> sets separatistDestroyer that got targeted as visited : O(log n)
	 * details of the data structure can be found in the AssaultHandler class, it is a modified version of the Segment Tree data structure
	 *
	 * procedure: O(n*log n)
	 * 1 filter Separatist ships to a list                       : O(n)
	 * 2 create a list of all warships                           : O(n)
	 * 3 sort the list of warships by their coordinates          : O(n log n)
	 * 4 sort the list of Separatist ships by their coordinates  : O(n*log n)
	 * 5 create a AssaultHandler data structure                  : O(n)
	 * 6 iterate over the warships list                          : O(n)*O(log n)
	 * 7 	if the warships is a republic cruiser
	 * 8		get the target of the assault                    : O(log n)
	 * 9		update the target of the assault                 : O(1)
	 * 10	else
	 * 11	   	set the warship as visited                       : O(log n)
	 * 12 iterate over all the attacks 							 : O(n)
	 * 13 	execute the attack                                   : O(1)
	 */
	public void assault(){
		ArrayList<Warship> destroyers = new ArrayList<>();
		ArrayList<Warship> warships = new ArrayList<>();
		for (Warship warship : this.warships.values()) {
			if (warship.getState() == WarshipState.DESTROYED) continue;
			if (warship instanceof SeparatistDestroyer){
				destroyers.add(warship);
			}
			warships.add(warship);
		}
		if(destroyers.size() == 0) return;
		destroyers.sort(Comparator.comparingInt(Warship::getCoordinate));
		warships.sort(Comparator.comparingInt(Warship::getCoordinate));

		AssaultHandler assaultHandler = new AssaultHandler(destroyers);
		for (Warship warship : warships) {
			if (warship.getAffiliation() == Affiliation.REPUBLIC) {
				var target= assaultHandler.getTarget(warship);
				if (target != null) {
					assaultHandler.setTarget(target,warship);
				}
			}else {
				assaultHandler.setVisited(warship);
			}
		}
		var attackMap = assaultHandler.getAttackMap();
		for (var target : attackMap.keySet()) {
			var warship = attackMap.get(target);
			warship.attack(target);
		}

	}

	@Override
	public String toString() {
		return "Sector{" +
				"id=" + id +
				", name='" + name + '\'' +
				", affiliation=" + affiliation +
				'}';
	}
}
