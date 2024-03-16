package model.characters.player.clicker;

import model.Settings;
import model.characters.Status;
import model.stateSystem.EventTracker;
import model.characters.player.Player;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;

import java.util.HashMap;
import java.util.Map;

public class Clicker {
    private final Map<Resource, ClickerTools> ownedClickerTools;
    private final EventTracker eventTracker;
    private int totalClicks = 0;
    private final Wallet wallet;
    private final WorkWallet workWallet;
    private final Status status;
    public Clicker(Player player) {
        this.eventTracker = player.getEventTracker();
        this.wallet = player.getWallet();
        this.workWallet = player.getWorkWallet();
        this.status = player.getStatus();
        this.ownedClickerTools = new HashMap<>();
        this.ownedClickerTools.put(Resource.Food, new FoodClicker(Settings.getInt("foodClicker"), Resource.Food));
    }
    public void addClickerTool(Resource type, ClickerTools tool) {
        ownedClickerTools.put(type, tool);
    }





    public void generateResources() {
        TransferPackage resourcesGenerated = generate();
        if (status != Status.King) {
            workWallet.addResources(resourcesGenerated);
            totalClicks++;
            String message = "Into Worker Wallet "+ clickerTransferMessage(resourcesGenerated);
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




