package model.stateSystem;

import model.characters.player.PlayerPreferences;
import model.time.Time;

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
        String time = (Time.getDay()<10 ? Time.getClock()+"" : Time.getClock());
        return String.format("%s %s %s", now.format(formatter), type, time+":  "+message);
    }
    private int maxErrorEvents = 5;
    private int maxMajorEvents = 50;
    private int maxResourceEvents = 20;
    private int maxMinorEvents = 10;
    private int maxShopEvents = 25;
    private int maxUtilityEvents = 25;

    private final LinkedList<String> majorEvents = new LinkedList<>();
    private final LinkedList<String> resourceEvents = new LinkedList<>();
    private final LinkedList<String> errorEvents = new LinkedList<>();
    private final LinkedList<String> minorEvents = new LinkedList<>();
    private final LinkedList<String> shopEvents = new LinkedList<>();
    private final LinkedList<String> utilityEvents = new LinkedList<>();
    private final PlayerPreferences preferences;

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
                        getPreferences().isShowShopEvents() ? shopEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowShopEvents() ? utilityEvents.stream() : Stream.<String>empty()
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
    public LinkedList<String> getUtilityEvents() {
        return utilityEvents;
    }
    public void addEvent(String message) {
        String type = extractTypeFromMessage(message);

        switch (type) {
            case "Error":
                addEventToCategory(errorEvents, message, maxErrorEvents);
                break;
            case "Major":
                addEventToCategory(majorEvents, message, maxMajorEvents);
                break;
            case "Resource":
                addEventToCategory(resourceEvents, message, maxResourceEvents);
                break;
            case "Minor":
                addEventToCategory(minorEvents, message, maxMinorEvents);
                break;
            case "Shop":
                addEventToCategory(shopEvents, message, maxShopEvents);
                break;
            case "Utility":
                addEventToCategory(utilityEvents, message, maxUtilityEvents);
                break;
            default:
                System.out.println("Unknown event type: " + type);
                break;
        }
    }

    private String extractTypeFromMessage(String message) {
        String[] parts = message.split(" ", 4);  // Splitting into four parts
        String extractedType = parts.length > 2 ? parts[2] : "Unknown";
        return extractedType;
    }

    private void addEventToCategory(LinkedList<String> eventList, String event, int maxSize) {
        if (eventList.size() >= maxSize) {
            eventList.removeFirst(); // Remove the oldest event to maintain the size
        }
        eventList.add(event);
    }

    public void setMaxErrorEvents(int size) {
        this.maxErrorEvents = size;
    }
    public void setMaxMajorEvents(int size) {
        this.maxMajorEvents = size;
    }
    public void setMaxResourceEvents(int size) {
        this.maxResourceEvents = size;
    }
    public void setMaxMinorEvents(int size) {
        this.maxMinorEvents = size;
    }
    public void setMaxShopEvents(int size) {
        this.maxShopEvents = size;
    }
}
