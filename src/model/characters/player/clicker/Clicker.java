package model.characters.player.clicker;

import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.Wallet;

import java.util.HashMap;
import java.util.Map;

public class Clicker {
    private Map<Resource, ClickerTools> clickerTools;
    private EventTracker eventTracker;
    private int totalClicks = 0;
    private Wallet wallet;
    public Clicker(EventTracker eventTracker, Wallet wallet) {
        this.eventTracker = eventTracker;
        this.wallet = wallet;
        this.clickerTools = new HashMap<>();
        this.clickerTools.put(Resource.Food, new FoodClicker());
    }
    public void addClickerTool(Resource type, ClickerTools tool) {
        clickerTools.put(type, tool);
    }
public void generateResources() {
        TransferPackage resourcesGenerated = generate();
        wallet.addResources(resourcesGenerated);
        totalClicks++;
        String message = eventMessage(resourcesGenerated);
        eventTracker.addEvent(message);
    }
    private TransferPackage generate() {
        double totalFood = 0;
        double totalAlloy = 0;
        double totalGold = 0;

        for (Map.Entry<Resource, ClickerTools> entry : clickerTools.entrySet()) {
            double amount = entry.getValue().getResourceAmount();
            switch (entry.getKey()) {
                case Food:
                    totalFood += amount;
                    break;
                case Alloy:
                    totalAlloy += amount;
                    break;
                case Gold:
                    totalGold += amount;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported resource type: " + entry.getKey());
            }
        }
        return new TransferPackage(totalFood, totalAlloy, totalGold);
    }
    private String eventMessage(TransferPackage resourcesGenerated) {
        return "Generated " + resourcesGenerated.food() + " food, " +
                resourcesGenerated.alloy() + " alloys, " +
                resourcesGenerated.gold() + " gold!";
    }
    public ClickerTools getFoodClicker() {
        return clickerTools.get(Resource.Food);
    }
    public ClickerTools getAlloyClicker() {
        return clickerTools.get(Resource.Alloy);
    }
    public ClickerTools getGoldClicker() {
        return clickerTools.get(Resource.Gold);
    }
    public int getTotalClicks() {
        return totalClicks;
    }

    public boolean isFoodClickerOwned() {
        return clickerTools.containsKey(Resource.Food);
    }

    public boolean isAlloyClickerOwned() {
        return clickerTools.containsKey(Resource.Alloy);
    }

    public boolean isGoldClickerOwned() {
        return clickerTools.containsKey(Resource.Gold);
    }

}




