package model.shop;

import model.characters.player.EventTracker;
import model.characters.player.Player;
import model.characters.player.clicker.AlloyClicker;
import model.characters.player.clicker.ClickerTools;
import model.characters.player.clicker.GoldClicker;
import model.resourceManagement.Resource;

public class ClickerShop {
    private int alloyClickerPrice = 100;
    private int goldClickerPrice = 1000;

    public ClickerShop() {
    }
    public boolean buyClicker(Resource type, Player player) {
        int price = getPrice(type);
        if (player.getWallet().hasEnoughResource(Resource.Gold, price)) {
            player.getWallet().subtractGold(price);
            ClickerTools newTool = createClickerTool(type);
            player.getClicker().addClickerTool(type, newTool);
            player.getEventTracker().addEvent(EventTracker.Message("Minor", "Successfully purchased " + type + " Clicker!"));
            return true; // Purchase was successful
        } else {
            player.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to buy " + type + " clicker."));
            return false; // Purchase failed
        }
    }

    public int getPrice(Resource type) {
        switch (type) {
            case Alloy:
                return alloyClickerPrice;
            case Gold:
                return goldClickerPrice;
            default:
                throw new IllegalArgumentException("Clicker type not available for purchase.");
        }
    }

    public ClickerTools createClickerTool(Resource type) {
        switch (type) {
            case Alloy:
                return new AlloyClicker();
            case Gold:
                return new GoldClicker();
            default:
                throw new IllegalArgumentException("Invalid clicker type.");
        }
    }

    public void setAlloyClickerPrice(int price) {
        this.alloyClickerPrice = price;
    }
    public void setGoldClickerPrice(int price) {
        this.goldClickerPrice = price;
    }
}
