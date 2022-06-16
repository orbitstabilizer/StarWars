package project.event;

import project.enums.EventType;

import java.util.ArrayList;


public class Event {
    public EventType type;
    public ArrayList<Integer> parameters;
    /**
     * Constructor for Event
     * @param type EventType of the event
     * @param parameters ArrayList<Integer> parameters of the event
     */
    public Event(EventType type, Integer... parameters) {
        this.type = type;
        this.parameters = new ArrayList<>();
        for (Integer parameter : parameters) {
            this.parameters.add(parameter);
        }
    }

    /**
     * @return string representation of the event
     */
    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", parameters=" + parameters +
                '}';
    }
}
