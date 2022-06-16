package project.executable;

import project.event.EventHandler;
import project.io.InputReader;
import project.io.OutputGenerator;
import project.sector.Sector;
import project.crewman.Crewman;
import project.warships.*;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {
	/**
	 * generates an output-file summarizing end state of entities in this world
	 * @param args input-file output-file
	 */
	public static void main(String[] args) {

		// relevant entities are stored in the following containers
		HashMap<Integer, Sector> sectors = new HashMap<>();
		HashMap<Integer, Warship> warships = new HashMap<>();
		HashMap<Integer, Crewman> crewMen = new HashMap<>();

		InputReader inputReader = new InputReader(args[0],sectors, warships, crewMen);
		inputReader.extractEntities();
		var events = inputReader.extractEvents();
		if (events == null) return;


		EventHandler eventHandler = new EventHandler(sectors, warships, crewMen,events);
		eventHandler.handleEvents();

		OutputGenerator outputGenerator = new OutputGenerator(args[1], new ArrayList<>(crewMen.values()), new ArrayList<>(warships.values()));
		outputGenerator.generateOutput();


	}
}
