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

class Clicker {
    private double food = 1;
    private double alloy = 0;
    private double gold = 0;
    private EventTracker eventTracker;
    private int totalClicks = 0;
    private Wallet wallet;

    public Clicker(EventTracker eventTracker, Wallet wallet) {
        this.eventTracker = eventTracker;
        this.wallet = wallet;
    }

    public void increase(Resource type) {
        switch (type) {
            case Food:
                this.food++;
                break;
            case Alloy:
                this.alloy++;
                break;
            case Gold:
                this.gold++;
                break;
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }

    public void generateResources() {
        wallet.addResources(generate());
        totalClicks++;
        String message = eventMessage();
        eventTracker.addEvent(message);
    }

    private TransferPackage generate() {
        return new TransferPackage(food, alloy, gold);
    }

    public String eventMessage() {
        return "Generated "+food+" food, "+alloy+" alloys, "+gold+" gold!";
    }
    public int getTotalClicks() {
        return totalClicks;
    }
    public double getFood() {
        return food;
    }
    public void setFood(double food) {
        this.food = food;
    }
    public double getAlloy() {
        return alloy;
    }
    public void setAlloy(double alloy) {
        this.alloy = alloy;
    }
    public double getGold() {
        return gold;
    }
    public void setGold(double gold) {
        this.gold = gold;
    }

}







