package model.characters.player;

import time.Time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventTracker {
    public static String Message(String type, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = (Time.getDay()<10 ? Time.getClock()+"  " : Time.getClock());
        return String.format("%s %s '%s'", now.format(formatter), type, time+"          "+message);
    }
    private static final int MAX_EVENTS = 10000; // Max number of events per list

    private LinkedList<String> majorEvents = new LinkedList<>();
    private LinkedList<String> resourceEvents = new LinkedList<>();
    private LinkedList<String> errorEvents = new LinkedList<>();
    private LinkedList<String> minorEvents = new LinkedList<>();
    private LinkedList<String> shopEvents = new LinkedList<>();
    private PlayerPreferences preferences;

    public EventTracker() {
        preferences = new PlayerPreferences();
    }
    public List<String> getCombinedEvents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Stream.of(
                        getPreferences().isShowMajorEvents() ? majorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowErrorEvents() ? errorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowResourceEvents() ? resourceEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowMinorEvents() ? minorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowShopEvents() ? shopEvents.stream() : Stream.<String>empty()
                )
                .flatMap(s -> s)
                .sorted(Comparator.comparing((String message) -> LocalDateTime.parse(message.split(" ")[0] + " " + message.split(" ")[1], formatter)))
                .map(this::removeTimestampAndType)
                .collect(Collectors.toList());
    }

    private String removeTimestampAndType(String message) {
        String[] parts = message.split(" ", 4);
        return parts.length > 3 ? parts[3] : message; // Return the message part or the original message if splitting failed
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
    public LinkedList<String> getShopEvents() {
        return shopEvents;
    }
    public void addEvent(String message) {
        String type = extractTypeFromMessage(message);

        switch (type) {
            case "Error":
                addEventToCategory(errorEvents, message);
                break;
            case "Major":
                addEventToCategory(majorEvents, message);
                break;
            case "Resource":
                addEventToCategory(resourceEvents, message);
                break;
            case "Minor":
                addEventToCategory(minorEvents, message);
                break;
            case "Shop":
                addEventToCategory(shopEvents, message);
                break;
            default:
                System.out.println("Unknown event type: " + type);  // Debugging: Log unknown types
                break;
        }
    }

    private String extractTypeFromMessage(String message) {
        String[] parts = message.split(" ", 4);  // Splitting into four parts
        String extractedType = parts.length > 2 ? parts[2] : "Unknown";
        return extractedType;
    }

    private void addEventToCategory(LinkedList<String> eventList, String event) {
        if (eventList.size() >= MAX_EVENTS) {
            eventList.removeFirst(); // Remove the oldest event to maintain the size
        }
        eventList.add(event);
    }
}

