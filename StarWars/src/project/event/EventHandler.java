package project.event;

import project.crewman.Crewman;
import project.crewman.concrete.Officer;
import project.enums.CrewmanState;
import project.enums.WarshipState;
import project.sector.Sector;
import project.warships.Warship;
import project.warships.concrete.RepublicCruiser;

import java.util.ArrayList;
import java.util.HashMap;

public class EventHandler {
    HashMap<Integer, Sector> sectors;
    HashMap<Integer, Warship> warships;
    HashMap<Integer, Crewman> crewMen;
    ArrayList<Event> events;

    /**
     * creates an EventHandler object and initializes it with the relevant entities and events
     * @param sectors HashMap containing all sectors
     * @param warships HashMap containing all warships
     * @param crewMen HashMap containing all crewmen
     * @param events ArrayList containing all events
     */
    public EventHandler(HashMap<Integer, Sector> sectors, HashMap<Integer, Warship> warships, HashMap<Integer, Crewman> crewMen, ArrayList<Event> events) {
        this.sectors = sectors;
        this.warships = warships;
        this.crewMen = crewMen;
        this.events = events;
    }

    /**
     * parses the events and calls the relevant handler
     */
    public void handleEvents() {

        for (Event event : events){
            try{
            switch (event.type){
                case ATTACK -> {
                    int attackerId = event.parameters.get(0);
                    int defenderId = event.parameters.get(1);
                    handleAttack(attackerId,defenderId);
                }
                case ASSAULT -> {
                    int sectorId = event.parameters.get(0);
                    handleAssault(sectorId);
                }
                case JUMP_TO_SECTOR -> {
                    int warshipId = event.parameters.get(0);
                    int sectorId = event.parameters.get(1);
                    int coordinate = event.parameters.get(2);
                    handleJumpToSector(warshipId,sectorId,coordinate);
                }
                case VISIT_COROUSANT -> {
                    int cruiserId = event.parameters.get(0);
                    handleVisitCorousant(cruiserId);
                }
                case ADD_CREWMAN -> {
                    int crewmanId = event.parameters.get(0);
                    int warshipId = event.parameters.get(1);
                    handleAddCrewman(crewmanId,warshipId);
                }
                case REMOVE_CREWMAN -> {
                    int crewmanId = event.parameters.get(0);
                    int warshipId = event.parameters.get(1);
                    handleRemoveCrewman(warshipId,crewmanId);
                }
                case TRAIN_OFFICER -> {
                    int officerId = event.parameters.get(0);
                    handleTrainOfficer(officerId);
                }
                case UPGRADE_SHIELD -> {
                    int warshipId = event.parameters.get(0);
                    int amount = event.parameters.get(1);
                    handleUpgradeShield(warshipId,amount);
                }
                case UPGRADE_ARMAMENT -> {
                    int warshipId = event.parameters.get(0);
                    int amount = event.parameters.get(1);
                    handleUpgradeArmament(warshipId,amount);

                }

            }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(event);
            }

        }
    }

    /**
     * handles upgrade armament event
     * if warship does not exist, or it is destroyed, nothing happens
     * @param warshipId warship id
     * @param amount amount of armament to upgrade
     */
    private void handleUpgradeArmament(int warshipId,int amount) {
        var warship = warships.get(warshipId);
        if (warship == null)
        	return;
        if (warships.get(warshipId).getState() != WarshipState.DESTROYED)
            if (warship != null) {
                warship.upgradeArmament(amount);
            }

    }

    /**
     * handles upgrade shield event
     * if warship does not exist, or it is destroyed, nothing happens
     * @param warshipId warship id
     * @param amount amount of shield to upgrade
     */
    private void handleUpgradeShield(int warshipId, int amount) {
        var warship = warships.get(warshipId);
        if (warship == null)
        	return;
        if (warships.get(warshipId).getState() != WarshipState.DESTROYED)
            if (warship != null) {
                warship.upgradeShield(amount);
            }
    }

    /**
     * handles train officer event
     * if officer does not exist, or is dead, nothing happens
     * @param officerId officer id
     */
    private void handleTrainOfficer(int officerId) {
        var crewman = crewMen.get(officerId);
        if (crewman == null)
            return;
        if (crewman.getState() != CrewmanState.DEAD )
            if (crewman instanceof Officer officer) {
                officer.train();
            }
    }
    /**
     * handles remove crewman event
     * if warship does not exist, or it is destroyed, or crewman does not exist, nothing happens
     * @param warshipId warship id
     * @param crewmanId crewman id
     */
    private void handleRemoveCrewman(int warshipId, int crewmanId) {
        var warship = warships.get(warshipId);
        var crewman = crewMen.get(crewmanId);
        if (warship == null || crewman == null) 
        	return;
        if (warship != null && crewman != null) {
            if (warships.get(warshipId).getState() != WarshipState.DESTROYED) {
                warship.removeCrewman(crewman);
            }
        }

    }

    /**
     * handles add crewman event
     * if warship does not exist, or it is destroyed,
     * or crewman does not exist, or crewman is not free, nothing happens
     * @param crewmanId crewman id
     * @param warshipId warship id
     */
    private void handleAddCrewman(int crewmanId, int warshipId) {
        var warship = warships.get(warshipId);
        var crewman = crewMen.get(crewmanId);
        if (warship == null || crewman == null) 
        	return;
        if (warship.getState() != WarshipState.DESTROYED && crewman.getState() == CrewmanState.FREE){
            warship.addCrewman(crewman);
        }

    }

    /**
     * handles jump to sector event
     * if warship does not exist, or it is destroyed,
     * or sector does not exist, nothing happens
     * @param warshipId warship id
     * @param sectorId sector id
     * @param coordinate coordinate
     */
    private void handleJumpToSector(int warshipId, int sectorId, int coordinate) {
        var warship = warships.get(warshipId);
        var sector = sectors.get(sectorId);
        if (warship != null && sector != null) {
            if (warship.getState() != WarshipState.DESTROYED) {
                warship.jumpToSector(sector, coordinate);
            }
        }
    }

    /**
     * handles visit corousant event
     * if warship does not exist, or it is destroyed,
     * or warship is not a Republic cruiser, nothing happens
     * @param cruiserId cruiser id
     */
    private void handleVisitCorousant(int cruiserId) {
        var warship = warships.get(cruiserId);
        if (warship != null) {
            if (warship.getState() != WarshipState.DESTROYED) {
                if (warship instanceof RepublicCruiser cruiser) {
                    cruiser.visitCorousant();
                }
            }
        }
    }

    /**
     * handles assault event in given sector
     * if sector does not exist, nothing happens
     * @param sectorId sector id
     */
    private void handleAssault(int sectorId) {
        var sector = sectors.get(sectorId);
        if (sector != null) {
            sector.assault();
        }
    }

    /**
     * handles attack event for given warships
     * if warship does not exist, nothing happens
     * or if warship is destroyed, nothing happens
     * or if warships have same affiliation, nothing happens
     * @param attackerId attacker id
     * @param defenderId defender id
     */
    private void handleAttack(int attackerId, int defenderId) {
        Warship attacker = warships.get(attackerId);
        Warship defender = warships.get(defenderId);
        if (attacker != null && defender != null) {
            if (attacker.getState() != WarshipState.DESTROYED) {
                if (defender.getState() != WarshipState.DESTROYED) {
                    if (attacker.getAffiliation() != defender.getAffiliation()) {
                        attacker.attack(defender);
                    }
                }
            }
        }
    }
}
