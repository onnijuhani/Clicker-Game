package model.characters.player;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventTracker {
    private static final int MAX_EVENTS = 10000; // Max number of events per list

    private LinkedList<String> majorEvents = new LinkedList<>();
    private LinkedList<String> resourceEvents = new LinkedList<>();
    private LinkedList<String> errorEvents = new LinkedList<>();
    private LinkedList<String> minorEvents = new LinkedList<>();
    private PlayerPreferences preferences;

    public EventTracker() {
        preferences = new PlayerPreferences();
    }
    public List<String> getCombinedEvents() {
        return Stream.of(
                        getPreferences().isShowMajorEvents() ? majorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowErrorEvents() ? errorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowResourceEvents() ? resourceEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowMinorEvents() ? minorEvents.stream() : Stream.<String>empty()
                )
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    public PlayerPreferences getPreferences() {
        return preferences;
    }
    public LinkedList<String> getErrorEvents() {
        return errorEvents;
    }
    public LinkedList<String> getMajorEvents() {
        return majorEvents;
    }

    public LinkedList<String> getResourceEvents() {
        return resourceEvents;
    }

    public LinkedList<String> getMinorEvents() {
        return minorEvents;
    }
    public void addErrorEvent(String event) {
        addEventToCategory(errorEvents, event);
    }

    public void addMajorEvent(String event) {
        addEventToCategory(majorEvents, event);
    }

    public void addResourceEvent(String event) {
        addEventToCategory(resourceEvents, event);
    }

    public void addMinorEvent(String event) {
        addEventToCategory(minorEvents, event);
    }

    private void addEventToCategory(LinkedList<String> eventList, String event) {
        if (eventList.size() >= MAX_EVENTS) {
            eventList.removeFirst(); // Remove the oldest event to maintain the size
        }
        eventList.add(event);
    }


}

class PlayerPreferences{


    private boolean showMajorEvents = true;
    private boolean showResourceEvents = true;
    private boolean showErrorEvents = true;
    private boolean showMinorEvents = true;

    public void setShowResourceEvents(boolean show) {
        this.showResourceEvents = show;
    }
    public void setShowMinorEvents(boolean show) {
        this.showMinorEvents = show;
    }

    public boolean isShowMajorEvents() {
        return showMajorEvents;
    }

    public boolean isShowResourceEvents() {
        return showResourceEvents;
    }

    public boolean isShowErrorEvents() {
        return showErrorEvents;
    }

    public boolean isShowMinorEvents() {
        return showMinorEvents;
    }

}
