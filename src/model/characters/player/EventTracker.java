package model.characters.player;

import java.util.LinkedList;

public class EventTracker {
    private LinkedList<String> events;
    public EventTracker(){
        this.events = new LinkedList<>();
    }
    public LinkedList<String> getEvents() {
        return events;
    }
    public void addEvent(String event) {
        events.add(event);
    }
}
