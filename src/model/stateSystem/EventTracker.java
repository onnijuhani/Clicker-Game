package model.stateSystem;

import model.Settings;
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

    private final LinkedList<String> majorEvents = new LinkedList<>();
    private final LinkedList<String> clickerEvents = new LinkedList<>();
    private final LinkedList<String> errorEvents = new LinkedList<>();
    private final LinkedList<String> minorEvents = new LinkedList<>();
    private final LinkedList<String> shopEvents = new LinkedList<>();
    private final LinkedList<String> utilityEvents = new LinkedList<>();
    private final PlayerPreferences preferences;


    private final int maxErrorEvents;
    private final int maxMajorEvents;
    private final int maxClickerEvents;
    private final int maxMinorEvents;
    private final int maxShopEvents;
    private final int maxUtilityEvents;

    public EventTracker(boolean isNpc) {
        preferences = new PlayerPreferences();

        if (isNpc) {
            // NPC's have lower amounts stored
            maxErrorEvents = 0;
            maxMajorEvents = Settings.getInt("maxMajorEvents");;
            maxClickerEvents = 0;
            maxMinorEvents = 0;
            maxShopEvents = 0;
            maxUtilityEvents = 0;
        } else {
            maxErrorEvents = Settings.getInt("maxErrorEvents");
            maxMajorEvents = Settings.getInt("maxMajorEvents");
            maxClickerEvents = Settings.getInt("maxClickerEvents");
            maxMinorEvents = Settings.getInt("maxMinorEvents");
            maxShopEvents = Settings.getInt("maxShopEvents");
            maxUtilityEvents = Settings.getInt("maxUtilityEvents");
        }
    }
    public List<String> getCombinedEvents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Stream.of(
                        getPreferences().isShowErrorEvents() ? errorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowClickerEvents() ? clickerEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowMinorEvents() ? minorEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowShopEvents() ? shopEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowShopEvents() ? utilityEvents.stream() : Stream.<String>empty(),
                        getPreferences().isShowMajorEvents() ? majorEvents.stream() : Stream.<String>empty()
                )
                .flatMap(s -> s)
                .sorted(Comparator.comparing((String message) -> LocalDateTime.parse(message.split(" ")[0] + " " + message.split(" ")[1], formatter)))
                .map(this::removeTimestampAndType)
                .collect(Collectors.toList());
    }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();

        majorEvents.forEach(message -> sb.append(message).append("\n"));

        return sb.toString();

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
    public LinkedList<String> getClickerEvents() {
        return clickerEvents;
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
            case "Major":
                addEventToCategory(majorEvents, message, maxMajorEvents);
                break;
            case "Error":
                addEventToCategory(errorEvents, message, maxErrorEvents);
                break;
            case "Clicker":
                addEventToCategory(clickerEvents, message, maxClickerEvents);
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
        return parts.length > 2 ? parts[2] : "Unknown";
    }
    private void addEventToCategory(LinkedList<String> eventList, String event, int maxSize) {
        // the core message from the new event
        String[] newMessageParts = event.split(" ", 5); // Split into parts, expecting at least 5
        String newCoreMessage = newMessageParts.length > 4 ? newMessageParts[4] : "";

        // Check for duplicate message only if the list is not empty
        if (!eventList.isEmpty()) {
            String lastEvent = eventList.getLast();
            String[] lastMessageParts = lastEvent.split(" ", 5);
            String lastCoreMessage = lastMessageParts.length > 4 ? lastMessageParts[4] : "";

            // If the core message is identical, do not add the new event
            if (newCoreMessage.equals(lastCoreMessage)) {
                return;
            }
        }

        if (eventList.size() >= maxSize && !eventList.isEmpty()) {
            eventList.removeFirst(); // Remove the oldest event to maintain the size
        }
        eventList.add(event);
    }
}

