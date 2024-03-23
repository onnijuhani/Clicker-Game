package model.characters.player.clicker;

import model.Settings;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.EventTracker;
import model.time.Time;

import java.util.HashMap;
import java.util.Map;

public class Clicker {
    private static Clicker instance;
    private final Map<Resource, ClickerTools> ownedClickerTools;
    private final EventTracker eventTracker;
    private int totalClicks = 0;
    private final Wallet wallet;
    private final WorkWallet workWallet;

    private final Person person;

    private Clicker(Person person) {
        this.person = person;
        this.eventTracker = person.getEventTracker();
        this.wallet = person.getWallet();
        this.workWallet = person.getWorkWallet();
        this.ownedClickerTools = new HashMap<>();
        this.ownedClickerTools.put(Resource.Food, new FoodClicker(Settings.getInt("foodClicker"), Resource.Food));
    }
    public static void initializeClicker(Person person) {
        if (instance == null) {
            instance = new Clicker(person);
        }
    }

    public static Clicker getInstance() {
        return instance;
    }

    public void addClickerTool(Resource type, ClickerTools tool) {
        ownedClickerTools.put(type, tool);
    }
    public void generateResources() {
        if(Time.gameOver){
            return;
        }
        TransferPackage resourcesGenerated = generate();
        if (person.getRole().getStatus() != Status.King) {
            workWallet.addResources(resourcesGenerated);
            totalClicks++;
            String message = "Clicker generated "+ clickerTransferMessage(resourcesGenerated);
            eventTracker.addEvent(EventTracker.Message("Clicker", message));
        } else {
            wallet.addResources(resourcesGenerated);
            totalClicks++;
            String message = clickerTransferMessage(resourcesGenerated);
            eventTracker.addEvent(EventTracker.Message("Clicker", message));
        }
    }
    private TransferPackage generate() {
        int totalFood = 0;
        int totalAlloy = 0;
        int totalGold = 0;

        for (Map.Entry<Resource, ClickerTools> entry : ownedClickerTools.entrySet()) {
            double amount = entry.getValue().getValue();
            switch (entry.getKey()) {
                case Food:
                    totalFood += (int) amount;
                    break;
                case Alloy:
                    totalAlloy += (int) amount;
                    break;
                case Gold:
                    totalGold += (int) amount;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported resource type: " + entry.getKey());
            }
        }
        return new TransferPackage(totalFood, totalAlloy, totalGold);
    }
    private String clickerTransferMessage(TransferPackage resourcesGenerated) {
        return "Generated " + resourcesGenerated.food() + " food, " +
                resourcesGenerated.alloy() + " alloys, " +
                resourcesGenerated.gold() + " gold!";
    }
    public ClickerTools getClickerTool(Resource resourceType) {
        return ownedClickerTools.get(resourceType);
    }
    public int getTotalClicks() {
        return totalClicks;
    }

    public boolean isFoodClickerOwned() {
        System.out.println(ownedClickerTools.containsKey(Resource.Food));
        return ownedClickerTools.containsKey(Resource.Food);
    }

    public boolean isAlloyClickerOwned() {
        System.out.println(ownedClickerTools.containsKey(Resource.Alloy));
        return ownedClickerTools.containsKey(Resource.Alloy);
    }

    public boolean isGoldClickerOwned() {
        System.out.println(ownedClickerTools.containsKey(Resource.Gold));
        return ownedClickerTools.containsKey(Resource.Gold);
    }

}




