package project.io;

import project.crewman.Crewman;
import project.crewman.General;
import project.crewman.concrete.Jedi;
import project.crewman.concrete.Officer;
import project.enums.WarshipState;
import project.warships.Warship;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class OutputGenerator {
    FileWriter logFile;
    ArrayList<Crewman> crewMen;
    ArrayList<Warship> warships;

    /**
     * Constructor for OutputGenerator
     *
     * @param fileName name of the file to write to
     * @param crew ArrayList of crewmen entities
     * @param warships ArrayList of warships entities
     */
    public OutputGenerator(String fileName,ArrayList<Crewman> crew, ArrayList<Warship> warships) {
        File output = new File(fileName);
        try {
            logFile = new FileWriter(output);
            this.crewMen = crew;
            this.warships = warships;
        } catch ( IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * writes final crewmen state to the logFile
     * @param crewmen ArrayList of crewmen entities
     */
    private void logCrewmen(ArrayList<Crewman> crewmen){
        crewmen.sort((c1,c2)->{
            if (c1 instanceof General && c2 instanceof General) {
                return Integer.compare(((General) c2).getCombatPower(), ((General) c1).getCombatPower());
            } else if (c1 instanceof General) {
                return -1;
            } else if (c2 instanceof General) {
                return 1;
            } else {
                return Integer.compare(((Officer) c2).getIntrinsicLevel(), ((Officer) c1).getIntrinsicLevel());
            }
        });
        for (var crewman : crewmen) {
            String type;
            if (crewman instanceof General)
                type = crewman instanceof Jedi ? "Jedi" : "Sith";
            else
                type = "Officer";

            switch (crewman.getState()) {
                case DEAD -> log(String.format("%s %s is killed by %s\n", type, crewman.getName(), crewman.getKilledBy().getName()));
                case FREE -> log(String.format("%s %s is free\n", type, crewman.getName()));
                case ONBOARD, CAPTURED -> log(String.format("%s %s is in %s\n", type, crewman.getName(), crewman.getCurrentWarship().getName()));
                case IMPRISONED -> log(String.format("%s %s is imprisoned\n", type, crewman.getName()));
            }
            if (type.equals("Officer") ) {
                Officer o = (Officer) crewman;
                log(String.format("%s %d\n", o.getIntrinsic().name(),o.getIntrinsicLevel()));
            }else{
                General g = (General) crewman;
                log(String.format("%d\n",g.getCombatPower()));
            }
        }

    }

    /**
     * writes final warships state to the logFile
     * @param warships ArrayList of warships entities
     */
    private void logWarships(ArrayList<Warship> warships){
        Collections.sort(warships);

        for (Warship warship : warships) {
            if (warship.getState() == WarshipState.DESTROYED) {
                log(String.format("Warship %s is destroyed by %s in (%s,%d)\n",
                        warship.getName(),
                        warship.getDestroyedBy().getName(),
                        warship.getCurrentSector().getName(),
                        warship.getCoordinate()));
            } else {
                log(String.format("Warship %s in (%s, %d)\n%s %d\n",
                        warship.getName(),
                        warship.getCurrentSector().getName(),
                        warship.getCoordinate(),
                        warship.getCommander().getName(),
                        warship.getPowerOutput()));
            }
        }
    }

    /**
     * writes to the logFile
     * @param s string to write
     */
    private void log(String s){
        try {
            logFile.write(s);
            logFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generates final states of entities and writes to the logFile
     */
    public void generateOutput() {
        logWarships(warships);
        logCrewmen(crewMen);
        try {
            logFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
