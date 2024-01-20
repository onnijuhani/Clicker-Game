import java.util.LinkedList;

public class Player {
    private Property property;
    private Wallet wallet;
    private String name;
    private EventTracker eventTracker;
    private Clicker clicker;

    public Player(Quarter spawn){
        this.property = new Shack("Your Own");
        this.property.setLocation(spawn);
        this.wallet = new Wallet();
        this.eventTracker = new EventTracker();
        this.clicker = new Clicker(eventTracker, wallet);
    }

    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public EventTracker getEventTracker() {
        return eventTracker;
    }
    public Clicker getClicker() {
        return clicker;
    }



}

class EventTracker {
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











