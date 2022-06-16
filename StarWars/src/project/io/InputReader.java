package project.io;

import project.crewman.Crewman;
import project.crewman.concrete.Jedi;
import project.crewman.concrete.Officer;
import project.crewman.concrete.Sith;
import project.enums.Affiliation;
import project.enums.EventType;
import project.enums.Intrinsic;
import project.event.Event;
import project.sector.Sector;
import project.warships.Warship;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InputReader {
    private Scanner input;
    HashMap<Integer, Sector> sectors;
    HashMap<Integer, Warship> warships;
    HashMap<Integer, Crewman> crewMen;

    /**
     * Constructor for InputReader
     * @param fileName  the name of the file to be read
     * @param sectors  the HashMap of Sectors
     * @param warships the HashMap of Warships
     * @param crewMen the HashMap of Crewmen
     */
    public InputReader(String fileName,HashMap<Integer, Sector> sectors, HashMap<Integer, Warship> warships, HashMap<Integer, Crewman> crewMen) {
        File inputFile = new File(fileName);
        try {
            this.input = new Scanner(inputFile);
            this.crewMen = crewMen;
            this.sectors = sectors;
            this.warships = warships;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Reads the input file and creates the entities
     */
    public void extractEntities() {
        if (!input.hasNext()) return;
        generateSectors();
        generateCrewmen();
        generateWarships();


    }

    /**
     * parses input file and generates relevant Events
     * @return  the ArrayList of Events
     */
    public  ArrayList<Event> extractEvents(){
        if (!input.hasNext()) return null;
        ArrayList<Event> events = new ArrayList<>();
        int eventCount = input.nextInt();
        for(int i = 0; i<eventCount ; i++){
            switch (input.nextInt()){
                case 10-> events.add(new Event(EventType.ATTACK, input.nextInt(),input.nextInt()));
                case 11-> events.add(new Event(EventType.ASSAULT,input.nextInt()));
                case 20-> events.add(new Event(EventType.JUMP_TO_SECTOR,input.nextInt(),input.nextInt(),input.nextInt()));
                case 30-> events.add(new Event(EventType.VISIT_COROUSANT,input.nextInt()));
                case 40-> events.add(new Event(EventType.ADD_CREWMAN,input.nextInt(),input.nextInt()));
                case 41-> events.add(new Event(EventType.REMOVE_CREWMAN,input.nextInt(),input.nextInt()));
                case 50-> events.add(new Event(EventType.TRAIN_OFFICER,input.nextInt()));
                case 51->{
                    int warshipId = input.nextInt();
                    String armOrShield = input.next();
                    int amount = input.nextInt();
                    if (armOrShield.equals("Armament")){
                        events.add(new Event(EventType.UPGRADE_ARMAMENT,warshipId,amount));
                    }else if (armOrShield.equals("Shield")){
                        events.add(new Event(EventType.UPGRADE_SHIELD,warshipId,amount));
                    }
                }
            }
        }
        return events;
    }

    /**
     * generates warships from input file
     */
    private void generateWarships() {
        int warshipCount = input.nextInt();
        for (int i = 1 ; i<= warshipCount; i++) {
            String className = input.next();
            String warshipName = input.next();
            int sectorId = input.nextInt();
            int coordinate = input.nextInt();
            ArrayList<Crewman> crew = new ArrayList<>();
            int crewSize = input.nextInt();
            for(int j = 0; j< crewSize;j++){
                crew.add(crewMen.get(input.nextInt()));
            }
            try {
                Warship w = (Warship) Class.forName("project.warships.concrete."+className)
                        .getConstructor(
                                int.class,
                                String.class,
                                Sector.class,
                                int.class,
                                ArrayList.class)
                        .newInstance(i, warshipName, sectors.get(sectorId), coordinate, crew);
                warships.put(i, w);
                sectors.get(sectorId).addWarship(w);


            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * generates crewmen from input file
     */
    private void generateCrewmen() {
        int crewmanCount = input.nextInt();
        String crewmanType;
        for(int i = 1; i <= crewmanCount; i++) {
            crewmanType = input.next();
            switch (crewmanType){
                case "Officer":
                    String name = input.next();
                    Intrinsic intrinsic = Intrinsic.valueOf(input.next());
                    int intrinsicLevel = input.nextInt();
                    crewMen.put(i, new Officer(i, name, intrinsic, intrinsicLevel));
                    break;
                case "Jedi":
                    name = input.next();
                    int experience = input.nextInt();
                    int midichlorian = input.nextInt();
                    int intelligence = input.nextInt();
                    crewMen.put(i, new Jedi(i, name, experience, midichlorian, intelligence));
                    break;
                case "Sith":
                    name = input.next();
                    experience = input.nextInt();
                    midichlorian = input.nextInt();
                    int persuasion = input.nextInt();
                    crewMen.put(i, new Sith(i, name, experience, midichlorian, persuasion));

            }
        }
    }

    /**
     * generates sectors from input file
     */
    private void generateSectors() {
        int sectorCount = input.nextInt();
        String sectorName,sectorAffiliation;
        for(int i = 1; i <= sectorCount; i++) {
            sectorName = input.next();
            sectorAffiliation = input.next();
            sectors.put(i, new Sector(i, sectorName, Affiliation.valueOf(sectorAffiliation)));
        }
    }


}
